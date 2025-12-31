package id.neotica.neotica.pages

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.*
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
import com.varabyte.kobweb.framework.annotations.DelicateApi
import com.varabyte.kobweb.silk.components.graphics.Image
import com.varabyte.kobweb.silk.components.navigation.Link
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.base
import com.varabyte.kobweb.silk.style.breakpoint.Breakpoint
import com.varabyte.kobweb.silk.style.toModifier
import com.varabyte.kobweb.silk.theme.breakpoint.rememberBreakpoint
import id.neotica.neotica.components.NeoColor
import id.neotica.neotica.components.icons.NeoIcons
import id.neotica.neotica.components.layouts.NeoLayoutData
import id.neotica.neotica.components.modifiers.BackgroundHoverStyle
import id.neotica.neotica.components.others.NeoText
import id.neotica.neotica.domain.model.TechStacks
import id.neotica.neotica.utils.homeDesc
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.vh

// Container that has a tagline and grid on desktop, and just the tagline on mobile
val HeroContainerStyle = CssStyle {
    base { Modifier.fillMaxWidth().gap(2.cssRem) }
    Breakpoint.MD { Modifier.margin { top(20.vh) } }
}

// A demo grid that appears on the homepage because it looks good
val HomeGridStyle = CssStyle.base {
    Modifier
        .gap(0.5.cssRem)
        .width(70.cssRem)
        .height(18.cssRem)
}

private val GridCellColorVar by StyleVariable<Color>()
val HomeGridCellStyle = CssStyle.base {
    Modifier
        .backgroundColor(GridCellColorVar.value())
        .boxShadow(blurRadius = 0.6.cssRem, color = GridCellColorVar.value())
        .borderRadius(1.cssRem)
}


@InitRoute
fun initHomePage(ctx: InitRouteContext) {
    ctx.data.add(NeoLayoutData("Home"))
}

@OptIn(DelicateApi::class)
@Page
@Layout(".components.layouts.NeoPageLayout")
@Composable
fun HomePage() {
    val breakpoint = rememberBreakpoint()
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
                .fillMaxWidth()
                .fontFamily("IBM Plex Mono")
                .gap(2.cssRem),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val imageWidth = when (breakpoint) {
                Breakpoint.ZERO -> 400
                Breakpoint.SM -> 500
                Breakpoint.MD -> 500
                else -> 700
            }


            Image(
                src = "/main_banner.png",
                width = imageWidth
            )
            NeoText(
                text = homeDesc,
                modifier = Modifier.fontSize(1.2.cssRem).lineHeight(1.5).textAlign(textAlign = TextAlign.Center)
            )
            Link(
                path = "/projects",
                modifier = BackgroundHoverStyle.toModifier()
                    .padding(1.cssRem)
                    .borderRadius(1.cssRem)
            ) {
                NeoText(
                    text = "> View Projects",
                    modifier = Modifier.fontSize(1.2.cssRem).textDecorationLine(TextDecorationLine.None)
                )
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val stacks = listOf(
                    TechStacks(
                        title = "Java",
                        image = NeoIcons.JAVA,
                    ),
                    TechStacks(
                        title = "Kotlin",
                        image = NeoIcons.KOTLIN,
                    ),
                    TechStacks(
                        title = "Compose",
                        image = NeoIcons.COMPOSE,
                    ),
                    TechStacks(
                        title = "Ktor",
                        image = NeoIcons.KTOR,
                    ),
                    TechStacks(
                        title = "Swift",
                        image = NeoIcons.SWIFT,
                    )
                )
                NeoText(
                    text = "Our Core Technologies",
                    modifier = Modifier
                        .color(NeoColor.white)
                        .fontSize(1.5.cssRem)
                        .fontWeight(FontWeight.Bold)
                )
                Row(
                    modifier = Modifier
                        .gap(1.cssRem)
                        .padding(bottom = 1.cssRem),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    stacks.forEach {
                        Column(
                            modifier = Modifier,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Image(src = it.image, width = 50)
                            NeoText(
                                text = it.title,
                                modifier = Modifier.fontWeight(FontWeight.Bold)
                            )
                        }
                    }
                }
            }
        }
    }
}