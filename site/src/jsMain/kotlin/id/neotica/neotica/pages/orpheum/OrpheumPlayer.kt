package id.neotica.neotica.pages.orpheum

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.AnimationIterationCount
import com.varabyte.kobweb.compose.css.Cursor
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.css.ObjectFit
import com.varabyte.kobweb.compose.css.Overflow
import com.varabyte.kobweb.compose.css.TextAlign
import com.varabyte.kobweb.compose.foundation.layout.Arrangement
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Color
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.silk.components.graphics.Image
import com.varabyte.kobweb.silk.components.overlay.Overlay
import com.varabyte.kobweb.silk.components.overlay.OverlayVars
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.style.animation.Keyframes
import com.varabyte.kobweb.silk.style.animation.toAnimation
import id.neotica.neotica.components.NeoColor
import id.neotica.neotica.components.retro.RetroColor
import id.neotica.neotica.components.retro.retroBevel
import id.neotica.neotica.domain.model.orpheum.TrackRemoteModel
import id.neotica.neotica.utils.Constants
import kotlinx.browser.window
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div
import org.w3c.dom.HTMLAudioElement
import org.w3c.dom.events.Event

internal fun formatDuration(totalSeconds: Int): String {
    val min = totalSeconds / 60
    val sec = totalSeconds % 60
    return "${min}:${sec.toString().padStart(2, '0')}"
}

/** Resolve a relative Orpheum media path (`/media/<token>`) against the service mount. */
internal fun resolveOrpheumMedia(path: String?): String? {
    if (path.isNullOrBlank()) return null
    return when {
        path.startsWith("http://") || path.startsWith("https://") -> path
        path.startsWith("/") -> "${Constants.ORPHEUM_DEV_URL}/orpheum$path"
        else -> "${Constants.ORPHEUM_DEV_URL}/orpheum/$path"
    }
}

val CdSpinKeyframes = Keyframes {
    from { Modifier.rotate(0.deg) }
    to { Modifier.rotate(360.deg) }
}

/**
 * A retro track list with built-in playback: cards + seek bar, a sticky mini-CD, and a
 * click-to-fullscreen spinning disc with Play/Pause/Stop.
 */
@Composable
fun OrpheumTrackListPlayer(tracks: List<TrackRemoteModel>, modifier: Modifier = Modifier) {
    val audioPlayer = remember { window.document.createElement("audio") as HTMLAudioElement }
    var nowPlaying by remember { mutableStateOf<TrackRemoteModel?>(null) }
    var isPlaying by remember { mutableStateOf(false) }
    var currentTime by remember { mutableStateOf(0.0) }
    var trackDuration by remember { mutableStateOf(0.0) }
    var cdFullscreen by remember { mutableStateOf(false) }

    // The "ended" listener is registered once, so it needs fresh references to auto-advance.
    val latestTracks by rememberUpdatedState(tracks)
    val latestNowPlaying by rememberUpdatedState(nowPlaying)

    fun play(track: TrackRemoteModel) {
        audioPlayer.src = "${Constants.ORPHEUM_DEV_URL}/orpheum/stream/${track.id}"
        audioPlayer.play().catch { e -> console.warn("Orpheum playback failed: ${e.message}") }
        nowPlaying = track
        isPlaying = true
        currentTime = 0.0
        trackDuration = 0.0
    }

    DisposableEffect(Unit) {
        audioPlayer.setAttribute("style", "display: none")
        window.document.body?.appendChild(audioPlayer)

        val onEnded = { _: Event ->
            val list = latestTracks
            val index = list.indexOfFirst { it.id == latestNowPlaying?.id }
            val next = if (index >= 0) list.getOrNull(index + 1) else null
            if (next != null) play(next) else isPlaying = false
        }
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
            audioPlayer.parentNode?.removeChild(audioPlayer)
        }
    }

    fun toggleTrack(track: TrackRemoteModel) {
        val current = nowPlaying
        when {
            current?.id == track.id && isPlaying -> {
                audioPlayer.pause()
                isPlaying = false
            }
            current?.id == track.id && !isPlaying -> {
                audioPlayer.play().catch { e -> console.warn("Orpheum playback failed: ${e.message}") }
                isPlaying = true
            }
            else -> {
                play(track)
                cdFullscreen = false
            }
        }
    }

    Column(
        modifier = modifier.fillMaxWidth().gap(0.8.cssRem)
    ) {
        tracks.forEach { track ->
            RetroTrackCard(
                track = track,
                isActive = track.id == nowPlaying?.id,
                isPlaying = isPlaying,
                currentTime = currentTime,
                trackDuration = trackDuration,
                onTogglePlay = { toggleTrack(track) },
                onSeek = { time -> audioPlayer.currentTime = time }
            )
        }
    }

    // Sticky "now playing" CD — click to enlarge to fullscreen
    val playingTrack = nowPlaying
    if (playingTrack != null) {
        Box(
            modifier = Modifier
                .position(Position.Fixed)
                .right(1.5.cssRem)
                .bottom(1.5.cssRem)
                .cursor(Cursor.Pointer)
                .onClick { cdFullscreen = true }
        ) {
            SpinningDisc(
                coverUrl = resolveOrpheumMedia(playingTrack.albumCover),
                size = 64,
                playing = isPlaying
            )
        }
    }

    val fullScreenTrack = if (cdFullscreen) nowPlaying else null
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
                    SpinningDisc(
                        coverUrl = resolveOrpheumMedia(fullScreenTrack.albumCover),
                        size = 260,
                        playing = isPlaying
                    )

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
                        BevelButton(if (isPlaying) "\u23F8  Pause" else "\u25B6  Play", RetroColor.babyBlue) {
                            if (isPlaying) {
                                audioPlayer.pause()
                                isPlaying = false
                            } else {
                                audioPlayer.play().catch { e -> console.warn("Orpheum playback failed: ${e.message}") }
                                isPlaying = true
                            }
                        }
                        BevelButton("\u2715  Close", RetroColor.steelBlue) { cdFullscreen = false }
                        BevelButton("\u25A0  Stop", Color.rgb(0xB0, 0x3A, 0x3A)) {
                            audioPlayer.pause()
                            audioPlayer.currentTime = 0.0
                            audioPlayer.src = ""
                            isPlaying = false
                            nowPlaying = null
                            currentTime = 0.0
                            trackDuration = 0.0
                            cdFullscreen = false
                        }
                    }
                }
            }
        }
    }
}

@Composable
internal fun BevelButton(label: String, background: Color, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .retroBevel(background)
            .padding(leftRight = 1.1.cssRem, topBottom = 0.45.cssRem)
            .cursor(Cursor.Pointer)
            .onClick { onClick() }
    ) {
        SpanText(
            text = label,
            modifier = Modifier.fontSize(1.1.cssRem).fontWeight(FontWeight.Bold).color(NeoColor.backgroundPrimary)
        )
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
            TrackCover(coverUrl = resolveOrpheumMedia(track.albumCover), size = 58)

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
internal fun TrackCover(coverUrl: String?, size: Int) {
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