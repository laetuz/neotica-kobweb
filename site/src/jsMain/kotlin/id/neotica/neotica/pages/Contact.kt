package id.neotica.neotica.pages

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.foundation.layout.Arrangement
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.data.add
import com.varabyte.kobweb.core.init.InitRoute
import com.varabyte.kobweb.core.init.InitRouteContext
import com.varabyte.kobweb.core.layout.Layout
import com.varabyte.kobweb.silk.components.layout.SimpleGrid
import com.varabyte.kobweb.silk.components.layout.numColumns
import com.varabyte.kobweb.silk.components.navigation.Link
import com.varabyte.kobweb.silk.components.text.SpanText
import id.neotica.neotica.components.button.NeoButtonLink
import id.neotica.neotica.components.NeoColor
import id.neotica.neotica.components.icons.NeoIcons
import id.neotica.neotica.components.layouts.NeoLayoutData
import id.neotica.neotica.components.others.NeoText
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
    Box(
        modifier = Modifier
            .backgroundColor(NeoColor.backgroundPrimary)
            .fillMaxSize()
            .padding(2.cssRem),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .maxWidth(900.px)
                .fillMaxWidth()
                .backgroundColor(NeoColor.colorPrimary20)
                .borderRadius(1.5.cssRem)
                .padding(3.cssRem)
        ) {
            SimpleGrid(
                numColumns = numColumns(base = 1, md = 2),
                modifier = Modifier.fillMaxWidth().gap(2.cssRem)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.Center
                ) {
                    NeoText(
                        text = "Get in Touch",
                        modifier = Modifier
                            .fontSize(2.5.cssRem)
                            .fontWeight(FontWeight.Bold)
                            .margin(bottom = 1.cssRem)
                            .lineHeight(1.2)
                    )

                    NeoText(
                        text = "We're here to help. Reach out to us for any inquiries or collaborations.",
                        modifier = Modifier
                            .margin(bottom = 2.cssRem)
                            .opacity(0.8)
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(1.5.cssRem),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        StyledSocialLink("LinkedIn", "https://www.linkedin.com/company/neotica")
                        StyledSocialLink("Instagram", "https://instagram.com/neotica.id")
                    }
                }

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    NeoButtonLink(
                        "Chat with us on Whatsapp!",
                        "https://wa.me/6289512971217",
                        NeoIcons.WHATSAPP
                    )
                }
            }
        }
    }
}

@Composable
fun StyledSocialLink(text: String, url: String) {
    Link(
        path = url,
        modifier = Modifier
            .color(Colors.White)
            .fontWeight(FontWeight.SemiBold)
//            .textDecorationLine(com.varabyte.kobweb.compose.css.TextDecorationLine.None)
            .opacity(0.7)
//            .transition(com.varabyte.kobweb.compose.css.CSSTransition("opacity", 0.2.s))
//            .onHover { opacity(1) }
    ) {
        SpanText(text)
    }
}