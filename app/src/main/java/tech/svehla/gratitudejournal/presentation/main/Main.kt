package tech.svehla.gratitudejournal.presentation.main

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.*
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import tech.svehla.gratitudejournal.R
import tech.svehla.gratitudejournal.presentation.detail.DetailScreen
import tech.svehla.gratitudejournal.presentation.history.HistoryScreen
import tech.svehla.gratitudejournal.presentation.settings.SettingsScreen
import java.time.LocalDate

val items = listOf(
    NavScreen.History,
    NavScreen.Detail,
    NavScreen.Settings
)

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
//        topBar = {
//            AppTopBar(
//                navController = navController,
//                title = "Gratitude Journal"
//            )
//        },
        bottomBar = {
            AppBottomBar(
                navController,
                currentDestination
            )
        }
    ) { innerPadding ->
        AppNavHost(
            navController = navController,
            innerPadding = innerPadding
        )
    }
}

@Composable
fun AppTopBar(
    navController: NavController,
    title: String,
    modifier: Modifier = Modifier,
    showBackButton: Boolean = false
) {
    TopAppBar(
        title = { Text(title) },
        navigationIcon = {
            if (showBackButton) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                }
            }
        },
        modifier = modifier
    )
}

@Composable
fun AppBottomBar(
    navController: NavController,
    currentDestination: NavDestination?
) {
    BottomNavigation {
        items.forEach { screen ->
            BottomNavigationItem(
                icon = {
                    Icon(
                        screen.icon,
                        contentDescription = stringResource(screen.resourceId)
                    )
                },
                label = { Text(stringResource(screen.resourceId)) },
                selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                onClick = {
                    val route = if (screen == NavScreen.Detail) {
                        NavScreen.Detail.constructRoute(LocalDate.now().toString())
                    } else {
                        screen.route
                    }
                    navController.navigate(route) {
                        // Pop up to the start destination of the graph to
                        // avoid building up a large stack of destinations
                        // on the back stack as users select items
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
//                            inclusive = true
                        }
                        // Avoid multiple copies of the same destination when
                        // reselecting the same item
                        launchSingleTop = true
                        // Restore state when reselecting a previously selected item
                        restoreState = true
                    }
                },
            )
        }
    }
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    innerPadding: PaddingValues = PaddingValues(top = 16.dp, bottom = 16.dp)
) {
    NavHost(
        navController,
        startDestination = NavScreen.Detail.routeWithArgument,
        Modifier.padding(innerPadding)
    ) {
        composable(NavScreen.History.route) {
            HistoryScreen(
                viewModel = hiltViewModel()
            ) {
                navController.navigate("${NavScreen.Detail.route}/$it")
            }
        }
        composable(
            route = NavScreen.Detail.routeWithArgument,
            arguments = listOf(navArgument(NavScreen.Detail.argument0) {
                type = NavType.StringType
                defaultValue = LocalDate.now().toString()
            }),
        ) { backStackEntry ->
            val date = backStackEntry.arguments?.getString(NavScreen.Detail.argument0)
                ?: return@composable

            DetailScreen(
                date = date,
                viewModel = hiltViewModel(),
                onBackPressed = {
                    navController.popBackStack()
                }
            )
        }
        composable(NavScreen.Settings.route) {
            SettingsScreen(
                viewModel = hiltViewModel()
            )
        }
    }
}

sealed class NavScreen(val route: String, @StringRes val resourceId: Int, val icon: ImageVector) {
    object History : NavScreen("history", R.string.menu_history, Icons.Filled.List)
    object Detail : NavScreen("detail", R.string.menu_journal, Icons.Filled.Add) {
        const val routeWithArgument: String = "detail/{date}"
        const val argument0: String = "date"
        fun constructRoute(date: String): String {
            return "${route}/${date}"
        }

    }

    object Settings : NavScreen("settings", R.string.menu_settings, Icons.Filled.Settings)
}