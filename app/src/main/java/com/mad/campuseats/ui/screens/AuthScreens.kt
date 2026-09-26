package com.mad.campuseats.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mad.campuseats.navigation.Screen
import com.mad.campuseats.ui.components.AuthScaffold
import com.mad.campuseats.ui.components.AuthTextField
import com.mad.campuseats.ui.components.PrimaryButton
import com.mad.campuseats.ui.theme.*
import com.mad.campuseats.viewmodel.AppViewModel

@Composable
fun LoginScreen(navController: NavController, viewModel: AppViewModel) {
    val state by viewModel.state.collectAsState()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    AuthScaffold(
        heroColor = Pink,
        title = "CampusEats",
        subtitle = "Hungry on campus? Order in minutes."
    ) {
        Text("Log in", style = MaterialTheme.typography.titleLarge, color = Charcoal)
        Spacer(Modifier.height(16.dp))
        AuthTextField(email, { email = it }, "University email", keyboardType = KeyboardType.Email)
        Spacer(Modifier.height(12.dp))
        AuthTextField(password, { password = it }, "Password", password = true)

        state.authError?.let {
            Spacer(Modifier.height(10.dp))
            Text(it, color = Danger, style = MaterialTheme.typography.bodySmall)
        }

        Spacer(Modifier.height(20.dp))
        PrimaryButton(text = "Log in", onClick = { viewModel.login(email, password) })

        Spacer(Modifier.height(8.dp))
        TextButton(
            onClick = {
                viewModel.clearAuthError()
                navController.navigate(Screen.Register.route)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("New here? Create an account", color = Pink)
        }
        TextButton(
            onClick = {
                viewModel.clearAuthError()
                navController.navigate(Screen.AdminLogin.route)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Restaurant / cafeteria admin login", color = MutedText)
        }
    }

    LaunchedEffect(state.currentUser) {
        if (state.currentUser != null) {
            navController.navigate(Screen.Home.route) { popUpTo(0) }
        }
    }
}

@Composable
fun RegisterScreen(navController: NavController, viewModel: AppViewModel) {
    val state by viewModel.state.collectAsState()
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var studentId by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    AuthScaffold(
        heroColor = Pink,
        title = "Create account",
        subtitle = "Sign up with your Premier University details",
        onBack = { navController.popBackStack() }
    ) {
        AuthTextField(name, { name = it }, "Full name")
        Spacer(Modifier.height(12.dp))
        AuthTextField(email, { email = it }, "University email", keyboardType = KeyboardType.Email)
        Spacer(Modifier.height(12.dp))
        AuthTextField(studentId, { studentId = it }, "Student ID")
        Spacer(Modifier.height(12.dp))
        AuthTextField(password, { password = it }, "Password", password = true)

        state.authError?.let {
            Spacer(Modifier.height(10.dp))
            Text(it, color = Danger, style = MaterialTheme.typography.bodySmall)
        }

        Spacer(Modifier.height(20.dp))
        PrimaryButton(text = "Create account", onClick = { viewModel.register(name, email, studentId, password) })
        Spacer(Modifier.height(8.dp))
        TextButton(onClick = { navController.popBackStack() }, modifier = Modifier.fillMaxWidth()) {
            Text("Already have an account? Log in", color = Pink)
        }
    }

    LaunchedEffect(state.currentUser) {
        if (state.currentUser != null) {
            navController.navigate(Screen.Home.route) { popUpTo(0) }
        }
    }
}

@Composable
fun AdminLoginScreen(navController: NavController, viewModel: AppViewModel) {
    val state by viewModel.state.collectAsState()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    AuthScaffold(
        heroColor = Charcoal,
        title = "Admin portal",
        subtitle = "Restaurant & cafeteria management",
        onBack = {
            viewModel.clearAuthError()
            navController.popBackStack()
        }
    ) {
        AuthTextField(email, { email = it }, "Admin email", keyboardType = KeyboardType.Email)
        Spacer(Modifier.height(12.dp))
        AuthTextField(password, { password = it }, "Password", password = true)

        state.authError?.let {
            Spacer(Modifier.height(10.dp))
            Text(it, color = Danger, style = MaterialTheme.typography.bodySmall)
        }

        Spacer(Modifier.height(20.dp))
        PrimaryButton(text = "Log in as admin", onClick = { viewModel.loginAdmin(email, password) })
    }

    LaunchedEffect(state.currentUser) {
        if (state.currentUser != null) {
            navController.navigate(Screen.AdminDashboard.route) { popUpTo(0) }
        }
    }
}
