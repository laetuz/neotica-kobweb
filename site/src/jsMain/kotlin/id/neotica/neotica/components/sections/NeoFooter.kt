package id.neotica.neotica.components.sections

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.TextAlign
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.backgroundColor
import com.varabyte.kobweb.compose.ui.modifiers.color
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.fontFamily
import com.varabyte.kobweb.compose.ui.modifiers.fontSize
import com.varabyte.kobweb.compose.ui.modifiers.textAlign
import com.varabyte.kobweb.silk.components.text.SpanText
import id.neotica.neotica.components.NeoColor
import id.neotica.neotica.components.retro.RetroColor
import org.jetbrains.compose.web.css.em

@Composable
fun NeoFooter(modifier: Modifier = Modifier, retro: Boolean = false) {
    SpanText(
        text = "© Neotica.id 2026",
        modifier = modifier
            .backgroundColor(NeoColor.backgroundPrimary)
            .fillMaxWidth()
            .textAlign(TextAlign.Center)
            .fontSize(0.8.em)
            .color(if (retro) RetroColor.cream else NeoColor.white)
            .then(if (retro) Modifier.fontFamily("VT323", "monospace") else Modifier)
    )
}