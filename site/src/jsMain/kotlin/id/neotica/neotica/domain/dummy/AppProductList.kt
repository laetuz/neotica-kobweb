package id.neotica.neotica.domain.dummy

import id.neotica.neotica.domain.model.AppProduct
import id.neotica.neotica.domain.model.AppVersion

object AppProductList {
    val products = listOf(
        AppProduct(
            packageName = "id.neotica.launcher",
            title = "Neotica Launcher",
            developer = "Neotica Studio",
            description = "A lightweight home screen replacement for legacy Android devices. " +
                    "Designed to breathe new life into older hardware with a clean, " +
                    "minimal interface that respects the Holo design language. " +
                    "Features customizable grid sizes, gesture shortcuts, and " +
                    "a built-in app drawer with smooth animations.",
            iconUrl = "/neotica-logo.png",
            rating = 4.5f,
            category = "APPLICATION",
            size = "2.4 MB",
            installCount = "15K+",
            versionHistory = listOf(
                AppVersion("2.1.0", 210, "Added gesture shortcuts and icon pack support. Fixed drawer lag on low-end devices. Updated target SDK to 19.", 7, "/releases/launcher-v2.1.0.apk"),
                AppVersion("2.0.0", 200, "Major rewrite. New Holo-themed settings panel. Performance optimizations for devices with <512MB RAM.", 7, "/releases/launcher-v2.0.0.apk"),
                AppVersion("1.0.0", 100, "Initial release. Basic launcher functionality with customizable grid and app drawer.", 7, "/releases/launcher-v1.0.0.apk"),
            ),
            screenshots = listOf("/main_banner.png")
        ),
        AppProduct(
            packageName = "id.neotica.retroboard",
            title = "RetroBoard",
            developer = "Neotica Studio",
            description = "A nostalgic software keyboard that brings back the look and feel of " +
                    "early Android keyboards. Supports multiple languages, swipe typing, " +
                    "and customizable themes. Optimized for devices with hardware keyboards " +
                    "as well as touch-only devices.",
            iconUrl = "/neotica-logo.png",
            rating = 4.2f,
            category = "APPLICATION",
            size = "3.1 MB",
            installCount = "8.2K+",
            versionHistory = listOf(
                AppVersion("1.5.0", 150, "New theme engine with Holo Light/Dark presets. Added emoji support for API 11+.", 7, "/releases/retroboard-v1.5.0.apk"),
                AppVersion("1.4.0", 140, "Swipe typing improvements. Reduced APK size by 40%.", 7, "/releases/retroboard-v1.4.0.apk"),
                AppVersion("1.0.0", 100, "First public release. Basic keyboard with Holo styling and multi-language support.", 7, "/releases/retroboard-v1.0.0.apk"),
            ),
            screenshots = listOf("/main_banner.png")
        ),
        AppProduct(
            packageName = "com.retrostudio.droidterm",
            title = "DroidTerm",
            developer = "Retro Studio",
            description = "A full-featured terminal emulator for Android. Run shell commands, " +
                    "SSH into remote servers, and write scripts right on your device. " +
                    "Supports VT100/xterm escape sequences, multiple sessions, " +
                    "and hardware keyboard shortcuts.",
            iconUrl = "/neotica-logo.png",
            rating = 4.7f,
            category = "APPLICATION",
            size = "1.8 MB",
            installCount = "42K+",
            versionHistory = listOf(
                AppVersion("3.2.0", 320, "SSH key management UI. Added support for custom color schemes.", 7, "/releases/droidterm-v3.2.0.apk"),
                AppVersion("3.1.0", 310, "Split-screen session support. Bug fixes for Samsung devices.", 7, "/releases/droidterm-v3.1.0.apk"),
                AppVersion("3.0.0", 300, "Material redesign with Holo compatibility layer. New session manager.", 7, "/releases/droidterm-v3.0.0.apk"),
            ),
            screenshots = listOf("/main_banner.png")
        ),
    )
}
