package id.neotica.neotica.components.others

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.color
import com.varabyte.kobweb.silk.components.text.SpanText
import id.neotica.neotica.components.NeoColor

@Composable
fun NeoText(
    text: String,
    modifier: Modifier = Modifier
) {
    SpanText(
        text = text,
        modifier = modifier.color(NeoColor.white)
    )
}