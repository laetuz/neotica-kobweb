package id.neotica.neotica.pages.orpheum

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.css.Overflow
import com.varabyte.kobweb.compose.css.TextAlign
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.data.add
import com.varabyte.kobweb.core.init.InitRoute
import com.varabyte.kobweb.core.init.InitRouteContext
import com.varabyte.kobweb.core.layout.Layout
import com.varabyte.kobweb.core.rememberPageContext
import com.varabyte.kobweb.silk.components.navigation.Link
import com.varabyte.kobweb.silk.components.text.SpanText
import id.neotica.neotica.components.NeoColor
import id.neotica.neotica.components.layouts.NeoLayoutData
import id.neotica.neotica.components.retro.RainbowDivider
import id.neotica.neotica.components.retro.RetroColor
import id.neotica.neotica.components.retro.StarfieldStars
import id.neotica.neotica.domain.model.orpheum.TrackFeedResponse
import id.neotica.neotica.domain.model.orpheum.TrackRemoteModel
import id.neotica.neotica.utils.Constants
import id.neotica.neotica.utils.queryParam
import kotlinx.browser.window
import kotlinx.coroutines.await
import kotlinx.serialization.json.Json
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.px

private const val CATALOG_LIMIT = 12

@InitRoute
fun initOrpheumTracksPage(ctx: InitRouteContext) {
    ctx.data.add(NeoLayoutData("All Tracks \u2014 Orpheum | Neotica.id", "/orpheum"))
}

@Page
@Composable
@Layout(".components.layouts.NeoPageLayout")
fun OrpheumTracksPage() {
    val ctx = rememberPageContext()
    val jsonParser = remember { Json { ignoreUnknownKeys = true } }

    var page by remember {
        mutableStateOf(ctx.route.queryParams["page"]?.toIntOrNull()?.coerceAtLeast(1) ?: 1)
    }
    var retryTick by remember { mutableStateOf(0) }
    var tracks by remember { mutableStateOf<List<TrackRemoteModel>>(emptyList()) }
    var totalPages by remember { mutableStateOf(1) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

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
        injectMeta("og:title", "All Tracks \u2014 Orpheum | Neotica.id")
        injectMeta("og:description", "Browse every track in the Neotica music catalog, newest first.")
        injectMeta("og:type", "website")
        injectMeta("og:url", "https://neotica.id/orpheum/tracks")
        injectMeta("og:image", "https://neotica.id/projects/orpheum/orpheum_banner.png")

        head.querySelector("link[rel='canonical']")?.let { head.removeChild(it) }
        head.appendChild(window.document.createElement("link").apply {
            setAttribute("rel", "canonical")
            setAttribute("href", "https://neotica.id/orpheum/tracks")
        })
    }

    // Keep browser Back/Forward in sync with ?page=
    DisposableEffect(Unit) {
        val onPopState = { _: org.w3c.dom.events.Event ->
            page = queryParam(window.location.search, "page")?.toIntOrNull()?.coerceAtLeast(1) ?: 1
        }
        window.addEventListener("popstate", onPopState)
        onDispose { window.removeEventListener("popstate", onPopState) }
    }

    LaunchedEffect(page, retryTick) {
        isLoading = true
        errorMessage = null
        try {
            val url = "${Constants.ORPHEUM_DEV_URL}/orpheum/catalog/tracks/new?page=$page&limit=$CATALOG_LIMIT"
            val response = window.fetch(url).await()
            if (!response.ok) throw Exception("HTTP Error: ${response.status} ${response.statusText}")
            val feed = jsonParser.decodeFromString<TrackFeedResponse>(response.text().await())
            if (feed.totalPages in 1 until page) {
                page = feed.totalPages
                return@LaunchedEffect
            }
            tracks = feed.data
            totalPages = feed.totalPages
        } catch (e: Throwable) {
            console.error("Failed to load track catalog: ", e)
            errorMessage = e.message ?: "An unknown network error occurred (Likely CORS)."
        }
        isLoading = false
    }

    fun goToPage(newPage: Int) {
        page = newPage
        window.history.pushState(null, "", "/orpheum/tracks?page=$newPage")
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
        Link(
            text = "\u2190  Back to Orpheum",
            path = "/orpheum",
            modifier = Modifier.align(Alignment.Start).fontSize(1.1.cssRem).color(RetroColor.skyBlue)
        )

        Column(
            modifier = Modifier.fillMaxWidth().gap(0.3.cssRem),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SpanText(
                text = "\u2726  All Tracks  \u2726",
                modifier = Modifier
                    .fontSize(2.2.cssRem)
                    .fontWeight(FontWeight.Bold)
                    .color(RetroColor.babyBlue)
                    .textAlign(TextAlign.Center)
                    .textShadow(3.px, 3.px, color = RetroColor.navy.copy(alpha = 200))
            )
            SpanText(
                text = "Every track in the catalog, newest first.",
                modifier = Modifier.fontSize(1.05.cssRem).color(RetroColor.cream).textAlign(TextAlign.Center)
            )
        }

        RainbowDivider()

        when {
            isLoading -> SpanText(
                text = "Loading tracks...",
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
            tracks.isEmpty() -> SpanText(
                text = "No tracks available.",
                modifier = Modifier.fontSize(1.3.cssRem).color(RetroColor.cream).textAlign(TextAlign.Center)
            )
            else -> Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OrpheumTrackListPlayer(
                    tracks = tracks,
                    modifier = Modifier.maxWidth(760.px)
                )
                if (totalPages > 1) {
                    OrpheumPagination(page, totalPages) { newPage -> goToPage(newPage) }
                }
            }
        }
    }
}