package com.example.handmadeproducts

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.handmadeproducts.data.DataSource
import com.example.handmadeproducts.ui.screens.CheckoutScreen
import com.example.handmadeproducts.ui.screens.HomeScreen
import com.example.handmadeproducts.ui.screens.LoginScreen
import com.example.handmadeproducts.ui.screens.ProductDetailScreen
import com.example.handmadeproducts.ui.theme.HandmadeProductsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HandmadeProductsTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    var userName by remember { mutableStateOf("") }

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(onLogin = { name ->
                userName = name
                navController.navigate("home") {
                    popUpTo("login") { inclusive = true }
                }
            })
        }
        composable("home") {
            HomeScreen(
                userName = userName,
                onProductClick = { product ->
                    navController.navigate("detail/${product.id}")
                }
            )
        }
        composable(
            route = "detail/{productId}",
            arguments = listOf(navArgument("productId") { type = NavType.IntType })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getInt("productId")
            val product = DataSource.products.find { it.id == productId }
            
            if (product != null) {
                ProductDetailScreen(
                    product = product,
                    onBack = { navController.popBackStack() },
                    onAddToCart = {
                        navController.navigate("checkout")
                    }
                )
            }
        }
        composable("checkout") {
            CheckoutScreen(onHomeClick = {
                navController.navigate("home") {
                    popUpTo("home") { inclusive = true }
                }
            })
        }
    }
}
