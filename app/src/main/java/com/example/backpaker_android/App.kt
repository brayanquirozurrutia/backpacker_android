package com.example.backpaker_android

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.backpaker_android.ui.screens.auth.AccountActivationScreen
import com.example.backpaker_android.ui.screens.auth.CreateAccountScreen
import com.example.backpaker_android.ui.screens.auth.ForgotPasswordScreen
import com.example.backpaker_android.ui.screens.home.HomeScreen
import com.example.backpaker_android.ui.screens.auth.LoginScreen


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = "login",
        modifier = modifier
    ) {
        composable("login") {
            LoginScreen(
                onLoginSuccess = { navController.navigate("home") },
                onCreateAccount = { navController.navigate("create_account") },
                onNavigateToActivateAccount = { navController.navigate("activate_account") },
                onForgotPassword = { navController.navigate("forgot_password") }
            )
        }
        composable("home") {
            HomeScreen()
        }
        composable("create_account") {
            CreateAccountScreen(
                onRegisterSuccess = { navController.navigate("activate_account") },
                onBack = { navController.popBackStack() }
            )
        }
        composable("activate_account") {
            AccountActivationScreen(
                onActivationSuccess = { navController.navigate("login") }
            )
        }
        composable("forgot_password") {
            ForgotPasswordScreen(
                onPasswordResetSuccess = { navController.navigate("login") },
                onBack = { navController.popBackStack() }
            )
        }
    }
}

