package id.neotica.neotica.pages.photobooth.res

import id.neotica.neotica.pages.photobooth.BackgroundOption
import id.neotica.neotica.pages.photobooth.res.PhotoBoothResources.BG_PINK1
import id.neotica.neotica.pages.photobooth.res.PhotoBoothResources.BG_STARS2

val pbBackgroundList = listOf(
    // --- ADD YOUR IMAGE RESOURCES HERE ---
    // Put these images in your Kobweb project's `src/jsMain/resources/public` folder!
    BackgroundOption("Y2K Stars", "#000000", "#FFFFFF", imageUrl = BG_STARS2),
    BackgroundOption("Pink Grid", "#FFD1DC", "#000000", imageUrl = BG_PINK1),

    // --- STANDARD SOLID COLORS ---
    BackgroundOption("Classic White", "#FFFFFF", "#000000"),
    BackgroundOption("Dark Mode", "#1A1A1A", "#FFFFFF"),
    BackgroundOption("Retro Gray", "#CCCCCC", "#1A1A1A"),
    BackgroundOption("Vintage Blue", "#AEC6CF", "#1A1A1A")
)