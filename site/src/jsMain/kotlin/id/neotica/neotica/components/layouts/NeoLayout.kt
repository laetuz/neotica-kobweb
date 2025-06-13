package id.neotica.neotica.components.layouts

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.backgroundColor
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxSize
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.core.PageContext
import com.varabyte.kobweb.core.data.getValue
import com.varabyte.kobweb.core.layout.Layout
import com.varabyte.kobweb.core.rememberPageContext
import id.neotica.neotica.components.NeoColor
import id.neotica.neotica.components.sections.NeoFooter
import id.neotica.neotica.components.sections.header.NeoNavHeader
import kotlinx.browser.document
import org.jetbrains.compose.web.css.cssRem

class NeoLayoutData(val title: String, val route: String? = "")

@Layout
@Composable
fun NeoPageLayout(ctx: PageContext, content: @Composable () -> Unit) {
    val ctx = rememberPageContext()
    val data = ctx.data.getValue<NeoLayoutData>()
    LaunchedEffect(data.title) {
        document.title = "::${data.title}"
    }
//    val colorMode by rememberCol()
//    val sitePalette = colorMode.toSitePalette()

    // This Column will be the main container for the entire page content
    Column(modifier = Modifier.fillMaxSize().backgroundColor(NeoColor.backgroundPrimary)) {
        // Set the browser tab title using the title from PageLayoutData
        // If pageData is null (e.g., no InitRoute added data), fallback to a default title
//        Title(pageData?.title ?: "Neotica")

        Column(
            Modifier
                .fillMaxWidth()
                .padding(leftRight = 2.cssRem)
        ) {
            NeoNavHeader(data.route) // Pass current path to header for active link highlighting
        }

        // This is where the actual content of the page (e.g., AboutPage, HomePage) will be rendered.
        // It should expand to fill the remaining space below the header and above the footer.
        Box(modifier = Modifier.weight(1f)) { // Use weight to make content area flexible
            content()
        }

        // Footer common to all pages using this layout.
        // Note: If you need a footer that scrolls *with* the content within a specific page,
        // it's better to include NeoFooter directly inside that page's Composable.
        // But for a common layout footer, this is where it goes.
        NeoFooter()
    }
}