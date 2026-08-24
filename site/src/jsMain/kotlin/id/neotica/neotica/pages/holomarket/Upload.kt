package id.neotica.neotica.pages.holomarket

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.varabyte.kobweb.compose.css.BoxShadow
import com.varabyte.kobweb.compose.css.Cursor
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.css.Overflow
import com.varabyte.kobweb.compose.css.TextAlign
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
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.base
import com.varabyte.kobweb.silk.style.toModifier
import id.neotica.neotica.components.NeoColor
import id.neotica.neotica.components.layouts.NeoLayoutData
import id.neotica.neotica.utils.Constants
import kotlin.math.roundToInt
import kotlinx.browser.window
import kotlinx.coroutines.await
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.attributes.accept
import org.jetbrains.compose.web.attributes.multiple
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
import org.jetbrains.compose.web.css.height
import org.jetbrains.compose.web.css.marginBottom
import org.jetbrains.compose.web.css.padding
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.css.width
import org.jetbrains.compose.web.dom.FileInput
import org.jetbrains.compose.web.dom.TextArea
import org.jetbrains.compose.web.dom.TextInput
import org.w3c.fetch.RequestInit
import org.w3c.files.File

private const val MAX_APK_BYTES: Long = 30L * 1024L * 1024L

val SubmitPanelStyle = CssStyle.base {
    Modifier
        .fillMaxWidth()
        .backgroundColor(NeoColor.backgroundPrimaryTransparent)
        .border(2.px, LineStyle.Solid, RetroColor.steelBlue.copy(alpha = 90))
        .borderRadius(4.px)
        .padding(1.4.cssRem)
        .gap(0.8.cssRem)
        .boxShadow(2.px, 2.px, color = Color.rgb(0, 0, 0).copy(alpha = 115))
}

private val ErrorColor = Color.rgb(0xE0, 0x6C, 0x6C)

private data class SubmitResult(val message: String, val isSuccess: Boolean)

@InitRoute
fun initAppSubmissionPage(ctx: InitRouteContext) {
    ctx.data.add(NeoLayoutData("HoloMarket \u00B7 Submit your app"))
}

@Page
@Layout(".components.layouts.NeoPageLayout")
@Composable
fun HoloMarketUploadPage() {
    val scope = rememberCoroutineScope()

    var title by remember { mutableStateOf("") }
    var developer by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var categories by remember { mutableStateOf("") }
    var apkFile by remember { mutableStateOf<File?>(null) }
    var screenshots by remember { mutableStateOf<List<File>>(emptyList()) }
    var validationError by remember { mutableStateOf<String?>(null) }
    var isSubmitting by remember { mutableStateOf(false) }
    var result by remember { mutableStateOf<SubmitResult?>(null) }

    val submit: suspend () -> Unit = {
        isSubmitting = true
        result = null
        try {
            val form: dynamic = js("new FormData()")
            form.append("title", title)
            form.append("developer", developer)
            form.append("description", description)
            form.append("categories", categories)
            apkFile?.let { form.append("file", it, it.name) }
            screenshots.forEach { form.append("screenshots", it, it.name) }

            val response =
                window.fetch("${Constants.PUBLIC_API_URL}/submit", RequestInit("POST", body = form)).await()
            if (response.ok) {
                result = SubmitResult("\u2726  Thanks! Your submission was received.  \u2726", isSuccess = true)
            } else if (response.status.toInt() == 429) {
                val seconds = response.headers.get("Retry-After")?.toIntOrNull() ?: 60
                result = SubmitResult("Too many submissions. Please try again in $seconds seconds.", isSuccess = false)
            } else if (response.status.toInt() == 400) {
                val text = response.text().await()
                result = SubmitResult(text.ifBlank { "The server rejected the submission." }, isSuccess = false)
            } else {
                result =
                    SubmitResult("Something went wrong (HTTP ${response.status}). Please try again.", isSuccess = false)
            }
        } catch (e: Throwable) {
            console.error("Submit failed: ${e.message}")
            result = SubmitResult("Network error. Please try again.", isSuccess = false)
        } finally {
            isSubmitting = false
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
        Link(
            text = "\u2190  Back to HoloMarket",
            path = "/holomarket",
            modifier = Modifier
                .align(Alignment.Start)
                .fontSize(1.cssRem)
                .color(RetroColor.skyBlue)
        )

        Column(
            modifier = Modifier.fillMaxWidth().gap(0.4.cssRem),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SpanText(
                text = "\u2726  Submit Your App  \u2726",
                modifier = Modifier
                    .fontSize(2.cssRem)
                    .fontWeight(FontWeight.Bold)
                    .color(RetroColor.babyBlue)
                    .textAlign(TextAlign.Center)
                    .textShadow(2.px, 2.px, color = Color.rgb(0, 0, 0).copy(alpha = 153))
            )
            SpanText(
                text = "Got a great APK for the retro Android community? Send it our way!",
                modifier = Modifier
                    .fontSize(1.05.cssRem)
                    .color(RetroColor.cream)
                    .textAlign(TextAlign.Center)
            )
        }

        RainbowDivider()

        Column(
            modifier = SubmitPanelStyle.toModifier().maxWidth(640.px),
            horizontalAlignment = Alignment.Start
        ) {
            FieldLabel("App Title")
            TextInput(
                value = title,
                attrs = {
                    placeholder("My Cool App")
                    onInput { title = it.value }
                    style { retroFieldStyle() }
                }
            )

            FieldLabel("Developer (optional)")
            TextInput(
                value = developer,
                attrs = {
                    placeholder("Your name / studio")
                    onInput { developer = it.value }
                    style { retroFieldStyle() }
                }
            )

            FieldLabel("Description (optional)")
            TextArea(
                value = description,
                attrs = {
                    placeholder("What does the app do?")
                    onInput { description = it.value }
                    style { retroFieldStyle(); height(6.cssRem) }
                }
            )

            FieldLabel("Categories (optional)")
            TextInput(
                value = categories,
                attrs = {
                    placeholder("e.g. tools,test")
                    onInput { categories = it.value }
                    style { retroFieldStyle() }
                }
            )

            FieldLabel("APK File *")
            FileInput(
                value = "",
                attrs = {
                    accept("application/vnd.android.package-archive,.apk")
                    onInput { ev ->
                        apkFile = ev.target.files?.item(0)
                        validationError = null
                    }
                    style { retroFieldStyle() }
                }
            )
            FileStatusLine(apkFile)

            FieldLabel("Screenshots (optional)")
            FileInput(
                value = "",
                attrs = {
                    accept("image/png,image/jpeg,image/webp")
                    multiple()
                    onInput { ev ->
                        val files = ev.target.files
                        screenshots = if (files == null) emptyList()
                        else (0 until files.length).mapNotNull { files.item(it) }
                    }
                    style { retroFieldStyle() }
                }
            )
            if (screenshots.isNotEmpty()) {
                SpanText(
                    text = "${screenshots.size} screenshot(s) selected.",
                    modifier = Modifier
                        .fontSize(0.9.cssRem)
                        .color(RetroColor.cyan)
                )
            }

            validationError?.let {
                SpanText(
                    text = it,
                    modifier = Modifier
                        .fontSize(0.95.cssRem)
                        .color(ErrorColor)
                        .fontWeight(FontWeight.Bold)
                        .margin(top = 0.3.cssRem)
                )
            }

            Box(
                modifier = Modifier
                    .cursor(Cursor.Pointer)
                    .onClick {
                        if (!isSubmitting) {
                            val error = validate(apkFile, screenshots)
                            if (error != null) {
                                validationError = error
                            } else {
                                validationError = null
                                result = null
                                scope.launch { submit() }
                            }
                        }
                    }
                    .align(Alignment.CenterHorizontally)
                    .margin(top = 0.5.cssRem)
                    .backgroundColor(if (isSubmitting) RetroColor.steelBlue else RetroColor.babyBlue)
                    .border(2.px, LineStyle.Solid, RetroColor.navy)
                    .borderRadius(4.px)
                    .padding(leftRight = 2.cssRem, topBottom = 0.7.cssRem)
                    .boxShadow(
                        BoxShadow.of(0.px, 1.px, 0.px, 1.px, NeoColor.white.copy(alpha = 200), inset = true),
                        BoxShadow.of(0.px, (-1).px, 0.px, 1.px, Color.rgb(0, 0, 0).copy(alpha = 120), inset = true),
                        BoxShadow.of(3.px, 3.px, 0.px, 0.px, Color.rgb(0, 0, 0).copy(alpha = 127)),
                    )
            ) {
                SpanText(
                    text = if (isSubmitting) "UPLOADING..." else "SUBMIT FOR REVIEW",
                    modifier = Modifier
                        .fontSize(1.2.cssRem)
                        .fontWeight(FontWeight.Bold)
                        .color(NeoColor.backgroundPrimary)
                )
            }

            result?.let { r ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .margin(top = 0.5.cssRem)
                        .backgroundColor(if (r.isSuccess) RetroColor.cream else RetroColor.beige)
                        .border(2.px, LineStyle.Solid, if (r.isSuccess) RetroColor.navy else ErrorColor)
                        .borderRadius(4.px)
                        .padding(1.cssRem)
                ) {
                    SpanText(
                        text = r.message,
                        modifier = Modifier
                            .fontSize(1.cssRem)
                            .fontWeight(FontWeight.Bold)
                            .color(if (r.isSuccess) RetroColor.navy else ErrorColor)
                            .textAlign(TextAlign.Center)
                            .fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
private fun FieldLabel(text: String) {
    SpanText(
        text = text,
        modifier = Modifier
            .fontSize(1.1.cssRem)
            .fontWeight(FontWeight.Bold)
            .color(RetroColor.babyBlue)
            .margin(top = 0.4.cssRem)
    )
}

@Composable
private fun FileStatusLine(file: File?) {
    if (file != null) {
        SpanText(
            text = "${file.name}  \u00B7  ${formatBytes(file.size.toLong())}",
            modifier = Modifier
                .fontSize(0.95.cssRem)
                .color(RetroColor.cyan)
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

private fun formatBytes(bytes: Long): String = when {
    bytes >= 1024L * 1024L -> {
        val mb = bytes / 1024.0 / 1024.0
        "${(mb * 10).roundToInt() / 10.0} MB"
    }
    bytes >= 1024L -> "${bytes / 1024L} KB"
    else -> "$bytes B"
}

private fun validate(apkFile: File?, screenshots: List<File>): String? {
    if (apkFile == null) return "Please choose an APK file to submit."
    if (!apkFile.name.endsWith(".apk", ignoreCase = true)) return "The file must be a valid .apk."
    if (apkFile.size.toLong() > MAX_APK_BYTES) return "The APK is larger than 30 MB. Please upload a smaller file."
    if (screenshots.any { !it.name.endsWith(".png", true) && !it.name.endsWith(".jpg", true) &&
            !it.name.endsWith(".jpeg", true) && !it.name.endsWith(".webp", true) }
    ) {
        return "Screenshots must be PNG, JPEG, or WebP images."
    }
    return null
}