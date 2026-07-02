package id.neotica.neotica.pages.holomarket

import androidx.compose.runtime.Composable
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
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.base
import com.varabyte.kobweb.silk.style.toModifier
import id.neotica.neotica.components.NeoColor
import id.neotica.neotica.components.layouts.NeoLayoutData
import id.neotica.neotica.components.others.NeoText
import id.neotica.neotica.domain.dummy.AppProductList
import id.neotica.neotica.domain.model.AppProduct
import id.neotica.neotica.domain.model.AppVersion
import org.jetbrains.compose.web.css.LineStyle
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.px

val SectionLabelStyle = CssStyle.base {
    Modifier
        .color(NeoColor.colorPrimary)
        .fontSize(0.9.cssRem)
        .fontWeight(FontWeight.Bold)
}

@Composable
private fun HoloDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.px)
            .backgroundColor(NeoColor.colorPrimary.copy(alpha = 30))
            .margin(topBottom = 1.cssRem)
    )
}

@InitRoute
fun initProductDetailPage(ctx: InitRouteContext) {
    ctx.data.add(NeoLayoutData("HoloMarket \u00B7 Product Detail"))
}

@Page
@Layout(".components.layouts.NeoPageLayout")
@Composable
fun HoloMarketProductDetailPage() {
    val product = AppProductList.products.first()

    Column(
        modifier = Modifier
            .backgroundColor(NeoColor.backgroundPrimary)
            .fillMaxSize()
            .padding(leftRight = 2.cssRem, topBottom = 2.cssRem)
            .overflow(Overflow.Auto)
            .gap(0.5.cssRem)
    ) {
        ActionBar(product.title)

        HoloDivider()

        AppHeader(product)

        HoloDivider()

        MetadataBar(product)

        HoloDivider()

        DescriptionSection(product.description)

        HoloDivider()

        DownloadButton()

        HoloDivider()

        VersionHistory(product.versionHistory)

        HoloDivider()

        ScreenshotsSection(product.screenshots)
    }
}

@Composable
private fun ActionBar(title: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SpanText(
            text = "HoloMarket",
            modifier = Modifier
                .color(NeoColor.colorPrimary)
                .fontSize(1.cssRem)
                .fontWeight(FontWeight.Bold)
        )
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SpanText(
            text = title,
            modifier = Modifier
                .color(NeoColor.white)
                .fontSize(1.8.cssRem)
                .fontWeight(FontWeight.Bold)
        )
    }
}

@Composable
private fun AppHeader(product: AppProduct) {
    Row(
        modifier = Modifier.fillMaxWidth().gap(1.cssRem),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            src = product.iconUrl,
            modifier = Modifier.size(64.px).borderRadius(12.px)
        )

        Column {
            NeoText(
                text = product.title,
                modifier = Modifier.fontSize(1.5.cssRem).fontWeight(FontWeight.Bold)
            )
            NeoText(
                text = product.developer,
                modifier = Modifier.fontSize(0.9.cssRem).color(NeoColor.colorPrimary)
            )
        }
    }
}

@Composable
private fun MetadataBar(product: AppProduct) {
    Row(
        modifier = Modifier.fillMaxWidth().gap(1.5.cssRem),
        verticalAlignment = Alignment.CenterVertically
    ) {
        StarRating(product.rating)

        Badge(product.category)

        SpanText(
            text = product.size,
            modifier = Modifier.color(NeoColor.white).fontSize(0.85.cssRem)
        )

        SpanText(
            text = product.installCount,
            modifier = Modifier.color(NeoColor.white).fontSize(0.85.cssRem)
        )
    }
}

@Composable
private fun StarRating(rating: Float) {
    val fullStars = rating.toInt()
    val hasHalf = rating - fullStars >= 0.25f
    val stars = buildString {
        repeat(fullStars) { append('\u2605') }
        if (hasHalf) append('\u00BD')
        val empty = 5 - fullStars - (if (hasHalf) 1 else 0)
        repeat(empty) { append('\u2606') }
    }

    Row(verticalAlignment = Alignment.CenterVertically) {
        SpanText(
            text = stars,
            modifier = Modifier
                .color(NeoColor.colorPrimary)
                .fontSize(1.2.cssRem)
        )
        SpanText(
            text = " $rating",
            modifier = Modifier.color(NeoColor.white).fontSize(0.85.cssRem)
        )
    }
}

@Composable
private fun Badge(category: String) {
    Box(
        modifier = Modifier
            .backgroundColor(NeoColor.colorPrimary.copy(alpha = 25))
            .padding(leftRight = 0.6.cssRem, topBottom = 0.2.cssRem)
            .borderRadius(4.px)
            .border(1.px, LineStyle.Solid, NeoColor.colorPrimary.copy(alpha = 60))
    ) {
        SpanText(
            text = category,
            modifier = Modifier
                .color(NeoColor.colorPrimary)
                .fontSize(0.75.cssRem)
                .fontWeight(FontWeight.Bold)
        )
    }
}

@Composable
private fun DescriptionSection(description: String) {
    Column {
        SpanText(
            text = "Description",
            modifier = SectionLabelStyle.toModifier()
        )
        NeoText(
            text = description,
            modifier = Modifier
                .fontSize(0.95.cssRem)
                .lineHeight(1.6)
                .margin(top = 0.5.cssRem)
        )
    }
}

@Composable
private fun DownloadButton() {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .backgroundColor(NeoColor.colorPrimary)
                .cursor(Cursor.Pointer)
                .borderRadius(6.px)
                .padding(topBottom = 0.8.cssRem)
                .onClick { }
        ) {
            SpanText(
                text = "DOWNLOAD",
                modifier = Modifier
                    .color(NeoColor.white)
                    .fontSize(1.1.cssRem)
                    .fontWeight(FontWeight.Bold)
                    .textAlign(TextAlign.Center)
                    .fillMaxWidth()
            )
        }
    }

    SpanText(
        text = "Version ${AppProductList.products.first().versionHistory.first().versionName} \u00B7 ${AppProductList.products.first().size}",
        modifier = Modifier
            .color(NeoColor.white.copy(alpha = 153))
            .fontSize(0.8.cssRem)
            .fillMaxWidth()
            .textAlign(TextAlign.Center)
            .margin(top = 0.3.cssRem)
    )
}

@Composable
private fun VersionHistory(versions: List<AppVersion>) {
    Column {
        SpanText(
            text = "Version History",
            modifier = SectionLabelStyle.toModifier()
        )

        versions.forEachIndexed { index, version ->
            VersionRow(version)

            if (index < versions.size - 1) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.px)
                        .backgroundColor(NeoColor.colorPrimary.copy(alpha = 15))
                        .margin(topBottom = 0.6.cssRem)
                )
            }
        }
    }
}

@Composable
private fun VersionRow(version: AppVersion) {
    Column(
        modifier = Modifier.margin(top = 0.6.cssRem).gap(0.3.cssRem)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            SpanText(
                text = "Version ${version.versionName} (${version.versionCode})",
                modifier = Modifier
                    .color(NeoColor.white)
                    .fontSize(1.cssRem)
                    .fontWeight(FontWeight.Bold)
            )

            Box(
                modifier = Modifier
                    .backgroundColor(NeoColor.colorPrimary)
                    .cursor(Cursor.Pointer)
                    .borderRadius(4.px)
                    .padding(leftRight = 0.6.cssRem, topBottom = 0.2.cssRem)
                    .onClick { }
            ) {
                SpanText(
                    text = "Download",
                    modifier = Modifier
                        .color(NeoColor.white)
                        .fontSize(0.8.cssRem)
                        .fontWeight(FontWeight.Bold)
                )
            }
        }

        SpanText(
            text = "Min SDK: ${version.minSdk}",
            modifier = Modifier
                .color(NeoColor.white.copy(alpha = 128))
                .fontSize(0.8.cssRem)
        )

        if (version.changelog.isNotBlank()) {
            NeoText(
                text = version.changelog,
                modifier = Modifier.fontSize(0.85.cssRem).lineHeight(1.5).margin(top = 0.2.cssRem)
            )
        }
    }
}

@Composable
private fun ScreenshotsSection(screenshots: List<String>) {
    if (screenshots.isEmpty()) return

    Column {
        SpanText(
            text = "Screenshots",
            modifier = SectionLabelStyle.toModifier()
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .overflow(Overflow.Auto)
                .gap(0.8.cssRem)
                .margin(top = 0.5.cssRem, bottom = 0.5.cssRem)
        ) {
            screenshots.forEach { url ->
                Image(
                    src = url,
                    modifier = Modifier
                        .height(200.px)
                        .borderRadius(8.px)
                        .border(1.px, LineStyle.Solid, NeoColor.colorPrimary.copy(alpha = 30))
                )
            }
        }
    }
}
