package com.nostadroid.notes.util

import android.text.Html

// Helper method to strip Markdown and HTML tags
fun stripFormatting(input: String): String {
  // Convert HTML character entities to regular Unicode
  val decodedInput = Html.fromHtml(input, Html.FROM_HTML_MODE_LEGACY).toString()
  return decodedInput
    .replace(Regex("<[^>]*>"), "") // Strips HTML tags (e.g., <b>text</b> -> text)
    .replace(Regex("^#{1,6}\\s+"), "") // Strips Markdown Headers (e.g., # Header -> Header)
    .replace(Regex("\\*\\*|__|\\*|_"), "") // Strips Bold/Italic Markdown operators
    .replace(Regex("~~"), "") // Strip tildes (strikethrough operator)
    .replace(Regex("\\[(.*?)]\\(.*?\\)"), "$1") // Strips Links, keeping the display text
    .replace(Regex("- "), "") // Strip beginning of unordered lists
    .replace(Regex("\\d+\\. "), "") // Strip beginning of ordered lists
    .trim()
}