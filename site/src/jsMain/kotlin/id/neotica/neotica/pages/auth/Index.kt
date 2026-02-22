package id.neotica.neotica.pages.auth

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import com.varabyte.kobweb.silk.components.text.SpanText
import id.neotica.neotica.components.NeoColor
import id.neotica.neotica.components.button.NeoButton
import id.neotica.neotica.components.layouts.NeoLayoutData
import id.neotica.neotica.components.others.NeoText
import id.neotica.neotica.domain.model.auth.LoginResponse
import id.neotica.neotica.utils.Constants
import kotlinx.browser.window
import kotlinx.coroutines.await
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.jetbrains.compose.web.attributes.placeholder
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.marginBottom
import org.jetbrains.compose.web.css.padding
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.css.width
import org.jetbrains.compose.web.dom.PasswordInput
import org.jetbrains.compose.web.dom.TextInput
import org.w3c.fetch.Headers
import org.w3c.fetch.RequestInit

@InitRoute()
fun initAuthPage(ctx: InitRouteContext) {
    ctx.data.add(NeoLayoutData("Auth", ""))
}

@Page
@Composable
@Layout(".components.layouts.NeoPageLayout")
fun AuthPage() {

    val ctx = rememberPageContext()
    val scope = rememberCoroutineScope()

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val jsonParser = remember { Json { ignoreUnknownKeys = true } }

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
            NeoText("Login")

            TextInput(
                value = username,
                attrs = {
                    placeholder("Username")
                    onInput { username = it.value }
                    style {
                        padding(10.px)
                        marginBottom(15.px)
                        width(100.percent)
                    }
                }
            )

            PasswordInput(
                value = password,
                attrs = {
                    placeholder("Password")
                    onInput { password = it.value }
                    style {
                        padding(10.px)
                        marginBottom(15.px)
                        width(100.percent)
                    }
                }
            )

            if (errorMessage != null) {
                SpanText(
                    text = errorMessage!!,
                    modifier = Modifier.color(NeoColor.negativePrimary).margin(bottom = 1.cssRem)
                )
            }

            NeoButton(
                if (isLoading) "Loading..." else "Login",
            ) {
                if (username.isNotBlank() && password.isNotBlank()) {
                    scope.launch {
                        isLoading = true
                        errorMessage = null

                        try {
                            val headers = Headers()
                            headers.append("username", username)
                            headers.append("password", password)

                            val requestInit = RequestInit("GET", headers = headers)

                            val response = window.fetch("${Constants.DEV_URL}/auth", requestInit).await()

                            if (response.ok) {
                                val text = response.text().await()
                                val authData = jsonParser.decodeFromString<LoginResponse>(text)

                                window.localStorage.setItem("token", authData.token)
                                window.localStorage.setItem("refreshToken", authData.refreshToken)

                                ctx.router.navigateTo("/auth/content")
                            } else {
                                errorMessage = "Login Failed: ${response.status}"
                            }

                        } catch (e: Throwable) {
                            errorMessage = "Network error. ${e.message}"
                            console.error(errorMessage)
                        } finally {
                            isLoading = false
                        }

                    }
                } else {
                    errorMessage = "Please enter both the username and password."
                }
            }
        }
    }
}