package com.mad.campuseats.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object AdminLogin : Screen("admin_login")

    object Home : Screen("home")
    object Cart : Screen("cart")
    object Orders : Screen("orders")
    object Profile : Screen("profile")

    object RestaurantDetail : Screen("restaurant/{restaurantId}") {
        fun createRoute(restaurantId: String) = "restaurant/$restaurantId"
    }

    object Checkout : Screen("checkout")

    object OrderTracking : Screen("order_tracking/{orderId}") {
        fun createRoute(orderId: String) = "order_tracking/$orderId"
    }

    object Review : Screen("review/{orderId}") {
        fun createRoute(orderId: String) = "review/$orderId"
    }

    object Complaint : Screen("complaint/{orderId}") {
        fun createRoute(orderId: String) = "complaint/$orderId"
    }

    object AdminDashboard : Screen("admin_dashboard")

    object ManageMenu : Screen("manage_menu/{restaurantId}") {
        fun createRoute(restaurantId: String) = "manage_menu/$restaurantId"
    }
}

/** Routes that show the bottom navigation bar for a logged-in student. */
val bottomNavRoutes = setOf(Screen.Home.route, Screen.Cart.route, Screen.Orders.route, Screen.Profile.route)
