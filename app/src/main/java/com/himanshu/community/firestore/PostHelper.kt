package com.himanshu.community.firebase

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore

object PostHelper {

    private val firestore = FirebaseFirestore.getInstance()

    // ✅ Create a new post
    fun createPost(userId: String, text: String, onResult: (Boolean) -> Unit) {
        val postData = hashMapOf(
            "userId" to userId,
            "text" to text,
            "createdAt" to Timestamp.now(),
            "likes" to emptyList<String>(), // start with empty likes
            "groupId" to "defaultGroupId" // For now, we can hardcode groupId
        )

        firestore.collection("posts")
            .add(postData)
            .addOnSuccessListener {
                onResult(true)
            }
            .addOnFailureListener { e ->
                Log.e("PostHelper", "Post create failed", e)
                onResult(false)
            }
    }

    // ✅ Fetch all posts
    fun fetchPosts(onPostsFetched: (List<String>) -> Unit) {
        firestore.collection("posts")
            .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                val posts = result.documents.mapNotNull { doc ->
                    doc.getString("text")
                }
                onPostsFetched(posts)
            }
            .addOnFailureListener { e ->
                Log.e("PostHelper", "Fetching posts failed", e)
                onPostsFetched(emptyList())
            }
    }
}
