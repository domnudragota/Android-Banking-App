package com.example.dorel

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class NewTransactionActivity : AppCompatActivity() {

    private var selectedDate: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_transaction)

        val nameField: EditText = findViewById(R.id.transaction_name)
        val ibanField: EditText = findViewById(R.id.transaction_iban)
        val amountField: EditText = findViewById(R.id.transaction_amount)
        val messageField: EditText = findViewById(R.id.transaction_message)
        val timeGroup: RadioGroup = findViewById(R.id.transaction_time_group)
        val submitButton: Button = findViewById(R.id.submit_transaction_button)

        // Show/Hide DatePickerDialog Based on Radio Selection
        timeGroup.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.time_schedule) {
                showDatePicker()
            } else {
                selectedDate = null // Reset the selected date for "ASAP"
            }
        }

        // Submit Transaction
        submitButton.setOnClickListener {
            val name = nameField.text.toString()
            val iban = ibanField.text.toString()
            val amount = amountField.text.toString().toDoubleOrNull()
            val message = messageField.text.toString()
            val transactionTime = if (selectedDate != null) selectedDate else "ASAP"

            if (name.isBlank() || iban.isBlank() || amount == null) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Add new transaction
            val newTransaction = Transaction(
                name = "$name (IBAN: $iban)",
                amount = -amount,
                date = transactionTime!!,
                category = "General",
                description = message
            )

            TransactionsActivity.addTransaction(newTransaction)

            Toast.makeText(this, "Transaction Submitted", Toast.LENGTH_SHORT).show()
            finish() // Close activity
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
            Toast.makeText(this, "Selected Date: $selectedDate", Toast.LENGTH_SHORT).show()
        }, year, month, day)

        datePickerDialog.show()
    }
}
