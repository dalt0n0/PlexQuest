package com.plexquest.app.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.plexquest.app.ui.screens.HomeScreen
import com.plexquest.app.ui.screens.LibraryScreen
import com.plexquest.app.ui.screens.LoginScreen
import com.plexquest.app.ui.screens.PlayerScreen
import com.plexquest.app.ui.screens.SearchScreen
import com.plexquest.app.ui.screens.ServerPickerScreen

object Routes {
    const val LOGIN = "login"
    const val SERVER_PICKER = "server_picker"
    const val HOME = "home"
    const val LIBRARY = "library/{sectionId}/{title}"
    const val SEARCH = "search"
    const val PLAYER = "player/{ratingKey}"

    fun library(sectionId: String, title: String) = "library/$sectionId/${title.encodeForNav()}"
    fun player(ratingKey: String) = "player/$ratingKey"

    private fun String.encodeForNav() = java.net.URLEncoder.encode(this, "UTF-8")
}

@Composable
fun PlexQuestNavHost() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.LOGIN) {

        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginSuccess = { navController.navigate(Routes.SERVER_PICKER) {
                    popUpTo(Routes.LOGIN) { inclusive = true }
                }}
            )
        }

        composable(Routes.SERVER_PICKER) {
            ServerPickerScreen(
                onServerSelected = { navController.navigate(Routes.HOME) {
                    popUpTo(Routes.SERVER_PICKER) { inclusive = true }
                }}
            )
        }

        composable(Routes.HOME) {
            HomeScreen(
                onLibraryClick = { id, title -> navController.navigate(Routes.library(id, title)) },
                onMediaClick = { ratingKey -> navController.navigate(Routes.player(ratingKey)) },
                onSearchClick = { navController.navigate(Routes.SEARCH) },
            )
        }

        composable(
            route = Routes.LIBRARY,
            arguments = listOf(
                navArgument("sectionId") { type = NavType.StringType },
                navArgument("title") { type = NavType.StringType },
            )
        ) { back ->
            LibraryScreen(
                sectionId = back.arguments?.getString("sectionId") ?: "",
                title = back.arguments?.getString("title")?.let {
                    java.net.URLDecoder.decode(it, "UTF-8")
                } ?: "",
                onMediaClick = { ratingKey -> navController.navigate(Routes.player(ratingKey)) },
                onBack = { navController.popBackStack() },
            )
        }

        composable(Routes.SEARCH) {
            SearchScreen(
                onMediaClick = { ratingKey -> navController.navigate(Routes.player(ratingKey)) },
                onBack = { navController.popBackStack() },
            )
        }

        composable(
            route = Routes.PLAYER,
            arguments = listOf(navArgument("ratingKey") { type = NavType.StringType })
        ) { back ->
            PlayerScreen(
                ratingKey = back.arguments?.getString("ratingKey") ?: "",
                onBack = { navController.popBackStack() },
            )
        }
    }
}
