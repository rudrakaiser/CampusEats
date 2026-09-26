package com.mad.campuseats.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mad.campuseats.data.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

enum class SortOption(val label: String) {
    NONE("Default"),
    PRICE_LOW_HIGH("Price: Low to High"),
    PRICE_HIGH_LOW("Price: High to Low")
}

data class AppUiState(
    val currentUser: User? = null,
    val restaurants: List<Restaurant> = SampleData.restaurants,
    // Admin-side edits to the base menu, layered on top of SampleData at read time.
    val addedItems: List<FoodItem> = emptyList(),
    val editedItems: Map<String, FoodItem> = emptyMap(),
    val deletedItemIds: Set<String> = emptySet(),
    val cart: List<CartItem> = emptyList(),
    val cartRestaurantId: String? = null,
    val orders: List<Order> = emptyList(),
    val reviews: List<Review> = emptyList(),
    val complaints: List<Complaint> = emptyList(),
    val favoriteIds: Set<String> = emptySet(),
    val searchQuery: String = "",
    val categoryFilter: FoodCategory? = null,
    val sortOption: SortOption = SortOption.NONE,
    val authError: String? = null
)

class AppViewModel : ViewModel() {

    private val _state = MutableStateFlow(AppUiState())
    val state: StateFlow<AppUiState> = _state
    private val auth = FirebaseAuth.getInstance()
    private val usersRef = FirebaseDatabase.getInstance().getReference("users")

    // ---------- Auth ----------

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _state.update { it.copy(authError = "Please enter both email and password.") }
            return
        }
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val uid = result.user?.uid ?: return@addOnSuccessListener
                usersRef.child(uid).get()
                    .addOnSuccessListener { snapshot ->
                        val user = snapshot.getValue(User::class.java)
                        if (user != null) {
                            _state.update { it.copy(currentUser = user, authError = null) }
                        } else {
                            _state.update { it.copy(authError = "No profile found for this account.") }
                        }
                    }
                    .addOnFailureListener { e ->
                        _state.update { it.copy(authError = "Could not load your profile: ${e.localizedMessage}") }
                    }
            }
            .addOnFailureListener { e ->
                _state.update { it.copy(authError = e.localizedMessage ?: "Login failed.") }
            }
    }

    fun register(name: String, email: String, studentId: String, password: String) {
        if (name.isBlank() || email.isBlank() || password.isBlank()) {
            _state.update { it.copy(authError = "Please fill in all required fields.") }
            return
        }
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val uid = result.user?.uid ?: return@addOnSuccessListener
                val user = User(id = uid, name = name, email = email, studentId = studentId, role = UserRole.STUDENT)
                usersRef.child(uid).setValue(user)
                    .addOnSuccessListener {
                        _state.update { it.copy(currentUser = user, authError = null) }
                    }
                    .addOnFailureListener { e ->
                        _state.update { it.copy(authError = "Account created but saving profile failed: ${e.localizedMessage}") }
                    }
            }
            .addOnFailureListener { e ->
                _state.update { it.copy(authError = e.localizedMessage ?: "Registration failed.") }
            }
    }

    fun loginAdmin(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _state.update { it.copy(authError = "Please enter both email and password.") }
            return
        }
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val uid = result.user?.uid ?: return@addOnSuccessListener
                usersRef.child(uid).get()
                    .addOnSuccessListener { snapshot ->
                        val user = snapshot.getValue(User::class.java)
                        when {
                            user == null -> _state.update { it.copy(authError = "No profile found for this account.") }
                            user.role != UserRole.ADMIN -> _state.update { it.copy(authError = "This account is not an admin account.") }
                            else -> _state.update { it.copy(currentUser = user, authError = null) }
                        }
                    }
                    .addOnFailureListener { e ->
                        _state.update { it.copy(authError = "Could not load your profile: ${e.localizedMessage}") }
                    }
            }
            .addOnFailureListener { e ->
                _state.update { it.copy(authError = e.localizedMessage ?: "Login failed.") }
            }
    }

    fun clearAuthError() = _state.update { it.copy(authError = null) }

    fun logout() {
        auth.signOut()
        _state.update { it.copy(currentUser = null, cart = emptyList(), cartRestaurantId = null) }
    }

    // ---------- Browsing ----------

    fun toggleFavorite(restaurantId: String) = _state.update {
        it.copy(favoriteIds = if (restaurantId in it.favoriteIds) it.favoriteIds - restaurantId else it.favoriteIds + restaurantId)
    }

    fun setSearchQuery(query: String) = _state.update { it.copy(searchQuery = query) }
    fun setCategoryFilter(category: FoodCategory?) = _state.update { it.copy(categoryFilter = category) }
    fun setSortOption(option: SortOption) = _state.update { it.copy(sortOption = option) }

    /** Base menu for a restaurant with admin edits/deletes/additions applied, before search/filter/sort. */
    fun baseMenuFor(restaurantId: String): List<FoodItem> {
        val s = _state.value
        val base = SampleData.itemsFor(restaurantId)
            .filter { it.id !in s.deletedItemIds }
            .map { s.editedItems[it.id] ?: it }
        val added = s.addedItems.filter { it.restaurantId == restaurantId }
        return base + added
    }

    fun foodForRestaurant(restaurantId: String): List<FoodItem> {
        var items = baseMenuFor(restaurantId)
        val query = _state.value.searchQuery.trim()
        if (query.isNotEmpty()) {
            items = items.filter { it.name.contains(query, ignoreCase = true) }
        }
        _state.value.categoryFilter?.let { category ->
            items = items.filter { it.category == category }
        }
        items = when (_state.value.sortOption) {
            SortOption.PRICE_LOW_HIGH -> items.sortedBy { it.price }
            SortOption.PRICE_HIGH_LOW -> items.sortedByDescending { it.price }
            SortOption.NONE -> items
        }
        return items
    }

    // ---------- Cart ----------

    fun addToCart(item: FoodItem) {
        _state.update { s ->
            // Cart items must all come from the same restaurant.
            if (s.cartRestaurantId != null && s.cartRestaurantId != item.restaurantId) {
                return@update s.copy(cart = listOf(CartItem(item, 1)), cartRestaurantId = item.restaurantId)
            }
            val existing = s.cart.find { it.foodItem.id == item.id }
            val newCart = if (existing != null) {
                s.cart.map { if (it.foodItem.id == item.id) it.copy(quantity = it.quantity + 1) else it }
            } else {
                s.cart + CartItem(item, 1)
            }
            s.copy(cart = newCart, cartRestaurantId = item.restaurantId)
        }
    }

    fun updateCartQuantity(itemId: String, delta: Int) {
        _state.update { s ->
            val updated = s.cart.mapNotNull {
                if (it.foodItem.id == itemId) {
                    val newQty = it.quantity + delta
                    if (newQty <= 0) null else it.copy(quantity = newQty)
                } else it
            }
            s.copy(cart = updated, cartRestaurantId = if (updated.isEmpty()) null else s.cartRestaurantId)
        }
    }

    fun clearCart() = _state.update { it.copy(cart = emptyList(), cartRestaurantId = null) }

    fun cartTotal(): Int = _state.value.cart.sumOf { it.foodItem.price * it.quantity }

    // ---------- Orders ----------

    fun placeOrder(deliveryMethod: DeliveryMethod, dropLocation: String): Order {
        val s = _state.value
        val restaurant = s.restaurants.find { it.id == s.cartRestaurantId }
        val fee = if (deliveryMethod == DeliveryMethod.DELIVERY) restaurant?.deliveryFee ?: 0 else 0
        val order = Order(
            id = UUID.randomUUID().toString().take(8).uppercase(),
            userId = s.currentUser?.id.orEmpty(),
            restaurantId = restaurant?.id.orEmpty(),
            restaurantName = restaurant?.name.orEmpty(),
            items = s.cart.map { OrderLineItem(it.foodItem.name, it.quantity, it.foodItem.price) },
            total = cartTotal() + fee,
            deliveryFee = fee,
            deliveryMethod = deliveryMethod,
            dropLocation = dropLocation,
            status = OrderStatus.PLACED,
            placedAt = System.currentTimeMillis()
        )
        _state.update { it.copy(orders = listOf(order) + it.orders, cart = emptyList(), cartRestaurantId = null) }
        simulateProgress(order.id)
        return order
    }

    /** Demo-only: automatically advances a freshly placed order so the tracking screen has something to show
     *  without needing a second (restaurant-side) device connected to the same backend. */
    private fun simulateProgress(orderId: String) {
        viewModelScope.launch {
            val sequence = listOf(OrderStatus.ACCEPTED, OrderStatus.PREPARING, OrderStatus.READY, OrderStatus.DELIVERED)
            for (next in sequence) {
                delay(4000)
                updateOrderStatus(orderId, next)
            }
        }
    }

    fun updateOrderStatus(orderId: String, status: OrderStatus) {
        _state.update { s ->
            s.copy(orders = s.orders.map { if (it.id == orderId) it.copy(status = status) else it })
        }
    }

    fun ordersForCurrentUser(): List<Order> {
        val userId = _state.value.currentUser?.id ?: return emptyList()
        return _state.value.orders.filter { it.userId == userId }
    }

    fun markReviewed(orderId: String) {
        _state.update { s ->
            s.copy(orders = s.orders.map { if (it.id == orderId) it.copy(reviewed = true) else it })
        }
    }

    // ---------- Reviews & Complaints ----------

    fun addReview(restaurantId: String, rating: Int, comment: String) {
        val userName = _state.value.currentUser?.name ?: "Student"
        val review = Review(UUID.randomUUID().toString(), restaurantId, userName, rating, comment)
        _state.update { it.copy(reviews = listOf(review) + it.reviews) }
    }

    fun reviewsFor(restaurantId: String): List<Review> =
        _state.value.reviews.filter { it.restaurantId == restaurantId }

    fun addComplaint(orderId: String, description: String, hasPhoto: Boolean) {
        val userName = _state.value.currentUser?.name ?: "Student"
        val complaint = Complaint(UUID.randomUUID().toString(), orderId, userName, description, hasPhoto)
        _state.update { it.copy(complaints = listOf(complaint) + it.complaints) }
    }

    // ---------- Admin: menu & order management ----------

    fun allOrders(): List<Order> = _state.value.orders

    fun addFoodItem(restaurantId: String, name: String, price: Int, category: FoodCategory, description: String) {
        val newItem = FoodItem(
            id = "custom-" + UUID.randomUUID().toString().take(8),
            restaurantId = restaurantId,
            name = name,
            description = description,
            price = price,
            category = category
        )
        _state.update { it.copy(addedItems = it.addedItems + newItem) }
    }

    fun editFoodItem(item: FoodItem) {
        _state.update { s ->
            if (item.id.startsWith("custom-")) {
                s.copy(addedItems = s.addedItems.map { if (it.id == item.id) item else it })
            } else {
                s.copy(editedItems = s.editedItems + (item.id to item))
            }
        }
    }

    fun deleteFoodItem(item: FoodItem) {
        _state.update { s ->
            if (item.id.startsWith("custom-")) {
                s.copy(addedItems = s.addedItems.filterNot { it.id == item.id })
            } else {
                s.copy(deletedItemIds = s.deletedItemIds + item.id)
            }
        }
    }
}
