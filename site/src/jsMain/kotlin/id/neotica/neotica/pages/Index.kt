package id.neotica.neotica.pages

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.BoxShadow
import com.varabyte.kobweb.compose.css.Cursor
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.css.Overflow
import com.varabyte.kobweb.compose.css.TextAlign
import com.varabyte.kobweb.compose.css.TextDecorationLine
import com.varabyte.kobweb.compose.foundation.layout.Arrangement
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
import id.neotica.neotica.components.NeoColor
import id.neotica.neotica.components.icons.NeoIcons
import id.neotica.neotica.components.layouts.NeoLayoutData
import id.neotica.neotica.components.retro.RainbowDivider
import id.neotica.neotica.components.retro.RetroColor
import id.neotica.neotica.components.retro.StarfieldStars
import id.neotica.neotica.domain.model.TechStacks
import id.neotica.neotica.utils.Constants.HOME_DESC
import org.jetbrains.compose.web.css.FlexWrap
import org.jetbrains.compose.web.css.LineStyle
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.px

@InitRoute
fun initHomePage(ctx: InitRouteContext) {
    ctx.data.add(NeoLayoutData("Neotica.id — Mobile Development Studio"))
}

@Page
@Layout(".components.layouts.NeoPageLayout")
@Composable
fun HomePage() {
    Column(
        modifier = Modifier
            .background {
                color(NeoColor.backgroundPrimary)
                image(StarfieldStars)
            }
            .fillMaxSize()
            .padding(leftRight = 1.5.cssRem, topBottom = 1.2.cssRem)
            .overflow(Overflow.Auto)
            .gap(1.5.cssRem)
            .fontFamily("VT323", "monospace"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().gap(0.5.cssRem),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SpanText(
                text = "\u2726  Neotica  \u2726",
                modifier = Modifier
                    .fontSize(3.cssRem)
                    .fontWeight(FontWeight.Bold)
                    .color(RetroColor.babyBlue)
                    .textAlign(TextAlign.Center)
                    .textShadow(3.px, 3.px, color = RetroColor.navy.copy(alpha = 200))
            )
            SpanText(
                text = "Mobile Development Studio",
                modifier = Modifier
                    .fontSize(1.2.cssRem)
                    .color(RetroColor.cyan)
                    .textAlign(TextAlign.Center)
            )
        }

        RainbowDivider()

        SpanText(
            text = HOME_DESC,
            modifier = Modifier
                .fontSize(1.15.cssRem)
                .color(RetroColor.cream)
                .textAlign(TextAlign.Center)
                .lineHeight(1.6)
                .maxWidth(700.px)
        )

        RainbowDivider()

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .gap(1.2.cssRem)
                .flexWrap(FlexWrap.Wrap),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
//            BevelButtonLink(
//                emoji = "\u25B7",
//                label = " View Projects",
//                path = "/projects",
//                background = RetroColor.babyBlue
//            )
            BevelButtonLink(
                emoji = "\uD83C\uDFB5",
                label = " Stream Orpheum",
                path = "/orpheum",
                background = RetroColor.steelBlue
            )
            BevelButtonLink(
                emoji = "\uD83D\uDCF7",
                label = " NeoBooth",
                path = "/photobooth",
                background = RetroColor.skyBlue
            )
            BevelButtonLink(
                emoji = "\uD83E\uDD16",
                label = " HoloMarket",
                path = "/holomarket",
                background = RetroColor.royalBlue
            )
        }

        RainbowDivider()

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .maxWidth(700.px)
                .backgroundColor(NeoColor.backgroundPrimaryTransparent)
                .border(2.px, LineStyle.Solid, RetroColor.steelBlue.copy(alpha = 90))
                .borderRadius(4.px)
                .padding(1.2.cssRem)
                .gap(1.cssRem)
                .boxShadow(2.px, 2.px, color = Color.rgb(0, 0, 0).copy(alpha = 115)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SpanText(
                text = "\u2726  Our Core Technologies  \u2726",
                modifier = Modifier
                    .fontSize(1.6.cssRem)
                    .fontWeight(FontWeight.Bold)
                    .color(RetroColor.skyBlue)
                    .textAlign(TextAlign.Center)
                    .textShadow(2.px, 2.px, color = Color.rgb(0, 0, 0).copy(alpha = 153))
            )

            val stacks = listOf(
                TechStacks(title = "Java", image = NeoIcons.JAVA),
                TechStacks(title = "Kotlin", image = NeoIcons.KOTLIN),
                TechStacks(title = "Compose", image = NeoIcons.COMPOSE),
                TechStacks(title = "Ktor", image = NeoIcons.KTOR),
                TechStacks(title = "Swift", image = NeoIcons.SWIFT),
            )
            Row(
                modifier = Modifier.gap(1.cssRem),
                verticalAlignment = Alignment.CenterVertically
            ) {
                stacks.forEach {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.gap(0.2.cssRem)
                    ) {
                        Image(src = it.image, width = 50)
                        SpanText(
                            text = it.title,
                            modifier = Modifier.fontWeight(FontWeight.Bold).color(RetroColor.babyBlue)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BevelButtonLink(emoji: String, label: String, path: String, background: Color) {
    Link(
        path = path,
        modifier = Modifier
            .backgroundColor(background)
            .border(2.px, LineStyle.Solid, RetroColor.navy)
            .borderRadius(4.px)
            .padding(leftRight = 1.2.cssRem, topBottom = 0.6.cssRem)
            .cursor(Cursor.Pointer)
            .boxShadow(
                BoxShadow.of(0.px, 1.px, 0.px, 1.px, NeoColor.white.copy(alpha = 200), inset = true),
                BoxShadow.of(0.px, (-1).px, 0.px, 1.px, Color.rgb(0, 0, 0).copy(alpha = 120), inset = true),
                BoxShadow.of(3.px, 3.px, 0.px, 0.px, Color.rgb(0, 0, 0).copy(alpha = 127)),
            )
    ) {
        SpanText(
            text = "$emoji$label",
            modifier = Modifier
                .fontSize(1.2.cssRem)
                .fontWeight(FontWeight.Bold)
                .color(NeoColor.backgroundPrimary)
                .textDecorationLine(TextDecorationLine.None)
        )
    }
}