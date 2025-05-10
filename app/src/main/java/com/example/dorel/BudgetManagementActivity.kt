package com.example.dorel

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import android.content.pm.PackageManager

class BudgetManagementActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_budget_management)

        requestNotificationPermission() // Ensure notification permission is granted

        val totalSpentText: TextView = findViewById(R.id.budget_total_spent)
        val spendingLimitInput: EditText = findViewById(R.id.spending_limit_input)
        val foodBudgetInput: EditText = findViewById(R.id.category_food_budget)
        val financeBudgetInput: EditText = findViewById(R.id.category_finance_budget)
        val mediaBudgetInput: EditText = findViewById(R.id.category_media_budget)
        val saveBudgetButton: Button = findViewById(R.id.save_budget_button)
        val backToMainButton: Button = findViewById(R.id.back_to_main_from_budget_button)

        // Fetch total spending from TransactionsActivity
        val totalSpent = TransactionsActivity.getTransactions()
            .filter { it.amount < 0 }
            .sumOf { it.amount }
        totalSpentText.text = "Total Spent: $${"%.2f".format(-totalSpent)}"

        // Save budget constraints
        saveBudgetButton.setOnClickListener {
            val spendingLimit = spendingLimitInput.text.toString().toDoubleOrNull()
            val foodBudget = foodBudgetInput.text.toString().toDoubleOrNull()
            val financeBudget = financeBudgetInput.text.toString().toDoubleOrNull()
            val mediaBudget = mediaBudgetInput.text.toString().toDoubleOrNull()

            if (spendingLimit == null || foodBudget == null || financeBudget == null || mediaBudget == null) {
                Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Example remaining budget calculations (replace with actual logic)
            val remainingFoodBudget = foodBudget - 50 // Example hardcoded value
            val remainingFinanceBudget = financeBudget - 100 // Example hardcoded value
            val remainingMediaBudget = mediaBudget - 25 // Example hardcoded value

            // Start the service with all data in a single intent
            val serviceIntent = Intent(this, BudgetService::class.java)
            serviceIntent.putExtra("FOOD_REMAINING", remainingFoodBudget)
            serviceIntent.putExtra("FINANCE_REMAINING", remainingFinanceBudget)
            serviceIntent.putExtra("MEDIA_REMAINING", remainingMediaBudget)
            startForegroundService(serviceIntent)

            Toast.makeText(this, "Budget saved successfully", Toast.LENGTH_SHORT).show()
        }

        // Back to Main Page
        backToMainButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun requestNotificationPermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    101 // Arbitrary request code
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 101) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Notification permission granted.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Notification permission denied.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
