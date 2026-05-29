package id.neotica.neotica.pages.orpheum

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.Cursor
import com.varabyte.kobweb.compose.css.Overflow
import com.varabyte.kobweb.compose.foundation.layout.Arrangement
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.data.add
import com.varabyte.kobweb.core.init.InitRoute
import com.varabyte.kobweb.core.init.InitRouteContext
import com.varabyte.kobweb.core.layout.Layout
import com.varabyte.kobweb.core.rememberPageContext
import com.varabyte.kobweb.silk.components.text.SpanText
import id.neotica.neotica.components.NeoColor
import id.neotica.neotica.components.layouts.NeoLayoutData
import kotlinx.browser.window
import kotlinx.coroutines.await
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.jetbrains.compose.web.css.FlexWrap
import org.jetbrains.compose.web.css.LineStyle
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.Button
import org.w3c.dom.HTMLAudioElement
import org.w3c.dom.events.Event

// --- Data Models ---
@Serializable
data class TrackFeedResponse(
    val data: List<TrackRemoteModel>,
    val page: Int,
    val limit: Int,
    @SerialName("total_items") val totalItems: Int,
    @SerialName("total_pages") val totalPages: Int
)

@Serializable
data class TrackRemoteModel(
    val id: String,
    @SerialName("album_id") val albumId: String,
    val title: String,
    @SerialName("duration_seconds") val durationSeconds: Int,
    @SerialName("file_url") val fileUrl: String,
    @SerialName("track_number") val trackNumber: Int
)

@InitRoute()
fun initOrpheumPage(ctx: InitRouteContext) {
    ctx.data.add(NeoLayoutData("Orpheum - Neotica.id", "/orpheum"))
}

@Page
@Composable
@Layout(".components.layouts.NeoPageLayout")
fun OrpheumPage() {
    val ctx = rememberPageContext()

    var errorMessage by remember { mutableStateOf<String?>(null) }
    var trackList by remember { mutableStateOf<List<TrackRemoteModel>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    val currentPageParam = ctx.route.queryParams["page"]?.toIntOrNull() ?: 1
    var currentPage by remember { mutableStateOf(currentPageParam) }
    var totalPages by remember { mutableStateOf(1) }

    // --- Audio Player State ---
    // Instantiate a single native HTML5 audio element for the lifecycle of this page
    val audioPlayer = remember { window.document.createElement("audio") as HTMLAudioElement }
    var currentlyPlayingId by remember { mutableStateOf<String?>(null) }
    var isPlaying by remember { mutableStateOf(false) }

    val jsonParser = remember { Json { ignoreUnknownKeys = true } }

    // Clean up audio bindings when the user navigates away
    DisposableEffect(Unit) {
        val onEnded = { _: Event -> isPlaying = false }
        audioPlayer.addEventListener("ended", onEnded)

        onDispose {
            audioPlayer.removeEventListener("ended", onEnded)
            audioPlayer.pause()
            audioPlayer.src = ""
        }
    }

    LaunchedEffect(currentPage) {
        isLoading = true
        errorMessage = null

        try {
            val newPath = "/orpheum?page=$currentPage"
            if (window.location.search != "?page=$currentPage") {
                window.history.pushState(null, "", newPath)
            }

            // Server-side pagination via Ktor
            val url = "https://dev.neotica.id/orpheum/catalog/tracks/new?page=$currentPage&limit=10"
            val response = window.fetch(url).await()

            if (!response.ok) {
                throw Exception("HTTP Error: ${response.status} ${response.statusText}")
            }

            val text = response.text().await()
            val feedResponse = jsonParser.decodeFromString(TrackFeedResponse.serializer(), text)

            trackList = feedResponse.data
            totalPages = feedResponse.totalPages

        } catch (e: Throwable) {
            console.error("Failed to load tracks: ", e)
            errorMessage = e.message ?: "An unknown network error occurred (Likely CORS)."
        } finally {
            isLoading = false
        }
    }

    Column(
        modifier = Modifier
            .backgroundColor(NeoColor.backgroundPrimary)
            .fillMaxSize()
            .padding(leftRight = 2.cssRem, topBottom = 2.cssRem)
            .overflow(Overflow.Auto)
            .gap(1.5.cssRem)
    ) {
        SpanText(
            text = "Orpheum Stream",
            modifier = Modifier
                .color(NeoColor.colorPrimary)
                .fontSize(2.cssRem)
//                .fontWeight("bold")
        )

        if (isLoading) {
            SpanText(
                text = "Loading audio tracks...",
                modifier = Modifier
                    .color(NeoColor.white)
                    .fontSize(1.5.cssRem)
            )
        } else if (errorMessage != null) {
            SpanText(
                text = "Failed to load: $errorMessage",
                modifier = Modifier.color(NeoColor.white).fontSize(1.5.cssRem) // Assuming you don't have a red NeoColor yet
            )
        }

        trackList.forEach { track ->
            val isActive = track.id == currentlyPlayingId

            Row(
                modifier = Modifier
                    .border(1.px, LineStyle.Solid, if (isActive) NeoColor.colorPrimary else NeoColor.backgroundPrimaryTransparent)
                    .backgroundColor(if (isActive) NeoColor.colorPrimary.copy(alpha = 30) else NeoColor.backgroundPrimary)
                    .borderRadius(8.px)
                    .padding(1.cssRem)
                    .fillMaxWidth()
                    .flexWrap(FlexWrap.Wrap)
                    .gap(1.cssRem),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    SpanText(
                        modifier = Modifier.fontSize(1.5.cssRem).color(NeoColor.white),
                        text = track.title
                    )
                    SpanText(
                        modifier = Modifier.fontSize(0.9.cssRem).color(NeoColor.white),
                        text = "Track ${track.trackNumber}"
                    )
                }

                // Playback Control Button
                Button(
                    attrs = Modifier
                        .backgroundColor(NeoColor.colorPrimary)
                        .color(NeoColor.white)
                        .padding(leftRight = 1.cssRem, topBottom = 0.5.cssRem)
                        .borderRadius(5.px)
                        .border(1.px, LineStyle.Solid, NeoColor.colorPrimary)
                        .cursor(Cursor.Pointer)
                        .onClick {
                            if (isActive && isPlaying) {
                                // Pause active track
                                audioPlayer.pause()
                                isPlaying = false
                            } else if (isActive && !isPlaying) {
                                // Resume active track
                                audioPlayer.play()
                                isPlaying = true
                            } else {
                                // Stream brand new track through the Ktor transparent pipe
                                audioPlayer.src = "https://dev.neotica.id/orpheum/stream/${track.id}"
                                audioPlayer.play()
                                currentlyPlayingId = track.id
                                isPlaying = true
                            }
                        }
                        .toAttrs()
                ) {
                    val icon = if (isActive && isPlaying) "⏸ Pause" else "▶️ Play"
                    SpanText(icon)
                }
            }
        }

        if (!isLoading && trackList.isNotEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(topBottom = 2.cssRem),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                PaginationFooter(currentPage, totalPages) { newPage ->
                    currentPage = newPage
                }
            }
        }
    }
}

@Composable
private fun PaginationFooter(
    currentPage: Int,
    totalPages: Int,
    onPageChange: (Int) -> Unit
) {
    Button(
        attrs = Modifier
            .backgroundColor(if (currentPage > 1) NeoColor.colorPrimary else NeoColor.backgroundPrimary)
            .color(NeoColor.white)
            .padding(10.px)
            .borderRadius(5.px)
            .border(1.px, LineStyle.Solid, NeoColor.colorPrimary)
            .cursor(if (currentPage > 1) Cursor.Pointer else Cursor.NotAllowed)
            .onClick { if (currentPage > 1) onPageChange(currentPage - 1) }
            .toAttrs { if (currentPage <= 1) attr("disabled", "true") }
    ) {
        SpanText("Previous")
    }

    SpanText(
        text = "Page $currentPage of ${if (totalPages == 0) 1 else totalPages}",
        modifier = Modifier
            .color(NeoColor.white)
            .margin(leftRight = 1.cssRem)
            .fontSize(1.2.cssRem)
    )

    Button(
        attrs = Modifier
            .backgroundColor(if (currentPage < totalPages) NeoColor.colorPrimary else NeoColor.backgroundPrimaryTransparent)
            .color(NeoColor.white)
            .padding(10.px)
            .borderRadius(5.px)
            .border(1.px, LineStyle.Solid, NeoColor.colorPrimary)
            .cursor(if (currentPage < totalPages) Cursor.Pointer else Cursor.NotAllowed)
            .onClick { if (currentPage < totalPages) onPageChange(currentPage + 1) }
            .toAttrs { if (currentPage >= totalPages) attr("disabled", "true") }
    ) {
        SpanText("Next")
    }
}