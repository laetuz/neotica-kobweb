package id.neotica.neotica.pages.photobooth

import kotlinx.browser.window
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLAnchorElement
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.HTMLImageElement

fun downloadPhotobooth(
    frameType: FrameType,
    photos: List<String>,
    stickers: List<PlacedSticker>,
    bgOption: BackgroundOption,
    selectedFilter: FilterOption
) {
    if (photos.isEmpty()) return

    val canvas = window.document.createElement("canvas") as HTMLCanvasElement
    val ctx = canvas.getContext("2d") as CanvasRenderingContext2D

    val scaleMultiplier = 4.0

    val padding = 24.0 * scaleMultiplier
    val gap = 10.0 * scaleMultiplier
    val watermarkMargin = 16.0 * scaleMultiplier
    val watermarkHeight = 24.0 * scaleMultiplier

    val photoSize = (if (frameType == FrameType.GRID_4) 150.0 else 200.0) * scaleMultiplier
    val cols = if (frameType == FrameType.GRID_4) 2 else 1
    val rows = if (frameType == FrameType.GRID_4) 2 else frameType.slots

    val width = (padding * 2) + (cols * photoSize) + ((cols - 1) * gap)
    val height = (padding * 2) + (rows * photoSize) + ((rows - 1) * gap) + watermarkMargin + watermarkHeight

    canvas.width = width.toInt()
    canvas.height = height.toInt()

    val processPhotosAndDownload = {
        var loadedCount = 0

        photos.forEachIndexed { index, dataUrl ->
            val img = window.document.createElement("img") as HTMLImageElement
            img.onload = {
                val col = if (frameType == FrameType.GRID_4) index % 2 else 0
                val row = if (frameType == FrameType.GRID_4) index / 2 else index

                val x = padding + col * (photoSize + gap)
                val y = padding + row * (photoSize + gap)

                // 1. Turn ON the filter for the photo
                ctx.asDynamic().filter = selectedFilter.cssValue

                // 2. Draw the photo with the filter applied
                ctx.drawImage(img, x, y, photoSize, photoSize)

                // 3. Turn OFF the filter immediately so stickers/watermarks stay normal
                ctx.asDynamic().filter = "none"

                loadedCount++

                if (loadedCount == photos.size) {

                    ctx.font = "bold ${22 * scaleMultiplier}px monospace"
                    ctx.fillStyle = bgOption.textColor
                    ctx.asDynamic().textAlign = "center"
                    ctx.asDynamic().textBaseline = "middle"

                    val watermarkY = padding + (rows * photoSize) + ((rows - 1) * gap) + watermarkMargin + (watermarkHeight / 2.0)
                    ctx.fillText("neotica.id", width / 2.0, watermarkY)

                    val fontSize = 32 * scaleMultiplier
                    ctx.font = "${fontSize}px Arial"
                    ctx.asDynamic().textAlign = "center"
                    ctx.asDynamic().textBaseline = "middle"

                    stickers.forEach { sticker ->
                        val targetX = sticker.x * scaleMultiplier
                        val targetY = sticker.y * scaleMultiplier
                        ctx.fillText(sticker.emoji, targetX, targetY)
                    }

                    val link = window.document.createElement("a") as HTMLAnchorElement
                    link.download = "neobooth-${kotlin.js.Date.now()}.png"
                    link.href = canvas.toDataURL("image/png")
                    link.click()
                }
            }
            img.src = dataUrl
        }
    }

    if (bgOption.imageUrl != null) {
        val bgImg = window.document.createElement("img") as HTMLImageElement

        bgImg.onload = {
            ctx.drawImage(bgImg, 0.0, 0.0, width, height)
            processPhotosAndDownload()
        }

        bgImg.onerror = { _, _, _, _, _ ->
            ctx.fillStyle = bgOption.hexColor
            ctx.fillRect(0.0, 0.0, width, height)
            processPhotosAndDownload()
        }

        bgImg.src = bgOption.imageUrl
    } else {
        ctx.fillStyle = bgOption.hexColor
        ctx.fillRect(0.0, 0.0, width, height)
        processPhotosAndDownload()
    }
}