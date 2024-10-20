package com.example.backpaker_android

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.backpaker_android.navigation.Routes
import com.example.backpaker_android.ui.components.CommonNavigationBar
import com.example.backpaker_android.ui.components.TripDialog
import com.example.backpaker_android.ui.screens.auth.AccountActivationScreen
import com.example.backpaker_android.ui.screens.auth.CreateAccountScreen
import com.example.backpaker_android.ui.screens.auth.ForgotPasswordScreen
import com.example.backpaker_android.ui.screens.home.HomeScreen
import com.example.backpaker_android.ui.screens.auth.LoginScreen
import com.example.backpaker_android.viewmodel.trip.TripViewModel


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val bottomBarRoutes = listOf(Routes.HOME, Routes.TRIP)
    var showTripDialog by remember { mutableStateOf(false) }
    val tripViewModel: TripViewModel = viewModel()

    val context = LocalContext.current

    Scaffold(
        bottomBar = {
            if (currentRoute in bottomBarRoutes) {
                CommonNavigationBar(
                    navController = navController,
                    onTripClick = { showTripDialog = true }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.LOGIN,
            modifier = modifier.padding(innerPadding)
        ) {
            // -------- Auth --------
            composable(Routes.LOGIN) {
                LoginScreen(
                    onLoginSuccess = { navController.navigate(Routes.HOME) },
                    onCreateAccount = { navController.navigate(Routes.CREATE_ACCOUNT) },
                    onNavigateToActivateAccount = { navController.navigate(Routes.ACTIVATE_ACCOUNT) },
                    onForgotPassword = { navController.navigate(Routes.FORGOT_PASSWORD) }
                )
            }
            composable(Routes.CREATE_ACCOUNT) {
                CreateAccountScreen(
                    onRegisterSuccess = { navController.navigate(Routes.ACTIVATE_ACCOUNT) },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Routes.ACTIVATE_ACCOUNT) {
                AccountActivationScreen(
                    onActivationSuccess = { navController.navigate(Routes.LOGIN) }
                )
            }
            composable(Routes.FORGOT_PASSWORD) {
                ForgotPasswordScreen(
                    onPasswordResetSuccess = { navController.navigate(Routes.LOGIN) },
                    onBack = { navController.popBackStack() }
                )
            }

            // -------- Home --------
            composable(Routes.HOME) {
                HomeScreen(navController = navController)
            }
        }
        if (showTripDialog) {
            TripDialog(
                onDismiss = { showTripDialog = false },
                onConfirm = { destination ->
                    if (destination.isNotBlank()) {
                        // Llama al ViewModel solo cuando el destino no esté vacío.
                        tripViewModel.sendTrip(destination, context)
                    }
                    showTripDialog = false
                },
                isLoading = tripViewModel.isLoading.collectAsState().value,
                errorMessage = tripViewModel.errorMessage.collectAsState().value
            )

        }
    }
}