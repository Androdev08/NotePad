package com.nostadroid.notes.screen.noteedit

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FormatAlignLeft
import androidx.compose.material.icons.automirrored.filled.FormatAlignRight
import androidx.compose.material.icons.automirrored.filled.FormatIndentIncrease
import androidx.compose.material.icons.automirrored.filled.FormatListBulleted
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.FormatAlignCenter
import androidx.compose.material.icons.filled.FormatAlignJustify
import androidx.compose.material.icons.filled.FormatBold
import androidx.compose.material.icons.filled.FormatItalic
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.FormatStrikethrough
import androidx.compose.material.icons.filled.FormatUnderlined
import androidx.compose.material.icons.filled.Subscript
import androidx.compose.material.icons.filled.Superscript
import androidx.compose.material.icons.filled.TextFormat
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import com.mohamedrejeb.richeditor.model.HeadingStyle
import com.mohamedrejeb.richeditor.model.RichTextState
import com.nostadroid.notes.model.FormatCategory
import com.nostadroid.notes.model.FormatOption

object FormatOptionsBuilder {
  fun buildCategories(
    state: RichTextState,
    screenViewModel: NoteEditViewModel,
    isHTML: Boolean
  ): List<FormatCategory> {
    val isCurrentTextBold =
      state.currentSpanStyle.fontWeight == FontWeight.Bold ||
          state.currentHeadingStyle != HeadingStyle.Normal
    // Declare the options for text formatting
    val textFormatOptions = listOf(
      // Bold
      FormatOption(
        icon = Icons.Default.FormatBold,
        enabled = isCurrentTextBold,
        htmlRequired = false) { _ -> state.toggleSpanStyle(SpanStyle(fontWeight = FontWeight.Bold)) },
      // Italic
      FormatOption(
        icon = Icons.Default.FormatItalic,
        enabled = state.currentSpanStyle.fontStyle == FontStyle.Italic,
        htmlRequired = false
      ) { _ -> state.toggleSpanStyle(SpanStyle(fontStyle = FontStyle.Italic)) },
      // Underlined
      FormatOption(
        icon = Icons.Default.FormatUnderlined,
        enabled = state.currentSpanStyle.textDecoration?.contains(TextDecoration.Underline) == true,
        htmlRequired = false
      ) { _ -> state.toggleSpanStyle(SpanStyle(textDecoration = TextDecoration.Underline)) },
      // Strikethrough
      FormatOption(
        icon = Icons.Default.FormatStrikethrough,
        enabled = state.currentSpanStyle.textDecoration?.contains(TextDecoration.LineThrough) == true,
        htmlRequired = false
      ) { _ -> state.toggleSpanStyle(SpanStyle(textDecoration = TextDecoration.LineThrough)) },
      // Heading styles
      FormatOption(
        icon = Icons.Default.FormatSize,
        enabled = state.currentHeadingStyle != HeadingStyle.Normal,
        htmlRequired = false
      ) { _ -> screenViewModel.setIsHeadingMenuShowing(true) },
      // Superscript
      FormatOption(
        icon = Icons.Default.Superscript,
        enabled = state.currentSpanStyle.baselineShift == BaselineShift.Superscript,
        htmlRequired = true
      ) { enabled ->
        if (enabled) {
          // If the selected text is a subscript, remove the style and replace it with superscript
          if(state.currentSpanStyle.baselineShift == BaselineShift.Subscript)
            state.removeSpanStyle(SpanStyle(baselineShift = BaselineShift.Subscript))
          state.addSpanStyle(SpanStyle(baselineShift = BaselineShift.Superscript))
        }
        else state.removeSpanStyle(SpanStyle(baselineShift = BaselineShift.Superscript))
      },
      // Subscript
      FormatOption(
        icon = Icons.Default.Subscript,
        enabled = state.currentSpanStyle.baselineShift == BaselineShift.Subscript,
        htmlRequired = true
      ) { enabled ->
        if (enabled) {
          // If the selected text is a superscript, remove the style and replace it with subscript
          if(state.currentSpanStyle.baselineShift == BaselineShift.Superscript)
            state.removeSpanStyle(SpanStyle(baselineShift = BaselineShift.Superscript))
          state.addSpanStyle(SpanStyle(baselineShift = BaselineShift.Subscript))
        }
        else state.removeSpanStyle(SpanStyle(baselineShift = BaselineShift.Subscript))
      },
    )
    val listOptions = listOf(
      // Unordered list
      FormatOption(
        icon = Icons.AutoMirrored.Default.FormatListBulleted,
        enabled = state.isUnorderedList,
        htmlRequired = false
      ) { enabled -> if (enabled) state.addUnorderedList() else state.removeUnorderedList() },
      // Ordered list
      FormatOption(
        icon = Icons.Default.FormatListNumbered,
        enabled = state.isOrderedList,
        htmlRequired = false
      ) { enabled -> if (enabled) state.addOrderedList() else state.removeOrderedList() },
      // Increase list indentation
      FormatOption(
        icon = Icons.AutoMirrored.Default.FormatIndentIncrease,
        enabled = false,
        htmlRequired = false
      ) { _ -> state.increaseListLevel() },
    )
    val alignOptions = listOf(
      // Left align
      FormatOption(
        icon = Icons.AutoMirrored.Default.FormatAlignLeft,
        enabled = state.currentParagraphStyle.textAlign == TextAlign.Left,
        htmlRequired = true
      ) { _ -> state.toggleParagraphStyle(ParagraphStyle(textAlign = TextAlign.Left)) },
      // Center align
      FormatOption(
        icon = Icons.Default.FormatAlignCenter,
        enabled = state.currentParagraphStyle.textAlign == TextAlign.Center,
        htmlRequired = true
      ) { _ -> state.toggleParagraphStyle(ParagraphStyle(textAlign = TextAlign.Center)) },
      // Right align
      FormatOption(
        icon = Icons.AutoMirrored.Default.FormatAlignRight,
        enabled = state.currentParagraphStyle.textAlign == TextAlign.Right,
        htmlRequired = true
      ) { _ -> state.toggleParagraphStyle(ParagraphStyle(textAlign = TextAlign.Right)) },
    )

    return listOf(
      FormatCategory("text", Icons.Default.TextFormat, textFormatOptions),
      FormatCategory("list", Icons.AutoMirrored.Default.List, listOptions),
      FormatCategory("alignment", Icons.Default.FormatAlignJustify, alignOptions, htmlOnly = true)
    ).filter { !it.htmlOnly || isHTML }
  }
}