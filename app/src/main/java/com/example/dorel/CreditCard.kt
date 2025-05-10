package com.example.dorel

data class CreditCard(
    val cardNumber: String = "",
    val cardHolderName: String = "",
    val expirationDate: String = "",
    val balance: Double = 0.0,
    val iban: String = ""
)
