package com.mad.campuseats.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mad.campuseats.ui.components.PrimaryButton
import com.mad.campuseats.ui.components.ScreenHeader
import com.mad.campuseats.ui.components.StarRatingInput
import com.mad.campuseats.ui.theme.*
import com.mad.campuseats.viewmodel.AppViewModel

@Composable
fun ReviewScreen(navController: NavController, viewModel: AppViewModel, orderId: String) {
    val state by viewModel.state.collectAsState()
    val order = state.orders.find { it.id == orderId } ?: return
    var rating by remember { mutableStateOf(5) }
    var comment by remember { mutableStateOf("") }
    var submitted by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
        ScreenHeader(title = "Rate your order", onBack = { navController.popBackStack() })

        if (submitted) {
            Column(
                modifier = Modifier.fillMaxSize().padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Success, modifier = Modifier.size(56.dp))
                Spacer(Modifier.height(12.dp))
                Text("Thanks for the feedback!", style = MaterialTheme.typography.titleMedium, color = Charcoal)
                Spacer(Modifier.height(20.dp))
                PrimaryButton(text = "Done", onClick = { navController.popBackStack() })
            }
        } else {
            Column(modifier = Modifier.weight(1f).padding(horizontal = 20.dp).padding(top = 16.dp)) {
                Text(order.restaurantName, style = MaterialTheme.typography.titleMedium, color = Charcoal)
                Spacer(Modifier.height(16.dp))
                Text("How was the food?", style = MaterialTheme.typography.bodyMedium, color = Ink)
                Spacer(Modifier.height(8.dp))
                StarRatingInput(rating = rating, onRatingChange = { rating = it })
                Spacer(Modifier.height(20.dp))
                Text("Leave a comment (optional)", style = MaterialTheme.typography.bodyMedium, color = Ink)
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = comment,
                    onValueChange = { comment = it },
                    placeholder = { Text("Tell others what you liked or didn't...") },
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Pink, cursorColor = Pink, unfocusedBorderColor = Line)
                )
            }
            Column(modifier = Modifier.navigationBarsPadding().padding(20.dp)) {
                PrimaryButton(text = "Submit review", onClick = {
                    viewModel.addReview(order.restaurantId, rating, comment)
                    viewModel.markReviewed(order.id)
                    submitted = true
                })
            }
        }
    }
}

@Composable
fun ComplaintScreen(navController: NavController, viewModel: AppViewModel, orderId: String) {
    val state by viewModel.state.collectAsState()
    val order = state.orders.find { it.id == orderId } ?: return
    var description by remember { mutableStateOf("") }
    var photoAttached by remember { mutableStateOf(false) }
    var submitted by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
        ScreenHeader(title = "Report a problem", onBack = { navController.popBackStack() })

        if (submitted) {
            Column(
                modifier = Modifier.fillMaxSize().padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Success, modifier = Modifier.size(56.dp))
                Spacer(Modifier.height(12.dp))
                Text("Complaint submitted", style = MaterialTheme.typography.titleMedium, color = Charcoal)
                Spacer(Modifier.height(4.dp))
                Text("The restaurant admin can see this now.", style = MaterialTheme.typography.bodySmall, color = MutedText)
                Spacer(Modifier.height(20.dp))
                PrimaryButton(text = "Done", onClick = { navController.popBackStack() })
            }
        } else {
            Column(modifier = Modifier.weight(1f).padding(horizontal = 20.dp).padding(top = 16.dp)) {
                Text("Order #${order.id} · ${order.restaurantName}", style = MaterialTheme.typography.titleMedium, color = Charcoal)
                Spacer(Modifier.height(16.dp))
                Text("What went wrong?", style = MaterialTheme.typography.bodyMedium, color = Ink)
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    placeholder = { Text("Describe the issue with your order...") },
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Pink, cursorColor = Pink, unfocusedBorderColor = Line)
                )
                Spacer(Modifier.height(16.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (photoAttached) PinkTint else Color.White)
                        .border(1.dp, if (photoAttached) Pink else Line, RoundedCornerShape(14.dp))
                        .clickable { photoAttached = !photoAttached }
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.CameraAlt, contentDescription = null, tint = if (photoAttached) Pink else MutedText)
                    Spacer(Modifier.width(10.dp))
                    Text(
                        if (photoAttached) "Photo attached — tap to remove" else "Attach a photo (optional)",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (photoAttached) Pink else MutedText
                    )
                }
            }
            Column(modifier = Modifier.navigationBarsPadding().padding(20.dp)) {
                PrimaryButton(text = "Submit complaint", enabled = description.isNotBlank(), onClick = {
                    viewModel.addComplaint(order.id, description, photoAttached)
                    submitted = true
                })
            }
        }
    }
}
