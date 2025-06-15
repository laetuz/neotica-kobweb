package id.neotica.alexandria.components.sections.header

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.FontStyle
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.css.OverflowWrap
import com.varabyte.kobweb.compose.foundation.layout.Arrangement
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.silk.components.graphics.Image
import com.varabyte.kobweb.silk.components.icons.HamburgerIcon
import com.varabyte.kobweb.silk.components.navigation.Link
import com.varabyte.kobweb.silk.components.navigation.UncoloredLinkVariant
import com.varabyte.kobweb.silk.components.navigation.UndecoratedLinkVariant
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.style.breakpoint.Breakpoint
import com.varabyte.kobweb.silk.style.breakpoint.displayIfAtLeast
import com.varabyte.kobweb.silk.style.breakpoint.displayUntil
import id.neotica.alexandria.components.NeoColor
import id.neotica.alexandria.components.widgets.IconButton
import org.jetbrains.compose.web.css.DisplayStyle
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.em
import org.jetbrains.compose.web.css.px

@Composable
fun NeoNavHeader(currentRoute: String? = "") {
    Row(
        modifier = Modifier
            .padding(topBottom = 1.cssRem)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .minWidth(0.px)
                .padding(right = 1.cssRem), // Added padding for spacing
            verticalAlignment = Alignment.CenterVertically //
        ) {
            Link("/") {
                // Block display overrides inline display of the <img> tag, so it calculates centering better
                Image(
                    "/alexandria-logo.svg",
                    "Alexandria Logo",
                    Modifier
                        .padding(right = 0.5.cssRem)
                        .height(2.cssRem)
                        .display(DisplayStyle.Block)
                )
            }
            SpanText(
                "Alexandria",
                Modifier
                    .overflowWrap(OverflowWrap.BreakWord)
                    .color(NeoColor.white)
                    .fontSize(1.5.em)
                    .fontStyle(FontStyle.Italic)
                    .fontWeight(FontWeight.Bold)
            )

        }
//        Row(Modifier.gap(1.5.cssRem).displayIfAtLeast(Breakpoint.MD), verticalAlignment = Alignment.CenterVertically) {
//            NeoMenuItems()
//        }
//
//        Row(
//            Modifier
//                .fontSize(1.5.cssRem)
//                .gap(1.cssRem)
//                .displayUntil(Breakpoint.MD),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            var menuState by remember { mutableStateOf(SideMenuState.CLOSED) }
//
//            HamburgerButton(onClick =  { menuState = SideMenuState.OPEN })
//
//            if (menuState != SideMenuState.CLOSED) {
//                NeoSideMenu(
//                    menuState,
//                    close = { menuState = menuState.close() },
//                    onAnimationEnd = { if (menuState == SideMenuState.CLOSING) menuState = SideMenuState.CLOSED }
//                )
//            }
//        }
    }
}

// Note: When the user closes the side menu, we don't immediately stop rendering it (at which point it would disappear
// abruptly). Instead, we start animating it out and only stop rendering it when the animation is complete.
enum class SideMenuState {
    CLOSED,
    OPEN,
    CLOSING;

    fun close() = when (this) {
        CLOSED -> CLOSED
        OPEN -> CLOSING
        CLOSING -> CLOSING
    }
}

@Composable
fun NeoMenuItems() {
    NavLink("/", "Home")
    NavLink("/about", "About")
    NavLink("/contact", "Contact")
}

@Composable
private fun NavLink(path: String, text: String) {
    Link(path, text, variant = UndecoratedLinkVariant.then(UncoloredLinkVariant))
}

@Composable
private fun HamburgerButton(onClick: () -> Unit) {
    IconButton(onClick) {
        HamburgerIcon()
    }
}

