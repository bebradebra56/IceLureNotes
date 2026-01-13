package com.icelurenote.sotfap.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavigationItem(
    val route: String,
    val icon: ImageVector,
    val label: String
) {
    object Journal : BottomNavigationItem(
        route = Screen.Journal.route,
        icon = Icons.Default.List,
        label = "Journal"
    )
    
    object Baits : BottomNavigationItem(
        route = Screen.Baits.route,
        icon = Icons.Default.Search,
        label = "Baits"
    )
    
    object Results : BottomNavigationItem(
        route = Screen.Results.route,
        icon = Icons.Default.Star,
        label = "Results"
    )
    
    object Settings : BottomNavigationItem(
        route = Screen.Settings.route,
        icon = Icons.Default.Settings,
        label = "Settings"
    )
}

val bottomNavigationItems = listOf(
    BottomNavigationItem.Journal,
    BottomNavigationItem.Baits,
    BottomNavigationItem.Results,
    BottomNavigationItem.Settings
)

