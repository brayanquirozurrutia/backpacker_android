package com.example.backpaker_android

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.backpaker_android.ui.screens.LoginScreen

@Composable
fun AppNavigation(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = "login",
        modifier = modifier
    ) {
        composable("login") {
            LoginScreen(onLoginSuccess = {
                navController.navigate("home")
            })
        }
        composable("home") {
            //HomeScreen()
        }
    }
}
