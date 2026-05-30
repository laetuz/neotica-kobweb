package id.neotica.neotica.pages.photobooth

enum class FrameType(val slots: Int, val label: String) {
    STRIP_2(2, "2-Grid Strip"),
    STRIP_3(3, "3-Grid Strip"),
    GRID_4(4, "2x2 Grid")
}

data class PlacedSticker(val emoji: String, val x: Double, val y: Double)

data class BackgroundOption(
    val name: String,
    val hexColor: String,
    val textColor: String,
    val imageUrl: String? = null
)

// NEW: Filter Option Model
data class FilterOption(val name: String, val cssValue: String)