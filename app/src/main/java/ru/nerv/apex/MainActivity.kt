package ru.nerv.apex

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import dagger.hilt.android.AndroidEntryPoint
import ru.nerv.apex.data.settings.SettingsRepository
import ru.nerv.apex.ui.races.RaceDetailScreen
import ru.nerv.apex.ui.races.RacesScreen
import ru.nerv.apex.ui.settings.SettingsScreen
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
                initialValue = "system"
            )

            ApexTheme(themeSetting = themeSetting) {
                Surface {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = "races") {
                        composable("races") {
                            RacesScreen(
                                onRaceClick = { roundId -> navController.navigate("race/$roundId") },
                                onSettingsClick = { navController.navigate("settings") }
                            )
                        }
                        composable(
                            route = "race/{roundId}",
                            arguments = listOf(navArgument("roundId") { type = NavType.StringType })
                        ) {
                            RaceDetailScreen(onBackClick = { navController.popBackStack() })
                        }
                        composable("settings") {
                            SettingsScreen(onBackClick = { navController.popBackStack() })
                        }
                    }
                }
            }
        }
    }
}