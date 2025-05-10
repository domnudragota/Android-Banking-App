package com.example.dorel

data class PieChartData(
    val label: String,   // The label for the category (e.g., "Food")
    val value: Float,    // The numeric value associated with the category
    val color: Int       // The color resource ID for this segment
)
