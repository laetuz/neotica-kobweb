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
import com.varabyte.kobweb.silk.components.layout.SimpleGrid
import com.varabyte.kobweb.silk.components.layout.numColumns
import com.varabyte.kobweb.silk.components.navigation.Link
import com.varabyte.kobweb.silk.components.text.SpanText
import id.neotica.neotica.components.NeoColor
import id.neotica.neotica.components.icons.NeoIcons
import id.neotica.neotica.components.layouts.NeoLayoutData
import org.jetbrains.compose.web.css.AlignItems
import org.jetbrains.compose.web.css.FlexDirection
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.percent

@InitRoute() // Match this with your @Page path if you define it explicitly
fun initProjectsPage(ctx: InitRouteContext) {
    ctx.data.add(NeoLayoutData("Projects Portfolio - Neotica.id", "/projects")) // Set your desired tab title here
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
                text = "Our Portfolio",
                modifier = Modifier
                    .fontSize(2.cssRem)
            )
        }

        SimpleGrid(
            numColumns = numColumns(1, 3),
            modifier = Modifier
                .fillMaxWidth()
                .gap(1.cssRem)
        ) {
            ProjectRow()
        }
    }
}

@Composable
private fun ProjectRow() {
    ProjectCard(
        name = "Neoverse",
        image = NeoIcons.NEOVERSE,
        desc = "An RPG inspired social media.",
        techStacks = listOf(
            NeoIcons.KOTLIN,
            NeoIcons.ANDROID,
            NeoIcons.COMPOSE,
            NeoIcons.KOIN,
            NeoIcons.FIRESTORE,
        )
    ) {
        Link("https://play.google.com/store/apps/details?id=id.neotica.neoverse") {
            SpanText("Play store")
        }

    }
    ProjectCard(
        name = "Alexandria",
        image = NeoIcons.ALEXANDRIA,
        desc = "A vast community-driven reading and writing platform.",
        techStacks = listOf(
            NeoIcons.KOTLIN,
            NeoIcons.ANDROID,
            NeoIcons.APPLE,
            NeoIcons.CMP,
            NeoIcons.KOIN,
            NeoIcons.NGINX,
            NeoIcons.KTOR,
            NeoIcons.POSTGRESQL,
            NeoIcons.DOCKER,
            NeoIcons.EC2,
            )
    ) {
        Link("https://alexandria.neotica.id") {
            SpanText("Website")
        }
    }
    ProjectCard(
        name = "Droidcore",
        image = NeoIcons.CMP,
        imageSize = 120,
        imageModifier = Modifier.padding(topBottom = 1.cssRem),
        desc = "A Compose Multiplatform library.",
        techStacks = listOf(
            NeoIcons.KOTLIN,
            NeoIcons.MAVENCENTRAL,
            NeoIcons.CMP,
        )
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

    ProjectCard(
        name = "KotSpam",
        image = NeoIcons.KOTSPAM,
        imageSize = 120,
        imageModifier = Modifier.padding(topBottom = 1.cssRem),
        desc = "KotSpam is a Kotlin-based tool designed to simulate automated message for spamming.",
        techStacks = listOf(
            NeoIcons.KOTLIN,
            NeoIcons.JAVA
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(0.8.cssRem)
        ) {
            Link("https://github.com/laetuz/KotSpam") {
                SpanText("Github")
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
    techStacks: List<String>,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .borderRadius(20.percent)
            .backgroundColor(NeoColor.colorPrimary20)
            .fillMaxSize()
            .padding(2.cssRem)
//            .width(16.cssRem)
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
        SimpleGrid(
            numColumns = numColumns(5, 5),
            modifier = Modifier.alignItems(AlignItems.Center)
        ) {
            techStacks.forEach {
                Image(
                    src = it,
                    width = 30,
                    modifier = Modifier
                )
            }
        }
    }
}