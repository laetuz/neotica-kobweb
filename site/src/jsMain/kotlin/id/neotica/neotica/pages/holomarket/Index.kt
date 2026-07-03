package id.neotica.neotica.pages.holomarket

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.varabyte.kobweb.compose.css.Cursor
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.css.Overflow
import com.varabyte.kobweb.compose.css.TextAlign
import com.varabyte.kobweb.compose.foundation.layout.Arrangement
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.data.add
import com.varabyte.kobweb.core.init.InitRoute
import com.varabyte.kobweb.core.init.InitRouteContext
import com.varabyte.kobweb.core.layout.Layout
import com.varabyte.kobweb.silk.components.graphics.Image
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.base
import com.varabyte.kobweb.silk.style.toModifier
import id.neotica.neotica.components.NeoColor
import id.neotica.neotica.components.layouts.NeoLayoutData
import id.neotica.neotica.components.others.NeoText
import id.neotica.neotica.components.resources.NeoResources
import kotlinx.browser.window
import kotlinx.coroutines.await
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.jetbrains.compose.web.css.LineStyle
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.px

@Serializable
private data class GitHubRelease(
    @SerialName("tag_name") val tagName: String,
    val assets: List<GitHubAsset>,
)

@Serializable
private data class GitHubAsset(
    @SerialName("browser_download_url") val browserDownloadUrl: String,
)

val FeatureCardStyle = CssStyle.base {
    Modifier
        .backgroundColor(NeoColor.backgroundPrimaryTransparent)
        .border(1.px, LineStyle.Solid, NeoColor.colorPrimary.copy(alpha = 25))
        .borderRadius(8.px)
        .padding(1.2.cssRem)
        .gap(0.8.cssRem)
}

@InitRoute
fun initHoloMarketLanding(ctx: InitRouteContext) {
    ctx.data.add(NeoLayoutData("HoloMarket \u00B7 Android App Store"))
}

@Page
@Layout(".components.layouts.NeoPageLayout")
@Composable
fun HoloMarketLandingPage() {
    var latestTag by remember { mutableStateOf(NeoResources.HOLOMARKET_LATEST_TAG) }
    var downloadUrl by remember { mutableStateOf(NeoResources.HOLOMARKET_DL_URL) }
    val jsonParser = remember { Json { ignoreUnknownKeys = true } }

    LaunchedEffect(Unit) {
        try {
            val response = window.fetch("https://api.github.com/repos/laetuz/HoloMarket/releases/latest").await()
            if (response.ok) {
                val text = response.text().await()
                val release = jsonParser.decodeFromString<GitHubRelease>(text)
                latestTag = release.tagName
                release.assets.firstOrNull()?.let { downloadUrl = it.browserDownloadUrl }
            }
        } catch (e: Throwable) {
            console.error("Failed to fetch latest release: ${e.message}")
        }
    }

    LaunchedEffect(Unit) {
        val head = window.document.head ?: return@LaunchedEffect

        val existing = head.querySelectorAll("meta[property^='og:']")
        for (i in 0 until existing.length) {
            existing.item(i)?.let { head.removeChild(it) }
        }

        fun injectMeta(property: String, content: String) {
            val el = window.document.createElement("meta").apply {
                setAttribute("property", property)
                setAttribute("content", content)
            }
            head.appendChild(el)
        }
        injectMeta("og:title", "HoloMarket - Android App Store for Legacy Devices")
        injectMeta("og:description", "A third-party marketplace featuring a curated collection of Android applications, games, and tools for Android 2.1+ devices.")
        injectMeta("og:type", "website")
        injectMeta("og:url", "https://neotica.id/holomarket")
        injectMeta("og:image", "https://neotica.id/projects/holomarket/ss-holomarket-1.png")
    }

    Column(
        modifier = Modifier
            .backgroundColor(NeoColor.backgroundPrimary)
            .fillMaxSize()
            .padding(leftRight = 2.cssRem, topBottom = 1.cssRem)
            .overflow(Overflow.Auto)
            .gap(1.5.cssRem),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HeroSection()

        DescriptionSection()

        FeatureSection()

        RequirementsSection()

        ScreenshotsSection()

        DownloadSection(latestTag, downloadUrl)
    }
}

@Composable
private fun HeroSection() {
    Column(
        modifier = Modifier.fillMaxWidth().gap(1.cssRem),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(260.px)
                .border(1.px, LineStyle.Solid, NeoColor.colorPrimary.copy(alpha = 25))
                .borderRadius(8.px)
        ) {
            Image(
                src = NeoResources.HOLOMARKET_ICON,
                modifier = Modifier.fillMaxSize()
            )
        }

        NeoText(
            text = "HoloMarket",
            modifier = Modifier
                .fontSize(3.cssRem)
                .fontWeight(FontWeight.Bold)
                .margin(top = 0.5.cssRem)
        )

        NeoText(
            text = "Discover great apps for your Android device",
            modifier = Modifier
                .fontSize(1.2.cssRem)
                .color(NeoColor.colorPrimary)
                .textAlign(TextAlign.Center)
        )

        NeoText(
            text = "A brand new third-party marketplace featuring a curated collection of applications, games, and tools. Lightweight, fast, and built for every Android phone and tablet.",
            modifier = Modifier
                .fontSize(0.95.cssRem)
                .textAlign(TextAlign.Center)
                .lineHeight(1.6)
                .maxWidth(600.px)
                .margin(top = 0.5.cssRem)
        )
    }
}

@Composable
private fun DescriptionSection() {
    Column(
        modifier = Modifier.fillMaxWidth().gap(1.cssRem).maxWidth(700.px),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SectionHeading("Introducing HoloMarket")

        NeoText(
            text = "There are millions of Android devices out there, and every one of them deserves access to great software. Neotica presents to you HoloMarket. It is a brand new app store that brings you a hand-picked selection of the finest applications the Android ecosystem has to offer.",
            modifier = Modifier.fontSize(0.95.cssRem).lineHeight(1.7).textAlign(TextAlign.Center)
        )

        NeoText(
            text = "Whether you are rocking the latest Android 4.0 Ice Cream Sandwich handset or holding onto your 2.3 Gingerbread device, HoloMarket is designed to work seamlessly across the board. With a decluttered minimalist Holo UI, easy to navigate, and ready to download. Just that simple.",
            modifier = Modifier.fontSize(0.95.cssRem).lineHeight(1.7).textAlign(TextAlign.Center)
        )
    }
}

@Composable
private fun FeatureSection() {
    Column(
        modifier = Modifier.fillMaxWidth().gap(1.cssRem).maxWidth(700.px),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SectionHeading("Features")

        Column(modifier = Modifier.fillMaxWidth().gap(1.cssRem)) {
            FeatureCard(
                "Browse by Category",
                "Explore apps sorted into APPLICATION, GAME, and ADULT categories with paginated feeds. Find exactly what you need without endless scrolling."
            )
            FeatureCard(
                "Featured Carousel",
                "A showcase of highlighted apps on the home screen. Discover great software you might have otherwise missed."
            )
            FeatureCard(
                "Search",
                "Find apps by keyword across the entire catalog. Fast client-side filtering with server-side pagination for larger result sets."
            )
            FeatureCard(
                "App Detail & Version History",
                "View full app descriptions, ratings (upcoming), screenshots, and a complete version history with changelogs. Download any previous version you need."
            )
            FeatureCard(
                "One-Tap Download & Install",
                "APK downloads with a progress bar via DownloadTask. Auto-launches the system package installer when the download completes."
            )
            FeatureCard(
                "Account & Sync",
                "Register, log in, and persist your session with JWT tokens stored in SharedPreferences. Your username is fetched from the API and cached locally."
            )
        }
    }
}

@Composable
private fun FeatureCard(title: String, description: String) {
    Row(
        modifier = FeatureCardStyle.toModifier().fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Column(modifier = Modifier.gap(0.3.cssRem)) {
            NeoText(
                text = title,
                modifier = Modifier.fontSize(1.1.cssRem).fontWeight(FontWeight.Bold)
            )
            NeoText(
                text = description,
                modifier = Modifier.fontSize(0.9.cssRem).lineHeight(1.6)
            )
        }
    }
}

@Composable
private fun RequirementsSection() {
    Column(
        modifier = Modifier.fillMaxWidth().gap(1.cssRem).maxWidth(700.px),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SectionHeading("Compatibility")

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .backgroundColor(NeoColor.backgroundPrimaryTransparent)
                .border(1.px, LineStyle.Solid, NeoColor.colorPrimary.copy(alpha = 25))
                .borderRadius(8.px)
                .padding(1.2.cssRem)
        ) {
            Column(modifier = Modifier.gap(0.5.cssRem).fillMaxWidth()) {
                RequirementRow("Android Version", "2.1 (Eclair) and up")
                RequirementRow("Minimum SDK", "7")
                RequirementRow("Target SDK", "21+")
                RequirementRow("Network", "HTTP connection (no TLS required)")
                RequirementRow("Storage", "Less than 5 MB for the app")
            }
        }
    }
}

@Composable
private fun RequirementRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        NeoText(
            text = label,
            modifier = Modifier
                .fontSize(0.9.cssRem)
                .color(NeoColor.colorPrimary)
                .weight(1)
        )
        NeoText(
            text = value,
            modifier = Modifier.fontSize(0.9.cssRem).textAlign(TextAlign.End)
        )
    }
}

@Composable
private fun ScreenshotsSection() {
    Column(
        modifier = Modifier.fillMaxWidth().gap(1.cssRem).maxWidth(700.px),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SectionHeading("Screenshots")

        NeoText(
            text = "HoloMarket running on Android 4.1 (Jelly Bean)",
            modifier = Modifier.fontSize(0.8.cssRem).color(NeoColor.colorPrimary)
        )

        Row(
            modifier = Modifier.fillMaxWidth().gap(1.cssRem),
            horizontalArrangement = Arrangement.Center
        ) {
            listOf(
                NeoResources.HOLOMARKET_SS_1,
                NeoResources.HOLOMARKET_SS_2,
                NeoResources.HOLOMARKET_SS_3,
            ).forEach { url ->
                Box(
                    modifier = Modifier
                        .width(160.px)
                        .backgroundColor(NeoColor.backgroundPrimaryTransparent)
                        .border(1.px, LineStyle.Solid, NeoColor.colorPrimary.copy(alpha = 25))
                        .borderRadius(8.px)
                        .overflow(Overflow.Hidden)
                ) {
                    Image(
                        src = url,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

@Composable
private fun DownloadSection(tag: String, apkUrl: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .gap(1.cssRem)
            .maxWidth(700.px)
            .padding(bottom = 2.cssRem),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SectionHeading("Get HoloMarket")

        NeoText(
            text = "Download the APK and sideload it onto your Android device. It is free, always will be.",
            modifier = Modifier.fontSize(0.95.cssRem).textAlign(TextAlign.Center)
        )

        Box(
            modifier = Modifier
                .backgroundColor(NeoColor.colorPrimary)
                .borderRadius(6.px)
                .padding(leftRight = 2.cssRem, topBottom = 0.8.cssRem)
                .margin(top = 0.5.cssRem)
                .cursor(Cursor.Pointer)
                .onClick { window.open(apkUrl, "_blank") }
        ) {
            NeoText(
                text = "DOWNLOAD HOLOMARKET $tag",
                modifier = Modifier
                    .fontSize(1.2.cssRem)
                    .fontWeight(FontWeight.Bold)
            )
        }

        NeoText(
            text = "$tag \u00B7 Requires Android 2.1+",
            modifier = Modifier.fontSize(0.8.cssRem).color(NeoColor.white.copy(alpha = 153))
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .backgroundColor(NeoColor.backgroundPrimaryTransparent)
                .border(1.px, LineStyle.Solid, NeoColor.colorPrimary.copy(alpha = 25))
                .borderRadius(8.px)
                .padding(1.cssRem)
                .margin(top = 1.cssRem)
        ) {
            Column(modifier = Modifier.gap(0.3.cssRem)) {
                NeoText(
                    text = "Installation Instructions",
                    modifier = Modifier.fontSize(0.9.cssRem).fontWeight(FontWeight.Bold)
                )
                NeoText(
                    text = "1. Download the APK file above to your device.",
                    modifier = Modifier.fontSize(0.85.cssRem)
                )
                NeoText(
                    text = "2. Open Settings, go to Applications, and check 'Unknown sources'.",
                    modifier = Modifier.fontSize(0.85.cssRem)
                )
                NeoText(
                    text = "3. Open the downloaded APK and follow the on-screen prompts to install.",
                    modifier = Modifier.fontSize(0.85.cssRem)
                )
                NeoText(
                    text = "4. Launch HoloMarket and start exploring.",
                    modifier = Modifier.fontSize(0.85.cssRem)
                )
            }
        }

        Box(
            modifier = Modifier
                .margin(top = 0.5.cssRem)
                .padding(1.cssRem)
        ) {
            NeoText(
                text = "HoloMarket is open source. Source code available on GitHub.",
                modifier = Modifier.fontSize(0.8.cssRem).color(NeoColor.colorPrimary).textAlign(TextAlign.Center)
            )
        }
    }
}

@Composable
private fun SectionHeading(text: String) {
    NeoText(
        text = text,
        modifier = Modifier
            .fontSize(1.5.cssRem)
            .fontWeight(FontWeight.Bold)
            .textAlign(TextAlign.Center)
            .fillMaxWidth()
    )
}
