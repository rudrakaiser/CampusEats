package com.mad.campuseats.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mad.campuseats.data.DeliveryMethod
import com.mad.campuseats.navigation.Screen
import com.mad.campuseats.ui.components.*
import com.mad.campuseats.ui.theme.*
import com.mad.campuseats.viewmodel.AppViewModel

@Composable
fun CheckoutScreen(navController: NavController, viewModel: AppViewModel) {
    val state by viewModel.state.collectAsState()
    val restaurant = state.restaurants.find { it.id == state.cartRestaurantId }
    val pickupSpot = "${restaurant?.name ?: "Cafeteria"} pickup counter"
    var method by remember { mutableStateOf(DeliveryMethod.PICKUP) }
    var location by remember { mutableStateOf(pickupSpot) }

    val subtotal = viewModel.cartTotal()
    val fee = if (method == DeliveryMethod.DELIVERY) restaurant?.deliveryFee ?: 0 else 0
    val total = subtotal + fee

    Column(modifier = Modifier.fillMaxSize().background(AppBg)) {
        ScreenHeader(title = "Checkout", onBack = { navController.popBackStack() })

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            CheckoutCard {
                SectionTitle("Delivery options")
                Spacer(Modifier.height(8.dp))
                MethodRow(
                    icon = Icons.Default.DirectionsWalk,
                    title = "Pick-up",
                    subtitle = "Collect from the counter",
                    price = "Free",
                    selected = method == DeliveryMethod.PICKUP
                ) {
                    method = DeliveryMethod.PICKUP
                    location = pickupSpot
                }
                HorizontalDivider(color = Line)
                MethodRow(
                    icon = Icons.Default.TwoWheeler,
                    title = "Delivery",
                    subtitle = restaurant?.deliveryTime ?: "20–30 min",
                    price = if ((restaurant?.deliveryFee ?: 0) == 0) "Free" else "৳${restaurant?.deliveryFee}",
                    selected = method == DeliveryMethod.DELIVERY
                ) {
                    method = DeliveryMethod.DELIVERY
                    location = "CSE Building, 2nd Floor Lobby"
                }
            }

            Spacer(Modifier.height(12.dp))
            CheckoutCard {
                SectionTitle(if (method == DeliveryMethod.PICKUP) "Pickup point" else "Delivery address")
                Spacer(Modifier.height(10.dp))
                MapPlaceholder(locationLabel = location)
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    singleLine = true,
                    leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = Pink) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Pink,
                        cursorColor = Pink,
                        unfocusedBorderColor = Line
                    )
                )
            }

            Spacer(Modifier.height(12.dp))
            CheckoutCard {
                SectionTitle("Payment")
                Spacer(Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Payment, contentDescription = null, tint = Pink)
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text("Cash", style = MaterialTheme.typography.titleSmall, color = Charcoal)
                        Text(
                            if (method == DeliveryMethod.PICKUP) "Pay when you collect" else "Pay when it arrives",
                            style = MaterialTheme.typography.bodySmall,
                            color = MutedText
                        )
                    }
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Pink)
                }
            }

            Spacer(Modifier.height(12.dp))
            CheckoutCard {
                SectionTitle("Order summary")
                Spacer(Modifier.height(10.dp))
                state.cart.forEach { cartItem ->
                    SummaryLine("${cartItem.quantity} × ${cartItem.foodItem.name}", "৳${cartItem.foodItem.price * cartItem.quantity}")
                    Spacer(Modifier.height(6.dp))
                }
                HorizontalDivider(color = Line, modifier = Modifier.padding(vertical = 8.dp))
                SummaryLine("Subtotal", "৳$subtotal")
                Spacer(Modifier.height(6.dp))
                SummaryLine("Delivery fee", if (fee == 0) "Free" else "৳$fee")
                HorizontalDivider(color = Line, modifier = Modifier.padding(vertical = 8.dp))
                SummaryLine("Total", "৳$total", bold = true)
            }
        }

        Column(Modifier.background(Color.White)) {
            HorizontalDivider(color = Line)
            Box(Modifier.navigationBarsPadding().padding(16.dp)) {
                PrimaryButton(
                    text = "Place order",
                    trailing = "৳$total",
                    enabled = state.cart.isNotEmpty(),
                    onClick = {
                        val order = viewModel.placeOrder(method, location)
                        navController.navigate(Screen.OrderTracking.createRoute(order.id)) {
                            popUpTo(Screen.Home.route)
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun CheckoutCard(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White)
            .padding(16.dp),
        content = content
    )
}

@Composable
private fun SummaryLine(label: String, value: String, bold: Boolean = false) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(
            label,
            style = if (bold) MaterialTheme.typography.titleSmall else MaterialTheme.typography.bodyMedium,
            color = if (bold) Charcoal else Ink
        )
        Text(
            value,
            style = if (bold) MaterialTheme.typography.titleSmall else MaterialTheme.typography.bodyMedium,
            color = if (bold) Charcoal else Ink
        )
    }
}

@Composable
private fun MethodRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    price: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = if (selected) Pink else MutedText)
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleSmall, color = Charcoal)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MutedText)
        }
        Text(price, style = MaterialTheme.typography.labelLarge, color = Ink)
        Spacer(Modifier.width(4.dp))
        RadioButton(
            selected = selected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(selectedColor = Pink, unselectedColor = MutedText)
        )
    }
}
