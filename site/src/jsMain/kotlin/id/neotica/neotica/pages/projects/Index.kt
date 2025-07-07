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
import com.varabyte.kobweb.silk.style.toModifier
import id.neotica.neotica.components.NeoColor
import id.neotica.neotica.components.icons.NeoIcons
import id.neotica.neotica.components.layouts.NeoLayoutData
import id.neotica.neotica.components.modifiers.BackgroundHoverStyle
import org.jetbrains.compose.web.css.AlignItems
import org.jetbrains.compose.web.css.FlexDirection
import org.jetbrains.compose.web.css.cssRem

@InitRoute()
fun initProjectsPage(ctx: InitRouteContext) {
    ctx.data.add(NeoLayoutData("Projects Portfolio - Neotica.id", "/projects")) // Set your desired tab title here
}

@Page
@Composable
@Layout(".components.layouts.NeoPageLayout")
fun ProjectsPage() {

    Column(
        modifier = Modifier
            .backgroundColor(NeoColor.backgroundPrimary)
            .fillMaxSize()
            .padding(leftRight = 2.cssRem)
            .overflow(Overflow.Auto)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SpanText(
                text = "Our Portfolio",
                modifier = Modifier
                    .fontSize(2.cssRem)
                    .fontWeight(FontWeight.Bold),
            )
            SpanText(
                text = "A showcase of our projects and experiments.",
                modifier = Modifier
                    .fontSize(1.2.cssRem)
                    .fontStyle(FontStyle.Italic)
                    .textAlign(TextAlign.Center)
            )
        }

        SimpleGrid(
            numColumns = numColumns(1, md = 2, lg = 3, xl = 4),
            modifier = Modifier
                .fillMaxWidth()
                .padding(topBottom = 1.cssRem)
                .gap(1.cssRem)
        ) {
            ProjectCard(
                name = "Neoverse",
                image = NeoIcons.NEOVERSE,
                desc = "An RPG inspired social media.",
                techStacks = listOf(
                    NeoIcons.KOTLIN,
                    NeoIcons.COMPOSE,
                    NeoIcons.KOIN,
                    NeoIcons.FIRESTORE,
                ),
                platforms = listOf(
                    NeoIcons.ANDROID
                ),
                download = listOf(
                    UrlList(
                        "Play store",
                        "https://play.google.com/store/apps/details?id=id.neotica.neoverse"
                    )
                )
            )
            ProjectCard(
                name = "Alexandria",
                image = NeoIcons.ALEXANDRIA,
                desc = "A vast community-driven reading and writing platform.",
                techStacks = listOf(
                    NeoIcons.KOTLIN,
                    NeoIcons.CMP,
                    NeoIcons.KOIN,
                    NeoIcons.NGINX,
                    NeoIcons.KTOR,
                    NeoIcons.POSTGRESQL,
                    NeoIcons.DOCKER,
                    NeoIcons.EC2,
                ),
                platforms = listOf(
                    NeoIcons.ANDROID,
                    NeoIcons.IOS
                ),
                download = listOf(
                    UrlList(
                        "Website",
                        "https://alexandria.neotica.id"
                    )
                )
            )
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
                ),
                platforms = listOf(
                    NeoIcons.ANDROID,
                    NeoIcons.IOS
                ),
                download = listOf(
                    UrlList(
                        "Github",
                        "https://github.com/laetuz/Droidcore"
                    ),
                    UrlList(
                        "Maven",
                        "https://mvnrepository.com/artifact/id.neotica/droidcore"
                    ),
                )
            )
            ProjectCard(
                name = "ModernADB",
                image = NeoIcons.MODERNADB,
                imageSize = 120,
                imageModifier = Modifier.padding(topBottom = 1.cssRem),
                desc = "A modern GUI ADB application for desktop.",
                techStacks = listOf(
                    NeoIcons.KOTLIN,
                    NeoIcons.CMP
                ),
                platforms = listOf(
                    NeoIcons.WINDOWS,
                    NeoIcons.LINUX,
                    NeoIcons.MACOS,
                ),
                download = listOf(
                    UrlList(
                        "Download",
                        "https://github.com/Neotica/ModernADB/releases"
                    ),
                    UrlList(
                        "Github",
                        "https://github.com/Neotica/ModernADB"
                    )
                )
            )
            ProjectCard(
                name = "KotSpam",
                image = NeoIcons.KOTSPAM,
                imageSize = 120,
                imageModifier = Modifier.padding(topBottom = 1.cssRem),
                desc = "KotSpam is a Kotlin-based tool designed to simulate automated message for spamming.",
                download = listOf(
                    UrlList(
                        "Download",
                        "https://github.com/laetuz/KotSpam/releases"
                    ),
                    UrlList(
                        "Github",
                        "https://github.com/laetuz/KotSpam"
                    )
                ),
                techStacks = listOf(
                    NeoIcons.KOTLIN,
                ),
                platforms = listOf(
                    NeoIcons.JAVA
                )
            )
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
    platforms: List<String>,
    download: List<UrlList>? = null,
    content: (@Composable () -> Unit)? = null
) {
    Column(
        modifier = Modifier
            .backgroundColor(NeoColor.colorPrimary20)
            .borderRadius(1.cssRem)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(2.cssRem)
                .flexDirection(FlexDirection.Column)
                .gap(1.cssRem),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                src = image,
                width = imageSize,
                modifier = Modifier
                    .borderRadius(1.cssRem)
                    .then(imageModifier ?: Modifier)
            )
            SpanText(
                text = name,
                modifier = Modifier
                    .fontWeight(FontWeight.Bold)
                    .textAlign(TextAlign.Center)
                    .fontSize(1.5.cssRem)
            )
            SpanText(
                text = desc,
                modifier = Modifier
                    .fontSize(1.2.cssRem)
                    .fontStyle(FontStyle.Italic)
                    .fontWeight(FontWeight.Light)
                    .textAlign(TextAlign.Center)
            )
            SpanText(
                text = "Tech stacks:",
                modifier = Modifier
                    .padding(top = 1.cssRem)
                    .fontWeight(FontWeight.Bold)
            )
            if (techStacks.size < 2) {
                Row {
                    techStacks.forEach {
                        Image(
                            src = it,
                            width = 30,
                            modifier = Modifier
                        )
                    }
                }

            } else {
                SimpleGrid(
                    numColumns = numColumns(5, sm = 7, md = 7, lg = 5, xl = 7), //numColumns(5, 5),
                    modifier = Modifier.alignItems(AlignItems.Center).gap(0.5.cssRem)
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

            content?.invoke()

        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(leftRight = 1.cssRem)
        ) {

            SpanText(
                text = "Platforms:",
                modifier = Modifier
                    .padding(top = 1.cssRem)
                    .fontWeight(FontWeight.Bold)
            )
            SimpleGrid(
                numColumns = numColumns(5, 5),
                modifier = Modifier.alignItems(AlignItems.Center).gap(0.5.cssRem)
            ) {
                platforms.forEach {
                    val platformIconHeight: Int = when (it) {
                        NeoIcons.IOS, NeoIcons.MACOS, NeoIcons.JAVA -> 40
                        else -> 30
                    }
                    Image(
                        src = it,
                        height = platformIconHeight,
                        modifier = Modifier
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .borderRadius(bottomLeft = 1.cssRem, bottomRight = 1.cssRem)
                .fillMaxWidth()
                .gap(1.cssRem),
            horizontalArrangement = Arrangement.Center
        ) {
            download?.let {
                if (it.size < 2) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(1.cssRem),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Link(
                            path = it.first().url,
                            modifier = BackgroundHoverStyle.toModifier()
                                .weight(1f)
                                .padding(1.cssRem)
                                .borderRadius(1.cssRem),
                        ) {
                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                SpanText(
                                    text = it.first().title,
                                )
                            }

                        }
                    }
                } else {
                    SimpleGrid(
                        numColumns = numColumns(1, sm = 2, md = 2, lg = 2, xl = 2),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        download.forEach { item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                                    .padding(1.cssRem),
                            ) {
                                Link(
                                    path = item.url,
                                    modifier = BackgroundHoverStyle.toModifier()
                                        .weight(1f)
                                        .padding(1.cssRem)
                                        .borderRadius(1.cssRem),
                                ) {
                                    Row(
                                        Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        SpanText(
                                            text = item.title,
                                        )
                                    }

                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private data class UrlList(
    val title: String,
    val url: String
)