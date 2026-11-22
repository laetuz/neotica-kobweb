package id.neotica.neotica.pages.orpheum

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Color
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.data.add
import com.varabyte.kobweb.core.init.InitRoute
import com.varabyte.kobweb.core.init.InitRouteContext
import com.varabyte.kobweb.core.layout.Layout
import com.varabyte.kobweb.framework.annotations.DelicateApi
import com.varabyte.kobweb.core.rememberPageContext
import com.varabyte.kobweb.silk.components.graphics.Image
import com.varabyte.kobweb.silk.components.layout.SimpleGrid
import com.varabyte.kobweb.silk.components.layout.numColumns
import com.varabyte.kobweb.silk.components.navigation.Link
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.style.breakpoint.Breakpoint
import com.varabyte.kobweb.silk.theme.breakpoint.rememberBreakpoint
import id.neotica.neotica.components.NeoColor
import id.neotica.neotica.components.layouts.NeoLayoutData
import id.neotica.neotica.components.retro.RainbowDivider
import id.neotica.neotica.components.retro.RetroColor
import id.neotica.neotica.components.retro.StarfieldStars
import id.neotica.neotica.domain.dummy.OrpheumTrackList
import id.neotica.neotica.domain.model.orpheum.AlbumRemoteModel
import id.neotica.neotica.domain.model.orpheum.HomeFeedResponse
import id.neotica.neotica.domain.model.orpheum.TrackFeedResponse
import id.neotica.neotica.domain.model.orpheum.TrackRemoteModel
import id.neotica.neotica.utils.Constants
import id.neotica.neotica.utils.encodeQueryComponent
import id.neotica.neotica.utils.queryParam
import kotlinx.browser.window
import kotlinx.coroutines.await
import kotlinx.serialization.json.Json
import org.jetbrains.compose.web.attributes.placeholder
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.TextInput

private const val USE_DUMMY_DATA = false
private const val ITEM_LIMIT = 20

private fun searchPath(query: String, page: Int): String =
    "/orpheum?q=${encodeQueryComponent(query)}&page=$page"

@InitRoute()
fun initOrpheumPage(ctx: InitRouteContext) {
    ctx.data.add(NeoLayoutData("Orpheum \u2014 Stream Music | Neotica.id", "/orpheum"))
}

@OptIn(DelicateApi::class)
@Page(routeOverride = "/orpheum")
@Composable
@Layout(".components.layouts.NeoPageLayout")
fun OrpheumPage() {
    val ctx = rememberPageContext()
    val breakpoint = rememberBreakpoint()
    val jsonParser = remember { Json { ignoreUnknownKeys = true } }

    val initialQuery = ctx.route.queryParams["q"].orEmpty()
    val initialPage = ctx.route.queryParams["page"]?.toIntOrNull()?.coerceAtLeast(1) ?: 1

    var searchInput by remember { mutableStateOf(initialQuery) }
    var appliedQuery by remember { mutableStateOf(initialQuery) }
    var searchPage by remember { mutableStateOf(initialPage) }
    var retryTick by remember { mutableStateOf(0) }

    var recentAlbums by remember { mutableStateOf<List<AlbumRemoteModel>>(emptyList()) }
    var trackList by remember { mutableStateOf<List<TrackRemoteModel>>(emptyList()) }
    var totalPages by remember { mutableStateOf(1) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val isSearchMode = appliedQuery.isNotBlank()

    LaunchedEffect(Unit) {
        val head = window.document.head ?: return@LaunchedEffect
        val existing = head.querySelectorAll("meta[property^='og:']")
        for (i in 0 until existing.length) existing.item(i)?.let { head.removeChild(it) }

        fun injectMeta(property: String, content: String) {
            head.appendChild(window.document.createElement("meta").apply {
                setAttribute("property", property)
                setAttribute("content", content)
            })
        }
        injectMeta("og:title", "Orpheum \u2014 Stream Music | Neotica.id")
        injectMeta("og:description", "Stream the Neotica music catalog on Orpheum — recent albums, new tracks, and search.")
        injectMeta("og:type", "website")
        injectMeta("og:url", "https://neotica.id/orpheum")
        injectMeta("og:image", "https://neotica.id/projects/orpheum/orpheum_banner.png")

        head.querySelector("link[rel='canonical']")?.let { head.removeChild(it) }
        head.appendChild(window.document.createElement("link").apply {
            setAttribute("rel", "canonical")
            setAttribute("href", "https://neotica.id/orpheum")
        })
    }

    // Keep browser Back/Forward in sync with the search state.
    DisposableEffect(Unit) {
        val onPopState = { _: org.w3c.dom.events.Event ->
            val q = queryParam(window.location.search, "q").orEmpty()
            val p = queryParam(window.location.search, "page")?.toIntOrNull()?.coerceAtLeast(1) ?: 1
            searchInput = q
            appliedQuery = q
            searchPage = p
        }
        window.addEventListener("popstate", onPopState)
        onDispose { window.removeEventListener("popstate", onPopState) }
    }

    LaunchedEffect(appliedQuery, searchPage, retryTick) {
        isLoading = true
        errorMessage = null
        try {
            if (USE_DUMMY_DATA) {
                if (appliedQuery.isBlank()) {
                    trackList = OrpheumTrackList.getHome(ITEM_LIMIT)
                    recentAlbums = emptyList()
                    totalPages = 1
                } else {
                    val result = OrpheumTrackList.search(appliedQuery, searchPage, ITEM_LIMIT)
                    trackList = result.data
                    totalPages = result.totalPages
                }
            } else if (appliedQuery.isBlank()) {
                val url = "${Constants.ORPHEUM_DEV_URL}/orpheum/home?limit=$ITEM_LIMIT"
                val response = window.fetch(url).await()
                if (!response.ok) throw Exception("HTTP Error: ${response.status} ${response.statusText}")
                val feed = jsonParser.decodeFromString<HomeFeedResponse>(response.text().await())
                recentAlbums = feed.recentAlbums
                trackList = feed.recentTracks
                totalPages = 1
            } else {
                val url = "${Constants.ORPHEUM_DEV_URL}/orpheum/catalog/search" +
                        "?q=${encodeQueryComponent(appliedQuery)}&page=$searchPage&limit=$ITEM_LIMIT"
                val response = window.fetch(url).await()
                if (!response.ok) throw Exception("HTTP Error: ${response.status} ${response.statusText}")
                val result = jsonParser.decodeFromString<TrackFeedResponse>(response.text().await())
                trackList = result.data
                totalPages = result.totalPages
            }
        } catch (e: Throwable) {
            console.error("Failed to load Orpheum: ", e)
            errorMessage = e.message ?: "An unknown network error occurred (Likely CORS)."
        }
        isLoading = false
    }

    fun applySearch() {
        val query = searchInput.trim()
        appliedQuery = query
        searchPage = 1
        window.history.pushState(null, "", if (query.isBlank()) "/orpheum" else searchPath(query, 1))
    }

    fun clearSearch() {
        searchInput = ""
        appliedQuery = ""
        searchPage = 1
        window.history.pushState(null, "", "/orpheum")
    }

    Column(
        modifier = Modifier
            .background {
                color(NeoColor.backgroundPrimary)
                image(StarfieldStars)
            }
            .fillMaxSize()
            .padding(leftRight = 1.5.cssRem, topBottom = 1.2.cssRem)
            .overflow(Overflow.Auto)
            .gap(1.2.cssRem)
            .fontFamily("VT323", "monospace"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().gap(0.3.cssRem),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SpanText(
                text = "\u2726  Orpheum Stream  \u2726",
                modifier = Modifier
                    .fontSize(2.2.cssRem)
                    .fontWeight(FontWeight.Bold)
                    .color(RetroColor.babyBlue)
                    .textAlign(TextAlign.Center)
                    .textShadow(3.px, 3.px, color = RetroColor.navy.copy(alpha = 200))
            )
            SpanText(
                text = "Hit those tracks right now.",
                modifier = Modifier
                    .fontSize(1.05.cssRem)
                    .color(RetroColor.cream)
                    .textAlign(TextAlign.Center)
            )
        }

        RainbowDivider()

        Row(
            modifier = Modifier.fillMaxWidth().maxWidth(760.px).gap(0.6.cssRem),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.weight(1f)) {
                TextInput(
                    value = searchInput,
                    attrs = {
                        placeholder("Search tracks or artists...")
                        onInput { searchInput = it.value }
                        style { retroFieldStyle() }
                    }
                )
            }
            BevelButton("Search", RetroColor.babyBlue) { applySearch() }
            if (isSearchMode || searchInput.isNotBlank()) {
                BevelButton("Clear", RetroColor.steelBlue) { clearSearch() }
            }
        }

        when {
            isLoading -> SpanText(
                text = "Loading audio tracks...",
                modifier = Modifier.fontSize(1.4.cssRem).color(RetroColor.cyan)
            )
            errorMessage != null -> Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.gap(0.6.cssRem)
            ) {
                SpanText(
                    text = "Failed to load: $errorMessage",
                    modifier = Modifier.fontSize(1.3.cssRem).color(NeoColor.negativePrimary).textAlign(TextAlign.Center)
                )
                BevelButton("Try again", RetroColor.babyBlue) { retryTick++ }
            }
            else -> Column(
                modifier = Modifier.fillMaxWidth().gap(1.2.cssRem),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (!isSearchMode && recentAlbums.isNotEmpty()) {
                    SectionTitle("Recent Albums")
                    if (breakpoint < Breakpoint.MD) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .maxWidth(900.px)
                                .overflow(Overflow.Auto)
                                .gap(0.9.cssRem)
                                .padding(bottom = 0.3.cssRem)
                        ) {
                            recentAlbums.forEach {
                                AlbumCard(it, Modifier.width(160.px).flexShrink(0f))
                            }
                        }
                    } else {
                        SimpleGrid(
                            numColumns = numColumns(base = 2, md = 3, lg = 4),
                            modifier = Modifier.fillMaxWidth().maxWidth(900.px).gap(0.9.cssRem)
                        ) {
                            recentAlbums.forEach { AlbumCard(it, Modifier.fillMaxWidth()) }
                        }
                    }
                    RainbowDivider()
                }

                if (isSearchMode) {
                    SectionTitle("Results for \u201C$appliedQuery\u201D")
                } else {
                    SectionTitle("Daily Tracks", path = "/orpheum/tracks")
                }

                if (trackList.isEmpty()) {
                    SpanText(
                        text = if (isSearchMode) "No tracks matched your search." else "No tracks available.",
                        modifier = Modifier.fontSize(1.3.cssRem).color(RetroColor.cream).textAlign(TextAlign.Center)
                    )
                } else {
                    OrpheumTrackListPlayer(
                        tracks = trackList,
                        modifier = Modifier.maxWidth(760.px)
                    )
                }

                if (isSearchMode && trackList.isNotEmpty() && totalPages > 1) {
                    OrpheumPagination(searchPage, totalPages) { newPage ->
                        searchPage = newPage
                        window.history.pushState(null, "", searchPath(appliedQuery, newPage))
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionTitle(text: String, path: String? = null) {
    val content: @Composable () -> Unit = {
        SpanText(
            text = "\u2726  $text  \u2726",
            modifier = Modifier
                .fillMaxWidth()
                .fontSize(1.5.cssRem)
                .fontWeight(FontWeight.Bold)
                .color(RetroColor.skyBlue)
                .textAlign(TextAlign.Center)
                .textShadow(2.px, 2.px, color = Color.rgb(0, 0, 0).copy(alpha = 153))
        )
    }
    if (path != null) {
        Link(
            path = path,
            modifier = Modifier
                .fillMaxWidth()
                .cursor(Cursor.Pointer)
                .textAlign(TextAlign.Center)
                .textDecorationLine(TextDecorationLine.None)
        ) { content() }
    } else {
        content()
    }
}

@Composable
private fun AlbumCard(album: AlbumRemoteModel, modifier: Modifier = Modifier) {
    Link(
        path = "/orpheum/album/${album.id}",
        modifier = modifier.textDecorationLine(TextDecorationLine.None)
    ) {
        Column(modifier = Modifier.fillMaxWidth().gap(0.25.cssRem)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.px)
                    .border(2.px, LineStyle.Solid, RetroColor.steelBlue.copy(alpha = 120))
                    .backgroundColor(RetroColor.navy.copy(alpha = 200))
                    .borderRadius(4.px)
                    .overflow(Overflow.Hidden),
                contentAlignment = Alignment.Center
            ) {
                val cover = resolveOrpheumMedia(album.coverUrl)
                if (cover != null) {
                    Image(
                        src = cover,
                        modifier = Modifier.fillMaxSize().objectFit(ObjectFit.Cover)
                    )
                } else {
                    SpanText(
                        text = "\u266A",
                        modifier = Modifier.fontSize(1.6.cssRem).color(RetroColor.skyBlue)
                    )
                }
            }
            SpanText(
                text = album.title,
                modifier = Modifier.fontSize(1.05.cssRem).fontWeight(FontWeight.Bold).color(RetroColor.cream)
            )
            val subtitle = buildString {
                append(album.artistName)
                if (album.releaseYear > 0) {
                    if (isNotEmpty()) append("  \u00B7  ")
                    append(album.releaseYear)
                }
            }
            if (subtitle.isNotBlank()) {
                SpanText(
                    text = subtitle,
                    modifier = Modifier.fontSize(0.85.cssRem).color(RetroColor.cyan)
                )
            }
        }
    }
}

private fun StyleScope.retroFieldStyle() {
    padding(10.px)
    width(100.percent)
    fontFamily("VT323", "monospace")
    fontSize(1.1.cssRem)
    backgroundColor(RetroColor.cream)
    color(RetroColor.navy)
    border(2.px, LineStyle.Solid, RetroColor.navy)
    borderRadius(3.px)
}