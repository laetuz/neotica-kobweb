package id.neotica.neotica.pages.auth

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.Overflow
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.data.add
import com.varabyte.kobweb.core.init.InitRoute
import com.varabyte.kobweb.core.init.InitRouteContext
import com.varabyte.kobweb.core.layout.Layout
import com.varabyte.kobweb.core.rememberPageContext
import id.neotica.neotica.components.NeoColor
import id.neotica.neotica.components.button.NeoButton
import id.neotica.neotica.components.layouts.NeoLayoutData
import id.neotica.neotica.components.others.NeoText
import kotlinx.serialization.json.Json
import org.jetbrains.compose.web.css.cssRem

@InitRoute()
fun initContentPage(ctx: InitRouteContext) {
    ctx.data.add(NeoLayoutData("Content", ""))
}

@Page
@Composable
@Layout(".components.layouts.NeoPageLayout")
fun ContentPage() {

    val ctx = rememberPageContext()
    val scope = rememberCoroutineScope()

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val jsonParser = remember { Json { ignoreUnknownKeys = true } }

    LaunchedEffect(Unit) {
        val isLoggedIn = AuthService().isLoggedIn()

        if (!isLoggedIn) {
            ctx.router.navigateTo("/auth")
        }
    }

    Box(
        modifier = Modifier
            .backgroundColor(NeoColor.backgroundPrimary)
            .fillMaxSize()
            .padding(leftRight = 2.cssRem)
            .overflow(Overflow.Auto),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            NeoText(
                text = "Content",
                modifier = Modifier.padding(bottom = 1.cssRem)
            )

            NeoText(
                text = "Kono Sekai",
                modifier = Modifier.padding(bottom = 1.cssRem)
            )

            NeoText(
                text = "why are you not transforming??",
                modifier = Modifier.padding(bottom = 1.cssRem)
            )

            NeoButton("Logout") {
                AuthService().logout()

                ctx.router.navigateTo("/auth")
            }
        }
    }
}