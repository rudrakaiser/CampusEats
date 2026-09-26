package com.mad.campuseats.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mad.campuseats.data.DeliveryMethod
import com.mad.campuseats.data.OrderStatus
import com.mad.campuseats.navigation.Screen
import com.mad.campuseats.ui.components.*
import com.mad.campuseats.ui.theme.*
import com.mad.campuseats.viewmodel.AppViewModel

@Composable
fun OrderTrackingScreen(navController: NavController, viewModel: AppViewModel, orderId: String) {
    val state by viewModel.state.collectAsState()
    val order = state.orders.find { it.id == orderId } ?: return
    val restaurant = state.restaurants.find { it.id == order.restaurantId }
    val isPickup = order.deliveryMethod == DeliveryMethod.PICKUP
    val steps = listOf(OrderStatus.PLACED, OrderStatus.ACCEPTED, OrderStatus.PREPARING, OrderStatus.READY, OrderStatus.DELIVERED)
    val currentIndex = steps.indexOf(order.status)
    StatusBarStyle(darkIcons = false)

    Column(modifier = Modifier.fillMaxSize().background(AppBg)) {
        // Pink status header
        Column(
            Modifier
                .fillMaxWidth()
                .background(Pink)
                .statusBarsPadding()
                .padding(horizontal = 8.dp)
                .padding(bottom = 20.dp)
        ) {
            Row(Modifier.height(56.dp), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Text("Order #${order.id}", style = MaterialTheme.typography.titleLarge, color = Color.White)
            }
            Column(Modifier.padding(horizontal = 8.dp)) {
                Text(
                    if (order.status == OrderStatus.DELIVERED) {
                        if (isPickup) "Enjoy your meal!" else "Delivered — enjoy!"
                    } else if (order.status == OrderStatus.DECLINED) "Order declined"
                    else order.status.label,
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    when (order.status) {
                        OrderStatus.DELIVERED -> order.restaurantName
                        OrderStatus.DECLINED -> "The restaurant couldn't take this order"
                        else -> if (isPickup) "Ready for pickup soon · ${order.restaurantName}"
                        else "Estimated ${restaurant?.deliveryTime ?: "20–30 min"} · ${order.restaurantName}"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.9f)
                )
                Spacer(Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    steps.forEachIndexed { index, _ ->
                        Box(
                            Modifier
                                .weight(1f)
                                .height(4.dp)
                                .clip(RoundedCornerShape(50))
                                .background(if (index <= currentIndex) Color.White else Color.White.copy(alpha = 0.35f))
                        )
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(PinkTint)
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = Pink)
                Spacer(Modifier.width(10.dp))
                Text(
                    "\"${order.status.label}\" — you'll be notified at each step",
                    style = MaterialTheme.typography.bodySmall,
                    color = PinkDark
                )
            }

            if (order.status != OrderStatus.DECLINED) {
                Spacer(Modifier.height(12.dp))
                Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(Color.White).padding(16.dp)) {
                    OrderStatusStepper(currentStatus = order.status)
                }
            }

            Spacer(Modifier.height(12.dp))
            Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(Color.White).padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        if (isPickup) "Pickup location" else "Delivery location",
                        style = MaterialTheme.typography.titleSmall,
                        color = Charcoal,
                        modifier = Modifier.weight(1f)
                    )
                    PillBadge(if (isPickup) "Pick-up" else "Delivery", Success)
                }
                Spacer(Modifier.height(10.dp))
                MapPlaceholder(locationLabel = order.dropLocation)
            }

            Spacer(Modifier.height(12.dp))
            Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(Color.White).padding(16.dp)) {
                Text("Order details", style = MaterialTheme.typography.titleSmall, color = Charcoal)
                Spacer(Modifier.height(10.dp))
                order.items.forEach { line ->
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("${line.quantity} × ${line.name}", style = MaterialTheme.typography.bodyMedium, color = Ink)
                        Text("৳${line.price * line.quantity}", style = MaterialTheme.typography.bodyMedium, color = Ink)
                    }
                    Spacer(Modifier.height(4.dp))
                }
                if (order.deliveryFee > 0) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Delivery fee", style = MaterialTheme.typography.bodyMedium, color = Ink)
                        Text("৳${order.deliveryFee}", style = MaterialTheme.typography.bodyMedium, color = Ink)
                    }
                }
                HorizontalDivider(color = Line, modifier = Modifier.padding(vertical = 8.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Total", style = MaterialTheme.typography.titleSmall, color = Charcoal)
                    Text("৳${order.total}", style = MaterialTheme.typography.titleSmall, color = Charcoal)
                }
            }
        }

        // Rating / complaint only once the order has actually arrived.
        if (order.status == OrderStatus.DELIVERED) {
            Column(Modifier.background(Color.White)) {
                HorizontalDivider(color = Line)
                Column(Modifier.navigationBarsPadding().padding(16.dp)) {
                    if (!order.reviewed) {
                        PrimaryButton(text = "Rate & review", onClick = {
                            navController.navigate(Screen.Review.createRoute(order.id))
                        })
                        Spacer(Modifier.height(10.dp))
                    }
                    OutlinePillButton(
                        text = "Report a problem",
                        color = Danger,
                        onClick = { navController.navigate(Screen.Complaint.createRoute(order.id)) }
                    )
                }
            }
        } else {
            Spacer(Modifier.navigationBarsPadding())
        }
    }
}
