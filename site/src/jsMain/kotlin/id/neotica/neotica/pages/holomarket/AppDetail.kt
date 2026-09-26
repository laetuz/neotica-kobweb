package id.neotica.neotica.pages.holomarket

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.Cursor
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.css.Overflow
import com.varabyte.kobweb.compose.css.TextAlign
import com.varabyte.kobweb.compose.css.TextDecorationLine
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
import com.varabyte.kobweb.core.rememberPageContext
import com.varabyte.kobweb.silk.components.graphics.Image
import com.varabyte.kobweb.silk.components.navigation.Link
import com.varabyte.kobweb.silk.components.text.SpanText
import id.neotica.neotica.components.NeoColor
import id.neotica.neotica.components.layouts.NeoLayoutData
import id.neotica.neotica.components.retro.RainbowDivider
import id.neotica.neotica.components.retro.RetroColor
import id.neotica.neotica.components.retro.StarfieldStars
import id.neotica.neotica.components.retro.retroBevel
import id.neotica.neotica.utils.Constants
import kotlin.math.roundToInt
import kotlinx.browser.window
import kotlinx.coroutines.await
import kotlinx.serialization.json.Json
import org.jetbrains.compose.web.css.LineStyle
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.px

@InitRoute
fun initAppDetailPage(ctx: InitRouteContext) {
    ctx.data.add(NeoLayoutData("HoloMarket \u00B7 App Detail"))
}

@Page(routeOverride = "/holomarket/app/{package}")
@Layout(".components.layouts.NeoPageLayout")
@Composable
fun HoloMarketAppDetailPage() {
    val ctx = rememberPageContext()
    val packageName = ctx.route.params["package"] ?: ""
    val jsonParser = remember { Json { ignoreUnknownKeys = true } }

    var detail by remember { mutableStateOf<PublicAppDetail?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(packageName) {
        isLoading = true
        errorMessage = null
        detail = null
        if (packageName.isBlank()) {
            errorMessage = "No app specified."
            isLoading = false
            return@LaunchedEffect
        }
        try {
            val url = "${Constants.PUBLIC_API_URL}/apps/${encodeQueryComponent(packageName)}"
            val response = window.fetch(url).await()
            when {
                response.ok -> {
                    val app = jsonParser.decodeFromString<PublicAppDetail>(response.text().await())
                    detail = app
                    injectAppHead(app, packageName)
                }
                response.status.toInt() == 404 -> errorMessage = "App not found."
                response.status.toInt() == 429 -> {
                    val seconds = response.headers.get("Retry-After")?.toIntOrNull() ?: 60
                    errorMessage = "Too many requests. Please try again in $seconds seconds."
                }
                else -> errorMessage = "Failed to load app (HTTP ${response.status})."
            }
        } catch (e: Throwable) {
            console.error("Failed to load app detail: ${e.message}")
            errorMessage = "Network error. Please try again."
        }
        isLoading = false
    }

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
        Link(
            text = "\u2190  All apps",
            path = "/holomarket/apps",
            modifier = Modifier.align(Alignment.Start).fontSize(1.1.cssRem).color(RetroColor.skyBlue)
        )

        when {
            isLoading -> SpanText(
                text = "Loading app...",
                modifier = Modifier.fontSize(1.4.cssRem).color(RetroColor.cyan)
            )
            errorMessage != null -> SpanText(
                text = errorMessage!!,
                modifier = Modifier.fontSize(1.4.cssRem).color(NeoColor.negativePrimary).textAlign(TextAlign.Center)
            )
            detail != null -> AppDetailContent(detail!!)
        }
    }
}

@Composable
private fun AppDetailContent(app: PublicAppDetail) {
    Column(
        modifier = Modifier.fillMaxWidth().maxWidth(760.px).gap(1.2.cssRem)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().gap(1.2.cssRem),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AppIcon(iconUrl = app.iconUrl, size = 96)
            Column(modifier = Modifier.fillMaxWidth().gap(0.2.cssRem)) {
                SpanText(
                    text = app.title.ifBlank { app.packageName },
                    modifier = Modifier
                        .fontSize(2.cssRem)
                        .fontWeight(FontWeight.Bold)
                        .color(RetroColor.babyBlue)
                        .textShadow(2.px, 2.px, color = RetroColor.navy.copy(alpha = 200))
                )
                if (app.developer.isNotBlank()) {
                    SpanText(
                        text = app.developer,
                        modifier = Modifier.fontSize(1.1.cssRem).color(RetroColor.cyan)
                    )
                }
                if (app.averageRating != null) {
                    val rating = (app.averageRating!! * 10).roundToInt() / 10.0
                    SpanText(
                        text = "\u2605 $rating  \u00B7  ${app.totalReviews} review(s)",
                        modifier = Modifier.fontSize(1.cssRem).color(RetroColor.cream)
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().gap(0.5.cssRem),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (app.category.isNotBlank()) CategoryBadge(app.category)
            app.categories.forEach { CategoryBadge(it) }
        }

        RainbowDivider()

        if (app.description.isNotBlank()) {
            SpanText(
                text = app.description,
                modifier = Modifier.fontSize(1.05.cssRem).color(RetroColor.cream).lineHeight(1.6)
            )
        }

        Link(
            path = app.downloadUrl,
            modifier = Modifier
                .retroBevel(RetroColor.babyBlue)
                .padding(leftRight = 1.6.cssRem, topBottom = 0.6.cssRem)
                .cursor(Cursor.Pointer)
        ) {
            SpanText(
                text = "\u2B07  Download" + (if (app.versionName.isNotBlank()) "  (v${app.versionName})" else ""),
                modifier = Modifier
                    .fontSize(1.3.cssRem)
                    .fontWeight(FontWeight.Bold)
                    .color(NeoColor.backgroundPrimary)
                    .textDecorationLine(TextDecorationLine.None)
            )
        }

        if (app.screenshots.isNotEmpty()) {
            SpanText(
                text = "Screenshots",
                modifier = Modifier.fontSize(1.4.cssRem).fontWeight(FontWeight.Bold).color(RetroColor.skyBlue)
            )
            Row(
                modifier = Modifier.fillMaxWidth().overflow(Overflow.Auto).gap(0.8.cssRem),
                horizontalArrangement = Arrangement.spacedBy(0.8.cssRem)
            ) {
                app.screenshots.forEach { shot ->
                    resolvePublicUrl(shot)?.let { url ->
                        Image(
                            src = url,
                            modifier = Modifier
                                .height(220.px)
                                .borderRadius(6.px)
                                .border(2.px, LineStyle.Solid, RetroColor.steelBlue.copy(alpha = 120))
                        )
                    }
                }
            }
        }

        if (app.versions.isNotEmpty()) {
            SpanText(
                text = "Version History",
                modifier = Modifier.fontSize(1.4.cssRem).fontWeight(FontWeight.Bold).color(RetroColor.skyBlue)
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .backgroundColor(NeoColor.backgroundPrimaryTransparent)
                    .border(2.px, LineStyle.Solid, RetroColor.steelBlue.copy(alpha = 90))
                    .borderRadius(4.px)
                    .padding(1.cssRem)
                    .gap(0.8.cssRem)
            ) {
                app.versions.forEachIndexed { index, version ->
                    VersionRow(version)
                    if (index < app.versions.size - 1) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.px)
                                .backgroundColor(RetroColor.steelBlue.copy(alpha = 60))
                        )
                    }
                }
            }
        }

        SpanText(
            text = app.packageName,
            modifier = Modifier.fontSize(0.8.cssRem).color(NeoColor.white.copy(alpha = 120))
        )
    }
}

@Composable
private fun CategoryBadge(label: String) {
    Box(
        modifier = Modifier
            .backgroundColor(RetroColor.steelBlue.copy(alpha = 60))
            .border(1.px, LineStyle.Solid, RetroColor.skyBlue.copy(alpha = 140))
            .borderRadius(4.px)
            .padding(leftRight = 0.6.cssRem, topBottom = 0.15.cssRem)
    ) {
        SpanText(
            text = label.replaceFirstChar { it.uppercase() },
            modifier = Modifier.fontSize(0.8.cssRem).fontWeight(FontWeight.Bold).color(RetroColor.cream)
        )
    }
}

@Composable
private fun VersionRow(version: PublicAppVersion) {
    Column(modifier = Modifier.fillMaxWidth().gap(0.2.cssRem)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SpanText(
                text = "v${version.versionName} (${version.versionCode})",
                modifier = Modifier.fontSize(1.05.cssRem).fontWeight(FontWeight.Bold).color(RetroColor.cream)
            )
            if (version.downloadUrl.isNotBlank()) {
                Link(
                    path = version.downloadUrl,
                    modifier = Modifier
                        .retroBevel(RetroColor.steelBlue)
                        .padding(leftRight = 0.8.cssRem, topBottom = 0.2.cssRem)
                        .cursor(Cursor.Pointer)
                ) {
                    SpanText(
                        text = "Download",
                        modifier = Modifier
                            .fontSize(0.9.cssRem)
                            .fontWeight(FontWeight.Bold)
                            .color(NeoColor.backgroundPrimary)
                            .textDecorationLine(TextDecorationLine.None)
                    )
                }
            }
        }
        val sdk = buildString {
            version.minSdk?.let { append("Min SDK: $it") }
            version.maxSdk?.let { if (isNotEmpty()) append("  \u00B7  "); append("Max SDK: $it") }
        }
        if (sdk.isNotBlank()) {
            SpanText(text = sdk, modifier = Modifier.fontSize(0.8.cssRem).color(RetroColor.cyan))
        }
        if (version.changelog.isNotBlank()) {
            SpanText(
                text = version.changelog,
                modifier = Modifier.fontSize(0.9.cssRem).color(RetroColor.beige).lineHeight(1.4)
            )
        }
    }
}

private fun injectAppHead(app: PublicAppDetail, packageName: String) {
    val head = window.document.head ?: return
    val canonicalUrl = "https://neotica.id/holomarket/app/$packageName"
    window.document.title = "${app.title.ifBlank { packageName }} \u00B7 HoloMarket"

    val existing = head.querySelectorAll("meta[property^='og:']")
    for (i in 0 until existing.length) existing.item(i)?.let { head.removeChild(it) }

    fun injectMeta(property: String, content: String) {
        head.appendChild(window.document.createElement("meta").apply {
            setAttribute("property", property)
            setAttribute("content", content)
        })
    }
    injectMeta("og:title", "${app.title} - HoloMarket")
    injectMeta("og:description", app.description.ifBlank { "Get ${app.title} on HoloMarket." })
    injectMeta("og:type", "website")
    injectMeta("og:url", canonicalUrl)
    resolvePublicUrl(app.iconUrl)?.let { injectMeta("og:image", it) }

    head.querySelector("link[rel='canonical']")?.let { head.removeChild(it) }
    head.appendChild(window.document.createElement("link").apply {
        setAttribute("rel", "canonical")
        setAttribute("href", canonicalUrl)
    })
}