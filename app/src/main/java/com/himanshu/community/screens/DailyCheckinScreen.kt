package com.himanshu.community.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

data class GroupPost(
    val id: String = "",
    val userId: String = "",
    val userName: String = "",
    val text: String = "",
    val timestamp: Long = 0L,
    val likes: List<String> = emptyList()
)

@Composable
fun DailyCheckinScreen() {
    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()
    val userId = auth.currentUser?.uid ?: ""
    var posts by remember { mutableStateOf<List<GroupPost>>(emptyList()) }
    var hasPostedToday by remember { mutableStateOf(false) }
    var userName by remember { mutableStateOf("You") }

    val currentDateKey = remember {
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }

    // 🔁 Fetch posts on load
    LaunchedEffect(Unit) {
        try {
            // Get user's name
            firestore.collection("users").document(userId).get().addOnSuccessListener {
                userName = it.getString("name") ?: "You"
            }

            val snapshot = firestore.collection("posts")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .await()

            posts = snapshot.documents.mapNotNull { doc ->
                val postUserId = doc.getString("userId") ?: return@mapNotNull null
                val text = doc.getString("text") ?: return@mapNotNull null
                val timestamp = doc.getLong("timestamp") ?: 0L
                val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(timestamp))
                if (postUserId == userId && dateStr == currentDateKey) {
                    hasPostedToday = true
                }

                GroupPost(
                    id = doc.id,
                    userId = postUserId,
                    userName = if (postUserId == userId) "You" else "User",
                    text = text,
                    timestamp = timestamp,
                    likes = doc.get("likes") as? List<String> ?: emptyList()
                )
            }
        } catch (e: Exception) {
            Log.e("DailyCheckinScreen", "Error loading posts", e)
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // ✅ Daily Check-in Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                Text("Daily Check-in", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                if (hasPostedToday) {
                    Text("✅ You’ve shared today!")
                } else {
                    Text("➕ Ready to share?", color = Color.Blue)
                }
            }
            Text("🕒 ${timeRemainingToday()}")
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ➕ Share Thoughts
        if (!hasPostedToday) {
            Button(onClick = {
                // TODO: Navigate to post screen
            }) {
                Text("Share how you're feeling")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 📰 Post Feed
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(posts) { post ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(Color.LightGray)
                                    .padding(4.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(post.userName, fontWeight = FontWeight.Bold)
                                Text(timeAgo(post.timestamp), fontSize = 12.sp, color = Color.Gray)
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(post.text)

                        Spacer(modifier = Modifier.height(8.dp))
                        val liked = post.likes.contains(userId)
                        val icon = if (liked) "💖" else "🤍"
                        TextButton(onClick = {
                            val updatedLikes = if (liked) {
                                post.likes - userId
                            } else {
                                post.likes + userId
                            }
                            firestore.collection("posts").document(post.id)
                                .update("likes", updatedLikes)
                                .addOnSuccessListener {
                                    posts = posts.map {
                                        if (it.id == post.id) it.copy(likes = updatedLikes) else it
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

fun timeAgo(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    val minutes = diff / 60000
    return when {
        minutes < 1 -> "Just now"
        minutes < 60 -> "$minutes min ago"
        minutes < 1440 -> "${minutes / 60} hr ago"
        else -> "${minutes / 1440} days ago"
    }
}

fun timeRemainingToday(): String {
    val now = Calendar.getInstance()
    val end = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 23)
        set(Calendar.MINUTE, 59)
        set(Calendar.SECOND, 59)
    }
    val diff = end.timeInMillis - now.timeInMillis
    val hours = diff / (1000 * 60 * 60)
    val minutes = (diff / (1000 * 60)) % 60
    val seconds = (diff / 1000) % 60
    return String.format("%02d:%02d:%02d remaining today", hours, minutes, seconds)
}
