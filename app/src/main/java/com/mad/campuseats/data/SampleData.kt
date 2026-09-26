package com.mad.campuseats.data

import androidx.compose.ui.graphics.Color

object SampleData {

    val restaurants = listOf(
        Restaurant(
            id = "r1",
            name = "Central Cafeteria",
            tagline = "Premier University's main dining hall",
            location = "Ground Floor, Main Academic Building",
            accentColor = Color(0xFFFF6B4A),
            avgRating = 4.2,
            ratingCount = 186,
            deliveryTime = "15–25 min",
            deliveryFee = 0,
            promo = "Free delivery"
        ),
        Restaurant(
            id = "r2",
            name = "CSE Building Canteen",
            tagline = "Quick bites between labs",
            location = "1st Floor, CSE Building",
            accentColor = Color(0xFF3D8B7D),
            avgRating = 4.0,
            ratingCount = 94,
            deliveryTime = "10–20 min",
            deliveryFee = 20
        ),
        Restaurant(
            id = "r3",
            name = "Rong Dhonu Food Court",
            tagline = "Biryani, rice & curry specialists",
            location = "Near West Gate, Premier University",
            accentColor = Color(0xFFB5442D),
            avgRating = 4.6,
            ratingCount = 231,
            deliveryTime = "25–35 min",
            deliveryFee = 40,
            promo = "Top rated"
        ),
        Restaurant(
            id = "r4",
            name = "Campus Corner Cafe",
            tagline = "Coffee, shakes & desserts",
            location = "Opposite Admin Block",
            accentColor = Color(0xFF6B4EFF),
            avgRating = 4.3,
            ratingCount = 152,
            deliveryTime = "15–25 min",
            deliveryFee = 30
        )
    )

    val foodItems = listOf(
        // Central Cafeteria
        FoodItem("f1", "r1", "Chicken Burger", "Grilled chicken patty, lettuce, house sauce", 180, FoodCategory.MEALS),
        FoodItem("f2", "r1", "French Fries", "Crispy salted fries, regular size", 90, FoodCategory.SNACKS),
        FoodItem("f3", "r1", "Chicken Fried Rice", "Wok-tossed rice with chicken & vegetables", 160, FoodCategory.MEALS),
        FoodItem("f4", "r1", "Coke", "330ml can, chilled", 40, FoodCategory.BEVERAGES),
        FoodItem("f5", "r1", "Vegetable Sandwich", "Grilled sandwich with fresh veggies", 110, FoodCategory.SNACKS),

        // CSE Building Canteen
        FoodItem("f6", "r2", "Beef Singara", "Two pieces, fresh from the fryer", 30, FoodCategory.SNACKS),
        FoodItem("f7", "r2", "Chicken Puff", "Flaky pastry, chicken filling", 35, FoodCategory.SNACKS),
        FoodItem("f8", "r2", "Instant Noodles", "Spicy chicken flavor, made to order", 70, FoodCategory.MEALS),
        FoodItem("f9", "r2", "Lemon Tea", "Hot, served in a paper cup", 20, FoodCategory.BEVERAGES),
        FoodItem("f10", "r2", "Milk Tea", "Classic campus milk tea", 20, FoodCategory.BEVERAGES),

        // Rong Dhonu Food Court
        FoodItem("f11", "r3", "Chicken Biryani", "Full plate with salad & borhani", 220, FoodCategory.MEALS),
        FoodItem("f12", "r3", "Beef Tehari", "Traditional Chattogram-style tehari", 200, FoodCategory.MEALS),
        FoodItem("f13", "r3", "Mixed Vegetable Rice", "Steamed rice with seasonal vegetables", 140, FoodCategory.MEALS),
        FoodItem("f14", "r3", "Borhani", "Spiced yogurt drink, 250ml", 30, FoodCategory.BEVERAGES),
        FoodItem("f15", "r3", "Chicken Roll", "Paratha-wrapped chicken roll", 90, FoodCategory.SNACKS),

        // Campus Corner Cafe
        FoodItem("f16", "r4", "Cold Coffee", "Iced coffee with milk foam", 120, FoodCategory.BEVERAGES),
        FoodItem("f17", "r4", "Chocolate Shake", "Thick shake, whipped cream on top", 150, FoodCategory.BEVERAGES),
        FoodItem("f18", "r4", "Brownie", "Warm chocolate brownie, single slice", 100, FoodCategory.DESSERTS),
        FoodItem("f19", "r4", "Cheesecake Slice", "New York style cheesecake", 160, FoodCategory.DESSERTS),
        FoodItem("f20", "r4", "Espresso", "Single shot, freshly ground", 90, FoodCategory.BEVERAGES)
    )

    fun itemsFor(restaurantId: String) = foodItems.filter { it.restaurantId == restaurantId }
}
