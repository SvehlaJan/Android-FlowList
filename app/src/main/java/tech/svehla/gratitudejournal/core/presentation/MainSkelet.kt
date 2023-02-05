package tech.svehla.gratitudejournal.core.presentation

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
import androidx.navigation.*
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import tech.svehla.gratitudejournal.R
import tech.svehla.gratitudejournal.detail.presentation.DetailScreenRoute
import tech.svehla.gratitudejournal.history.presentation.HistoryScreenRoute
import tech.svehla.gratitudejournal.settings.presentation.SettingsScreenRoute
import java.time.LocalDate

val items = listOf(
    NavRoute.History,
    NavRoute.Detail,
    NavRoute.Settings
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
                    val route = if (screen == NavRoute.Detail) {
                        NavRoute.Detail.constructRoute(LocalDate.now().toString())
                    } else {
                        screen.route
                    }
                    navController.navigate(route) {
                        // Pop up to the start destination of the graph to
                        // avoid building up a large stack of destinations
                        // on the back stack as users select items
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = false
                            inclusive = true
                        }
                        // Avoid multiple copies of the same destination when
                        // reselecting the same item
                        launchSingleTop = true
                        // Restore state when reselecting a previously selected item
                        restoreState = false
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
        startDestination = NavRoute.History.route,
        Modifier.padding(innerPadding)
    ) {
        composable(NavRoute.History.route) {
            HistoryScreenRoute(
                onNavigateToDetail = {
                    navController.navigate("${NavRoute.Detail.route}/$it") {
//                    popUpTo(NavScreen.History.route) {
//                        inclusive = true
//                    }
                    }
                },
            )
        }
        composable(
            route = NavRoute.Detail.routeWithArgument,
            arguments = listOf(navArgument(NavRoute.Detail.argument0) {
                type = NavType.StringType
                defaultValue = LocalDate.now().toString()
            }),
        ) { backStackEntry ->
            val date = backStackEntry.arguments?.getString(NavRoute.Detail.argument0)
                ?: return@composable

            DetailScreenRoute(
                date = date,
                onBackPressed = {
                    navController.navigateUp()
                }
            )
        }
        composable(NavRoute.Settings.route) {
            SettingsScreenRoute()
        }
    }
}

sealed class NavRoute(val route: String, @StringRes val resourceId: Int, val icon: ImageVector) {
    object History : NavRoute("history", R.string.menu_history, Icons.Filled.List)
    object Detail : NavRoute("detail", R.string.menu_journal, Icons.Filled.Add) {
        const val routeWithArgument: String = "detail/{date}"
        const val argument0: String = "date"
        fun constructRoute(date: String): String {
            return "${route}/${date}"
        }
    }

    object Settings : NavRoute("settings", R.string.menu_settings, Icons.Filled.Settings)
}