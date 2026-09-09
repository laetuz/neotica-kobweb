package id.neotica.neotica.pages.orpheum

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.foundation.layout.Arrangement
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Color
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.data.add
import com.varabyte.kobweb.core.init.InitRoute
import com.varabyte.kobweb.core.init.InitRouteContext
import com.varabyte.kobweb.core.layout.Layout
import com.varabyte.kobweb.core.rememberPageContext
import com.varabyte.kobweb.silk.components.graphics.Image
import com.varabyte.kobweb.silk.components.overlay.Overlay
import com.varabyte.kobweb.silk.components.overlay.OverlayVars
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.style.animation.Keyframes
import com.varabyte.kobweb.silk.style.animation.toAnimation
import id.neotica.neotica.components.NeoColor
import id.neotica.neotica.components.layouts.NeoLayoutData
import id.neotica.neotica.components.retro.RainbowDivider
import id.neotica.neotica.components.retro.RetroColor
import id.neotica.neotica.components.retro.StarfieldStars
import id.neotica.neotica.domain.dummy.OrpheumTrackList
import id.neotica.neotica.domain.model.orpheum.TrackFeedResponse
import id.neotica.neotica.domain.model.orpheum.TrackRemoteModel
import id.neotica.neotica.utils.Constants
import kotlinx.browser.window
import kotlinx.coroutines.await
import kotlinx.serialization.json.Json
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Div
import org.w3c.dom.HTMLAudioElement
import org.w3c.dom.events.Event


private const val USE_DUMMY_DATA = false

private fun formatDuration(totalSeconds: Int): String {
    val min = totalSeconds / 60
    val sec = totalSeconds % 60
    return "${min}:${sec.toString().padStart(2, '0')}"
}

val CdSpinKeyframes = Keyframes {
    from { Modifier.rotate(0.deg) }
    to { Modifier.rotate(360.deg) }
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
    var cdFullscreen by remember { mutableStateOf(false) }

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

    val activeTrack = trackList.firstOrNull { it.id == currentlyPlayingId }

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
                text = "Fresh tracks, served with a side of nostalgia.",
                modifier = Modifier
                    .fontSize(1.05.cssRem)
                    .color(RetroColor.cream)
                    .textAlign(TextAlign.Center)
            )
        }

        RainbowDivider()

        if (isLoading) {
            SpanText(
                text = "Loading audio tracks...",
                modifier = Modifier.fontSize(1.4.cssRem).color(RetroColor.cyan)
            )
        } else if (errorMessage != null) {
            SpanText(
                text = "Failed to load: $errorMessage",
                modifier = Modifier.fontSize(1.4.cssRem).color(NeoColor.negativePrimary)
            )
        }

        Column(
            modifier = Modifier.fillMaxWidth().maxWidth(760.px).gap(0.8.cssRem)
        ) {
            trackList.forEach { track ->
                RetroTrackCard(
                    track = track,
                    isActive = track.id == currentlyPlayingId,
                    isPlaying = isPlaying,
                    currentTime = currentTime,
                    trackDuration = trackDuration,
                    onTogglePlay = {
                        if (track.id == currentlyPlayingId && isPlaying) {
                            audioPlayer.pause()
                            isPlaying = false
                        } else if (track.id == currentlyPlayingId && !isPlaying) {
                            audioPlayer.play()
                            isPlaying = true
                        } else {
                            audioPlayer.src = "${Constants.ORPHEUM_DEV_URL}/orpheum/stream/${track.id}"
                            audioPlayer.play()
                            currentlyPlayingId = track.id
                            isPlaying = true
                            cdFullscreen = false
                        }
                    },
                    onSeek = { time -> audioPlayer.currentTime = time }
                )
            }
        }

        if (!isLoading && trackList.isNotEmpty()) {
            PaginationFooter(currentPage, totalPages) { newPage ->
                audioPlayer.pause()
                isPlaying = false
                currentlyPlayingId = null
                cdFullscreen = false
                currentPage = newPage
            }
        }
    }

    // Sticky "now playing" CD — click to enlarge to fullscreen
    val playingTrack = activeTrack
    if (playingTrack != null) {
        Box(
            modifier = Modifier
                .position(Position.Fixed)
                .right(1.5.cssRem)
                .bottom(1.5.cssRem)
                .cursor(Cursor.Pointer)
                .onClick { cdFullscreen = true }
        ) {
            SpinningDisc(coverUrl = "${Constants.ORPHEUM_DEV_URL}/orpheum/${playingTrack.coverUrl}", size = 64, playing = isPlaying)
        }
    }

    val fullScreenTrack = if (cdFullscreen) activeTrack else null
    if (fullScreenTrack != null) {
        Overlay(
            Modifier
                .setVariable(OverlayVars.BackgroundColor, NeoColor.backgroundPrimary.copy(alpha = 250))
                .onClick { cdFullscreen = false }
        ) {
            Box(
                modifier = Modifier.fillMaxSize().onClick { it.stopPropagation() },
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.gap(1.cssRem)
                ) {
                    SpinningDisc(coverUrl = "${Constants.ORPHEUM_DEV_URL}/orpheum/${fullScreenTrack.coverUrl}", size = 260, playing = isPlaying)

                    RetroProgressBar(
                        currentTime = currentTime,
                        trackDuration = trackDuration,
                        onSeek = { time -> audioPlayer.currentTime = time },
                        modifier = Modifier.maxWidth(360.px)
                    )

                    SpanText(
                        text = fullScreenTrack.title,
                        modifier = Modifier
                            .fontSize(1.8.cssRem)
                            .fontWeight(FontWeight.Bold)
                            .color(RetroColor.cream)
                            .textAlign(TextAlign.Center)
                    )
                    SpanText(
                        text = fullScreenTrack.artistName,
                        modifier = Modifier.fontSize(1.1.cssRem).color(RetroColor.cyan).textAlign(TextAlign.Center)
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(1.cssRem),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .retroBevel(RetroColor.babyBlue)
                                .padding(leftRight = 1.2.cssRem, topBottom = 0.5.cssRem)
                                .cursor(Cursor.Pointer)
                                .onClick {
                                    if (isPlaying) {
                                        audioPlayer.pause()
                                        isPlaying = false
                                    } else {
                                        audioPlayer.play()
                                        isPlaying = true
                                    }
                                }
                        ) {
                            SpanText(
                                text = if (isPlaying) "\u23F8  Pause" else "\u25B6  Play",
                                modifier = Modifier.fontSize(1.2.cssRem).fontWeight(FontWeight.Bold).color(NeoColor.backgroundPrimary)
                            )
                        }
                        Box(
                            modifier = Modifier
                                .retroBevel(RetroColor.steelBlue)
                                .padding(leftRight = 1.2.cssRem, topBottom = 0.5.cssRem)
                                .cursor(Cursor.Pointer)
                                .onClick { cdFullscreen = false }
                        ) {
                            SpanText(
                                text = "\u2715  Close",
                                modifier = Modifier.fontSize(1.2.cssRem).fontWeight(FontWeight.Bold).color(NeoColor.backgroundPrimary)
                            )
                        }
                        Box(
                            modifier = Modifier
                                .retroBevel(Color.rgb(0xB0, 0x3A, 0x3A))
                                .padding(leftRight = 1.2.cssRem, topBottom = 0.5.cssRem)
                                .cursor(Cursor.Pointer)
                                .onClick {
                                    audioPlayer.pause()
                                    audioPlayer.currentTime = 0.0
                                    audioPlayer.src = ""
                                    isPlaying = false
                                    currentlyPlayingId = null
                                    currentTime = 0.0
                                    trackDuration = 0.0
                                    cdFullscreen = false
                                }
                        ) {
                            SpanText(
                                text = "\u25A0  Stop",
                                modifier = Modifier.fontSize(1.2.cssRem).fontWeight(FontWeight.Bold).color(NeoColor.backgroundPrimary)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RetroTrackCard(
    track: TrackRemoteModel,
    isActive: Boolean,
    isPlaying: Boolean,
    currentTime: Double,
    trackDuration: Double,
    onTogglePlay: () -> Unit,
    onSeek: (Double) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                2.px,
                LineStyle.Solid,
                if (isActive) RetroColor.skyBlue.copy(alpha = 170) else RetroColor.steelBlue.copy(alpha = 90)
            )
            .backgroundColor(if (isActive) RetroColor.steelBlue.copy(alpha = 30) else NeoColor.backgroundPrimaryTransparent)
            .borderRadius(4.px)
            .padding(1.cssRem)
            .gap(0.6.cssRem)
            .boxShadow(2.px, 2.px, color = Color.rgb(0, 0, 0).copy(alpha = 115))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().gap(1.cssRem),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TrackCover(coverUrl = "${Constants.ORPHEUM_DEV_URL}/orpheum/${track.coverUrl}", size = 58)

            Column(modifier = Modifier.fillMaxWidth().gap(0.1.cssRem)) {
                SpanText(
                    text = track.title,
                    modifier = Modifier
                        .fontSize(1.2.cssRem)
                        .fontWeight(FontWeight.Bold)
                        .color(RetroColor.cream)
                )
                SpanText(
                    text = track.artistName,
                    modifier = Modifier.fontSize(0.9.cssRem).color(RetroColor.cyan)
                )
            }

            Box(
                modifier = Modifier
                    .retroBevel(RetroColor.babyBlue)
                    .padding(leftRight = 0.9.cssRem, topBottom = 0.4.cssRem)
                    .cursor(Cursor.Pointer)
                    .onClick { onTogglePlay() }
            ) {
                SpanText(
                    text = if (isActive && isPlaying) "\u23F8  Pause" else "\u25B6  Play",
                    modifier = Modifier.fontSize(1.05.cssRem).fontWeight(FontWeight.Bold).color(NeoColor.backgroundPrimary)
                )
            }
        }

        if (isActive) {
            RetroProgressBar(currentTime = currentTime, trackDuration = trackDuration, onSeek = onSeek)
        }
    }
}

@Composable
private fun RetroProgressBar(
    currentTime: Double,
    trackDuration: Double,
    onSeek: (Double) -> Unit,
    modifier: Modifier = Modifier
) {
    val progress = if (trackDuration > 0) (currentTime / trackDuration).coerceIn(0.0, 1.0) else 0.0
    val currentLabel = formatDuration(currentTime.toInt())
    val totalLabel = formatDuration(trackDuration.toInt())

    Row(
        modifier = modifier.fillMaxWidth().gap(0.5.cssRem),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SpanText(
            text = currentLabel,
            modifier = Modifier.fontSize(0.8.cssRem).color(RetroColor.cyan)
        )

        Div(
            attrs = Modifier
                .fillMaxWidth()
                .height(8.px)
                .border(1.px, LineStyle.Solid, RetroColor.navy)
                .backgroundColor(NeoColor.backgroundPrimary)
                .borderRadius(4.px)
                .cursor(Cursor.Pointer)
                .onClick { e ->
                    val rect = (e.currentTarget.asDynamic() as org.w3c.dom.Element)
                    val width = rect.getBoundingClientRect().width
                    if (width > 0) {
                        val offsetX = (e.asDynamic().offsetX as Double).coerceIn(0.0, width)
                        onSeek((offsetX / width) * trackDuration)
                    }
                }
                .toAttrs()
        ) {
            Div(
                attrs = Modifier
                    .height(8.px)
                    .backgroundColor(RetroColor.babyBlue)
                    .borderRadius(4.px)
                    .toAttrs { style { property("width", "${(progress * 100).toInt()}%") } }
            )
        }

        SpanText(
            text = totalLabel,
            modifier = Modifier.fontSize(0.8.cssRem).color(RetroColor.cyan)
        )
    }
}

@Composable
private fun TrackCover(coverUrl: String?, size: Int) {
    Box(
        modifier = Modifier
            .size(size.px)
            .border(2.px, LineStyle.Solid, RetroColor.navy)
            .backgroundColor(RetroColor.navy.copy(alpha = 200))
            .borderRadius(6.px)
            .overflow(Overflow.Hidden),
        contentAlignment = Alignment.Center
    ) {
        if (!coverUrl.isNullOrBlank()) {
            Image(
                src = coverUrl,
                modifier = Modifier.fillMaxSize().objectFit(ObjectFit.Cover)
            )
        } else {
            SpanText(
                text = "\u266A",
                modifier = Modifier
                    .fontSize(1.6.cssRem)
                    .color(RetroColor.babyBlue)
            )
        }
    }
}

@Composable
private fun SpinningDisc(coverUrl: String?, size: Int, playing: Boolean, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(size.px)
            .border(3.px, LineStyle.Solid, RetroColor.steelBlue)
            .borderRadius(50.percent)
            .padding(8.px)
            .backgroundColor(RetroColor.navy)
            .boxShadow(3.px, 3.px, color = Color.rgb(0, 0, 0).copy(alpha = 127))
            .animation(
                CdSpinKeyframes.toAnimation(
                    duration = 8.s,
                    timingFunction = AnimationTimingFunction.Linear,
                    iterationCount = AnimationIterationCount.Infinite,
                    playState = if (playing) AnimationPlayState.Running else AnimationPlayState.Paused
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .border(1.px, LineStyle.Solid, RetroColor.navy.copy(alpha = 180))
                .borderRadius(50.percent)
                .overflow(Overflow.Hidden),
            contentAlignment = Alignment.Center
        ) {
            if (!coverUrl.isNullOrBlank()) {
                Image(
                    src = coverUrl,
                    modifier = Modifier.fillMaxSize().objectFit(ObjectFit.Cover)
                )
            } else {
                SpanText(
                    text = "\u266A",
                    modifier = Modifier
                        .fontSize((size / 4).px)
                        .color(RetroColor.skyBlue)
                )
            }
        }

        Box(
            modifier = Modifier
                .size((size / 8).px)
                .border(1.px, LineStyle.Solid, RetroColor.navy)
                .borderRadius(50.percent)
                .backgroundColor(RetroColor.cream)
        )
    }
}

private fun Modifier.retroBevel(background: Color = RetroColor.babyBlue): Modifier = this
    .backgroundColor(background)
    .border(2.px, LineStyle.Solid, RetroColor.navy)
    .borderRadius(4.px)
    .boxShadow(
        BoxShadow.of(0.px, 1.px, 0.px, 1.px, NeoColor.white.copy(alpha = 200), inset = true),
        BoxShadow.of(0.px, (-1).px, 0.px, 1.px, Color.rgb(0, 0, 0).copy(alpha = 120), inset = true),
        BoxShadow.of(3.px, 3.px, 0.px, 0.px, Color.rgb(0, 0, 0).copy(alpha = 127)),
    )

@Composable
private fun PaginationFooter(
    currentPage: Int,
    totalPages: Int,
    onPageChange: (Int) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(topBottom = 2.cssRem).gap(0.5.cssRem),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        RetroPageButton(
            text = "\u00AB Prev",
            enabled = currentPage > 1,
            active = false,
            onClick = { if (currentPage > 1) onPageChange(currentPage - 1) }
        )

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
                        .color(RetroColor.cream)
                        .margin(leftRight = 0.4.cssRem)
                        .fontSize(1.2.cssRem)
                )
            } else {
                RetroPageButton(
                    text = page.toString(),
                    enabled = true,
                    active = page == currentPage,
                    onClick = { onPageChange(page) }
                )
            }
        }

        RetroPageButton(
            text = "Next \u00BB",
            enabled = currentPage < totalPages,
            active = false,
            onClick = { if (currentPage < totalPages) onPageChange(currentPage + 1) }
        )
    }
}

@Composable
private fun RetroPageButton(text: String, enabled: Boolean, active: Boolean, onClick: () -> Unit) {
    Button(
        attrs = Modifier
            .backgroundColor(
                when {
                    active -> RetroColor.babyBlue
                    enabled -> NeoColor.backgroundPrimaryTransparent
                    else -> NeoColor.backgroundPrimaryTransparent.copy(alpha = 60)
                }
            )
            .border(2.px, LineStyle.Solid, if (active) RetroColor.navy else RetroColor.steelBlue.copy(alpha = 90))
            .borderRadius(4.px)
            .padding(10.px)
            .cursor(if (enabled) Cursor.Pointer else Cursor.NotAllowed)
            .onClick { if (enabled) onClick() }
            .toAttrs { if (!enabled) attr("disabled", "true") }
    ) {
        SpanText(
            text = text,
            modifier = Modifier
                .fontSize(1.1.cssRem)
                .fontWeight(FontWeight.Bold)
                .color(
                    when {
                        active -> NeoColor.backgroundPrimary
                        enabled -> RetroColor.cream
                        else -> NeoColor.white.copy(alpha = 120)
                    }
                )
        )
    }
}