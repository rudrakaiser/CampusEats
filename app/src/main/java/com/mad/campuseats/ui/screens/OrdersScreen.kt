package com.mad.campuseats.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mad.campuseats.navigation.Screen
import com.mad.campuseats.ui.components.*
import com.mad.campuseats.ui.theme.*
import com.mad.campuseats.viewmodel.AppViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun OrdersScreen(navController: NavController, viewModel: AppViewModel) {
    val state by viewModel.state.collectAsState()
    val orders = viewModel.ordersForCurrentUser()
    val dateFormat = remember { SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()) }

    Column(modifier = Modifier.fillMaxSize().background(AppBg)) {
        ScreenHeader(title = "Orders")

        if (orders.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                EmptyState(Icons.Default.Receipt, "No orders yet", "Your placed orders will show up here.")
            }
        } else {
            LazyColumn(contentPadding = PaddingValues(16.dp)) {
                items(orders) { order ->
                    val restaurant = state.restaurants.find { it.id == order.restaurantId }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color.White)
                            .clickable { navController.navigate(Screen.OrderTracking.createRoute(order.id)) }
                            .padding(12.dp)
                    ) {
                        if (restaurant != null) {
                            RestaurantCoverImage(restaurant, Modifier.size(64.dp).clip(RoundedCornerShape(10.dp)))
                            Spacer(Modifier.width(12.dp))
                        }
                        Column(Modifier.weight(1f)) {
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Text(order.restaurantName, style = MaterialTheme.typography.titleSmall, color = Charcoal, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
                                Spacer(Modifier.width(8.dp))
                                PillBadge(order.status.label, statusColor(order.status))
                            }
                            Spacer(Modifier.height(2.dp))
                            Text(
                                order.items.joinToString(", ") { "${it.quantity} × ${it.name}" },
                                style = MaterialTheme.typography.bodySmall,
                                color = MutedText,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(Modifier.height(6.dp))
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(dateFormat.format(Date(order.placedAt)), style = MaterialTheme.typography.labelSmall, color = MutedText)
                                Text("৳${order.total}", style = MaterialTheme.typography.titleSmall, color = Charcoal)
                            }
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                }
            }
        }
    }
}
