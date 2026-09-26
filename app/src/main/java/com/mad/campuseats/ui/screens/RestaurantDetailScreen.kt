package com.mad.campuseats.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mad.campuseats.data.FoodCategory
import com.mad.campuseats.data.FoodItem
import com.mad.campuseats.navigation.Screen
import com.mad.campuseats.ui.components.*
import com.mad.campuseats.ui.theme.*
import com.mad.campuseats.viewmodel.AppViewModel
import com.mad.campuseats.viewmodel.SortOption
import kotlinx.coroutines.launch

@Composable
fun RestaurantDetailScreen(navController: NavController, viewModel: AppViewModel, restaurantId: String) {
    val state by viewModel.state.collectAsState()
    val restaurant = state.restaurants.find { it.id == restaurantId } ?: return
    var sortMenuOpen by remember { mutableStateOf(false) }
    var pendingSwitchItem by remember { mutableStateOf<FoodItem?>(null) }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    val density = LocalDensity.current
    val coverScrollPx = with(density) { 140.dp.toPx() }
    // Once the cover photo has scrolled away, the floating buttons turn into a normal white top bar.
    val scrolled by remember {
        derivedStateOf { listState.firstVisibleItemIndex > 0 || listState.firstVisibleItemScrollOffset > coverScrollPx }
    }
    StatusBarStyle(darkIcons = scrolled)

    LaunchedEffect(restaurantId) {
        viewModel.setSearchQuery("")
        viewModel.setCategoryFilter(null)
        viewModel.setSortOption(SortOption.NONE)
    }

    val menu = viewModel.foodForRestaurant(restaurantId)
    val reviews = viewModel.reviewsFor(restaurantId)
    val cartCount = state.cart.sumOf { it.quantity }
    val isFavorite = restaurantId in state.favoriteIds

    fun addItem(item: FoodItem) {
        // A cart only ever holds one restaurant's items (one pickup/delivery). Confirm before swapping.
        if (state.cartRestaurantId != null && state.cartRestaurantId != item.restaurantId) {
            pendingSwitchItem = item
        } else {
            viewModel.addToCart(item)
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawerContent(cartCount = cartCount) { route ->
                scope.launch { drawerState.close() }
                navController.navigate(route) {
                    popUpTo(Screen.Home.route) { inclusive = false }
                    launchSingleTop = true
                }
            }
        }
    ) {
        Box(Modifier.fillMaxSize().background(Color.White)) {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = if (cartCount > 0) 96.dp else 24.dp)
            ) {
                // Cover photo
                item {
                    Box(Modifier.fillMaxWidth().height(200.dp)) {
                        RestaurantCoverImage(restaurant, Modifier.fillMaxSize())
                        Box(
                            Modifier.fillMaxSize().background(
                                Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.35f), Color.Transparent))
                            )
                        )
                    }
                }

                // Info block
                item {
                    Column(Modifier.padding(16.dp)) {
                        Text(restaurant.name, style = MaterialTheme.typography.headlineSmall, color = Charcoal)
                        Spacer(Modifier.height(6.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RatingBadge(restaurant.avgRating, restaurant.ratingCount)
                            Text("  ·  ", color = MutedText)
                            Icon(Icons.Default.Schedule, contentDescription = null, tint = MutedText, modifier = Modifier.size(15.dp))
                            Spacer(Modifier.width(3.dp))
                            Text(restaurant.deliveryTime, style = MaterialTheme.typography.bodySmall, color = Ink)
                            Text("  ·  ", color = MutedText)
                            Text(deliveryFeeLabel(restaurant.deliveryFee), style = MaterialTheme.typography.bodySmall, color = Ink)
                        }
                        Spacer(Modifier.height(6.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = MutedText, modifier = Modifier.size(15.dp))
                            Spacer(Modifier.width(3.dp))
                            Text(restaurant.location, style = MaterialTheme.typography.bodySmall, color = MutedText)
                        }
                        Text(restaurant.tagline, style = MaterialTheme.typography.bodySmall, color = MutedText)
                        restaurant.promo?.let {
                            Spacer(Modifier.height(12.dp))
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(PinkTint)
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.LocalOffer, contentDescription = null, tint = Pink, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(8.dp))
                                Text(it, style = MaterialTheme.typography.labelLarge, color = PinkDark)
                            }
                        }
                        Spacer(Modifier.height(14.dp))
                        CampusSearchBar(
                            query = state.searchQuery,
                            onQueryChange = { viewModel.setSearchQuery(it) },
                            placeholder = "Search this menu"
                        )
                    }
                    HorizontalDivider(color = Line)
                }

                // Category tabs + sort
                item {
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        LazyRow(modifier = Modifier.weight(1f)) {
                            item { MenuTab("All", state.categoryFilter == null) { viewModel.setCategoryFilter(null) } }
                            items(FoodCategory.values().toList()) { category ->
                                MenuTab(category.label, state.categoryFilter == category) {
                                    viewModel.setCategoryFilter(if (state.categoryFilter == category) null else category)
                                }
                            }
                        }
                        Box {
                            IconButton(onClick = { sortMenuOpen = true }) {
                                Icon(Icons.Default.Sort, contentDescription = "Sort", tint = Charcoal)
                            }
                            DropdownMenu(expanded = sortMenuOpen, onDismissRequest = { sortMenuOpen = false }) {
                                SortOption.values().forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text(option.label) },
                                        onClick = {
                                            viewModel.setSortOption(option)
                                            sortMenuOpen = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                    HorizontalDivider(color = Line)
                }

                // Menu
                if (menu.isEmpty()) {
                    item {
                        EmptyState(Icons.Default.RestaurantMenu, "No items found", "Try clearing your search or filters.")
                    }
                } else {
                    items(menu) { menuItem ->
                        val inCart = state.cart.find { it.foodItem.id == menuItem.id }
                        MenuItemRow(
                            item = menuItem,
                            accent = restaurant.accentColor,
                            quantityInCart = inCart?.quantity ?: 0,
                            onAdd = { addItem(menuItem) },
                            onMinus = { viewModel.updateCartQuantity(menuItem.id, -1) },
                            onPlus = { viewModel.updateCartQuantity(menuItem.id, 1) }
                        )
                    }
                }

                // Reviews
                item {
                    Spacer(Modifier.height(16.dp))
                    Text(
                        "Ratings & reviews",
                        style = MaterialTheme.typography.titleLarge,
                        color = Charcoal,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    Spacer(Modifier.height(10.dp))
                }

                if (reviews.isEmpty()) {
                    item {
                        Text(
                            "No reviews yet — be the first after your order.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MutedText,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                } else {
                    items(reviews) { review ->
                        Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    Modifier.size(30.dp).clip(CircleShape).background(PinkTint),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(review.userName.take(1), color = Pink, style = MaterialTheme.typography.labelMedium)
                                }
                                Spacer(Modifier.width(8.dp))
                                Text(review.userName, style = MaterialTheme.typography.labelLarge, color = Charcoal)
                                Spacer(Modifier.width(8.dp))
                                repeat(review.rating) {
                                    Icon(Icons.Default.Star, contentDescription = null, tint = Warning, modifier = Modifier.size(14.dp))
                                }
                            }
                            if (review.comment.isNotBlank()) {
                                Spacer(Modifier.height(6.dp))
                                Text(review.comment, style = MaterialTheme.typography.bodySmall, color = Ink)
                            }
                        }
                        HorizontalDivider(color = Line, modifier = Modifier.padding(start = 16.dp))
                    }
                }
            }

            // Floating top bar over the cover photo
            Row(
                Modifier
                    .fillMaxWidth()
                    .background(if (scrolled) Color.White else Color.Transparent)
                    .statusBarsPadding()
                    .padding(horizontal = 8.dp)
                    .height(56.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RoundIconButton(Icons.Default.ArrowBack, "Back", plain = scrolled) { navController.popBackStack() }
                if (scrolled) {
                    Text(
                        restaurant.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = Charcoal,
                        maxLines = 1,
                        modifier = Modifier.weight(1f).padding(horizontal = 8.dp)
                    )
                } else {
                    Spacer(Modifier.weight(1f))
                }
                RoundIconButton(
                    if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    "Favourite",
                    plain = scrolled,
                    tint = if (isFavorite) Pink else Charcoal
                ) { viewModel.toggleFavorite(restaurantId) }
                RoundIconButton(Icons.Default.Menu, "Menu", plain = scrolled) { scope.launch { drawerState.open() } }
            }

            // "View your cart" bar
            if (cartCount > 0) {
                Column(
                    Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .background(Color.White)
                ) {
                    HorizontalDivider(color = Line)
                    Box(Modifier.navigationBarsPadding().padding(16.dp)) {
                        PrimaryButton(
                            text = "View your cart ($cartCount)",
                            trailing = "৳${viewModel.cartTotal()}",
                            onClick = {
                                navController.navigate(Screen.Cart.route) {
                                    popUpTo(Screen.Home.route) { inclusive = false }
                                    launchSingleTop = true
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    pendingSwitchItem?.let { item ->
        val otherRestaurantName = state.restaurants.find { it.id == state.cartRestaurantId }?.name ?: "another restaurant"
        AlertDialog(
            onDismissRequest = { pendingSwitchItem = null },
            containerColor = Color.White,
            title = { Text("Start a new order?") },
            text = {
                Text("Your cart has $cartCount item(s) from $otherRestaurantName. Adding from ${restaurant.name} will clear them, since an order can only come from one place at a time.")
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.clearCart()
                    viewModel.addToCart(item)
                    pendingSwitchItem = null
                }) { Text("Clear cart & add", color = Pink) }
            },
            dismissButton = {
                TextButton(onClick = { pendingSwitchItem = null }) { Text("Cancel", color = Ink) }
            }
        )
    }
}
