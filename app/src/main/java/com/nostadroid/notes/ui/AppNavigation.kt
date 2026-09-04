package com.nostadroid.notes.ui

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.nostadroid.notes.Screens
import com.nostadroid.notes.screen.home.HomeScreen
import com.nostadroid.notes.screen.settings.SettingsHomeScreen
import com.nostadroid.notes.ui.theme.NotesTheme

@Composable
fun AppNavigation() {
  val navController = rememberNavController()
  NotesTheme {
    NavHost(
      navController = navController,
      startDestination = Screens.Home.route
    ) {
      composable(route = Screens.Home.route) { HomeScreen(navController) } // Home screen doesn't have animations
      composableWithTransition(route = Screens.SettingsHome.route) { SettingsHomeScreen(navController) }
    }
  }
}

private fun NavGraphBuilder.composableWithTransition(
  route: String,
  content: @Composable (AnimatedContentScope.(NavBackStackEntry) -> Unit)
) {
  composable(
    route = route,
    enterTransition = {
      slideInHorizontally(
        initialOffsetX = { fullWidth -> (fullWidth * 0.1f).toInt() },
        animationSpec = tween(durationMillis = 300)
      ) + fadeIn(animationSpec = tween(durationMillis = 200))
    },
    exitTransition = { fadeOut(animationSpec = tween(durationMillis = 200)) },
    popEnterTransition = { fadeIn(animationSpec = tween(durationMillis = 300)) },
    popExitTransition = {
      slideOutHorizontally(
        targetOffsetX = { fullWidth -> (fullWidth * 0.1f).toInt() },
        animationSpec = tween(durationMillis = 300)
      ) + fadeOut(animationSpec = tween(durationMillis = 200))
    },
    content = content,
  )
}