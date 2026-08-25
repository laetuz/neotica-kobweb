package id.neotica.neotica.components.retro

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.functions.LinearGradient
import com.varabyte.kobweb.compose.css.functions.RadialGradient
import com.varabyte.kobweb.compose.css.functions.linearGradient
import com.varabyte.kobweb.compose.css.functions.repeatingRadialGradient
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Color
import com.varabyte.kobweb.compose.ui.modifiers.backgroundImage
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.height
import id.neotica.neotica.components.NeoColor
import kotlinx.browser.window
import org.jetbrains.compose.web.css.px

object RetroColor {
    val hotPink = Color.rgb(0xFF, 0x69, 0xB4)
    val cyan = Color.rgb(0x00, 0xFF, 0xFF)
    val yellow = Color.rgb(0xFF, 0xFF, 0x00)
    val lime = Color.rgb(0x7F, 0xFF, 0x00)
    val magenta = Color.rgb(0xFF, 0x00, 0xFF)

    val babyBlue = Color.rgb(0xAD, 0xD8, 0xE6)
    val skyBlue = Color.rgb(0x87, 0xCE, 0xEB)
    val royalBlue = Color.rgb(0x41, 0x69, 0xE1)
    val navy = Color.rgb(0x00, 0x00, 0x80)
    val steelBlue = Color.rgb(0x46, 0x82, 0xB4)
    val teal = Color.rgb(0x00, 0x80, 0x80)

    val beige = Color.rgb(0xF5, 0xF5, 0xDC)
    val cream = Color.rgb(0xFF, 0xFD, 0xD0)
    val tan = Color.rgb(0xD2, 0xB4, 0x8C)
}

val RainbowStrip = linearGradient(LinearGradient.Direction.ToRight) {
    add(RetroColor.navy)
    add(RetroColor.steelBlue)
    add(RetroColor.skyBlue)
    add(RetroColor.babyBlue)
    add(RetroColor.cream)
}

val StarfieldStars = repeatingRadialGradient(RadialGradient.Shape.Circle) {
    add(NeoColor.white.copy(alpha = 40), 1.px)
    add(NeoColor.transparent, 24.px)
}

fun isRetroRoute(path: String = window.location.pathname): Boolean =
    path == "/" || path.isEmpty() || path.startsWith("/holomarket") || path == "/contact"

@Composable
fun RainbowDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(4.px)
            .backgroundImage(RainbowStrip)
    )
}