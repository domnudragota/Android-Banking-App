package com.example.dorel

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class TransactionsActivity : AppCompatActivity() {

    companion object {
        private val transactionList = mutableListOf(
            Transaction(
                name = "Netflix Subscription",
                amount = -12.99,
                date = "2024-11-01",
                category = "Media",
                description = "Monthly Netflix payment"
            ),
            Transaction(
                name = "Grocery Store",
                amount = -45.30,
                date = "2024-11-02",
                category = "Food",
                description = "Weekly grocery shopping"
            ),
            Transaction(
                name = "Bank Loan Payment",
                amount = -200.00,
                date = "2024-11-03",
                category = "Finance",
                description = "Monthly loan repayment"
            )
        )

        fun addTransaction(transaction: Transaction) {
            transactionList.add(transaction)
        }

        fun getTransactions(): List<Transaction> {
            return transactionList
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transactions)

        // Setup New Transaction Button
        val newTransactionButton: Button = findViewById(R.id.new_transaction_button)
        newTransactionButton.setOnClickListener {
            val intent = Intent(this, NewTransactionActivity::class.java)
            startActivity(intent)
        }

        // Back to Main Page Button
        val backToMainButton: Button = findViewById(R.id.back_to_main_button)
        backToMainButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        // Set up RecyclerView for transactions
        val transactionRecyclerView: RecyclerView = findViewById(R.id.transaction_recycler)
        transactionRecyclerView.layoutManager = LinearLayoutManager(this)

        val adapter = TransactionAdapter(getTransactions())
        transactionRecyclerView.adapter = adapter

        // Calculate and show monthly spending
        val monthlySpendingText: TextView = findViewById(R.id.monthly_spending)
        val totalSpent = getTransactions().filter { it.amount < 0 }.sumOf { it.amount }
        monthlySpendingText.text = "Total Spent: $${"%.2f".format(-totalSpent)}"
    }

    override fun onResume() {
        super.onResume()
        // Refresh the RecyclerView when returning to the activity
        val transactionRecyclerView: RecyclerView = findViewById(R.id.transaction_recycler)
        val adapter = TransactionAdapter(getTransactions())
        transactionRecyclerView.adapter = adapter

        // Refresh the total spending amount
        val monthlySpendingText: TextView = findViewById(R.id.monthly_spending)
        val totalSpent = getTransactions().filter { it.amount < 0 }.sumOf { it.amount }
        monthlySpendingText.text = "Total Spent: $${"%.2f".format(-totalSpent)}"
    }
}
