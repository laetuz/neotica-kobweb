package id.neotica.neotica.pages.holomarket

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.Cursor
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.css.ObjectFit
import com.varabyte.kobweb.compose.css.Overflow
import com.varabyte.kobweb.compose.css.TextAlign
import com.varabyte.kobweb.compose.css.TextDecorationLine
import com.varabyte.kobweb.compose.foundation.layout.Arrangement
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Color
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.data.add
import com.varabyte.kobweb.core.init.InitRoute
import com.varabyte.kobweb.core.init.InitRouteContext
import com.varabyte.kobweb.core.layout.Layout
import com.varabyte.kobweb.core.rememberPageContext
import com.varabyte.kobweb.silk.components.graphics.Image
import com.varabyte.kobweb.silk.components.layout.SimpleGrid
import com.varabyte.kobweb.silk.components.layout.numColumns
import com.varabyte.kobweb.silk.components.navigation.Link
import com.varabyte.kobweb.silk.components.text.SpanText
import id.neotica.neotica.components.NeoColor
import id.neotica.neotica.components.layouts.NeoLayoutData
import id.neotica.neotica.components.retro.RainbowDivider
import id.neotica.neotica.components.retro.RetroColor
import id.neotica.neotica.components.retro.StarfieldStars
import id.neotica.neotica.components.retro.retroBevel
import id.neotica.neotica.utils.Constants
import kotlinx.browser.window
import kotlinx.coroutines.await
import kotlinx.serialization.json.Json
import org.jetbrains.compose.web.attributes.placeholder
import org.jetbrains.compose.web.css.LineStyle
import org.jetbrains.compose.web.css.StyleScope
import org.jetbrains.compose.web.css.backgroundColor
import org.jetbrains.compose.web.css.border
import org.jetbrains.compose.web.css.borderRadius
import org.jetbrains.compose.web.css.color
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.fontFamily
import org.jetbrains.compose.web.css.fontSize
import org.jetbrains.compose.web.css.padding
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.css.width
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.TextInput

private const val PAGE_LIMIT = 12

@InitRoute
fun initAppsPage(ctx: InitRouteContext) {
    ctx.data.add(NeoLayoutData("HoloMarket \u00B7 All Apps"))
}

@Page
@Layout(".components.layouts.NeoPageLayout")
@Composable
fun HoloMarketAppsPage() {
    val ctx = rememberPageContext()
    val jsonParser = remember { Json { ignoreUnknownKeys = true } }

    val initialPage = ctx.route.queryParams["page"]?.toIntOrNull() ?: 1
    var page by remember { mutableStateOf(initialPage) }
    var searchInput by remember { mutableStateOf("") }
    var appliedSearch by remember { mutableStateOf("") }

    var items by remember { mutableStateOf<List<PublicFeedItem>>(emptyList()) }
    var totalPages by remember { mutableStateOf(1) }
    var totalItems by remember { mutableStateOf(0L) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        val head = window.document.head ?: return@LaunchedEffect
        val existing = head.querySelectorAll("meta[property^='og:']")
        for (i in 0 until existing.length) existing.item(i)?.let { head.removeChild(it) }

        fun injectMeta(property: String, content: String) {
            head.appendChild(window.document.createElement("meta").apply {
                setAttribute("property", property)
                setAttribute("content", content)
            })
        }
        injectMeta("og:title", "HoloMarket - All Apps")
        injectMeta("og:description", "Browse the full HoloMarket catalog of Android apps, games, and tools.")
        injectMeta("og:type", "website")
        injectMeta("og:url", "https://neotica.id/holomarket/apps")
        injectMeta("og:image", "https://neotica.id/projects/holomarket/ss-holomarket-1.png")

        head.querySelector("link[rel='canonical']")?.let { head.removeChild(it) }
        head.appendChild(window.document.createElement("link").apply {
            setAttribute("rel", "canonical")
            setAttribute("href", "https://neotica.id/holomarket/apps")
        })
    }

    LaunchedEffect(page, appliedSearch) {
        isLoading = true
        errorMessage = null
        if (window.location.search != "?page=$page") {
            window.history.pushState(null, "", "/holomarket/apps?page=$page")
        }
        try {
            val searchQuery = appliedSearch.trim().let { if (it.isBlank()) "" else "&search=${encodeQueryComponent(it)}" }
            val url = "${Constants.PUBLIC_API_URL}/feed?page=$page&limit=$PAGE_LIMIT$searchQuery"
            val response = window.fetch(url).await()
            when {
                response.ok -> {
                    val feed = jsonParser.decodeFromString<PublicFeedResponse>(response.text().await())
                    items = feed.data
                    totalPages = feed.totalPages.coerceAtLeast(1)
                    totalItems = feed.totalItems
                }
                response.status.toInt() == 429 -> {
                    val seconds = response.headers.get("Retry-After")?.toIntOrNull() ?: 60
                    errorMessage = "Too many requests. Please try again in $seconds seconds."
                }
                else -> errorMessage = "Failed to load apps (HTTP ${response.status})."
            }
        } catch (e: Throwable) {
            console.error("Failed to load feed: ${e.message}")
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
        Column(
            modifier = Modifier.fillMaxWidth().gap(0.3.cssRem),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SpanText(
                text = "\u2726  All Apps  \u2726",
                modifier = Modifier
                    .fontSize(2.2.cssRem)
                    .fontWeight(FontWeight.Bold)
                    .color(RetroColor.babyBlue)
                    .textAlign(TextAlign.Center)
                    .textShadow(3.px, 3.px, color = RetroColor.navy.copy(alpha = 200))
            )
            SpanText(
                text = if (totalItems > 0) "$totalItems apps in the catalog" else "Browse the HoloMarket catalog",
                modifier = Modifier.fontSize(1.05.cssRem).color(RetroColor.cream).textAlign(TextAlign.Center)
            )
        }

        RainbowDivider()

        Row(
            modifier = Modifier.fillMaxWidth().maxWidth(760.px).gap(0.6.cssRem),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.weight(1f)) {
                TextInput(
                    value = searchInput,
                    attrs = {
                        placeholder("Search apps...")
                        onInput { searchInput = it.value }
                        style { retroFieldStyle() }
                    }
                )
            }
            Box(
                modifier = Modifier
                    .retroBevel(RetroColor.babyBlue)
                    .padding(leftRight = 1.2.cssRem, topBottom = 0.7.cssRem)
                    .cursor(Cursor.Pointer)
                    .onClick {
                        appliedSearch = searchInput
                        page = 1
                    }
            ) {
                SpanText(
                    text = "Search",
                    modifier = Modifier.fontSize(1.1.cssRem).fontWeight(FontWeight.Bold).color(NeoColor.backgroundPrimary)
                )
            }
        }

        when {
            isLoading -> SpanText(
                text = "Loading apps...",
                modifier = Modifier.fontSize(1.4.cssRem).color(RetroColor.cyan)
            )
            errorMessage != null -> Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.gap(0.6.cssRem)
            ) {
                SpanText(
                    text = errorMessage!!,
                    modifier = Modifier.fontSize(1.3.cssRem).color(NeoColor.negativePrimary).textAlign(TextAlign.Center)
                )
                Box(
                    modifier = Modifier
                        .retroBevel(RetroColor.babyBlue)
                        .padding(leftRight = 1.2.cssRem, topBottom = 0.5.cssRem)
                        .cursor(Cursor.Pointer)
                        .onClick { appliedSearch = appliedSearch }
                ) {
                    SpanText("TRY AGAIN", Modifier.fontSize(1.05.cssRem).fontWeight(FontWeight.Bold).color(NeoColor.backgroundPrimary))
                }
            }
            items.isEmpty() -> SpanText(
                text = "No apps found.",
                modifier = Modifier.fontSize(1.3.cssRem).color(RetroColor.cream)
            )
        }

        SimpleGrid(
            numColumns = numColumns(base = 1, md = 2, lg = 3),
            modifier = Modifier.fillMaxWidth().maxWidth(1100.px).gap(1.cssRem)
        ) {
            items.forEach { item -> AppCard(item) }
        }

        if (!isLoading && errorMessage == null && items.isNotEmpty()) {
            PaginationFooter(page, totalPages) { newPage -> page = newPage }
        }
    }
}

@Composable
private fun AppCard(item: PublicFeedItem) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .backgroundColor(NeoColor.backgroundPrimaryTransparent)
            .border(2.px, LineStyle.Solid, RetroColor.steelBlue.copy(alpha = 90))
            .borderRadius(4.px)
            .padding(1.cssRem)
            .gap(0.6.cssRem)
            .boxShadow(2.px, 2.px, color = Color.rgb(0, 0, 0).copy(alpha = 115))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().gap(0.8.cssRem),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AppIcon(iconUrl = item.iconUrl, size = 56)
            Column(modifier = Modifier.fillMaxWidth().gap(0.15.cssRem)) {
                Link(
                    path = "/holomarket/app/${item.packageName}",
                    modifier = Modifier.textDecorationLine(TextDecorationLine.None)
                ) {
                    SpanText(
                        text = item.title.ifBlank { item.packageName },
                        modifier = Modifier.fontSize(1.2.cssRem).fontWeight(FontWeight.Bold).color(RetroColor.cream)
                    )
                }
                if (item.category.isNotBlank()) {
                    SpanText(
                        text = item.category.replaceFirstChar { it.uppercase() },
                        modifier = Modifier.fontSize(0.85.cssRem).color(RetroColor.cyan)
                    )
                }
            }
        }

        if (item.description.isNotBlank()) {
            SpanText(
                text = item.description,
                modifier = Modifier.fontSize(0.9.cssRem).color(RetroColor.beige).lineHeight(1.4)
            )
        }

        Link(
            path = item.downloadUrl,
            modifier = Modifier
                .retroBevel(RetroColor.babyBlue)
                .padding(leftRight = 1.cssRem, topBottom = 0.4.cssRem)
                .cursor(Cursor.Pointer)
        ) {
            SpanText(
                text = "\u2B07  Download",
                modifier = Modifier
                    .fontSize(1.05.cssRem)
                    .fontWeight(FontWeight.Bold)
                    .color(NeoColor.backgroundPrimary)
                    .textDecorationLine(TextDecorationLine.None)
            )
        }
    }
}

@Composable
internal fun AppIcon(iconUrl: String?, size: Int) {
    val resolved = resolvePublicUrl(iconUrl)
    Box(
        modifier = Modifier
            .size(size.px)
            .border(2.px, LineStyle.Solid, RetroColor.navy)
            .backgroundColor(RetroColor.navy.copy(alpha = 200))
            .borderRadius(6.px)
            .overflow(Overflow.Hidden),
        contentAlignment = Alignment.Center
    ) {
        if (resolved != null) {
            Image(
                src = resolved,
                modifier = Modifier.fillMaxSize().objectFit(ObjectFit.Cover)
            )
        } else {
            SpanText(
                text = "\uD83D\uDCE6",
                modifier = Modifier.fontSize(1.4.cssRem)
            )
        }
    }
}

@Composable
private fun PaginationFooter(currentPage: Int, totalPages: Int, onPageChange: (Int) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(topBottom = 1.5.cssRem).gap(0.5.cssRem),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        RetroPageButton(
            text = "\u00AB Prev",
            enabled = currentPage > 1,
            active = false,
            onClick = { if (currentPage > 1) onPageChange(currentPage - 1) }
        )

        val visiblePages = buildList {
            val rangeStart = (currentPage - 2).coerceAtLeast(1)
            val rangeEnd = (currentPage + 2).coerceAtMost(totalPages)
            if (rangeStart > 1) {
                add(1)
                if (rangeStart > 2) add(-1)
            }
            for (i in rangeStart..rangeEnd) add(i)
            if (rangeEnd < totalPages) {
                if (rangeEnd < totalPages - 1) add(-1)
                add(totalPages)
            }
        }

        visiblePages.forEach { p ->
            if (p == -1) {
                SpanText(
                    text = "\u2026",
                    modifier = Modifier.color(RetroColor.cream).margin(leftRight = 0.4.cssRem).fontSize(1.2.cssRem)
                )
            } else {
                RetroPageButton(
                    text = p.toString(),
                    enabled = true,
                    active = p == currentPage,
                    onClick = { onPageChange(p) }
                )
            }
        }

        RetroPageButton(
            text = "Next \u00BB",
            enabled = currentPage < totalPages,
            active = false,
            onClick = { if (currentPage < totalPages) onPageChange(currentPage + 1) }
        )
    }
}

@Composable
private fun RetroPageButton(text: String, enabled: Boolean, active: Boolean, onClick: () -> Unit) {
    Button(
        attrs = Modifier
            .backgroundColor(
                when {
                    active -> RetroColor.babyBlue
                    enabled -> NeoColor.backgroundPrimaryTransparent
                    else -> NeoColor.backgroundPrimaryTransparent.copy(alpha = 60)
                }
            )
            .border(2.px, LineStyle.Solid, if (active) RetroColor.navy else RetroColor.steelBlue.copy(alpha = 90))
            .borderRadius(4.px)
            .padding(10.px)
            .cursor(if (enabled) Cursor.Pointer else Cursor.NotAllowed)
            .onClick { if (enabled) onClick() }
            .toAttrs { if (!enabled) attr("disabled", "true") }
    ) {
        SpanText(
            text = text,
            modifier = Modifier
                .fontSize(1.1.cssRem)
                .fontWeight(FontWeight.Bold)
                .color(
                    when {
                        active -> NeoColor.backgroundPrimary
                        enabled -> RetroColor.cream
                        else -> NeoColor.white.copy(alpha = 120)
                    }
                )
        )
    }
}

private fun StyleScope.retroFieldStyle() {
    padding(10.px)
    width(100.percent)
    fontFamily("VT323", "monospace")
    fontSize(1.1.cssRem)
    backgroundColor(RetroColor.cream)
    color(RetroColor.navy)
    border(2.px, LineStyle.Solid, RetroColor.navy)
    borderRadius(3.px)
}