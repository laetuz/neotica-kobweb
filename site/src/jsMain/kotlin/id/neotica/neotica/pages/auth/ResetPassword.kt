package id.neotica.neotica.pages.auth

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.Cursor
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.css.Overflow
import com.varabyte.kobweb.compose.css.TextAlign
import com.varabyte.kobweb.compose.css.TextDecorationLine
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Color
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.data.add
import com.varabyte.kobweb.core.init.InitRoute
import com.varabyte.kobweb.core.init.InitRouteContext
import com.varabyte.kobweb.core.layout.Layout
import com.varabyte.kobweb.silk.components.navigation.Link
import com.varabyte.kobweb.silk.components.text.SpanText
import id.neotica.neotica.components.NeoColor
import id.neotica.neotica.components.layouts.NeoLayoutData
import id.neotica.neotica.components.retro.RainbowDivider
import id.neotica.neotica.components.retro.RetroColor
import id.neotica.neotica.components.retro.StarfieldStars
import id.neotica.neotica.components.retro.retroBevel
import id.neotica.neotica.utils.Constants
import id.neotica.neotica.utils.queryParam
import kotlinx.browser.window
import kotlinx.coroutines.await
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.attributes.placeholder
import org.jetbrains.compose.web.css.LineStyle
import org.jetbrains.compose.web.css.StyleScope
import org.jetbrains.compose.web.css.backgroundColor
import org.jetbrains.compose.web.css.border
import org.jetbrains.compose.web.css.borderRadius
import org.jetbrains.compose.web.css.color
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.fontFamily
import org.jetbrains.compose.web.css.fontSize
import org.jetbrains.compose.web.css.marginBottom
import org.jetbrains.compose.web.css.padding
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.css.width
import org.jetbrains.compose.web.dom.PasswordInput
import org.w3c.fetch.Headers
import org.w3c.fetch.RequestInit

private const val MIN_PASSWORD_LENGTH = 8

@InitRoute
fun initResetPasswordPage(ctx: InitRouteContext) {
    ctx.data.add(NeoLayoutData("Reset Password", ""))
}

@Page
@Composable
@Layout(".components.layouts.NeoPageLayout")
fun ResetPasswordPage() {
    val scope = rememberCoroutineScope()

    // Read once for the initial validity check; the value is re-read at submit time.
    val initialToken = remember { queryParam(window.location.search, "token").orEmpty() }

    var password by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var success by remember { mutableStateOf(false) }

    fun submit() {
        if (isLoading) return
        // Read at call time; never store/render/log the token.
        val token = queryParam(window.location.search, "token").orEmpty()
        when {
            token.isBlank() -> errorMessage = "Invalid reset link. Request a new one."
            password.isEmpty() -> errorMessage = "Please enter a new password."
            password.length < MIN_PASSWORD_LENGTH ->
                errorMessage = "Password must be at least $MIN_PASSWORD_LENGTH characters."
            password != confirm -> errorMessage = "Passwords do not match."
            else -> scope.launch {
                isLoading = true
                errorMessage = null
                try {
                    val headers = Headers().apply {
                        append("Token", token)
                        append("NewPassword", password)
                    }
                    val response = window
                        .fetch("${Constants.DEV_URL}/auth/reset-password", RequestInit("POST", headers = headers))
                        .await()

                    when {
                        response.ok -> {
                            success = true
                            // Keep the single-use token out of the URL.
                            window.history.replaceState(null, "", "/auth/reset-password")
                        }
                        response.status.toInt() == 404 ->
                            errorMessage = "This reset link is invalid or has expired. Request a new one."
                        response.status.toInt() == 429 ->
                            errorMessage = "Too many attempts. Please try again later."
                        response.status.toInt() == 400 ->
                            errorMessage = "The request was rejected. Please check your password and try again."
                        else ->
                            errorMessage = "Password reset failed (${response.status}). Please try again later."
                    }
                } catch (e: Throwable) {
                    console.error("Reset password request failed: ${e.message}")
                    errorMessage = "Network error. Please try again."
                } finally {
                    isLoading = false
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .background {
                color(NeoColor.backgroundPrimary)
                image(StarfieldStars)
            }
            .fillMaxSize()
            .padding(leftRight = 1.5.cssRem, topBottom = 1.2.cssRem)
            .overflow(Overflow.Auto)
            .gap(1.2.cssRem)
            .fontFamily("VT323", "monospace"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().gap(0.3.cssRem),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SpanText(
                text = "\u2726  Reset Password  \u2726",
                modifier = Modifier
                    .fontSize(2.2.cssRem)
                    .fontWeight(FontWeight.Bold)
                    .color(RetroColor.babyBlue)
                    .textAlign(TextAlign.Center)
                    .textShadow(3.px, 3.px, color = RetroColor.navy.copy(alpha = 200))
            )
            SpanText(
                text = "Choose a new password for your account.",
                modifier = Modifier
                    .fontSize(1.05.cssRem)
                    .color(RetroColor.cream)
                    .textAlign(TextAlign.Center)
            )
        }

        RainbowDivider()

        when {
            initialToken.isBlank() -> RetroPanel {
                SpanText(
                    text = "Invalid reset link. Request a new one.",
                    modifier = Modifier
                        .fontSize(1.1.cssRem)
                        .fontWeight(FontWeight.Bold)
                        .color(NeoColor.negativePrimary)
                        .textAlign(TextAlign.Center)
                )
                RetroActionLink("Back to login", "/auth", RetroColor.steelBlue)
            }

            success -> RetroPanel(background = RetroColor.cream, borderColor = RetroColor.navy) {
                SpanText(
                    text = "\u2726  Password reset \u2014 you can now log in.  \u2726",
                    modifier = Modifier
                        .fontSize(1.15.cssRem)
                        .fontWeight(FontWeight.Bold)
                        .color(RetroColor.navy)
                        .textAlign(TextAlign.Center)
                )
                RetroActionLink("Go to login", "/auth", RetroColor.babyBlue)
            }

            else -> RetroPanel {
                PasswordInput(
                    value = password,
                    attrs = {
                        placeholder("New password")
                        onInput { password = it.value }
                        style { retroFieldStyle() }
                    }
                )
                PasswordInput(
                    value = confirm,
                    attrs = {
                        placeholder("Confirm new password")
                        onInput { confirm = it.value }
                        style { retroFieldStyle() }
                    }
                )

                errorMessage?.let {
                    SpanText(
                        text = it,
                        modifier = Modifier
                            .fontSize(1.cssRem)
                            .fontWeight(FontWeight.Bold)
                            .color(NeoColor.negativePrimary)
                            .textAlign(TextAlign.Center)
                    )
                }

                Box(
                    modifier = Modifier
                        .retroBevel(RetroColor.babyBlue)
                        .padding(leftRight = 1.6.cssRem, topBottom = 0.6.cssRem)
                        .cursor(Cursor.Pointer)
                        .onClick { submit() }
                ) {
                    SpanText(
                        text = if (isLoading) "Saving..." else "Reset password",
                        modifier = Modifier
                            .fontSize(1.2.cssRem)
                            .fontWeight(FontWeight.Bold)
                            .color(NeoColor.backgroundPrimary)
                    )
                }

                SpanText(
                    text = "At least $MIN_PASSWORD_LENGTH characters.",
                    modifier = Modifier.fontSize(0.85.cssRem).color(RetroColor.cyan)
                )
            }
        }
    }
}

@Composable
private fun RetroPanel(
    background: Color = NeoColor.backgroundPrimaryTransparent,
    borderColor: Color = RetroColor.steelBlue.copy(alpha = 90),
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .maxWidth(460.px)
            .backgroundColor(background)
            .border(2.px, LineStyle.Solid, borderColor)
            .borderRadius(4.px)
            .padding(1.5.cssRem)
            .gap(0.7.cssRem)
            .boxShadow(2.px, 2.px, color = Color.rgb(0, 0, 0).copy(alpha = 115)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        content()
    }
}

@Composable
private fun RetroActionLink(label: String, path: String, background: Color) {
    Link(
        path = path,
        modifier = Modifier
            .retroBevel(background)
            .padding(leftRight = 1.4.cssRem, topBottom = 0.5.cssRem)
            .cursor(Cursor.Pointer)
            .textDecorationLine(TextDecorationLine.None)
    ) {
        SpanText(
            text = label,
            modifier = Modifier
                .fontSize(1.1.cssRem)
                .fontWeight(FontWeight.Bold)
                .color(NeoColor.backgroundPrimary)
        )
    }
}

private fun StyleScope.retroFieldStyle() {
    padding(11.px)
    marginBottom(14.px)
    width(100.percent)
    fontFamily("VT323", "monospace")
    fontSize(1.1.cssRem)
    backgroundColor(RetroColor.cream)
    color(RetroColor.navy)
    border(2.px, LineStyle.Solid, RetroColor.navy)
    borderRadius(3.px)
}