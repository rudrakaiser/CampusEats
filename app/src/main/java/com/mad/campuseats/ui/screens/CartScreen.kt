package com.mad.campuseats.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mad.campuseats.navigation.Screen
import com.mad.campuseats.ui.components.*
import com.mad.campuseats.ui.theme.*
import com.mad.campuseats.viewmodel.AppViewModel

@Composable
fun CartScreen(navController: NavController, viewModel: AppViewModel) {
    val state by viewModel.state.collectAsState()
    val restaurant = state.restaurants.find { it.id == state.cartRestaurantId }

    Column(modifier = Modifier.fillMaxSize().background(AppBg)) {
        ScreenHeader(title = "Your cart")

        if (state.cart.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                EmptyState(Icons.Default.ShoppingBag, "Your cart is empty", "Browse a restaurant and add something tasty.")
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp)
            ) {
                item {
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color.White)
                            .padding(16.dp)
                    ) {
                        Text("Your order from", style = MaterialTheme.typography.labelMedium, color = MutedText)
                        Text(restaurant?.name ?: "", style = MaterialTheme.typography.titleMedium, color = Charcoal)
                        Spacer(Modifier.height(8.dp))
                        state.cart.forEachIndexed { index, cartItem ->
                            if (index > 0) HorizontalDivider(color = Line)
                            Row(
                                Modifier.fillMaxWidth().padding(vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(Modifier.weight(1f)) {
                                    Text(cartItem.foodItem.name, style = MaterialTheme.typography.titleSmall, color = Charcoal)
                                    Text("৳${cartItem.foodItem.price} each", style = MaterialTheme.typography.labelSmall, color = MutedText)
                                }
                                QuantityStepper(
                                    quantity = cartItem.quantity,
                                    onMinus = { viewModel.updateCartQuantity(cartItem.foodItem.id, -1) },
                                    onPlus = { viewModel.updateCartQuantity(cartItem.foodItem.id, 1) }
                                )
                                Spacer(Modifier.width(12.dp))
                                Text(
                                    "৳${cartItem.foodItem.price * cartItem.quantity}",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = Charcoal,
                                    modifier = Modifier.width(56.dp)
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color.White)
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Subtotal", style = MaterialTheme.typography.bodyMedium, color = Ink)
                        Text("৳${viewModel.cartTotal()}", style = MaterialTheme.typography.titleSmall, color = Charcoal)
                    }
                }
            }

            Column(Modifier.background(Color.White)) {
                HorizontalDivider(color = Line)
                Box(Modifier.padding(16.dp)) {
                    PrimaryButton(
                        text = "Go to checkout",
                        trailing = "৳${viewModel.cartTotal()}",
                        onClick = { navController.navigate(Screen.Checkout.route) }
                    )
                }
            }
        }
    }
}
