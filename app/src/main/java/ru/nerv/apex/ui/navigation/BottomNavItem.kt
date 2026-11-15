package ru.nerv.apex.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import ru.nerv.apex.R

sealed class BottomNavItem(
    val route: String, val icon: ImageVector, @StringRes val titleResId: Int
) {
    data object Races : BottomNavItem(
        "races", Icons.Default.DateRange, R.string.nav_bar_races
    )

    data object Teams : BottomNavItem(
        "teams", Icons.Default.Person, R.string.nav_bar_teams
    )

    data object Settings : BottomNavItem(
            "settings", Icons.Default.Settings, R.string.nav_bar_settings
    )

    data object About : BottomNavItem(
        "about", Icons.Default.Info, R.string.nav_bar_about
    )
}