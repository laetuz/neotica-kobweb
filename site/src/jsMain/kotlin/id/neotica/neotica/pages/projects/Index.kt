package id.neotica.neotica.pages.projects

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.FontStyle
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.css.Overflow
import com.varabyte.kobweb.compose.css.TextAlign
import com.varabyte.kobweb.compose.foundation.layout.Arrangement
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
    ProjectCard(
        name = "Neoverse",
        image = "/projects/neoverse-logo.png",
        desc = "An RPG inspired social media.",
        techStacks = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                val neoverseStacks = listOf(
                    "/projects/stacks/android-head_3D.png",
                    "/projects/stacks/jetpack_compose_icon.png",
                    "/projects/stacks/koin_logo.png",
                    "/projects/stacks/firestore_logo.svg",
                )

                neoverseStacks.forEach {
                    Image(
                        src = it,
                        width = 30,
                        modifier = Modifier
                    )
                }
            }
        }
    ) {
        Link("https://play.google.com/store/apps/details?id=id.neotica.neoverse") {
            SpanText("Play store")
        }


//        SimpleGrid(
//            numColumns = numColumns(2, 2),
//        ){
//            SpanText("Kotlin")
//            SpanText("Kotlin Multiplatform Compose")
//            SpanText("Firebase")
//        }PostgreSQL_logo.3colors.120x120.png
    }
    ProjectCard(
        name = "Alexandria",
        image = "/projects/alexandria-logo.png",
        desc = "A vast community-driven reading and writing platform.",
        techStacks = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                val alexandriaStacks = listOf(
                    "/projects/stacks/android-head_3D.png",
                    "/projects/stacks/apple_logo_black.svg",
                    "/projects/compose-multiplatform-logo.svg",
                    "/projects/stacks/koin_logo.png",
                )

                alexandriaStacks.forEach {
                    Image(
                        src = it,
                        width = 30,
                        modifier = Modifier
                    )
                }
            }

            Row {
                val alexandriaStacks2 = listOf(
                    "/projects/stacks/nginx-icon.svg",
                    "/projects/stacks/ktor_logo.svg",
                    "/projects/stacks/postgresql_logo.png",
                    "/projects/stacks/docker-logo.svg",
                )

                alexandriaStacks2.forEach {
                    Image(
                        src = it,
                        width = 30,
                        modifier = Modifier
                    )
                }
            }
        }
    ) {
        Link("https://alexandria.neotica.id") {
            SpanText("Website")
        }
    }
    ProjectCard(
        name = "Droidcore",
        image = "/projects/compose-multiplatform-logo.svg",
        imageSize = 120,
        imageModifier = Modifier.padding(topBottom = 1.cssRem),
        desc = "A Compose Multiplatform library.",
        techStacks = {
            Row {
                val alexandriaStacks = listOf(
                    "/projects/stacks/maven-central-icon.png",
                    "/projects/compose-multiplatform-logo.svg",
                )

                alexandriaStacks.forEach {
                    Image(
                        src = it,
                        width = 30,
                        modifier = Modifier
                    )
                }
            }
        }
    ) {
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

@Composable
private fun ProjectCard(
    name: String,
    image: String,
    imageSize: Int? = 180,
    imageModifier: Modifier? = Modifier,
    desc: String,
    techStacks: @Composable () -> Unit,
    content: @Composable () -> Unit
) {
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
            src = image,
            width = imageSize,
            modifier = Modifier
                .borderRadius(20.percent)
                .then(imageModifier ?: Modifier)
        )
        SpanText(
            text = name,
            modifier = Modifier
                .fontWeight(FontWeight.Bold)
                .textAlign(TextAlign.Center)
        )
        SpanText(
            text = desc,
            modifier = Modifier
                .fontSize(0.8.cssRem)
                .fontStyle(FontStyle.Italic)
                .textAlign(TextAlign.Center)
        )
        content()
        SpanText(
            text = "Tech stacks:",
            modifier = Modifier
                .padding(top = 1.cssRem)
                .fontSize(0.9.cssRem)
        )
        techStacks()
    }
}