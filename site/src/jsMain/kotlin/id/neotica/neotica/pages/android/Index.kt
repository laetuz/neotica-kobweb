package id.neotica.neotica.pages.android

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.Overflow
import com.varabyte.kobweb.compose.foundation.layout.Arrangement
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.data.add
import com.varabyte.kobweb.core.init.InitRoute
import com.varabyte.kobweb.core.init.InitRouteContext
import com.varabyte.kobweb.core.layout.Layout
import com.varabyte.kobweb.silk.components.graphics.Image
import com.varabyte.kobweb.silk.components.text.SpanText
import id.neotica.neotica.components.NeoButton
import id.neotica.neotica.components.NeoColor
import id.neotica.neotica.components.layouts.NeoLayoutData
import id.neotica.neotica.domain.model.YoutubeVideo
import id.neotica.neotica.utils.toDate
import kotlinx.browser.window
import kotlinx.coroutines.await
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.attributes.placeholder
import org.jetbrains.compose.web.css.FlexWrap
import org.jetbrains.compose.web.css.LineStyle
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.Input

@InitRoute()
fun initProjectsPage(ctx: InitRouteContext) {
    ctx.data.add(NeoLayoutData("Android - Neotica.id", "/android")) // Set your desired tab title here
}

@Page
@Composable
@Layout(".components.layouts.NeoPageLayout")
fun AndroidPage() {

    val linkToJson = "https://raw.githubusercontent.com/Neotica/JsonDB/refs/heads/main/android-video-archives"
    var searchText by remember { mutableStateOf("") }

    var videoList by remember { mutableStateOf<List<YoutubeVideo>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    val filteredList = videoList.filter {
        it.title.contains(searchText, ignoreCase = true) ||
                it.description.contains(searchText, ignoreCase = true)
    }

    LaunchedEffect(Unit) {
        try {
            val response = window.fetch(linkToJson).await()
            val text = response.text().await()

            videoList = Json.decodeFromString(
                ListSerializer(YoutubeVideo.serializer()),
                text
            )
        } catch (e: Exception) {
            console.error("Failed to load videos: ", e)
        } finally {
            isLoading = false
        }
    }

    Column(
        modifier = Modifier
            .backgroundColor(NeoColor.backgroundPrimary)
            .fillMaxSize()
            .padding(leftRight = 2.cssRem)
            .overflow(Overflow.Auto)
            .gap(1.5.cssRem)
    ) {
        Input(
            type = InputType.Text,
            attrs = Modifier
                .fillMaxWidth()
                .padding(1.cssRem)
                .backgroundColor(NeoColor.backgroundPrimary)
                .color(NeoColor.white)
                .border(1.px, LineStyle.Solid, NeoColor.colorPrimary)
                .borderRadius(8.px)
                .toAttrs {
                    placeholder("Search videos...")
                    onInput { event ->
                        searchText = event.value
                    }
                }
        )

        if (isLoading) {
            SpanText(
                text = "Loading videos...",
                modifier = Modifier
                    .color(NeoColor.white)
                    .fontSize(1.5.cssRem)
            )
        }

        filteredList.forEach { data ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .border(1.px, LineStyle.Solid, NeoColor.colorPrimary)
                    .borderRadius(8.px)
                    .margin(topBottom = 1.cssRem)
                    .padding(1.cssRem)
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .gap(1.5.cssRem)
                        .flexWrap(FlexWrap.Wrap),
                    horizontalArrangement = Arrangement.spacedBy(1.cssRem),
                    verticalAlignment = Alignment.Top
                ) {
                    Image(
                        src = "https://img.youtube.com/vi/${data.videoId}/hqdefault.jpg",
                        height = 164,
                        width = 164,
                    )
                    Column(
                        modifier = Modifier.fillMaxWidth().flex(1),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(
                            Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                SpanText(
                                    modifier = Modifier
                                        .fontSize(1.5.cssRem)
                                    /*.onClick {
                                        ctx.router.navigateTo(data.url)
                                    }*/,
                                    text = data.title
                                )
                                SpanText(
                                    modifier = Modifier
                                        .fontSize(0.8.cssRem)
                                    /*.onClick {
                                        ctx.router.navigateTo(data.url)
                                    }*/,
                                    text = "${data.dateUploaded.toDate().getDate()}/${data.dateUploaded.toDate().getMonth()+1}/${data.dateUploaded.toDate().getFullYear()}"
                                )
                            }

                            SpanText(
                                modifier = Modifier
                                    .fontSize(1.cssRem),
                                text = data.description
                            )
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth().padding(top = 2.cssRem)
                                .flexWrap(FlexWrap.Wrap),
                            horizontalArrangement = Arrangement.spacedBy(1.cssRem),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            NeoButton(title = "Go to Video", url = data.url, "")
                            NeoButton(title = "Go to YouTube Video", url = "https://www.youtube.com/watch?v=${data.videoId}", "")
                        }
                    }
                }
            }
        }
    }
}