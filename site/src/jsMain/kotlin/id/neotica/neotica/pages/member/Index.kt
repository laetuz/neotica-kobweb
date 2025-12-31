package id.neotica.neotica.pages.member

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.css.Overflow
import com.varabyte.kobweb.compose.css.TextAlign
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
import com.varabyte.kobweb.core.rememberPageContext
import com.varabyte.kobweb.silk.components.graphics.Image
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.selectors.hover
import com.varabyte.kobweb.silk.style.selectors.link
import com.varabyte.kobweb.silk.style.selectors.visited
import com.varabyte.kobweb.silk.style.toModifier
import id.neotica.neotica.components.NeoButton
import id.neotica.neotica.components.NeoColor
import id.neotica.neotica.components.layouts.NeoLayoutData
import id.neotica.neotica.domain.dummy.MemberList
import org.jetbrains.compose.web.css.LineStyle
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.px

@InitRoute()
fun initMemberPage(ctx: InitRouteContext) {
    ctx.data.add(NeoLayoutData("Member", ""))
}

@Page
@Composable
@Layout(".components.layouts.NeoPageLayout")
fun MemberPage() {

    val ctx = rememberPageContext()
    val memberList = MemberList().memberList

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
//            Image(
//                src = "/profile/martin_profile.jpg",
//                width = 200,
//                modifier = Modifier
//                    .borderRadius(20.percent)
//            )
            SpanText(
                modifier = Modifier
                    .fontWeight(FontWeight.SemiBold)
                    .fontSize(2.5.cssRem)
                    .textAlign(TextAlign.Center)
                    .color(NeoColor.white)
                    .margin(bottom = 2.cssRem),
                text = "Neotica Team",
            )
            Column {
                memberList.sortedBy { it.joinDate.getTime() }.forEachIndexed { index, data ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(0.5.cssRem),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        SpanText(
                            modifier = HoverText.toModifier()
                                .fontSize(1.cssRem)
                                .onClick {
                                    ctx.router.navigateTo("/member/${data.url}")
                                },
                            text = "${index+1}. ${data.name}, " +
                                    "Joined ${data.joinDate.getDate()}th ${data.joinDate.getMonth().formatMonth()} ${data.joinDate.getFullYear()}."
                        )
                    }
                }
            }
        }
    }
}

@Page("/member/{slug}")
@Composable
@Layout(".components.layouts.NeoPageLayout")
fun MemberDetailPage() {
    val ctx = rememberPageContext()
    val slug = ctx.route.params["slug"]

    val memberList = MemberList().memberList

    val member = memberList.find { it.url == slug }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .backgroundColor(NeoColor.backgroundPrimary)
            .padding(2.cssRem),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().maxWidth(800.px),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (member != null) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .backgroundColor(com.varabyte.kobweb.compose.ui.graphics.Color.rgba(255, 255, 255, 0.05f)) // Glass effect
                        .borderRadius(20.px)
                        .padding(3.cssRem)
                        .border(1.px, LineStyle.Solid, com.varabyte.kobweb.compose.ui.graphics.Color.rgba(255, 255, 255, 0.1f)),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    // Profile image
                    Box(
                        modifier = Modifier
                            .margin(bottom = 2.cssRem)
                            .borderRadius(20.percent)
                            .overflow(Overflow.Hidden)
                            .boxShadow(0.px, 4.px, 20.px, color = com.varabyte.kobweb.compose.ui.graphics.Color.rgba(0, 0,0,1)) // Shadow
                    ) {
                        if (member.imageUrl.isNotEmpty()) {
                            Image(
                                src = member.imageUrl,
                                width = 150,
                                height = 150,
                                modifier = Modifier.objectFit(com.varabyte.kobweb.compose.css.ObjectFit.Cover)
                            )
                        }
                    }

                    SpanText(
                        text = member.name,
                        modifier = Modifier
                            .fontSize(3.cssRem)
                            .fontWeight(FontWeight.Bold)
                            .color(NeoColor.white)
                            .textAlign(TextAlign.Center)
                    )

                    SpanText(
                        text = "Member since ${member.joinDate.getFullYear()}",
                        modifier = Modifier
                            .fontSize(1.2.cssRem)
                            .color(NeoColor.white)
                            .opacity(0.6)
                            .margin(top = 0.5.cssRem)
                    )

                    Box(
                        modifier = Modifier
                            .width(50.px)
                            .height(4.px)
                            .backgroundColor(NeoColor.colorPrimary)
                            .borderRadius(2.px)
                            .margin(topBottom = 2.cssRem)
                    )

                    // Tech stack
                    if (member.techStack.isNotEmpty()) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.margin(bottom = 2.cssRem)
                        ) {
                            SpanText(
                                text = "Tech Stack",
                                modifier = Modifier
                                    .fontSize(1.2.cssRem)
                                    .fontWeight(FontWeight.Bold)
                                    .color(NeoColor.white)
                                    .margin(bottom = 1.cssRem)
                                    .opacity(0.8)
                            )

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(1.5.cssRem),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                member.techStack.forEach { stack ->
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                    ) {
                                        Image(
                                            src = stack.image,
                                            width = 40,
                                            modifier = Modifier.margin(bottom = 0.5.cssRem)
                                        )
                                        SpanText(
                                            text = stack.title,
                                            modifier = Modifier.fontSize(0.9.cssRem).fontWeight(FontWeight.Medium).color(NeoColor.white)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Buttons
                    if (member.buttonLinks.isNotEmpty()) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(2.cssRem),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            member.buttonLinks.forEach { btn ->
                                NeoButton(
                                    title = btn.name,
                                    url = btn.url,
                                    imageSrc = btn.imageUrl,
//                                    modifier = Modifier.width(160.px) // Consistent button width
                                )
                            }
                        }
                    }
                }
            } else {
                // 404 State
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    SpanText("Member not found", Modifier.fontSize(2.cssRem).color(NeoColor.white))
                    SpanText("Slug: $slug", Modifier.color(NeoColor.white).opacity(0.5))
                }
            }
        }
    }
}

private fun Int.formatMonth(): String {
    val months = listOf(
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    )
    return months.getOrElse(this) { "Unknown" }
}

val HoverText = CssStyle {
    base {
        Modifier
            .background(NeoColor.backgroundPrimary)
    }
    link {
        Modifier.color(NeoColor.white) // Set your static text color
    }
    visited {
        Modifier.color(NeoColor.white) // Use the same color to keep it static
    }
    hover {
        Modifier.backgroundColor(NeoColor.colorPrimary)
    }
}