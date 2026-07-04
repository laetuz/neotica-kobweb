package id.neotica.neotica.pages.photobooth

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.Cursor
import com.varabyte.kobweb.compose.css.ObjectFit
import com.varabyte.kobweb.compose.css.Overflow
import com.varabyte.kobweb.compose.foundation.layout.Arrangement
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.styleModifier
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.data.add
import com.varabyte.kobweb.core.init.InitRoute
import com.varabyte.kobweb.core.init.InitRouteContext
import com.varabyte.kobweb.core.layout.Layout
import com.varabyte.kobweb.silk.components.text.SpanText
import id.neotica.neotica.components.NeoColor
import id.neotica.neotica.components.layouts.NeoLayoutData
import id.neotica.neotica.pages.photobooth.res.PhotoBoothResources.BG_PINK1
import id.neotica.neotica.pages.photobooth.res.PhotoBoothResources.BG_STARS1
import id.neotica.neotica.pages.photobooth.res.PhotoBoothResources.BG_STARS2
import id.neotica.neotica.pages.photobooth.res.pbBackgroundList
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Img
import org.jetbrains.compose.web.dom.Video
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.HTMLVideoElement
import org.w3c.dom.HTMLImageElement
import org.w3c.dom.HTMLAnchorElement
import kotlin.js.json
import kotlin.math.min

@InitRoute()
fun initPhotoboothPage(ctx: InitRouteContext) {
    ctx.data.add(NeoLayoutData("NeoBooth - Neotica.id", "/photobooth"))
}

@Page
@Composable
@Layout(".components.layouts.NeoPageLayout")
fun PhotoboothPage() {
    var currentStage by remember { mutableStateOf(0) }
    var selectedFrame by remember { mutableStateOf(FrameType.STRIP_3) }
    var timerSeconds by remember { mutableStateOf(3) }

    var capturedPhotos by remember { mutableStateOf(listOf<String>()) }
    var videoElement by remember { mutableStateOf<HTMLVideoElement?>(null) }

    var selectedSticker by remember { mutableStateOf("✨") }
    var placedStickers by remember { mutableStateOf(listOf<PlacedSticker>()) }

    // Background options for the final strip
    val backgroundOptions = remember { pbBackgroundList }
    var selectedBackground by remember { mutableStateOf(backgroundOptions.first()) }

    val stickers = listOf("✨", "💖", "✌️", "🕶️", "👑", "🔥", "🌸", "⭐")

    val filterOptions = remember {
        listOf(
            FilterOption("Normal", "none"),
            FilterOption("B&W", "grayscale(100%)"),
            FilterOption("Sepia", "sepia(100%)"),
            FilterOption("Vintage", "sepia(50%) contrast(120%) saturate(80%)"),
            FilterOption("Cool", "hue-rotate(180deg) saturate(120%)")
        )
    }
    var selectedFilter by remember { mutableStateOf(filterOptions.first()) }

    DisposableEffect(currentStage) {
        onDispose {
            if (currentStage != 1) {
                val tracks = videoElement?.srcObject.asDynamic()?.getTracks()
                if (tracks != null) {
                    for (i in 0 until (tracks.length as Int)) {
                        tracks[i].stop()
                    }
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .backgroundColor(NeoColor.backgroundPrimary)
            .fillMaxSize()
            .padding(leftRight = 2.cssRem, topBottom = 2.cssRem)
            .overflow(Overflow.Auto)
            .gap(1.5.cssRem),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SpanText(
            text = "NeoBooth",
            modifier = Modifier.color(NeoColor.colorPrimary).fontSize(2.5.cssRem)
        )

        when (currentStage) {
            0 -> SetupStage(
                selectedFrame = selectedFrame,
                selectedTimer = timerSeconds,
                onSelectFrame = { selectedFrame = it },
                onSelectTimer = { timerSeconds = it },
                onStart = {
                    capturedPhotos = emptyList()
                    placedStickers = emptyList()
                    currentStage = 1
                }
            )
            1 -> CaptureStage(
                frameType = selectedFrame,
                capturedCount = capturedPhotos.size,
                timerSeconds = timerSeconds,
                onVideoReady = { videoElement = it },
                onCapture = { photoDataUrl ->
                    capturedPhotos = capturedPhotos + photoDataUrl
                    if (capturedPhotos.size >= selectedFrame.slots) {
                        currentStage = 2
                    }
                }
            )
            2 -> DecorateStage(
                frameType = selectedFrame,
                photos = capturedPhotos,
                stickers = stickers,
                selectedSticker = selectedSticker,
                placedStickers = placedStickers,
                backgroundOptions = backgroundOptions,
                selectedBackground = selectedBackground,
                filterOptions = filterOptions,         // Pass options
                selectedFilter = selectedFilter,       // Pass state
                onSelectFilter = { selectedFilter = it }, // Update state
                onSelectBackground = { selectedBackground = it },
                onSelectSticker = { selectedSticker = it },
                onPlaceSticker = { x, y ->
                    placedStickers = placedStickers + PlacedSticker(selectedSticker, x, y)
                },
                onUndoSticker = {
                    if (placedStickers.isNotEmpty()) placedStickers = placedStickers.dropLast(1)
                },
                onReset = { currentStage = 0 }
            )
        }
    }
}