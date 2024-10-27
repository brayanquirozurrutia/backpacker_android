package com.example.backpaker_android.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.backpaker_android.navigation.Routes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.height

@Composable
fun CommonNavigationBar(
    navController: NavController,
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = Color.White,
        modifier = Modifier.height(56.dp)
    ) {
        val items = listOf(
            NavigationItem(Routes.HOME, Icons.Filled.Home, "Inicio"),
            NavigationItem(Routes.SEARCH, Icons.Filled.Search, "Buscar"),
            NavigationItem(Routes.TRIP, Icons.Filled.Add, "Viajar"),
            NavigationItem(Routes.FAVORITES, Icons.Filled.Favorite, "Favoritos"),
            NavigationItem(Routes.PROFILE, Icons.Filled.Person, "Perfil")
        )

        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            launchSingleTop = true
                            popUpTo(Routes.HOME) {
                                saveState = true
                            }
                            restoreState = true
                        }
                    }
                },
                alwaysShowLabel = true,
                colors = NavigationBarItemDefaults.colors(
                    unselectedIconColor = Color.LightGray,
                    selectedIconColor = Color.White
                )
            )
        }
    }
}

data class NavigationItem(
    val route: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val label: String
)
