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
import com.varabyte.kobweb.compose.css.BoxShadow
import com.varabyte.kobweb.compose.foundation.layout.Arrangement
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Color
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.data.add
import com.varabyte.kobweb.core.init.InitRoute
import com.varabyte.kobweb.core.init.InitRouteContext
import com.varabyte.kobweb.core.layout.Layout
import com.varabyte.kobweb.silk.components.graphics.Image
import com.varabyte.kobweb.silk.components.navigation.Link
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.base
import com.varabyte.kobweb.silk.style.toModifier
import id.neotica.neotica.components.NeoColor
import id.neotica.neotica.components.layouts.NeoLayoutData
import id.neotica.neotica.components.others.NeoText
import id.neotica.neotica.components.resources.NeoResources
import id.neotica.neotica.utils.Constants
import kotlinx.browser.window
import kotlinx.coroutines.await
import kotlinx.coroutines.delay
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.jetbrains.compose.web.css.LineStyle
import org.jetbrains.compose.web.css.color
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.fontSize
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.css.textAlign
import org.jetbrains.compose.web.css.width
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text
import kotlin.time.Duration.Companion.milliseconds

@Serializable
private data class HoloMarketLatest(
    @SerialName("version_name") val versionName: String = "",
    @SerialName("version_code") val versionCode: Int = 0,
    @SerialName("file_url") val fileUrl: String = "",
    @SerialName("changelog") val changelog: String = "",
    @SerialName("min_sdk") val minSdk: Int = 0,
    @SerialName("max_sdk") val maxSdk: Int = 0,
    @SerialName("created_at") val createdAt: Long = 0,
)

val FeatureCardStyle = CssStyle.base {
    Modifier
        .backgroundColor(NeoColor.backgroundPrimaryTransparent)
        .border(2.px, LineStyle.Solid, RetroColor.steelBlue.copy(alpha = 90))
        .borderRadius(4.px)
        .padding(1.2.cssRem)
        .gap(0.8.cssRem)
        .boxShadow(2.px, 2.px, color = Color.rgb(0, 0, 0).copy(alpha = 115))
}

@InitRoute
fun initHoloMarketLanding(ctx: InitRouteContext) {
    ctx.data.add(NeoLayoutData("HoloMarket \u00B7 Android App Store"))
}

@Page
@Layout(".components.layouts.NeoPageLayout")
@Composable
fun HoloMarketLandingPage() {
    var latestVersion by remember { mutableStateOf("") }
    var downloadUrl by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var fetchAttempt by remember { mutableStateOf(0) }
    val jsonParser = remember { Json { ignoreUnknownKeys = true } }

    LaunchedEffect(fetchAttempt) {
        errorMessage = null
        try {
            val response = window.fetch("${Constants.PUBLIC_API_URL}/holomarket/latest").await()
            if (response.ok) {
                val text = response.text().await()
                val release = jsonParser.decodeFromString<HoloMarketLatest>(text)
                release.versionName.takeIf { it.isNotBlank() }?.let { latestVersion = it }
                if (release.fileUrl.isNotBlank()) downloadUrl = release.fileUrl
            } else if (response.status.toInt() == 429) {
                val retryAfterSeconds = response.headers.get("Retry-After")?.toIntOrNull() ?: 60
                errorMessage = "Sorry, try again in $retryAfterSeconds seconds"
            } else {
                errorMessage = "Sorry, try again in 60 seconds"
            }
        } catch (e: Throwable) {
            console.error("Failed to fetch latest release: ${e.message}")
            errorMessage = "Sorry, try again in 60 seconds"
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
        injectMeta("og:description", "A third-party marketplace featuring a curated collection of Android applications, games, and tools for Android 1.5+ devices.")
        injectMeta("og:type", "website")
        injectMeta("og:url", "https://neotica.id/holomarket")
        injectMeta("og:image", "https://neotica.id/projects/holomarket/ss-holomarket-1.png")
    }

    Column(
        modifier = Modifier
            .background {
                color(NeoColor.backgroundPrimary)
                image(StarfieldStars)
            }
            .fillMaxSize()
            .padding(leftRight = 2.cssRem, topBottom = 1.cssRem)
            .overflow(Overflow.Auto)
            .gap(1.5.cssRem)
            .fontFamily("VT323", "monospace"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ErrorBanner(
            message = errorMessage,
            onDismiss = { errorMessage = null },
            onRetry = { fetchAttempt++ }
        )

        HeroSection()

        RainbowDivider()

        DescriptionSection()

        RainbowDivider()

        FeatureSection()

        RainbowDivider()

        RequirementsSection()

        RainbowDivider()

        ScreenshotsSection()

        RainbowDivider()

        if (latestVersion.isNotBlank() && downloadUrl.isNotBlank()) {
            DownloadSection(latestVersion, downloadUrl)
        } else if (errorMessage == null) {
            Box(
                modifier = Modifier
                    .padding(bottom = 2.cssRem)
                    .fillMaxWidth()
                    .maxWidth(700.px)
            ) {
                SpanText(
                    text = "Checking for the latest version...",
                    modifier = Modifier
                        .fontSize(0.9.cssRem)
                        .color(RetroColor.skyBlue)
                        .textAlign(TextAlign.Center)
                        .fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun ErrorBanner(message: String?, onDismiss: () -> Unit, onRetry: () -> Unit) {
    if (message == null) return

    LaunchedEffect(message) {
        delay(5000.milliseconds)
        onDismiss()
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .backgroundColor(RetroColor.royalBlue)
            .border(2.px, LineStyle.Solid, RetroColor.navy)
            .borderRadius(4.px)
            .padding(leftRight = 1.cssRem, topBottom = 0.8.cssRem)
            .gap(0.5.cssRem)
            .boxShadow(3.px, 3.px, color = Color.rgb(0, 0, 0).copy(alpha = 127))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SpanText(
                text = message,
                modifier = Modifier
                    .fontSize(1.cssRem)
                    .color(RetroColor.cream)
                    .fontWeight(FontWeight.Bold)
                    .weight(1)
            )
            Box(
                modifier = Modifier
                    .cursor(Cursor.Pointer)
                    .onClick { onDismiss() }
                    .padding(leftRight = 0.5.cssRem)
            ) {
                SpanText(
                    text = "\u00D7",
                    modifier = Modifier
                        .fontSize(1.3.cssRem)
                        .fontWeight(FontWeight.Bold)
                        .color(RetroColor.cream)
                )
            }
        }

        Box(
            modifier = Modifier
                .cursor(Cursor.Pointer)
                .borderRadius(4.px)
                .padding(leftRight = 0.8.cssRem, topBottom = 0.3.cssRem)
                .onClick { onRetry() }
                .backgroundColor(RetroColor.babyBlue)
                .border(2.px, LineStyle.Solid, RetroColor.navy)
                .boxShadow(2.px, 2.px, color = Color.rgb(0, 0, 0).copy(alpha = 127))
        ) {
            SpanText(
                text = "TRY AGAIN",
                modifier = Modifier
                    .fontSize(0.9.cssRem)
                    .fontWeight(FontWeight.Bold)
                    .color(RetroColor.navy)
            )
        }
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
//                .border(3.px, LineStyle.Solid, RetroColor.steelBlue)
//                .backgroundColor(NeoColor.white.copy(alpha = 37))
//                .borderRadius(4.px)
//                .padding(16.px)
//                .boxShadow(4.px, 4.px, color = Color.rgb(0, 0, 0).copy(alpha = 127))
        ) {
            Image(
                src = NeoResources.HOLOMARKET_ICON,
                modifier = Modifier.fillMaxSize()
            )
        }

        SpanText(
            text = "\u2726  HoloMarket  \u2726",
            modifier = Modifier
                .fontSize(3.cssRem)
                .fontWeight(FontWeight.Bold)
                .color(RetroColor.babyBlue)
                .margin(top = 0.5.cssRem)
                .textShadow(3.px, 3.px, color = RetroColor.navy.copy(alpha = 200))
        )

        SpanText(
            text = "Discover great apps for your Android device",
            modifier = Modifier
                .fontSize(1.2.cssRem)
                .color(RetroColor.cream)
                .textAlign(TextAlign.Center)
        )

        NeoText(
            text = "A marketplace featuring a curated collection of applications, games, and tools. Lightweight, fast, and built for every Android phone and tablet; brought to you by Neotica.",
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
        SectionHeading("Introducing HoloMarket", accent = RetroColor.skyBlue)

        NeoText(
            text = "There are millions of Android devices out there, and every one of them deserves access to great software. Neotica presents to you HoloMarket. It is a brand new app store that brings you a hand-picked selection of the finest applications the Android ecosystem has to offer.",
            modifier = Modifier.fontSize(0.95.cssRem).lineHeight(1.7).textAlign(TextAlign.Center)
        )

        NeoText(
            text = "Whether you are rocking the latest Android 4.0 Ice Cream Sandwich handset or holding onto your 1.5 Cupcake device, HoloMarket is designed to work seamlessly across the board. With a decluttered minimalist Holo UI, easy to navigate, and ready to download. Just that simple.",
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
        SectionHeading("Features", accent = RetroColor.babyBlue)

        Column(modifier = Modifier.fillMaxWidth().gap(1.cssRem)) {
            FeatureCard(
                "OVER 1000+ Applications!!",
                "We manage a lot of legacy applications in our ecosystem! We have more than 1000s of applications within our database, more than 100 are personally tested and used by our CEO to pass our quality checking. We will add review systems in the future to include You as our quality tester as well."
            )
            FeatureCard(
                "Browse by Category",
                "Explore apps sorted into APPLICATION, GAME, and SIMULATOR categories with paginated feeds. Find exactly what you need without endless scrolling."
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
                "View full app descriptions, ratings (upcoming), and a complete version history with changelogs. Download any previous version you need."
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
            SpanText(
                text = title,
                modifier = Modifier
                    .fontSize(1.1.cssRem)
                    .fontWeight(FontWeight.Bold)
                    .color(RetroColor.babyBlue)
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
        SectionHeading("Compatibility", accent = RetroColor.royalBlue)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .backgroundColor(NeoColor.backgroundPrimaryTransparent)
                .border(2.px, LineStyle.Solid, RetroColor.steelBlue.copy(alpha = 80))
                .borderRadius(4.px)
                .padding(1.2.cssRem)
                .boxShadow(2.px, 2.px, color = Color.rgb(0, 0, 0).copy(alpha = 115))
        ) {
            Column(modifier = Modifier.gap(0.5.cssRem).fillMaxWidth()) {
                RequirementRow("Android Version", "1.5 (Cupcake) and up", 0)
                RequirementRow("Minimum SDK", "3", 1)
                RequirementRow("Target SDK", "21+", 2)
                RequirementRow("Network", "HTTP connection (no TLS required)", 3)
                RequirementRow("Storage", "Less than 5 MB for the app", 4)
            }
        }
    }
}

@Composable
private fun RequirementRow(label: String, value: String, index: Int) {
    val stripe = if (index % 2 == 0) RetroColor.steelBlue.copy(alpha = 22) else RetroColor.navy.copy(alpha = 22)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .backgroundColor(stripe)
            .padding(leftRight = 0.5.cssRem, topBottom = 0.2.cssRem),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        SpanText(
            text = label,
            modifier = Modifier
                .fontSize(0.9.cssRem)
                .color(RetroColor.skyBlue)
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
        SectionHeading("Screenshots", accent = RetroColor.steelBlue)

        SpanText(
            text = "HoloMarket running on Android 4.1 (Jelly Bean)",
            modifier = Modifier.fontSize(0.8.cssRem).color(RetroColor.skyBlue)
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
        SectionHeading("Get HoloMarket", accent = RetroColor.skyBlue)

        NeoText(
            text = "Download the APK and sideload it onto your Android device. It is free, always will be.",
            modifier = Modifier.fontSize(0.95.cssRem).textAlign(TextAlign.Center)
        )

        Box(
            modifier = Modifier
                .backgroundColor(RetroColor.babyBlue)
                .border(2.px, LineStyle.Solid, RetroColor.navy)
                .borderRadius(4.px)
                .padding(leftRight = 2.cssRem, topBottom = 0.8.cssRem)
                .margin(top = 0.5.cssRem)
                .cursor(Cursor.Pointer)
                .boxShadow(
                    BoxShadow.of(0.px, 1.px, 0.px, 1.px, NeoColor.white.copy(alpha = 200), inset = true),
                    BoxShadow.of(0.px, (-1).px, 0.px, 1.px, Color.rgb(0, 0, 0).copy(alpha = 120), inset = true),
                    BoxShadow.of(3.px, 3.px, 0.px, 0.px, Color.rgb(0, 0, 0).copy(alpha = 127)),
                )
                .onClick { window.open(apkUrl, "_blank") }
        ) {
            SpanText(
                text = "DOWNLOAD HOLOMARKET $tag",
                modifier = Modifier
                    .fontSize(1.3.cssRem)
                    .fontWeight(FontWeight.Bold)
                    .color(NeoColor.backgroundPrimary)
            )
        }

        NeoText(
            text = "$tag \u00B7 Requires Android 1.5+",
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

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .margin(top = 0.5.cssRem)
                .padding(1.cssRem)
                .gap(0.6.cssRem),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Span(
                attrs = {
                    style {
                        fontSize(0.8.cssRem)
                        color(NeoColor.white)
                        textAlign("center")
                        width(100.percent)
                    }
                }
            ) {
                Text("HoloMarket is open source. Source code available on ")
                Link(
                    text = "GitHub.",
                    path = "https://github.com/laetuz/HoloMarket",
                    modifier = Modifier
                        .fontSize(0.8.cssRem)
                        .color(RetroColor.skyBlue)
                )
            }

            Link(
                text = "\u2726  Submit an app for review  \u2726",
                path = "/holomarket/upload",
                modifier = Modifier
                    .fontSize(0.85.cssRem)
                    .color(RetroColor.babyBlue)
                    .textAlign(TextAlign.Center)
                    .fillMaxWidth()
            )
        }
    }
}

@Composable
private fun SectionHeading(text: String, accent: Color = RetroColor.skyBlue) {
    SpanText(
        text = "\u2726  $text  \u2726",
        modifier = Modifier
            .fontSize(1.6.cssRem)
            .fontWeight(FontWeight.Bold)
            .color(accent)
            .textAlign(TextAlign.Center)
            .fillMaxWidth()
            .textShadow(2.px, 2.px, color = Color.rgb(0, 0, 0).copy(alpha = 153))
    )
}
