package id.neotica.neotica.pages.photobooth

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.Cursor
import com.varabyte.kobweb.compose.css.FontWeight
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
import com.varabyte.kobweb.silk.components.text.SpanText
import id.neotica.neotica.components.NeoColor
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
import kotlin.js.json
import kotlin.math.min

@Composable
fun SetupStage(
    selectedFrame: FrameType,
    selectedTimer: Int,
    onSelectFrame: (FrameType) -> Unit,
    onSelectTimer: (Int) -> Unit,
    onStart: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.gap(2.cssRem)
    ) {
        SpanText("Choose your frame layout:", modifier = Modifier.color(NeoColor.white).fontSize(1.2.cssRem))
        Row(horizontalArrangement = Arrangement.spacedBy(1.cssRem), modifier = Modifier.flexWrap(FlexWrap.Wrap)) {
            FrameType.entries.forEach { frame ->
                Button(
                    attrs = Modifier
                        .backgroundColor(if (selectedFrame == frame) NeoColor.colorPrimary else NeoColor.backgroundPrimaryTransparent)
                        .color(NeoColor.white)
                        .padding(1.cssRem)
                        .borderRadius(8.px)
                        .border(2.px, LineStyle.Solid, NeoColor.colorPrimary)
                        .cursor(Cursor.Pointer)
                        .onClick { onSelectFrame(frame) }
                        .toAttrs()
                ) {
                    SpanText(frame.label)
                }
            }
        }

        SpanText("Capture Timer:", modifier = Modifier.color(NeoColor.white).fontSize(1.2.cssRem))
        Row(horizontalArrangement = Arrangement.spacedBy(1.cssRem)) {
            listOf(0, 3, 5).forEach { time ->
                Button(
                    attrs = Modifier
                        .backgroundColor(if (selectedTimer == time) NeoColor.colorPrimary else NeoColor.backgroundPrimaryTransparent)
                        .color(NeoColor.white)
                        .padding(leftRight = 1.5.cssRem, topBottom = 0.8.cssRem)
                        .borderRadius(8.px)
                        .border(2.px, LineStyle.Solid, NeoColor.colorPrimary)
                        .cursor(Cursor.Pointer)
                        .onClick { onSelectTimer(time) }
                        .toAttrs()
                ) {
                    SpanText(if (time == 0) "Off" else "${time}s")
                }
            }
        }

        Button(
            attrs = Modifier
                .backgroundColor(NeoColor.colorPrimary)
                .color(NeoColor.white)
                .padding(leftRight = 2.cssRem, topBottom = 1.cssRem)
                .borderRadius(8.px)
                .styleModifier { property("border", "none") }
                .cursor(Cursor.Pointer)
                .onClick { onStart() }
                .toAttrs()
        ) {
            SpanText("Start Camera 📸", modifier = Modifier.fontSize(1.2.cssRem))
        }
    }
}

@Composable
fun CaptureStage(
    frameType: FrameType,
    capturedCount: Int,
    timerSeconds: Int,
    onVideoReady: (HTMLVideoElement) -> Unit,
    onCapture: (String) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var countdown by remember { mutableStateOf<Int?>(null) }
    var showBlink by remember { mutableStateOf(false) }

    SpanText(
        text = "Photo ${capturedCount + 1} of ${frameType.slots}",
        modifier = Modifier.color(NeoColor.white).fontSize(1.5.cssRem)
    )

    Box(
        modifier = Modifier
            .width(300.px)
            .height(400.px)
            .backgroundColor(Color.black)
            .border(4.px, LineStyle.Solid, NeoColor.colorPrimary)
            .borderRadius(8.px)
            .overflow(Overflow.Hidden)
            .position(Position.Relative),
        contentAlignment = Alignment.Center
    ) {
        Video(
            attrs = Modifier
                .fillMaxSize()
                .objectFit(ObjectFit.Cover)
                .toAttrs {
                    attr("autoplay", "true")
                    attr("playsinline", "true")
                    ref { video ->
                        onVideoReady(video)
                        window.navigator.asDynamic().mediaDevices.getUserMedia(json("video" to true))
                            .then { stream -> video.srcObject = stream }
                        onDispose { }
                    }
                }
        )

        if (countdown != null) {
            SpanText(
                text = countdown.toString(),
                modifier = Modifier
                    .color(Color.white)
                    .fontSize(5.cssRem)
                    .styleModifier { property("text-shadow", "0px 4px 10px rgba(0,0,0,0.8)") }
            )
        }

        if (showBlink) {
            Box(modifier = Modifier.fillMaxSize().backgroundColor(Color.white))
        }
    }

    Button(
        attrs = Modifier
            .backgroundColor(if (countdown == null) NeoColor.white else Color.gray)
            .color(NeoColor.backgroundPrimary)
            .padding(leftRight = 3.cssRem, topBottom = 1.cssRem)
            .borderRadius(50.percent)
            .styleModifier { property("border", "none") }
            .cursor(if (countdown == null) Cursor.Pointer else Cursor.NotAllowed)
            .onClick {
                if (countdown != null) return@onClick

                coroutineScope.launch {
                    if (timerSeconds > 0) {
                        for (i in timerSeconds downTo 1) {
                            countdown = i
                            delay(1000)
                        }
                    }
                    countdown = null

                    showBlink = true
                    delay(100)
                    showBlink = false

                    val video = document.querySelector("video") as? HTMLVideoElement
                    if (video != null) {
                        val canvas = document.createElement("canvas") as HTMLCanvasElement
                        val size = min(video.videoWidth, video.videoHeight)
                        canvas.width = size
                        canvas.height = size
                        val ctx = canvas.getContext("2d") as CanvasRenderingContext2D

                        ctx.translate(canvas.width.toDouble(), 0.0)
                        ctx.scale(-1.0, 1.0)

                        val sourceX = (video.videoWidth - size) / 2.0
                        val sourceY = (video.videoHeight - size) / 2.0

                        ctx.drawImage(
                            video,
                            sourceX, sourceY, size.toDouble(), size.toDouble(),
                            0.0, 0.0, size.toDouble(), size.toDouble()
                        )

                        val dataUrl = canvas.toDataURL("image/png")
                        onCapture(dataUrl)
                    }
                }
            }
            .toAttrs()
    ) {
        SpanText("SNAP")
    }
}

@Composable
fun DecorateStage(
    frameType: FrameType,
    photos: List<String>,
    stickers: List<String>,
    selectedSticker: String,
    placedStickers: List<PlacedSticker>,
    backgroundOptions: List<BackgroundOption>,
    selectedBackground: BackgroundOption,
    filterOptions: List<FilterOption>, // NEW
    selectedFilter: FilterOption,      // NEW
    onSelectFilter: (FilterOption) -> Unit, // NEW
    onSelectBackground: (BackgroundOption) -> Unit,
    onSelectSticker: (String) -> Unit,
    onPlaceSticker: (Double, Double) -> Unit,
    onUndoSticker: () -> Unit,
    onReset: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.gap(1.5.cssRem)) {
        SpanText("Decorate your strip! Select a sticker and tap the photo.", modifier = Modifier.color(NeoColor.white))

        // Sticker Row (Keep existing)
        Row(
            modifier = Modifier.backgroundColor(Color.darkgray).padding(0.5.cssRem).borderRadius(8.px).flexWrap(FlexWrap.Wrap),
            horizontalArrangement = Arrangement.spacedBy(0.5.cssRem)
        ) {
            stickers.forEach { sticker ->
                Box(
                    modifier = Modifier
                        .padding(0.5.cssRem)
                        .backgroundColor(if (sticker == selectedSticker) NeoColor.colorPrimary else Color.transparent)
                        .borderRadius(4.px)
                        .cursor(Cursor.Pointer)
                        .onClick { onSelectSticker(sticker) }
                ) {
                    SpanText(sticker, modifier = Modifier.fontSize(1.5.cssRem))
                }
            }
        }

        // Background Row (Keep existing)
        Row(
            modifier = Modifier.gap(0.8.cssRem).flexWrap(FlexWrap.Wrap),
            horizontalArrangement = Arrangement.Center
        ) {
            backgroundOptions.forEach { option ->
                Box(
                    modifier = Modifier
                        .padding(0.4.cssRem)
                        .border(2.px, LineStyle.Solid, if (selectedBackground == option) NeoColor.colorPrimary else Color.transparent)
                        .borderRadius(6.px)
                        .cursor(Cursor.Pointer)
                        .onClick { onSelectBackground(option) }
                        .styleModifier {
                            if (option.imageUrl != null) {
                                property("background-image", "url(${option.imageUrl})")
                                property("background-size", "cover")
                                property("background-position", "center")
                            } else {
                                property("background-color", option.hexColor)
                            }
                        }
                ) {
                    SpanText(
                        text = option.name,
                        modifier = Modifier
                            .padding(leftRight = 0.6.cssRem, topBottom = 0.3.cssRem)
                            .fontSize(0.9.cssRem)
                            .styleModifier {
                                property("color", option.textColor)
                                property("text-shadow", "0px 1px 4px rgba(0,0,0,0.8)")
                            }
                    )
                }
            }
        }

        // NEW: Photo Filter Row
        Row(
            modifier = Modifier.gap(0.8.cssRem).flexWrap(FlexWrap.Wrap),
            horizontalArrangement = Arrangement.Center
        ) {
            filterOptions.forEach { filter ->
                Button(
                    attrs = Modifier
                        .backgroundColor(if (selectedFilter == filter) NeoColor.colorPrimary else Color.darkgray)
                        .color(Color.white)
                        .padding(leftRight = 1.cssRem, topBottom = 0.5.cssRem)
                        .borderRadius(4.px)
                        .styleModifier { property("border", "none") }
                        .cursor(Cursor.Pointer)
                        .onClick { onSelectFilter(filter) }
                        .toAttrs()
                ) {
                    SpanText(filter.name)
                }
            }
        }

        // The Frame Box (Keep mostly existing, but update the PhotoSlot calls)
        Box(
            modifier = Modifier
                .position(Position.Relative)
                .padding(24.px)
                .cursor(Cursor.Crosshair)
                .onClick { event ->
                    val target = event.currentTarget.asDynamic()
                    val rect = target.getBoundingClientRect()
                    val x = event.clientX.toDouble() - (rect.left as Double)
                    val y = event.clientY.toDouble() - (rect.top as Double)
                    onPlaceSticker(x, y)
                }
                .styleModifier {
                    if (selectedBackground.imageUrl != null) {
                        property("background-image", "url(${selectedBackground.imageUrl})")
                        property("background-size", "cover")
                        property("background-position", "center")
                    } else {
                        property("background-color", selectedBackground.hexColor)
                        property("background-image", "none")
                    }
                }
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                if (frameType == FrameType.GRID_4) {
                    Column(modifier = Modifier.gap(10.px)) {
                        Row(modifier = Modifier.gap(10.px)) {
                            // NEW: Pass the cssValue to the slot
                            PhotoSlot(photos.getOrNull(0), 150, selectedFilter.cssValue)
                            PhotoSlot(photos.getOrNull(1), 150, selectedFilter.cssValue)
                        }
                        Row(modifier = Modifier.gap(10.px)) {
                            PhotoSlot(photos.getOrNull(2), 150, selectedFilter.cssValue)
                            PhotoSlot(photos.getOrNull(3), 150, selectedFilter.cssValue)
                        }
                    }
                } else {
                    Column(modifier = Modifier.gap(10.px)) {
                        photos.forEach { photoData ->
                            PhotoSlot(photoData, 200, selectedFilter.cssValue)
                        }
                    }
                }

                SpanText(
                    text = "neotica.id",
                    modifier = Modifier
                        .margin(top = 16.px)
                        .fontSize(1.2.cssRem)
                        .fontWeight(FontWeight.Bold)
                        .styleModifier {
                            property("color", selectedBackground.textColor)
                            property("font-family", "monospace")
                        }
                )
            }

            placedStickers.forEach { sticker ->
                SpanText(
                    text = sticker.emoji,
                    modifier = Modifier
                        .position(Position.Absolute)
                        .left(sticker.x.px - 16.px)
                        .top(sticker.y.px - 16.px)
                        .fontSize(2.cssRem)
                        .styleModifier {
                            property("pointer-events", "none")
                        }
                )
            }
        }

        // Action Buttons (Update download call to include selectedFilter)
        Row(horizontalArrangement = Arrangement.spacedBy(1.cssRem), modifier = Modifier.flexWrap(FlexWrap.Wrap)) {
            Button(
                attrs = Modifier.backgroundColor(Color.transparent).color(NeoColor.colorPrimary).padding(1.cssRem).styleModifier { property("border", "none") }.cursor(Cursor.Pointer).onClick { onUndoSticker() }.toAttrs()
            ) { SpanText("Undo ↩️") }

            Button(
                attrs = Modifier.backgroundColor(NeoColor.colorPrimary).color(Color.white).padding(1.cssRem).borderRadius(8.px).styleModifier { property("border", "none") }.cursor(Cursor.Pointer)
                    // NEW: Pass the filter down to the downloader!
                    .onClick { downloadPhotobooth(frameType, photos, placedStickers, selectedBackground, selectedFilter) }
                    .toAttrs()
            ) { SpanText("Download 💾") }

            Button(
                attrs = Modifier.backgroundColor(Color.transparent).color(NeoColor.colorPrimary).padding(1.cssRem).styleModifier { property("border", "none") }.cursor(Cursor.Pointer).onClick { onReset() }.toAttrs()
            ) { SpanText("Start Over 🔄") }
        }
    }
}

// NEW: Accept filterCss and apply it to the Img styleModifier
@Composable
private fun PhotoSlot(dataUrl: String?, sizePx: Int, filterCss: String) {
    if (dataUrl != null) {
        Img(
            src = dataUrl,
            attrs = Modifier
                .width(sizePx.px)
                .height(sizePx.px)
                .objectFit(ObjectFit.Cover)
                .backgroundColor(Color.black)
                .styleModifier { property("filter", filterCss) } // Applies filter on screen!
                .toAttrs()
        )
    } else {
        Box(modifier = Modifier.width(sizePx.px).height(sizePx.px).backgroundColor(Color.lightgray))
    }
}