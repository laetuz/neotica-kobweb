package id.neotica.alexandria.pages

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
import com.varabyte.kobweb.silk.components.text.SpanText
import id.neotica.alexandria.components.NeoColor
import id.neotica.alexandria.components.layouts.NeoLayoutData
import id.neotica.alexandria.utils.aboutDesc
import org.jetbrains.compose.web.css.cssRem

@InitRoute() // Match this with your @Page path if you define it explicitly
fun initAboutPage(ctx: InitRouteContext) {
    ctx.data.add(NeoLayoutData("About Us - Neotica.id", "/about")) // Set your desired tab title here
}

@Page
@Composable
@Layout(".components.layouts.NeoPageLayout")
fun AboutPage() {

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
            SpanText(aboutDesc)
        }
    }
}