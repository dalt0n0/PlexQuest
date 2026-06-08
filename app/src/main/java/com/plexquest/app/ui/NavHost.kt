package com.plexquest.app.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.plexquest.app.ui.screens.DetailScreen
import com.plexquest.app.ui.screens.HomeScreen
import com.plexquest.app.ui.screens.LibraryScreen
import com.plexquest.app.ui.screens.LoginScreen
import com.plexquest.app.ui.screens.PlexOAuthScreen
import com.plexquest.app.ui.screens.PlayerScreen
import com.plexquest.app.ui.screens.SearchScreen
import com.plexquest.app.ui.screens.ServerPickerScreen
import com.plexquest.app.ui.screens.SettingsScreen
import com.plexquest.app.viewmodel.SplashViewModel

object Routes {
    const val LOGIN = "login"
    const val OAUTH = "oauth"
    const val SERVER_PICKER = "server_picker"
    const val HOME = "home"
    const val LIBRARY = "library/{sectionId}/{title}"
    const val DETAIL = "detail/{ratingKey}"
    const val SEARCH = "search"
    const val PLAYER = "player/{ratingKey}"
    const val SETTINGS = "settings"

    fun library(sectionId: String, title: String) = "library/$sectionId/${title.encodeForNav()}"
    fun detail(ratingKey: String) = "detail/$ratingKey"
    fun player(ratingKey: String) = "player/$ratingKey"

    private fun String.encodeForNav() = java.net.URLEncoder.encode(this, "UTF-8")
}

@Composable
fun PlexQuestNavHost(vm: SplashViewModel = hiltViewModel()) {
    val startDest by vm.startDestination.collectAsState()

    if (startDest == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    // Snapshot once — NavHost must never see startDestination change after creation.
    // If startDest flowed again (e.g. servers saved mid-flow), NavController would
    // rebuild its graph and collide with in-flight programmatic navigation.
    val initialDest = remember { startDest!! }
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = initialDest) {

        composable(Routes.LOGIN) {
            LoginScreen(
                onSignInWithPlex = {
                    navController.navigate(Routes.OAUTH)
                },
                onLoginSuccess = {
                    navController.navigate(Routes.SERVER_PICKER) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
            )
        }

        composable(Routes.OAUTH) {
            PlexOAuthScreen(
                onSuccess = {
                    navController.navigate(Routes.SERVER_PICKER) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onCancel = { navController.popBackStack() },
            )
        }

        composable(Routes.SERVER_PICKER) {
            ServerPickerScreen(
                onServerSelected = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.SERVER_PICKER) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.HOME) {
            HomeScreen(
                onLibraryClick = { id, title -> navController.navigate(Routes.library(id, title)) },
                onMediaClick = { ratingKey -> navController.navigate(Routes.detail(ratingKey)) },
                onSearchClick = { navController.navigate(Routes.SEARCH) },
                onSettingsClick = { navController.navigate(Routes.SETTINGS) },
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
                title = back.arguments?.getString("title")?.decoded() ?: "",
                onMediaClick = { ratingKey -> navController.navigate(Routes.detail(ratingKey)) },
                onBack = { navController.popBackStack() },
            )
        }

        composable(
            route = Routes.DETAIL,
            arguments = listOf(navArgument("ratingKey") { type = NavType.StringType }),
        ) { back ->
            DetailScreen(
                ratingKey = back.arguments?.getString("ratingKey") ?: "",
                onPlay = { ratingKey -> navController.navigate(Routes.player(ratingKey)) },
                onChildClick = { ratingKey -> navController.navigate(Routes.detail(ratingKey)) },
                onBack = { navController.popBackStack() },
            )
        }

        composable(Routes.SEARCH) {
            SearchScreen(
                onMediaClick = { ratingKey -> navController.navigate(Routes.detail(ratingKey)) },
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

        composable(Routes.SETTINGS) {
            SettingsScreen(
                onBack = { navController.popBackStack() },
                onLoggedOut = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                },
            )
        }
    }
}

private fun String.decoded() = java.net.URLDecoder.decode(this, "UTF-8")
