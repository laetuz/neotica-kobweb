package id.neotica.neotica.pages

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.Overflow
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.data.add
import com.varabyte.kobweb.core.init.InitRoute
import com.varabyte.kobweb.core.init.InitRouteContext
import com.varabyte.kobweb.core.layout.Layout
import com.varabyte.kobweb.silk.components.navigation.Link
import com.varabyte.kobweb.silk.components.text.SpanText
import id.neotica.neotica.components.NeoColor
import id.neotica.neotica.components.layouts.NeoLayoutData
import org.jetbrains.compose.web.css.cssRem

@InitRoute() // Match this with your @Page path if you define it explicitly
fun initContactPage(ctx: InitRouteContext) {
    ctx.data.add(NeoLayoutData("Contact Us - Neotica.id", "/contact")) // Set your desired tab title here
}

@Page
@Composable
@Layout(".components.layouts.NeoPageLayout")
fun ContactPage() {

    Box(
        modifier = Modifier
            .backgroundColor(NeoColor.backgroundPrimary)
            .fillMaxSize()
            .padding(leftRight = 2.cssRem)
            .overflow(Overflow.Auto)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Link("https://www.linkedin.com/company/neotica") {
                SpanText("LinkedIn")
            }
            Link("https://instagram.com/neotica.id") {
                SpanText("Instagram")
            }
        }
    }
}