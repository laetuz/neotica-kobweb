package id.neotica.neotica.components.modifiers

import com.varabyte.kobweb.compose.css.TextDecorationLine
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.background
import com.varabyte.kobweb.compose.ui.modifiers.backgroundColor
import com.varabyte.kobweb.compose.ui.modifiers.border
import com.varabyte.kobweb.compose.ui.modifiers.color
import com.varabyte.kobweb.compose.ui.modifiers.textDecorationLine
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.selectors.hover
import com.varabyte.kobweb.silk.style.selectors.link
import com.varabyte.kobweb.silk.style.selectors.visited
import id.neotica.neotica.components.NeoColor
import org.jetbrains.compose.web.css.LineStyle
import org.jetbrains.compose.web.css.px

val BackgroundHoverStyle = CssStyle {
    base {
        Modifier
            .background(NeoColor.backgroundPrimary)
            .textDecorationLine(TextDecorationLine.None)
            .border(
                width = 2.px,
                style = LineStyle.Solid,
                color = NeoColor.white
            )
    }
    link {
        Modifier.color(NeoColor.white) // Set your static text color
    }
    visited {
        Modifier.color(NeoColor.white) // Use the same color to keep it static
    }
    hover {
        Modifier.backgroundColor(NeoColor.colorPrimary)
            .border(
                width = 2.px,
                style = LineStyle.Solid,
                color = NeoColor.white
            )
    }
}

val BackgroundHoverStyleNegative = CssStyle {
    base {
        Modifier
            .background(NeoColor.backgroundPrimary)
            .textDecorationLine(TextDecorationLine.None)
            .border(
                width = 2.px,
                style = LineStyle.Solid,
                color = NeoColor.white
            )
    }
    link {
        Modifier.color(NeoColor.white) // Set your static text color
    }
    visited {
        Modifier.color(NeoColor.white) // Use the same color to keep it static
    }
    hover {
        Modifier.backgroundColor(NeoColor.negativePrimary)
            .border(
                width = 2.px,
                style = LineStyle.Solid,
                color = NeoColor.white
            )
    }
}

val BackgroundHoverStyleMain = CssStyle {
    base {
        Modifier
            .background(NeoColor.colorPrimary)
            .textDecorationLine(TextDecorationLine.None)
    }
    link {
        Modifier.color(NeoColor.white) // Set your static text color
    }
    visited {
        Modifier.color(NeoColor.white) // Use the same color to keep it static
    }
    hover { Modifier.backgroundColor(NeoColor.backgroundPrimaryTransparent) }
}