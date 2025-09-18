package com.giraone.archiver.ui.components

import android.content.Context
import android.widget.TextView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import io.noties.markwon.Markwon
import java.util.Locale

@Composable
fun MarkdownDisplay(
    content: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val markwon = remember { Markwon.create(context) }

    AndroidView(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        factory = { ctx ->
            TextView(ctx).apply {
                textSize = 16f
                setPadding(16, 16, 16, 16)
            }
        },
        update = { textView ->
            markwon.setMarkdown(textView, content)
        }
    )
}

@Composable
fun HelpMarkdownDisplay(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val markdownContent = remember { loadHelpContent(context) }

    MarkdownDisplay(
        content = markdownContent,
        modifier = modifier
    )
}

private fun loadHelpContent(context: Context): String {
    val locale = Locale.getDefault().language
    val filename = when (locale) {
        "de" -> "help_de.md"
        "fr" -> "help_fr.md"
        else -> "help_en.md"
    }

    return try {
        context.assets.open(filename).bufferedReader().use { it.readText() }
    } catch (e: Exception) {
        // Fallback to English if the localized version is not available
        try {
            context.assets.open("help_en.md").bufferedReader().use { it.readText() }
        } catch (e: Exception) {
            "Help content not available."
        }
    }
}