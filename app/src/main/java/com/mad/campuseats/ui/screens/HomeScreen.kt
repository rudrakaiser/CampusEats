package com.mad.campuseats.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mad.campuseats.data.FoodCategory
import com.mad.campuseats.navigation.Screen
import com.mad.campuseats.ui.components.*
import com.mad.campuseats.ui.theme.*
import com.mad.campuseats.viewmodel.AppViewModel

@Composable
fun HomeScreen(navController: NavController, viewModel: AppViewModel) {
    val state by viewModel.state.collectAsState()
    StatusBarStyle(darkIcons = false)

    val query = state.searchQuery.trim()
    val restaurants = state.restaurants.filter { restaurant ->
        val matchesQuery = query.isEmpty() || restaurant.name.contains(query, ignoreCase = true)
        val matchesCategory = state.categoryFilter == null ||
            viewModel.baseMenuFor(restaurant.id).any { it.category == state.categoryFilter }
        matchesQuery && matchesCategory
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(Color.White),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // Pink header: location + search
        item {
            Column(
                Modifier
                    .fillMaxWidth()
                    .background(Pink)
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp)
                    .padding(top = 8.dp, bottom = 16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.White)
                    Spacer(Modifier.width(6.dp))
                    Column(Modifier.weight(1f)) {
                        Text("Deliver to", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.85f))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Premier University, Chattogram", style = MaterialTheme.typography.titleSmall, color = Color.White, maxLines = 1)
                            Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = Color.White)
                        }
                    }
                    Box(
                        Modifier.size(36.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.22f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            state.currentUser?.name?.take(1)?.uppercase() ?: "S",
                            color = Color.White,
                            style = MaterialTheme.typography.titleSmall
                        )
                    }
                }
                Spacer(Modifier.height(14.dp))
                CampusSearchBar(
                    query = state.searchQuery,
                    onQueryChange = { viewModel.setSearchQuery(it) },
                    containerColor = Color.White
                )
            }
        }

        // Category tiles
        item {
            Spacer(Modifier.height(16.dp))
            LazyRow(
                contentPadding = PaddingValues(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                item {
                    CategoryTile("All", Icons.Default.Restaurant, state.categoryFilter == null) {
                        viewModel.setCategoryFilter(null)
                    }
                }
                items(FoodCategory.values().toList()) { category ->
                    CategoryTile(category.label, category.icon(), state.categoryFilter == category) {
                        viewModel.setCategoryFilter(if (state.categoryFilter == category) null else category)
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
        }

        // Promo banners
        item {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item { PromoBanner("Skip the queue", "Order ahead, pick up between classes", Icons.Default.DirectionsWalk, dark = false) }
                item { PromoBanner("Free delivery", "Central Cafeteria, every day", Icons.Default.TwoWheeler, dark = true) }
            }
            Spacer(Modifier.height(22.dp))
        }

        item {
            Text(
                "All restaurants (${restaurants.size})",
                style = MaterialTheme.typography.titleLarge,
                color = Charcoal,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(Modifier.height(14.dp))
        }

        if (restaurants.isEmpty()) {
            item {
                EmptyState(Icons.Default.Storefront, "No matches", "Try a different search term.")
            }
        } else {
            items(restaurants) { restaurant ->
                Box(Modifier.padding(horizontal = 16.dp)) {
                    RestaurantCard(
                        restaurant = restaurant,
                        isFavorite = restaurant.id in state.favoriteIds,
                        onFavoriteClick = { viewModel.toggleFavorite(restaurant.id) },
                        onClick = { navController.navigate(Screen.RestaurantDetail.createRoute(restaurant.id)) }
                    )
                }
                Spacer(Modifier.height(22.dp))
            }
        }
    }
}
