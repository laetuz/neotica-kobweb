package id.neotica.neotica.components.layouts

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.core.PageContext
import com.varabyte.kobweb.core.data.getValue
import com.varabyte.kobweb.core.layout.Layout
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.toAttrs
import id.neotica.neotica.components.NeoColor
import id.neotica.neotica.components.sections.NeoFooter
import id.neotica.neotica.components.sections.header.NeoNavHeader
import kotlinx.browser.document
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.fr
import org.jetbrains.compose.web.dom.Div

class NeoLayoutData(val title: String, val route: String? = "")

@Layout
@Composable
fun NeoPageLayout(ctx: PageContext, content: @Composable /*ColumnScope.*/() -> Unit) {
//    val ctx = rememberPageContext()
    val data = ctx.data.getValue<NeoLayoutData>()
    LaunchedEffect(data.title) {
        document.title = "::${data.title}"
    }

    // This Column will be the main container for the entire page content
    Box(
        modifier = Modifier
            .fillMaxSize()
            .backgroundColor(NeoColor.backgroundPrimary)
            .gridTemplateRows { size(1.fr); size(minContent) },
        contentAlignment = Alignment.TopCenter
    )
    {

        Column(
            Modifier
                .fillMaxWidth().gridRow(1),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.fillMaxSize().padding(leftRight = 2.cssRem)
            ) { NeoNavHeader(data.route) }
            Div(PageContentStyle.toAttrs()) { content() }
        }
        NeoFooter(Modifier.fillMaxWidth().gridRow(2))

    }
}

val PageContentStyle = CssStyle {
    base { Modifier.fillMaxSize()/*.padding(leftRight = 2.cssRem, top = 4.cssRem)*/ }
//    Breakpoint.MD { Modifier.maxWidth(60.cssRem) }
}