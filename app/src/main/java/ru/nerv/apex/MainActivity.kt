package ru.nerv.apex

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import dagger.hilt.android.AndroidEntryPoint
import ru.nerv.apex.data.settings.SettingsRepository
import ru.nerv.apex.ui.about.AboutScreen
import ru.nerv.apex.ui.navigation.BottomNavItem
import ru.nerv.apex.ui.races.RaceDetailScreen
import ru.nerv.apex.ui.races.RacesScreen
import ru.nerv.apex.ui.settings.SettingsScreen
import ru.nerv.apex.ui.teams.TeamsScreen
import ru.nerv.apex.ui.theme.ApexTheme
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var settingsRepository: SettingsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeSetting by settingsRepository.themeFlow.collectAsStateWithLifecycle(
                initialValue = "dark"
            )

            ApexTheme(themeSetting = themeSetting) {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                val showBottomBar = currentDestination?.route in listOf(
                    BottomNavItem.Races.route,
                    BottomNavItem.Teams.route,
                    BottomNavItem.Settings.route,
                    BottomNavItem.About.route
                )

                Scaffold(
                    bottomBar = {
                        if (showBottomBar) {
                            NavigationBar {
                                val items = listOf(
                                    BottomNavItem.Races,
                                    BottomNavItem.Teams,
                                    BottomNavItem.Settings,
                                    BottomNavItem.About
                                )
                                items.forEach { item ->
                                    NavigationBarItem(
                                        icon = {
                                            Icon(
                                                item.icon,
                                                contentDescription = stringResource(item.titleResId)
                                            )
                                        },
                                        label = { Text(stringResource(item.titleResId)) },
                                        selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                                        onClick = {
                                            navController.navigate(item.route) {
                                                popUpTo(navController.graph.findStartDestination().id) {
                                                    saveState = true
                                                }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = BottomNavItem.Races.route,
                    ) {
                        composable(BottomNavItem.Races.route) {
                            RacesScreen(
                                onRaceClick = { roundId -> navController.navigate("race/$roundId") },
                                contentPadding = innerPadding
                            )
                        }
                        composable(
                            route = "race/{roundId}",
                            arguments = listOf(navArgument("roundId") { type = NavType.StringType })
                        ) {
                            RaceDetailScreen(onBackClick = { navController.popBackStack() })
                        }
                        composable(BottomNavItem.Teams.route) {
                            TeamsScreen(contentPadding = innerPadding)
                        }
                        composable(BottomNavItem.Settings.route) {
                            SettingsScreen(onBackClick = { navController.popBackStack() })
                        }
                        composable(BottomNavItem.About.route) {
                            AboutScreen()
                        }
                    }
                }
            }
        }
    }
}