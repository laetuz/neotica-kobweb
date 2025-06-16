package id.neotica.neotica.pages.profile

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.css.Overflow
import com.varabyte.kobweb.compose.css.TextAlign
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.data.add
import com.varabyte.kobweb.core.init.InitRoute
import com.varabyte.kobweb.core.init.InitRouteContext
import com.varabyte.kobweb.silk.components.graphics.Image
import com.varabyte.kobweb.silk.components.text.SpanText
import id.neotica.neotica.components.NeoButton
import id.neotica.neotica.components.NeoColor
import id.neotica.neotica.components.layouts.NeoLayoutData
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.percent

@InitRoute() // Match this with your @Page path if you define it explicitly
fun initRyoMartinPage(ctx: InitRouteContext) {
    ctx.data.add(NeoLayoutData("Ryo Martin, Mobile Dev", "")) // Set your desired tab title here
}

@Page
@Composable
//@Layout(".components.layouts.NeoPageLayout")
fun RyoMartinPage() {

    Box(
        modifier = Modifier
            .backgroundColor(NeoColor.backgroundPrimary)
            .fillMaxSize()
            .padding(leftRight = 2.cssRem)
            .overflow(Overflow.Auto),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                src = "/profile/martin_profile.jpg",
                width = 200,
                modifier = Modifier
                    .borderRadius(20.percent)
            )
            SpanText(
                modifier = Modifier
                    .fontWeight(FontWeight.SemiBold)
                    .fontSize(1.cssRem)
                    .textAlign(TextAlign.Center)
                    .fillMaxWidth(),
                text = "Ryo Martin",
            )
            SpanText(
                modifier = Modifier
                    .fontSize(0.8.cssRem),
                text = "Mobile Developer | Android (Kotlin) | iOS (Swift) | Kotlin Multiplatform"
            )
            NeoButton(
                "Github",
                "https://github.com/laetuz",
                "/icons/github-mark.svg",
                Modifier.padding(top = 0.8.cssRem)
            )
            NeoButton(
                "LinkedIn",
                "https://linkedin.com/in/ryo-martin",
                "/icons/linkedin_icon.svg",
                Modifier.padding(top = 0.8.cssRem)
            )
        }
    }
}