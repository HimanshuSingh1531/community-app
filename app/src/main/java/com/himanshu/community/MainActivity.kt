package com.himanshu.community

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.himanshu.community.screens.HomeScreen
import com.himanshu.community.screens.LoginScreen
import com.himanshu.community.screens.SignUpScreen
import com.himanshu.community.ui.theme.CommunityTheme
import com.google.firebase.firestore.FirebaseFirestore // ✅ import this
import com.himanshu.community.screens.UpdateProfileScreen
import com.himanshu.community.screens.PostScreen
import com.himanshu.community.screens.FeedScreen
import com.himanshu.community.screens.DailyCheckinScreen



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ✅ Fix: Enable Firestore network
        FirebaseFirestore.getInstance().enableNetwork()

        setContent {
            CommunityTheme {
                AppNavigator()
            }
        }
    }
}

@Composable
fun AppNavigator() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(navController)
        }
        composable("signup") {
            SignUpScreen(navController)
        }
        composable("home") {
            HomeScreen(navController)
        }
        composable("update_profile") {
            UpdateProfileScreen(navController)
        }
        composable("post") {
            PostScreen(navController) // ✅ Add this line
        }
        composable("feed") {
            FeedScreen()
        }
        composable("daily_checkin") {
            DailyCheckinScreen()
        }



    }
}
