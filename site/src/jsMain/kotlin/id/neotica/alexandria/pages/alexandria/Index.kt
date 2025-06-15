package id.neotica.alexandria.pages.alexandria

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.Overflow
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.data.add
import com.varabyte.kobweb.core.init.InitRoute
import com.varabyte.kobweb.core.init.InitRouteContext
import com.varabyte.kobweb.core.layout.Layout
import com.varabyte.kobweb.silk.components.text.SpanText
import id.neotica.alexandria.components.NeoColor
import id.neotica.alexandria.components.layouts.NeoLayoutData
import kotlinx.browser.window
import kotlinx.coroutines.await
import org.jetbrains.compose.web.css.cssRem

@InitRoute() // Match this with your @Page path if you define it explicitly
fun initAlexandriaPage(ctx: InitRouteContext) {
    ctx.data.add(NeoLayoutData("Alexandria", "/alexandria")) // Set your desired tab title here
}

@Page
@Composable
@Layout(".components.layouts.NeoPageLayout")
fun AlexandriaPage() {
    var checkString by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        try {
            val response = window.fetch("https://jsonplaceholder.typicode.com/posts/1").await()
            val jsonText = response.text().await()

            if (response.ok) {
//                val post =
                checkString = jsonText
            } else {
                checkString = response.statusText
            }
        }catch (e: Exception) {}
    }
    Box(
        modifier = Modifier
            .backgroundColor(NeoColor.backgroundPrimary)
            .fillMaxSize()
            .padding(leftRight = 2.cssRem)
            .overflow(Overflow.Auto)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            SpanText("Alexandria")
            SpanText(checkString)
        }
    }
}