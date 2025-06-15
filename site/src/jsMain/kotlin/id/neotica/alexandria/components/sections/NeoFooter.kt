package id.neotica.alexandria.components.sections

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.TextAlign
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.fontSize
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.compose.ui.modifiers.textAlign
import com.varabyte.kobweb.silk.components.text.SpanText
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.em

@Composable
fun NeoFooter() {
    SpanText(
        text = "Neotica.id 2025",
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 1.cssRem) // Padding from the bottom edge
            .textAlign(TextAlign.Center)
            .fontSize(0.8.em)
    )
}