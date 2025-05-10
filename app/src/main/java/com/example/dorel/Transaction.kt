package com.example.dorel

data class Transaction(
    val name: String,
    val amount: Double,
    val date: String,
    val category: String,
    val description: String? = null
)
