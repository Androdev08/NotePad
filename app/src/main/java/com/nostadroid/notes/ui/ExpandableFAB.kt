package com.nostadroid.notes.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.nostadroid.notes.screen.home.HomeViewModel
import com.nostadroid.notes.screen.noteedit.NoteEditScreen

@Composable
fun ExpandableFAB(
  isExpanded: Boolean,
  onClick: () -> Unit,
  onExpandedChange: (Boolean) -> Unit,
  viewModel: HomeViewModel,
  modifier: Modifier = Modifier
) {
  val animatedPadding by animateIntAsState(if(isExpanded) 0 else 16)
  val navBarPadding = WindowInsets.navigationBars.asPaddingValues()
  val layoutDirection = LocalLayoutDirection.current
  val isRtl = layoutDirection == LayoutDirection.Rtl

  AnimatedContent(
    targetState = isExpanded,
    modifier = modifier
      .padding(
        bottom = navBarPadding.calculateBottomPadding() + animatedPadding.dp,
        end = navBarPadding.calculateEndPadding(if (isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr) + animatedPadding.dp
      )
  ) { expanded ->
    if (expanded) {
      // Transition into the new note screen
      // The export option isn't shown at the new note screen
      NoteEditScreen(false, null, viewModel, {}) { show ->
        onExpandedChange(show)
      }
    } else {
      // Use the floating action button
      FloatingActionButton(onClick = onClick) {
        Icon(Icons.Default.Add, contentDescription = null)
      }
    }
  }
}