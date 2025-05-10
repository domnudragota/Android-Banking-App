package com.example.dorel

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ViewEditCreditCardActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private lateinit var cardNumberField: EditText
    private lateinit var cardHolderNameField: EditText
    private lateinit var expirationDateField: EditText
    private lateinit var balanceField: EditText
    private lateinit var saveButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_edit_credit_card)

        // Initialize Firebase
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // Bind UI elements
        cardNumberField = findViewById(R.id.edit_card_number)
        cardHolderNameField = findViewById(R.id.edit_card_holder_name)
        expirationDateField = findViewById(R.id.edit_expiration_date)
        balanceField = findViewById(R.id.edit_balance)
        saveButton = findViewById(R.id.save_changes_button)

        // Fetch and display credit card details
        fetchCreditCardDetails()

        // Save button click listener
        saveButton.setOnClickListener {
            saveChangesToFirestore()
        }
    }

    private fun fetchCreditCardDetails() {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            Toast.makeText(this, "User not logged in.", Toast.LENGTH_SHORT).show()
            return
        }

        firestore.collection("users")
            .document(userId)
            .collection("creditCards")
            .document("default") // Assuming "default" is the ID for the credit card document
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    // Populate fields with data from Firestore
                    cardNumberField.setText(document.getString("cardNumber"))
                    cardHolderNameField.setText(document.getString("cardHolderName"))
                    expirationDateField.setText(document.getString("expirationDate"))
                    balanceField.setText(document.getDouble("balance")?.toString())
                } else {
                    Toast.makeText(this, "No credit card found.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error fetching credit card: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveChangesToFirestore() {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            Toast.makeText(this, "User not logged in.", Toast.LENGTH_SHORT).show()
            return
        }

        // Retrieve updated data from input fields
        val updatedCardHolderName = cardHolderNameField.text.toString()
        val updatedExpirationDate = expirationDateField.text.toString()
        val updatedBalance = balanceField.text.toString().toDoubleOrNull()

        if (updatedBalance == null) {
            Toast.makeText(this, "Please enter a valid balance.", Toast.LENGTH_SHORT).show()
            return
        }

        // Update Firestore document
        val creditCardData = mapOf(
            "cardHolderName" to updatedCardHolderName,
            "expirationDate" to updatedExpirationDate,
            "balance" to updatedBalance
        )

        firestore.collection("users")
            .document(userId)
            .collection("creditCards")
            .document("default") // Assuming "default" is the ID for the credit card document
            .update(creditCardData)
            .addOnSuccessListener {
                Toast.makeText(this, "Credit card updated successfully.", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error updating credit card: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
