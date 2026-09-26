package com.mad.campuseats.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mad.campuseats.data.FoodCategory
import com.mad.campuseats.data.FoodItem
import com.mad.campuseats.data.OrderStatus
import com.mad.campuseats.navigation.Screen
import com.mad.campuseats.ui.components.EmptyState
import com.mad.campuseats.ui.components.OutlinePillButton
import com.mad.campuseats.ui.components.ScreenHeader
import com.mad.campuseats.ui.components.StatusBarStyle
import com.mad.campuseats.ui.components.PillBadge
import com.mad.campuseats.ui.components.PrimaryButton
import com.mad.campuseats.ui.components.statusColor
import com.mad.campuseats.ui.theme.*
import com.mad.campuseats.viewmodel.AppViewModel

@Composable
fun AdminDashboardScreen(navController: NavController, viewModel: AppViewModel) {
    val state by viewModel.state.collectAsState()
    var tab by remember { mutableStateOf(0) }

    StatusBarStyle(darkIcons = false)
    Column(modifier = Modifier.fillMaxSize().background(AppBg)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Pink)
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Admin dashboard", style = MaterialTheme.typography.headlineSmall, color = Color.White)
                Text(state.currentUser?.name ?: "Restaurant admin", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.9f))
            }
            IconButton(onClick = {
                viewModel.logout()
                navController.navigate(Screen.Login.route) { popUpTo(0) }
            }) {
                Icon(Icons.Default.Logout, contentDescription = "Log out", tint = Color.White)
            }
        }

        TabRow(selectedTabIndex = tab, containerColor = Color.White, contentColor = Pink) {
            Tab(selected = tab == 0, onClick = { tab = 0 }, text = { Text("Orders") })
            Tab(selected = tab == 1, onClick = { tab = 1 }, text = { Text("Menus") })
        }

        Spacer(Modifier.height(12.dp))

        when (tab) {
            0 -> AdminOrdersTab(viewModel)
            1 -> AdminMenusTab(navController, viewModel)
        }
    }
}

@Composable
private fun AdminOrdersTab(viewModel: AppViewModel) {
    val state by viewModel.state.collectAsState()
    val orders = viewModel.allOrders()

    if (orders.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            EmptyState(icon = Icons.Default.Receipt, title = "No orders yet", subtitle = "Incoming student orders will appear here.")
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 20.dp)
    ) {
        items(orders) { order ->
            var menuOpen by remember { mutableStateOf(false) }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .padding(14.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Order #${order.id}", style = MaterialTheme.typography.titleSmall, color = Charcoal)
                    PillBadge(order.status.label, statusColor(order.status))
                }
                Spacer(Modifier.height(4.dp))
                Text(order.restaurantName, style = MaterialTheme.typography.bodySmall, color = MutedText)
                Spacer(Modifier.height(6.dp))
                Text(
                    order.items.joinToString(", ") { "${it.name} ×${it.quantity}" },
                    style = MaterialTheme.typography.bodySmall, color = Ink
                )
                Spacer(Modifier.height(10.dp))
                Box {
                    OutlinedButton(
                        onClick = { menuOpen = true },
                        shape = RoundedCornerShape(50),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, Pink)
                    ) {
                        Text("Update status", color = Pink)
                    }
                    DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                        OrderStatus.values().forEach { status ->
                            DropdownMenuItem(
                                text = { Text(status.label) },
                                onClick = {
                                    viewModel.updateOrderStatus(order.id, status)
                                    menuOpen = false
                                }
                            )
                        }
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
        }
    }
}

@Composable
private fun AdminMenusTab(navController: NavController, viewModel: AppViewModel) {
    val state by viewModel.state.collectAsState()
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 20.dp)
    ) {
        items(state.restaurants) { restaurant ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .clickable { navController.navigate(Screen.ManageMenu.createRoute(restaurant.id)) }
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(restaurant.accentColor),
                    contentAlignment = Alignment.Center
                ) {
                    Text(restaurant.name.take(1), color = Color.White, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(restaurant.name, style = MaterialTheme.typography.titleSmall, color = Charcoal)
                    Text("${viewModel.baseMenuFor(restaurant.id).size} menu items", style = MaterialTheme.typography.labelSmall, color = MutedText)
                }
                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MutedText)
            }
            Spacer(Modifier.height(12.dp))
        }
    }
}

@Composable
fun ManageMenuScreen(navController: NavController, viewModel: AppViewModel, restaurantId: String) {
    val state by viewModel.state.collectAsState()
    val restaurant = state.restaurants.find { it.id == restaurantId } ?: return
    val items = viewModel.baseMenuFor(restaurantId)
    var showAddDialog by remember { mutableStateOf(false) }
    var editingItem by remember { mutableStateOf<FoodItem?>(null) }

    Column(modifier = Modifier.fillMaxSize().background(AppBg)) {
        ScreenHeader(title = restaurant.name, onBack = { navController.popBackStack() })

        LazyColumn(
            modifier = Modifier.weight(1f).padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            items(items) { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.White)
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(item.name, style = MaterialTheme.typography.titleSmall, color = Charcoal)
                        Text("${item.category.label} · ৳${item.price}", style = MaterialTheme.typography.labelSmall, color = MutedText)
                    }
                    IconButton(onClick = { editingItem = item }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Ink)
                    }
                    IconButton(onClick = { viewModel.deleteFoodItem(item) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Danger)
                    }
                }
                Spacer(Modifier.height(10.dp))
            }
        }

        Column(modifier = Modifier.background(Color.White).navigationBarsPadding().padding(16.dp)) {
            PrimaryButton(text = "Add food item", onClick = { showAddDialog = true })
        }
    }

    if (showAddDialog) {
        FoodItemDialog(
            title = "Add food item",
            initial = null,
            onDismiss = { showAddDialog = false },
            onSave = { name, price, category, description ->
                viewModel.addFoodItem(restaurantId, name, price, category, description)
                showAddDialog = false
            }
        )
    }

    editingItem?.let { item ->
        FoodItemDialog(
            title = "Edit food item",
            initial = item,
            onDismiss = { editingItem = null },
            onSave = { name, price, category, description ->
                viewModel.editFoodItem(item.copy(name = name, price = price, category = category, description = description))
                editingItem = null
            }
        )
    }
}

@Composable
private fun FoodItemDialog(
    title: String,
    initial: FoodItem?,
    onDismiss: () -> Unit,
    onSave: (String, Int, FoodCategory, String) -> Unit
) {
    var name by remember { mutableStateOf(initial?.name ?: "") }
    var priceText by remember { mutableStateOf(initial?.price?.toString() ?: "") }
    var description by remember { mutableStateOf(initial?.description ?: "") }
    var category by remember { mutableStateOf(initial?.category ?: FoodCategory.MEALS) }
    var categoryMenuOpen by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        title = { Text(title) },
        text = {
            Column {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Item name") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(
                    value = priceText,
                    onValueChange = { priceText = it.filter { c -> c.isDigit() } },
                    label = { Text("Price (৳)") },
                    singleLine = true,
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(10.dp))
                Box {
                    OutlinedButton(onClick = { categoryMenuOpen = true }, modifier = Modifier.fillMaxWidth()) {
                        Text(category.label)
                    }
                    DropdownMenu(expanded = categoryMenuOpen, onDismissRequest = { categoryMenuOpen = false }) {
                        FoodCategory.values().forEach { c ->
                            DropdownMenuItem(text = { Text(c.label) }, onClick = { category = c; categoryMenuOpen = false })
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val price = priceText.toIntOrNull() ?: 0
                if (name.isNotBlank() && price > 0) {
                    onSave(name, price, category, description)
                }
            }) { Text("Save", color = Pink) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
