package com.himanshu.community.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

data class Post(
    val id: String = "",
    val userId: String = "",
    val text: String = "",
    val timestamp: Long = 0L,
    val likes: List<String> = emptyList()
)

@Composable
fun FeedScreen() {
    val firestore = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    val currentUserId = auth.currentUser?.uid ?: ""
    var posts by remember { mutableStateOf<List<Post>>(emptyList()) }

    LaunchedEffect(Unit) {
        try {
            val snapshot = firestore.collection("posts")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .await()

            posts = snapshot.documents.mapNotNull { doc ->
                val postId = doc.id
                val userId = doc.getString("userId") ?: return@mapNotNull null
                val text = doc.getString("text") ?: return@mapNotNull null
                val timestamp = doc.getLong("timestamp") ?: 0L
                val likes = doc.get("likes") as? List<String> ?: emptyList()
                Post(postId, userId, text, timestamp, likes)
            }
        } catch (e: Exception) {
            Log.e("FeedScreen", "Error loading posts", e)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Community Feed 📰", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(posts) { post ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("User: ${post.userId}", style = MaterialTheme.typography.labelMedium)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(post.text, style = MaterialTheme.typography.bodyLarge)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            val isLiked = post.likes.contains(currentUserId)
                            val icon = if (isLiked) "💖" else "🤍"
                            TextButton(onClick = {
                                val newLikes = if (isLiked) {
                                    post.likes - currentUserId
                                } else {
                                    post.likes + currentUserId
                                }

                                firestore.collection("posts")
                                    .document(post.id)
                                    .update("likes", newLikes)
                                    .addOnSuccessListener {
                                        posts = posts.map {
                                            if (it.id == post.id) it.copy(likes = newLikes) else it
                                        }
                                    }
                            }) {
                                Text("$icon I feel same (${post.likes.size})")
                            }
                        }
                    }
                }
            }
        }
    }
}
