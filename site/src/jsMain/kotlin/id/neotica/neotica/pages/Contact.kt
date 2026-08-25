package id.neotica.neotica.pages

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.BoxShadow
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
import com.varabyte.kobweb.compose.ui.graphics.Color
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.data.add
import com.varabyte.kobweb.core.init.InitRoute
import com.varabyte.kobweb.core.init.InitRouteContext
import com.varabyte.kobweb.core.layout.Layout
import com.varabyte.kobweb.silk.components.graphics.Image
import com.varabyte.kobweb.silk.components.layout.SimpleGrid
import com.varabyte.kobweb.silk.components.layout.numColumns
import com.varabyte.kobweb.silk.components.navigation.Link
import com.varabyte.kobweb.silk.components.text.SpanText
import id.neotica.neotica.components.NeoColor
import id.neotica.neotica.components.icons.NeoIcons
import id.neotica.neotica.components.layouts.NeoLayoutData
import id.neotica.neotica.components.retro.RainbowDivider
import id.neotica.neotica.components.retro.RetroColor
import id.neotica.neotica.components.retro.StarfieldStars
import org.jetbrains.compose.web.css.LineStyle
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.px

@InitRoute()
fun initContactPage(ctx: InitRouteContext) {
    ctx.data.add(NeoLayoutData("Contact Us - Neotica.id", "/contact"))
}

@Page
@Composable
@Layout(".components.layouts.NeoPageLayout")
fun ContactPage() {
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
            modifier = Modifier.fillMaxWidth().gap(0.4.cssRem),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SpanText(
                text = "\u2726  Get in Touch  \u2726",
                modifier = Modifier
                    .fontSize(2.5.cssRem)
                    .fontWeight(FontWeight.Bold)
                    .color(RetroColor.babyBlue)
                    .textAlign(TextAlign.Center)
                    .textShadow(3.px, 3.px, color = RetroColor.navy.copy(alpha = 200))
            )
            SpanText(
                text = "We\u2019re here to help. Reach out to us for any inquiries or collaborations.",
                modifier = Modifier
                    .fontSize(1.15.cssRem)
                    .color(RetroColor.cream)
                    .textAlign(TextAlign.Center)
                    .maxWidth(700.px)
            )
        }

        RainbowDivider()

        Box(
            modifier = Modifier
                .maxWidth(900.px)
                .fillMaxWidth()
                .backgroundColor(NeoColor.backgroundPrimaryTransparent)
                .border(2.px, LineStyle.Solid, RetroColor.steelBlue.copy(alpha = 90))
                .borderRadius(4.px)
                .padding(2.2.cssRem)
                .boxShadow(2.px, 2.px, color = Color.rgb(0, 0, 0).copy(alpha = 115))
        ) {
            SimpleGrid(
                numColumns = numColumns(base = 1, md = 2),
                modifier = Modifier.fillMaxWidth().gap(2.cssRem)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.Center
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(1.5.cssRem),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RetroSocialLink("LinkedIn", "https://www.linkedin.com/company/neotica")
                        RetroSocialLink("Instagram", "https://instagram.com/neotica.id")
                    }
                }

                Column(
                    modifier = Modifier.fillMaxWidth().gap(1.cssRem),
                    verticalArrangement = Arrangement.spacedBy(1.cssRem),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    RetroBevelButtonLink(
                        label = "Chat with us on WhatsApp!",
                        url = "https://wa.me/6289512971217",
                        iconSrc = NeoIcons.WHATSAPP,
                        background = RetroColor.babyBlue
                    )
                    RetroBevelButtonLink(
                        label = "Or email us!",
                        url = "mailto:martin@neotica.id",
                        iconSrc = NeoIcons.EMAIL,
                        background = RetroColor.steelBlue
                    )
                }
            }
        }
    }
}

@Composable
private fun RetroSocialLink(text: String, url: String) {
    Link(
        path = url,
        text = text,
        modifier = Modifier
            .color(RetroColor.skyBlue)
            .fontSize(1.1.cssRem)
            .fontWeight(FontWeight.SemiBold)
            .textDecorationLine(TextDecorationLine.None)
    )
}

@Composable
private fun RetroBevelButtonLink(label: String, url: String, iconSrc: String, background: Color) {
    Link(
        path = url,
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
        Row(
            modifier = Modifier.gap(0.5.cssRem),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                src = iconSrc,
                height = 20
            )
            SpanText(
                text = label,
                modifier = Modifier
                    .fontSize(1.2.cssRem)
                    .fontWeight(FontWeight.Bold)
                    .color(NeoColor.backgroundPrimary)
                    .textDecorationLine(TextDecorationLine.None)
            )
        }
    }
}