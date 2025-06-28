package com.himanshu.community.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.himanshu.community.firebase.PostHelper

@Composable
fun PostScreen(navController: NavController) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()

    var content by remember { mutableStateOf("") }
    val userId = auth.currentUser?.uid ?: ""

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Create a Post 📝", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = content,
            onValueChange = { content = it },
            label = { Text("What's on your mind?") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = {
            if (content.isNotEmpty()) {
                PostHelper.createPost(userId, content) { success ->
                    if (success) {
                        Toast.makeText(context, "Post Created 🎉", Toast.LENGTH_SHORT).show()
                        navController.popBackStack() // Go back to HomeScreen
                    } else {
                        Toast.makeText(context, "Post Failed ❌", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(context, "Content can't be empty!", Toast.LENGTH_SHORT).show()
            }
        }) {
            Text("Post")
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = {
            navController.popBackStack() // Go back without posting
        }) {
            Text("Cancel")
        }
    }
}
