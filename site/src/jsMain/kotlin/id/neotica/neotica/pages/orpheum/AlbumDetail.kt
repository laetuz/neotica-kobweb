package id.neotica.neotica.pages.orpheum

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.css.Overflow
import com.varabyte.kobweb.compose.css.TextAlign
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
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
import id.neotica.neotica.domain.model.orpheum.AlbumWithTracks
import id.neotica.neotica.utils.Constants
import id.neotica.neotica.utils.encodeQueryComponent
import kotlinx.browser.window
import kotlinx.coroutines.await
import kotlinx.serialization.json.Json
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.px

@InitRoute()
fun initOrpheumAlbumPage(ctx: InitRouteContext) {
    ctx.data.add(NeoLayoutData("Album \u2014 Orpheum | Neotica.id", "/orpheum"))
}

@Page(routeOverride = "/orpheum/album/{albumId}")
@Composable
@Layout(".components.layouts.NeoPageLayout")
fun OrpheumAlbumPage() {
    val ctx = rememberPageContext()
    val albumId = ctx.route.params["albumId"].orEmpty()
    val jsonParser = remember { Json { ignoreUnknownKeys = true } }

    var detail by remember { mutableStateOf<AlbumWithTracks?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var retryTick by remember { mutableStateOf(0) }

    LaunchedEffect(albumId, retryTick) {
        isLoading = true
        errorMessage = null
        detail = null
        if (albumId.isBlank()) {
            errorMessage = "No album specified."
            isLoading = false
            return@LaunchedEffect
        }
        try {
            val url = "${Constants.ORPHEUM_DEV_URL}/orpheum/albums/${encodeQueryComponent(albumId)}"
            val response = window.fetch(url).await()
            when {
                response.ok -> {
                    val data = jsonParser.decodeFromString<AlbumWithTracks>(response.text().await())
                    detail = data
                    injectAlbumHead(data, albumId)
                }
                response.status.toInt() == 404 -> errorMessage = "Album not found."
                response.status.toInt() == 429 -> {
                    val seconds = response.headers.get("Retry-After")?.toIntOrNull() ?: 60
                    errorMessage = "Too many requests. Please try again in $seconds seconds."
                }
                else -> errorMessage = "Failed to load album (HTTP ${response.status})."
            }
        } catch (e: Throwable) {
            console.error("Failed to load album: ${e.message}")
            errorMessage = "Network error. Please try again."
        }
        isLoading = false
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

        when {
            isLoading -> SpanText(
                text = "Loading album...",
                modifier = Modifier.fontSize(1.4.cssRem).color(RetroColor.cyan)
            )
            errorMessage != null -> Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.gap(0.6.cssRem)
            ) {
                SpanText(
                    text = errorMessage!!,
                    modifier = Modifier.fontSize(1.4.cssRem).color(NeoColor.negativePrimary).textAlign(TextAlign.Center)
                )
                BevelButton("Try again", RetroColor.babyBlue) { retryTick++ }
            }
            detail != null -> AlbumContent(detail!!)
        }
    }
}

@Composable
private fun AlbumContent(detail: AlbumWithTracks) {
    val album = detail.album
    Column(
        modifier = Modifier.fillMaxWidth().maxWidth(760.px).gap(1.2.cssRem)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().gap(1.2.cssRem),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TrackCover(coverUrl = resolveOrpheumMedia(album.coverUrl), size = 180)
            Column(modifier = Modifier.fillMaxWidth().gap(0.25.cssRem)) {
                SpanText(
                    text = album.title,
                    modifier = Modifier
                        .fontSize(2.2.cssRem)
                        .fontWeight(FontWeight.Bold)
                        .color(RetroColor.babyBlue)
                        .textShadow(2.px, 2.px, color = RetroColor.navy.copy(alpha = 200))
                )
                SpanText(
                    text = album.artistName,
                    modifier = Modifier.fontSize(1.15.cssRem).color(RetroColor.cyan)
                )
                val meta = buildString {
                    if (album.releaseYear > 0) append(album.releaseYear)
                    if (detail.tracks.isNotEmpty()) {
                        if (isNotEmpty()) append("  \u00B7  ")
                        append("${detail.tracks.size} track(s)")
                    }
                }
                if (meta.isNotBlank()) {
                    SpanText(
                        text = meta,
                        modifier = Modifier.fontSize(1.cssRem).color(RetroColor.cream)
                    )
                }
            }
        }

        RainbowDivider()

        if (detail.tracks.isEmpty()) {
            SpanText(
                text = "No tracks in this album yet.",
                modifier = Modifier.fontSize(1.3.cssRem).color(RetroColor.cream).textAlign(TextAlign.Center)
            )
        } else {
            OrpheumTrackListPlayer(tracks = detail.tracks, modifier = Modifier.fillMaxWidth())
        }
    }
}

private fun injectAlbumHead(detail: AlbumWithTracks, albumId: String) {
    val head = window.document.head ?: return
    val album = detail.album
    val canonicalUrl = "https://neotica.id/orpheum/album/$albumId"
    window.document.title = "${album.title} \u2014 Orpheum | Neotica.id"

    val existing = head.querySelectorAll("meta[property^='og:']")
    for (i in 0 until existing.length) existing.item(i)?.let { head.removeChild(it) }

    fun injectMeta(property: String, content: String) {
        head.appendChild(window.document.createElement("meta").apply {
            setAttribute("property", property)
            setAttribute("content", content)
        })
    }
    injectMeta("og:title", "${album.title} \u2014 Orpheum")
    injectMeta("og:description", "${album.title} by ${album.artistName}${if (album.releaseYear > 0) " (${album.releaseYear})" else ""} \u2014 stream on Orpheum.")
    injectMeta("og:type", "music.album")
    injectMeta("og:url", canonicalUrl)
    resolveOrpheumMedia(album.coverUrl)?.let { injectMeta("og:image", it) }

    head.querySelector("link[rel='canonical']")?.let { head.removeChild(it) }
    head.appendChild(window.document.createElement("link").apply {
        setAttribute("rel", "canonical")
        setAttribute("href", canonicalUrl)
    })
}