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
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun UpdateProfileScreen(navController: NavController) {
    val context = LocalContext.current
    val firestore = FirebaseFirestore.getInstance()
    val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    var name by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Update Profile", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Your Name") },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = {
            if (name.isNotEmpty()) {
                firestore.collection("users").document(uid)
                    .update("name", name)
                    .addOnSuccessListener {
                        Toast.makeText(context, "Profile Updated ✅", Toast.LENGTH_SHORT).show()
                        navController.navigate("home") {
                            popUpTo("update_profile") { inclusive = true }
                        }
                    }
                    .addOnFailureListener {
                        Toast.makeText(context, "Failed to update!", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(context, "Name can't be empty!", Toast.LENGTH_SHORT).show()
            }
        }) {
            Text("Save Name")
        }
    }
}
