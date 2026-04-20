package com.example.handmadeproducts.data

import androidx.annotation.DrawableRes

data class Product(
    val id: Int,
    val name: String,
    val description: String,
    val price: Double,
    val category: String,
    val seller: String,
    val rating: Double,
    val deliveryTime: String,
    val imageRes: Int // We'll use placeholder images or resource IDs
)

data class Category(
    val name: String,
    val icon: Int? = null
)
