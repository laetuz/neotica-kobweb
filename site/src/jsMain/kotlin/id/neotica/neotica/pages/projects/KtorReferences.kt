package id.neotica.neotica.pages

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.css.Overflow
import com.varabyte.kobweb.compose.css.TextDecorationLine
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.data.add
import com.varabyte.kobweb.core.init.InitRoute
import com.varabyte.kobweb.core.init.InitRouteContext
import com.varabyte.kobweb.core.layout.Layout
import com.varabyte.kobweb.silk.components.text.SpanText
import id.neotica.neotica.components.NeoColor
import id.neotica.neotica.components.layouts.NeoLayoutData
import org.jetbrains.compose.web.css.LineStyle
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.A
import org.jetbrains.compose.web.dom.H1
import org.jetbrains.compose.web.dom.H2
import org.jetbrains.compose.web.dom.Hr
import org.jetbrains.compose.web.dom.Text

@InitRoute()
fun initKtorReferencesPage(ctx: InitRouteContext) {
    ctx.data.add(NeoLayoutData("Ktor Resources - Ryo Martin", "/ktorreferences")) 
}

@Page(routeOverride = "ktor-resources") // Accessible via neotica.id/ktor-resources
@Composable
@Layout(".components.layouts.NeoPageLayout")
fun KtorReferencesPage() {
    Box(
        modifier = Modifier
            .backgroundColor(NeoColor.backgroundPrimary)
            .fillMaxSize()
            .padding(leftRight = 2.cssRem, topBottom = 3.cssRem)
            .overflow(Overflow.Auto)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .maxWidth(800.px)
                .gap(2.cssRem)
        ) {
            // Header
            Column(modifier = Modifier.gap(0.5.cssRem)) {
                H1(attrs = {
                    style {
                        margin(0.px)
                        fontSize(2.cssRem)
                        color(Colors.Black)
                    }
                }) {
                    Text("References & Resources")
                }
                SpanText(
                    text = "Links and materials from the Ktor Backend presentation by Ryo Martin.",
                    modifier = Modifier.color(Colors.DarkGray).fontSize(1.2.cssRem)
                )
            }

            Hr(attrs = { style { width(100.percent); border(1.px, LineStyle.Solid, Colors.LightGray) } })

            // 1. Articles & Guides
            ResourceSection("Articles & Guides") {
                ResourceItem(
                    title = "Developing Backend with a Mobile Developer Mindset Using Ktor",
                    author = "by Ryo Martin",
                    url = "https://medium.com/@laetuzg/developing-backend-with-a-mobile-developer-mindset-using-ktor-52a07997fb20"
                )
                ResourceItem(
                    title = "Ktor Backend for Android Developers: A Guide to Your First Backend API & Routing",
                    author = "by Ryo Martin",
                    url = "https://medium.com/@laetuzg/ktor-backend-for-android-developers-a-guide-to-your-first-backend-api-routing-c85df2c56f91"
                )
            }

            // 2. Recommended Videos
            ResourceSection("Recommended Videos") {
                ResourceItem(
                    title = "Ktor Backend for Android Developers: Routing Fundamental",
                    author = "by Martin Kotlin",
                    url = "https://www.youtube.com/watch?v=nEKgpBNhdKk"
                )
                ResourceItem(
                    title = "Opinionated Ktor Services",
                    author = "by Simon Vergauwen",
                    url = "https://www.youtube.com/watch?v=JOZFZ__3M7Q"
                )
            }

            // 3. GitHub Examples
            ResourceSection("Ktor Client Architecture Examples (GitHub)") {
                ResourceItem(
                    title = "PeopleInSpace (Direct API)",
                    url = "https://github.com/joreilly/PeopleInSpace"
                )
                ResourceItem(
                    title = "KtorClientAndroid (Retrofit Mimic)",
                    url = "https://github.com/philipplackner/KtorClientAndroid"
                )
                ResourceItem(
                    title = "KaMPKit (Enterprise Safe Calls)",
                    url = "https://github.com/touchlab/KaMPKit"
                )
            }

            // 4. Starter Project
            ResourceSection("Starter Project & Benchmarks") {
                ResourceItem(
                    title = "Neotica Ktor Starter",
                    url = "https://github.com/laetuz/neotica-ktor-starter"
                )
                ResourceItem(
                    title = "TechEmpower Web Framework Benchmarks",
                    url = "https://www.techempower.com/benchmarks"
                )
            }
        }
    }
}

// --- Helper Composables ---

@Composable
fun ResourceSection(title: String, content: @Composable () -> Unit) {
    Column(modifier = Modifier.gap(1.cssRem).fillMaxWidth()) {
        H2(attrs = {
            style {
                margin(0.px)
                fontSize(1.4.cssRem)
                color(Colors.Black)
            }
        }) {
            Text(title)
        }
        Column(modifier = Modifier.gap(1.2.cssRem).fillMaxWidth()) {
            content()
        }
    }
}

@Composable
fun ResourceItem(title: String, author: String? = null, url: String) {
    Column(modifier = Modifier.gap(0.2.cssRem)) {
        A(
            href = url,
            attrs = {
                style {
                    color(Colors.Black)
                    textDecoration("underline")
                    fontWeight("bold")
                    fontSize(1.1.cssRem)
                }
            }
        ) {
            Text(title)
        }
        
        if (author != null) {
            SpanText(
                text = author,
                modifier = Modifier.color(Colors.DarkGray).fontSize(0.9.cssRem)
            )
        }
        
        A(
            href = url,
            attrs = {
                style {
                    color(Colors.Gray)
                    fontSize(0.85.cssRem)
                    textDecoration("none")
                }
            }
        ) {
            Text(url)
        }
    }
}
