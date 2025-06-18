package id.neotica.neotica.pages.projects

import androidx.compose.runtime.Composable
import androidx.compose.runtime.currentComposer
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.varabyte.kobweb.compose.css.FontStyle
import com.varabyte.kobweb.compose.css.Overflow
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
import com.varabyte.kobweb.silk.components.navigation.Link
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.style.breakpoint.Breakpoint
import com.varabyte.kobweb.silk.style.breakpoint.displayIfAtLeast
import com.varabyte.kobweb.silk.style.breakpoint.displayUntil
import id.neotica.neotica.components.NeoColor
import id.neotica.neotica.components.layouts.NeoLayoutData
import id.neotica.neotica.components.sections.header.NeoSideMenu
import id.neotica.neotica.components.sections.header.SideMenuState
import id.neotica.neotica.utils.aboutDesc
import org.jetbrains.compose.web.css.FlexDirection
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.percent

@InitRoute() // Match this with your @Page path if you define it explicitly
fun initProjectsPage(ctx: InitRouteContext) {
    ctx.data.add(NeoLayoutData("Projects - Neotica.id", "/projects")) // Set your desired tab title here
}

@Page
@Composable
@Layout(".components.layouts.NeoPageLayout")
fun ProjectsPage() {

    Column (
        modifier = Modifier
            .backgroundColor(NeoColor.backgroundPrimary)
            .fillMaxSize()
            .padding(leftRight = 2.cssRem)
            .overflow(Overflow.Auto)
    ) {
        Row(
            Modifier
                .fontSize(1.5.cssRem)
                .gap(1.cssRem),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SpanText(
                text = "Projects",
                modifier = Modifier
                    .fontSize(2.cssRem)
            )
        }

        Row (
            Modifier.displayIfAtLeast(Breakpoint.MD),
            horizontalArrangement = Arrangement.spacedBy(1.cssRem),
        ) {
            ProjectRow()
        }

        Column(
            Modifier.displayUntil(Breakpoint.MD).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(1.cssRem),
            horizontalAlignment = Alignment.CenterHorizontally
        ) { ProjectRow() }

    }
}

@Composable
private fun ProjectRow() {
    Column(
        modifier = Modifier
            .borderRadius(20.percent)
            .backgroundColor(NeoColor.colorPrimary20)
            .fillMaxSize()
            .padding(2.cssRem)
            .width(16.cssRem)
            .flexDirection(FlexDirection.Column),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            src = "/projects/neoverse-logo.png",
            width = 180,
            modifier = Modifier
                .borderRadius(20.percent)
        )
        SpanText(
            text = "Neoverse"
        )
        SpanText(
            text = "An RPG inspired social media.",
            modifier = Modifier
                .fontSize(0.8.cssRem)
                .fontStyle(FontStyle.Italic)
        )
        Link("https://play.google.com/store/apps/details?id=id.neotica.neoverse") {
            SpanText("Play store")
        }
    }

    Column(
        modifier = Modifier
            .borderRadius(20.percent)
            .backgroundColor(NeoColor.colorPrimary20)
            .padding(2.cssRem)
            .width(16.cssRem),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            src = "/projects/alexandria-logo.png",
            width = 180,
            modifier = Modifier
                .borderRadius(20.percent)
        )
        SpanText(
            text = "Alexandria"
        )
        SpanText(
            text = "A vast community-driven reading and writing platform.",
            modifier = Modifier
                .fontSize(0.8.cssRem)
                .fontStyle(FontStyle.Italic)
        )
        Link("https://alexandria.neotica.id") {
            SpanText("Website")
        }
    }

    Column(
        modifier = Modifier
            .borderRadius(20.percent)
            .backgroundColor(NeoColor.colorPrimary20)
            .padding(2.cssRem)
            .width(16.cssRem),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            src = "/projects/compose-multiplatform-logo.svg",
            width = 180,
            modifier = Modifier
                .borderRadius(20.percent)
        )
        SpanText(
            text = "Droidcore"
        )
        SpanText(
            text = "A Compose Multiplatform library.",
            modifier = Modifier
                .fontSize(0.8.cssRem)
                .fontStyle(FontStyle.Italic)
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(0.8.cssRem)
        ) {
            Link("https://github.com/laetuz/Droidcore") {
                SpanText("Github")
            }
            Link("https://mvnrepository.com/artifact/id.neotica/droidcore") {
                SpanText("Maven")
            }
        }

    }
}