package com.nostadroid.notes.screen.noteedit

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FormatAlignLeft
import androidx.compose.material.icons.automirrored.filled.FormatAlignRight
import androidx.compose.material.icons.automirrored.filled.FormatIndentDecrease
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
      FormatOption(Icons.Default.FormatBold, isCurrentTextBold, false) { _ ->
        state.toggleSpanStyle(SpanStyle(fontWeight = FontWeight.Bold))
      }, // Bold
      FormatOption(
        Icons.Default.FormatItalic,
        state.currentSpanStyle.fontStyle == FontStyle.Italic, false
      ) { _ ->
        state.toggleSpanStyle(SpanStyle(fontStyle = FontStyle.Italic))
      }, // Italic
      FormatOption(
        Icons.Default.FormatUnderlined,
        state.currentSpanStyle.textDecoration?.contains(TextDecoration.Underline) == true,
        false
      ) { _ ->
        state.toggleSpanStyle(SpanStyle(textDecoration = TextDecoration.Underline))
      }, // Underlined
      FormatOption(
        Icons.Default.FormatStrikethrough,
        state.currentSpanStyle.textDecoration?.contains(TextDecoration.LineThrough) == true,
        false
      ) { _ ->
        state.toggleSpanStyle(SpanStyle(textDecoration = TextDecoration.LineThrough))
      }, // Strikethrough
      FormatOption(
        Icons.Default.FormatSize,
        state.currentHeadingStyle != HeadingStyle.Normal, false
      ) { _ ->
        screenViewModel.setIsHeadingMenuShowing(true)
      }, // Heading styles
      FormatOption(
        Icons.Default.Superscript,
        state.currentSpanStyle.baselineShift == BaselineShift.Superscript, true
      ) { enabled ->
        if (enabled) {
          // If the selected text is a subscript, remove the style and replace it with superscript
          if(state.currentSpanStyle.baselineShift == BaselineShift.Subscript)
            state.removeSpanStyle(SpanStyle(baselineShift = BaselineShift.Subscript))
          state.addSpanStyle(SpanStyle(baselineShift = BaselineShift.Superscript))
        }
        else state.removeSpanStyle(SpanStyle(baselineShift = BaselineShift.Superscript))
      }, // Superscript
      FormatOption(
        Icons.Default.Subscript,
        state.currentSpanStyle.baselineShift == BaselineShift.Subscript, true
      ) { enabled ->
        if (enabled) {
          // If the selected text is a superscript, remove the style and replace it with subscript
          if(state.currentSpanStyle.baselineShift == BaselineShift.Superscript)
            state.removeSpanStyle(SpanStyle(baselineShift = BaselineShift.Superscript))
          state.addSpanStyle(SpanStyle(baselineShift = BaselineShift.Subscript))
        }
        else state.removeSpanStyle(SpanStyle(baselineShift = BaselineShift.Subscript))
      }, // Subscript
    )
    val listOptions = listOf(
      FormatOption(
        Icons.AutoMirrored.Default.FormatListBulleted,
        state.isUnorderedList,
        false
      ) { enabled ->
        if (enabled) state.addUnorderedList() else state.removeUnorderedList()
      }, // Unordered list
      FormatOption(
        Icons.Default.FormatListNumbered,
        state.isOrderedList,
        false
      ) { enabled ->
        if (enabled) state.addOrderedList() else state.removeOrderedList()
      }, // Ordered list
      FormatOption(
        Icons.AutoMirrored.Default.FormatIndentIncrease,
        enabled = false,
        htmlRequired = false
      ) { _ -> state.increaseListLevel() }, // Increase list indentation
      FormatOption(
        Icons.AutoMirrored.Default.FormatIndentDecrease,
        enabled = false,
        htmlRequired = false
      ) { _ -> state.decreaseListLevel() }, // Decrease list indentation
    )
    val alignOptions = listOf(
      FormatOption(
        Icons.AutoMirrored.Default.FormatAlignLeft,
        state.currentParagraphStyle.textAlign == TextAlign.Left, true
      ) { _ ->
        state.toggleParagraphStyle(ParagraphStyle(textAlign = TextAlign.Left))
      }, // Left align
      FormatOption(
        Icons.Default.FormatAlignCenter,
        state.currentParagraphStyle.textAlign == TextAlign.Center, true
      ) { _ ->
        state.toggleParagraphStyle(ParagraphStyle(textAlign = TextAlign.Center))
      }, // Center align
      FormatOption(
        Icons.AutoMirrored.Default.FormatAlignRight,
        state.currentParagraphStyle.textAlign == TextAlign.Right, true
      ) { _ ->
        state.toggleParagraphStyle(ParagraphStyle(textAlign = TextAlign.Right))
      }, // Right align
    )

    return listOf(
      FormatCategory("text", Icons.Default.TextFormat, textFormatOptions),
      FormatCategory("list", Icons.AutoMirrored.Default.List, listOptions),
      FormatCategory("alignment", Icons.Default.FormatAlignJustify, alignOptions, htmlOnly = true)
    ).filter { !it.htmlOnly || isHTML }
  }
}