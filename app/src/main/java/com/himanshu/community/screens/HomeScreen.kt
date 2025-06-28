package com.himanshu.community.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    val email = user?.email ?: "Unknown"

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Welcome 👋", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                },
                actions = {
                    TextButton(onClick = {
                        Toast.makeText(context, "Profile Clicked", Toast.LENGTH_SHORT).show()
                    }) {
                        Text("Profile")
                    }
                }
            )
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(24.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.Top),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Logged in as: $email", style = MaterialTheme.typography.bodyLarge)

                Button(
                    onClick = {
                        navController.navigate("daily_checkin")
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("📅 Daily Check-In")
                }

                Button(
                    onClick = {
                        navController.navigate("community")
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("💬 Community Feed")
                }

                Button(
                    onClick = {
                        navController.navigate("post")
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("📝 Create Post")
                }

                Button(
                    onClick = {
                        navController.navigate("update_profile")
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("👤 Update Profile")
                }

                Button(
                    onClick = {
                        auth.signOut()
                        Toast.makeText(context, "Logged out", Toast.LENGTH_SHORT).show()
                        navController.navigate("login") {
                            popUpTo("home") { inclusive = true }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("🚪 Logout")
                }
            }
        }
    )
}
