package com.mad.campuseats.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import com.mad.campuseats.navigation.Screen
import com.mad.campuseats.ui.components.*
import com.mad.campuseats.ui.theme.*
import com.mad.campuseats.viewmodel.AppViewModel

@Composable
fun ProfileScreen(navController: NavController, viewModel: AppViewModel) {
    val state by viewModel.state.collectAsState()
    val user = state.currentUser
    var notificationsEnabled by remember { mutableStateOf(true) }
    StatusBarStyle(darkIcons = false)

    Column(modifier = Modifier.fillMaxSize().background(AppBg).verticalScroll(rememberScrollState())) {
        Row(
            Modifier
                .fillMaxWidth()
                .background(Pink)
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier.size(60.dp).clip(CircleShape).background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Text(user?.name?.take(1)?.uppercase() ?: "S", color = Pink, style = MaterialTheme.typography.headlineSmall)
            }
            Spacer(Modifier.width(14.dp))
            Column {
                Text(user?.name ?: "Student", style = MaterialTheme.typography.titleLarge, color = Color.White)
                Text(user?.email ?: "", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.9f))
                if (!user?.studentId.isNullOrBlank()) {
                    Text("ID: ${user?.studentId}", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.9f))
                }
            }
        }

        Spacer(Modifier.height(16.dp))
        Column(
            Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(Color.White)
        ) {
            SettingsRow(Icons.Default.Notifications, "Order notifications") {
                Switch(
                    checked = notificationsEnabled,
                    onCheckedChange = { notificationsEnabled = it },
                    colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Pink)
                )
            }
            HorizontalDivider(color = Line)
            SettingsRow(Icons.Default.Receipt, "Order history", onClick = { navController.navigate(Screen.Orders.route) }) {
                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MutedText)
            }
            HorizontalDivider(color = Line)
            SettingsRow(Icons.Default.Favorite, "Favourite restaurants") {
                Text("${state.favoriteIds.size}", style = MaterialTheme.typography.labelLarge, color = MutedText)
            }
            HorizontalDivider(color = Line)
            SettingsRow(Icons.Default.Info, "About CampusEats") {
                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MutedText)
            }
        }

        Spacer(Modifier.height(24.dp))
        Box(Modifier.padding(horizontal = 16.dp)) {
            OutlinePillButton(
                text = "Log out",
                color = Pink,
                icon = Icons.Default.Logout,
                onClick = {
                    viewModel.logout()
                    navController.navigate(Screen.Login.route) { popUpTo(0) }
                }
            )
        }
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun SettingsRow(
    icon: ImageVector,
    label: String,
    onClick: (() -> Unit)? = null,
    trailing: @Composable () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = Ink)
        Spacer(Modifier.width(14.dp))
        Text(label, style = MaterialTheme.typography.bodyMedium, color = Charcoal, modifier = Modifier.weight(1f))
        trailing()
    }
}
