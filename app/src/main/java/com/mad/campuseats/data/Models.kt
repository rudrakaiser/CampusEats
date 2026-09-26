package com.mad.campuseats.data

import androidx.compose.ui.graphics.Color

enum class UserRole { STUDENT, ADMIN }

data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val studentId: String = "",
    val role: UserRole = UserRole.STUDENT
)

enum class FoodCategory(val label: String) {
    MEALS("Meals"),
    SNACKS("Snacks"),
    BEVERAGES("Beverages"),
    DESSERTS("Desserts")
}

data class Restaurant(
    val id: String,
    val name: String,
    val tagline: String,
    val location: String,
    val accentColor: Color,
    val avgRating: Double,
    val ratingCount: Int,
    val deliveryTime: String = "20–30 min",
    val deliveryFee: Int = 30,
    val promo: String? = null
)

/**
 * Placeholder cover photo, locked per restaurant so it stays the same between launches.
 * Swap for a real photo URL (e.g. from Firebase Storage) once restaurants upload their own.
 */
val Restaurant.coverPhotoUrl: String
    get() = "https://loremflickr.com/800/500/restaurant,food?lock=${id.hashCode() and 0xFFFF}"

data class FoodItem(
    val id: String,
    val restaurantId: String,
    val name: String,
    val description: String,
    var price: Int,
    val category: FoodCategory
)

/** Placeholder food photo, keyed by category and locked per item. Replace with Firebase Storage URLs later. */
val FoodItem.imageUrl: String
    get() {
        val tags = when (category) {
            FoodCategory.MEALS -> "food,dinner"
            FoodCategory.SNACKS -> "snack,fastfood"
            FoodCategory.BEVERAGES -> "drink,coffee"
            FoodCategory.DESSERTS -> "dessert,cake"
        }
        return "https://loremflickr.com/300/300/$tags?lock=${id.hashCode() and 0xFFFF}"
    }

data class CartItem(
    val foodItem: FoodItem,
    var quantity: Int
)

enum class DeliveryMethod { PICKUP, DELIVERY }

enum class OrderStatus(val label: String) {
    PLACED("Order placed"),
    ACCEPTED("Restaurant accepted"),
    PREPARING("Preparing"),
    READY("Ready"),
    DELIVERED("Delivered"),
    DECLINED("Declined")
}

data class OrderLineItem(
    val name: String,
    val quantity: Int,
    val price: Int
)

data class Order(
    val id: String,
    val userId: String,
    val restaurantId: String,
    val restaurantName: String,
    val items: List<OrderLineItem>,
    val total: Int,
    val deliveryMethod: DeliveryMethod,
    val dropLocation: String,
    var status: OrderStatus,
    val placedAt: Long,
    var reviewed: Boolean = false,
    val deliveryFee: Int = 0
)

data class Review(
    val id: String,
    val restaurantId: String,
    val userName: String,
    val rating: Int,
    val comment: String
)

data class Complaint(
    val id: String,
    val orderId: String,
    val userName: String,
    val description: String,
    val hasPhoto: Boolean
)
