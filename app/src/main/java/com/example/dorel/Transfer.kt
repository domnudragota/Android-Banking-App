package com.example.dorel

data class Transfer(
    val name: String,
    val iban: String,
    val amount: Double,
    val message: String?,
    val date: String
)
