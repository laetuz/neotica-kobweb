package id.neotica.neotica.pages.orpheum

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.Cursor
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.foundation.layout.Arrangement
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.silk.components.text.SpanText
import id.neotica.neotica.components.NeoColor
import id.neotica.neotica.components.retro.RetroColor
import org.jetbrains.compose.web.css.LineStyle
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.Button

/** Retro pagination strip with Prev/Next and a numbered window (with ellipses). */
@Composable
internal fun OrpheumPagination(
    currentPage: Int,
    totalPages: Int,
    onPageChange: (Int) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(topBottom = 1.5.cssRem).gap(0.5.cssRem),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        RetroPageButton(
            text = "\u00AB Prev",
            enabled = currentPage > 1,
            active = false,
            onClick = { if (currentPage > 1) onPageChange(currentPage - 1) }
        )

        val visiblePages = buildList {
            val rangeStart = (currentPage - 2).coerceAtLeast(1)
            val rangeEnd = (currentPage + 2).coerceAtMost(totalPages)
            if (rangeStart > 1) {
                add(1)
                if (rangeStart > 2) add(-1)
            }
            for (i in rangeStart..rangeEnd) add(i)
            if (rangeEnd < totalPages) {
                if (rangeEnd < totalPages - 1) add(-1)
                add(totalPages)
            }
        }

        visiblePages.forEach { page ->
            if (page == -1) {
                SpanText(
                    text = "\u2026",
                    modifier = Modifier
                        .color(RetroColor.cream)
                        .margin(leftRight = 0.4.cssRem)
                        .fontSize(1.2.cssRem)
                )
            } else {
                RetroPageButton(
                    text = page.toString(),
                    enabled = true,
                    active = page == currentPage,
                    onClick = { onPageChange(page) }
                )
            }
        }

        RetroPageButton(
            text = "Next \u00BB",
            enabled = currentPage < totalPages,
            active = false,
            onClick = { if (currentPage < totalPages) onPageChange(currentPage + 1) }
        )
    }
}

@Composable
private fun RetroPageButton(text: String, enabled: Boolean, active: Boolean, onClick: () -> Unit) {
    Button(
        attrs = Modifier
            .backgroundColor(
                when {
                    active -> RetroColor.babyBlue
                    enabled -> NeoColor.backgroundPrimaryTransparent
                    else -> NeoColor.backgroundPrimaryTransparent.copy(alpha = 60)
                }
            )
            .border(2.px, LineStyle.Solid, if (active) RetroColor.navy else RetroColor.steelBlue.copy(alpha = 90))
            .borderRadius(4.px)
            .padding(10.px)
            .cursor(if (enabled) Cursor.Pointer else Cursor.NotAllowed)
            .onClick { if (enabled) onClick() }
            .toAttrs { if (!enabled) attr("disabled", "true") }
    ) {
        SpanText(
            text = text,
            modifier = Modifier
                .fontSize(1.1.cssRem)
                .fontWeight(FontWeight.Bold)
                .color(
                    when {
                        active -> NeoColor.backgroundPrimary
                        enabled -> RetroColor.cream
                        else -> NeoColor.white.copy(alpha = 120)
                    }
                )
        )
    }
}