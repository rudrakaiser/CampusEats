package com.mad.campuseats.navigation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.mad.campuseats.ui.components.BottomNavBar
import com.mad.campuseats.ui.screens.*
import com.mad.campuseats.viewmodel.AppViewModel

@Composable
fun CampusEatsApp(viewModel: AppViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val state by viewModel.state.collectAsState()

    Scaffold(
        // Every screen handles its own status/navigation-bar insets (edge-to-edge look).
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            if (currentRoute in bottomNavRoutes) {
                BottomNavBar(
                    currentRoute = currentRoute,
                    cartCount = state.cart.sumOf { it.quantity },
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(Screen.Home.route) { inclusive = false }
                            launchSingleTop = true
                        }
                    }
                )
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Login.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(Screen.Login.route) { LoginScreen(navController, viewModel) }
            composable(Screen.Register.route) { RegisterScreen(navController, viewModel) }
            composable(Screen.AdminLogin.route) { AdminLoginScreen(navController, viewModel) }

            composable(Screen.Home.route) { HomeScreen(navController, viewModel) }
            composable(Screen.Cart.route) { CartScreen(navController, viewModel) }
            composable(Screen.Orders.route) { OrdersScreen(navController, viewModel) }
            composable(Screen.Profile.route) { ProfileScreen(navController, viewModel) }

            composable(
                route = Screen.RestaurantDetail.route,
                arguments = listOf(navArgument("restaurantId") { type = NavType.StringType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getString("restaurantId").orEmpty()
                RestaurantDetailScreen(navController, viewModel, id)
            }

            composable(Screen.Checkout.route) { CheckoutScreen(navController, viewModel) }

            composable(
                route = Screen.OrderTracking.route,
                arguments = listOf(navArgument("orderId") { type = NavType.StringType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getString("orderId").orEmpty()
                OrderTrackingScreen(navController, viewModel, id)
            }

            composable(
                route = Screen.Review.route,
                arguments = listOf(navArgument("orderId") { type = NavType.StringType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getString("orderId").orEmpty()
                ReviewScreen(navController, viewModel, id)
            }

            composable(
                route = Screen.Complaint.route,
                arguments = listOf(navArgument("orderId") { type = NavType.StringType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getString("orderId").orEmpty()
                ComplaintScreen(navController, viewModel, id)
            }

            composable(Screen.AdminDashboard.route) { AdminDashboardScreen(navController, viewModel) }

            composable(
                route = Screen.ManageMenu.route,
                arguments = listOf(navArgument("restaurantId") { type = NavType.StringType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getString("restaurantId").orEmpty()
                ManageMenuScreen(navController, viewModel, id)
            }
        }
    }
}
