package com.example.pc2apolaya.presentation.navigation
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.pc2apolaya.presentation.auth.LoginScreen
import com.example.pc2apolaya.presentation.Screen.ConversionScreen
import com.example.pc2apolaya.presentation.auth.RegisterScreen


@Composable
fun NavigationMenu(){
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login"){
        composable("login"){ LoginScreen(navController) }


        //Navigation
        composable("home"){
            ConversionScreen(navController)
        }
        composable("register") {
            RegisterScreen(navController)
        }



    }

}