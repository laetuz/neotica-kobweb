package id.neotica.neotica.components

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.foundation.layout.Arrangement
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.backgroundColor
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.compose.ui.modifiers.title
import com.varabyte.kobweb.silk.components.forms.Button
import com.varabyte.kobweb.silk.components.forms.ButtonSize
import com.varabyte.kobweb.silk.components.graphics.Image
import com.varabyte.kobweb.silk.components.text.SpanText
import kotlinx.browser.window
import org.jetbrains.compose.web.css.cssRem

@Composable
fun NeoButton(
    title: String,
    url: String,
    imageSrc: String,
    modifier: Modifier? = null
) {
    Box(modifier?: Modifier) {
        Button(
            onClick = {
                window.location.href = url
            },
            size = ButtonSize.SM,
            modifier = Modifier
//            .then(modifier ?: Modifier)
                .title(url)
                .backgroundColor(NeoColor.colorPrimary)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Image(
                    src = imageSrc,
                    height = 20,
                    modifier = Modifier.padding(right = 0.5.cssRem)
                )
                SpanText(title)
            }

        }
    }

}