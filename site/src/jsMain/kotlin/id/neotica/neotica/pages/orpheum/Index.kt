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
import id.neotica.neotica.components.others.NeoText
import id.neotica.neotica.domain.dummy.OrpheumTrackList
import id.neotica.neotica.utils.Constants
import kotlinx.browser.window
import kotlinx.coroutines.await
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.jetbrains.compose.web.css.FlexWrap
import org.jetbrains.compose.web.css.LineStyle
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Div
import org.w3c.dom.HTMLAudioElement
import org.w3c.dom.events.Event

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
    @SerialName("track_number") val trackNumber: Int,
    @SerialName("artist_name") val artistName: String
)

private const val USE_DUMMY_DATA = false

private fun formatDuration(totalSeconds: Int): String {
    val min = totalSeconds / 60
    val sec = totalSeconds % 60
    return "${min}:${sec.toString().padStart(2, '0')}"
}

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

    val audioPlayer = remember { window.document.createElement("audio") as HTMLAudioElement }
    var currentlyPlayingId by remember { mutableStateOf<String?>(null) }
    var isPlaying by remember { mutableStateOf(false) }
    var currentTime by remember { mutableStateOf(0.0) }
    var trackDuration by remember { mutableStateOf(0.0) }

    val jsonParser = remember { Json { ignoreUnknownKeys = true } }

    DisposableEffect(Unit) {
        val onEnded = { _: Event -> isPlaying = false }
        val onTimeUpdate = { _: Event ->
            currentTime = audioPlayer.currentTime
            trackDuration = audioPlayer.duration
        }
        audioPlayer.addEventListener("ended", onEnded)
        audioPlayer.addEventListener("timeupdate", onTimeUpdate)

        onDispose {
            audioPlayer.removeEventListener("ended", onEnded)
            audioPlayer.removeEventListener("timeupdate", onTimeUpdate)
            audioPlayer.pause()
            audioPlayer.src = ""
        }
    }

    LaunchedEffect(currentPage) {
        isLoading = true
        errorMessage = null

        val newPath = "/orpheum?page=$currentPage"
        if (window.location.search != "?page=$currentPage") {
            window.history.pushState(null, "", newPath)
        }

        if (USE_DUMMY_DATA) {
            val feedResponse = OrpheumTrackList.getPage(currentPage)
            trackList = feedResponse.data
            totalPages = feedResponse.totalPages
        } else {
            try {
                val url = "${Constants.ORPHEUM_DEV_URL}/orpheum/catalog/tracks/new?page=$currentPage&limit=10"
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
            }
        }

        isLoading = false
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
        )

        if (isLoading) {
            NeoText(
                text = "Loading audio tracks...",
                modifier = Modifier.fontSize(1.5.cssRem)
            )
        } else if (errorMessage != null) {
            NeoText(
                text = "Failed to load: $errorMessage",
                modifier = Modifier.color(NeoColor.negativePrimary).fontSize(1.5.cssRem)
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
                Column(modifier = Modifier.fillMaxWidth(60.percent)) {
                    NeoText(
                        modifier = Modifier.fontSize(1.5.cssRem),
                        text = track.title
                    )
                    NeoText(
                        modifier = Modifier.fontSize(0.9.cssRem),
                        text = "${track.artistName}  \u00B7  ${formatDuration(track.durationSeconds)}"
                    )
                }

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
                                audioPlayer.pause()
                                isPlaying = false
                            } else if (isActive && !isPlaying) {
                                audioPlayer.play()
                                isPlaying = true
                            } else {
                                audioPlayer.src = "${Constants.ORPHEUM_DEV_URL}/orpheum/stream/${track.id}"
                                audioPlayer.play()
                                currentlyPlayingId = track.id
                                isPlaying = true
                            }
                        }
                        .toAttrs()
                ) {
                    SpanText(if (isActive && isPlaying) "\u23F8 Pause" else "\u25B6 Play")
                }
            }

            if (isActive) {
                val progress = if (trackDuration > 0) (currentTime / trackDuration).coerceIn(0.0, 1.0) else 0.0
                val currentLabel = formatDuration(currentTime.toInt())
                val totalLabel = formatDuration(trackDuration.toInt())

                Row(
                    modifier = Modifier.fillMaxWidth().gap(0.5.cssRem).padding(topBottom = 0.3.cssRem),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    NeoText(text = currentLabel, modifier = Modifier.fontSize(0.8.cssRem))

                    Div(
                        attrs = Modifier
                            .fillMaxWidth()
                            .height(6.px)
                            .backgroundColor(NeoColor.backgroundPrimaryTransparent)
                            .borderRadius(3.px)
                            .cursor(Cursor.Pointer)
                            .onClick { e ->
                                val rect = (e.currentTarget.asDynamic() as org.w3c.dom.Element)
                                val width = rect.getBoundingClientRect().width
                                if (width > 0) {
                                    val offsetX = (e.asDynamic().offsetX as Double).coerceIn(0.0, width)
                                    audioPlayer.currentTime = (offsetX / width) * trackDuration
                                }
                            }
                            .toAttrs()
                    ) {
                        Div(
                            attrs = Modifier
                                .height(6.px)
                                .backgroundColor(NeoColor.colorPrimary)
                                .borderRadius(3.px)
                                .toAttrs { style { property("width", "${(progress * 100).toInt()}%") } }
                        )
                    }

                    NeoText(text = totalLabel, modifier = Modifier.fontSize(0.8.cssRem))
                }
            }
        }

        if (!isLoading && trackList.isNotEmpty()) {
            PaginationFooter(currentPage, totalPages) { newPage ->
                audioPlayer.pause()
                isPlaying = false
                currentlyPlayingId = null
                currentPage = newPage
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
    Row(
        modifier = Modifier.fillMaxWidth().padding(topBottom = 2.cssRem),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
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
            SpanText("\u00AB Prev")
        }

        val visiblePages = buildList {
            val rangeStart = (currentPage - 2).coerceAtLeast(1)
            val rangeEnd = (currentPage + 2).coerceAtMost(totalPages)
            if (rangeStart > 1) {
                add(1)
                if (rangeStart > 2) add(-1)
            }
            for (i in rangeStart..rangeEnd) add(i)
            if (rangeEnd < totalPages) {
                if (rangeEnd < totalPages - 1) add(-1)
                add(totalPages)
            }
        }

        visiblePages.forEach { page ->
            if (page == -1) {
                SpanText(
                    text = "\u2026",
                    modifier = Modifier
                        .color(NeoColor.white)
                        .margin(leftRight = 0.5.cssRem)
                        .fontSize(1.2.cssRem)
                )
            } else {
                Button(
                    attrs = Modifier
                        .backgroundColor(if (page == currentPage) NeoColor.colorPrimary else NeoColor.backgroundPrimaryTransparent)
                        .color(NeoColor.white)
                        .padding(10.px)
                        .borderRadius(5.px)
                        .border(1.px, LineStyle.Solid, NeoColor.colorPrimary)
                        .cursor(Cursor.Pointer)
                        .onClick { onPageChange(page) }
                        .toAttrs { if (page == currentPage) attr("disabled", "true") }
                ) {
                    SpanText(page.toString())
                }
            }
        }

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
            SpanText("Next \u00BB")
        }
    }
}
