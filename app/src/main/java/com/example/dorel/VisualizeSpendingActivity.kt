package com.example.dorel

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class VisualizeSpendingActivity : AppCompatActivity() {

    private lateinit var customPieChart: CustomPieChartView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_visualize_spending)

        customPieChart = findViewById(R.id.custom_pie_chart)

        // Use a thread to process data for the pie chart
        val handler = Handler(Looper.getMainLooper())
        Thread {
            val spendingData = calculateMonthlySpending()
            handler.post {
                populatePieChart(spendingData)
            }
        }.start()
    }

    private fun calculateMonthlySpending(): List<Pair<String, Float>> {
        val transactions = TransactionsActivity.getTransactions()

        val foodSpent = transactions.filter { it.category == "Food" }.sumOf { it.amount }
        val financeSpent = transactions.filter { it.category == "Finance" }.sumOf { it.amount }
        val mediaSpent = transactions.filter { it.category == "Media" }.sumOf { it.amount }

        return listOf(
            Pair("Food", foodSpent.toFloat()),
            Pair("Finance", financeSpent.toFloat()),
            Pair("Media", mediaSpent.toFloat())
        )
    }

    private fun populatePieChart(data: List<Pair<String, Float>>) {
        val pieData = data.map { it.second }
        val labels = data.map { it.first }

        // Retrieve colors from resources
        val colors = listOf(
            ContextCompat.getColor(this, R.color.color1),
            ContextCompat.getColor(this, R.color.color2),
            ContextCompat.getColor(this, R.color.color3),
            ContextCompat.getColor(this, R.color.color4)
        )

        // Set the data and colors to the custom pie chart
        customPieChart.setData(pieData, labels, colors)
    }
}
