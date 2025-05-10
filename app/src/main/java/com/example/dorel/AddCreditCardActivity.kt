package com.example.dorel

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AddCreditCardActivity : AppCompatActivity() {

    private lateinit var cardNumberInput: EditText
    private lateinit var cardHolderNameInput: EditText
    private lateinit var expirationDateInput: EditText
    private lateinit var balanceInput: EditText
    private lateinit var saveButton: Button
    private val firestore = FirebaseFirestore.getInstance()
    private val currentUser = FirebaseAuth.getInstance().currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_credit_card)

        // Initialize UI components
        cardNumberInput = findViewById(R.id.card_number_input)
        cardHolderNameInput = findViewById(R.id.card_holder_name_input)
        expirationDateInput = findViewById(R.id.expiration_date_input)
        balanceInput = findViewById(R.id.balance_input)
        saveButton = findViewById(R.id.save_button)

        // Save button click listener
        saveButton.setOnClickListener {
            saveCreditCard()
        }
    }

    private fun saveCreditCard() {
        val cardNumber = cardNumberInput.text.toString()
        val cardHolderName = cardHolderNameInput.text.toString()
        val expirationDate = expirationDateInput.text.toString()
        val balance = balanceInput.text.toString().toDoubleOrNull()

        if (cardNumber.isBlank() || cardHolderName.isBlank() || expirationDate.isBlank() || balance == null) {
            Toast.makeText(this, "Please fill all fields correctly.", Toast.LENGTH_SHORT).show()
            return
        }

        // Check if the user is logged in
        currentUser?.let { user ->
            // Reference for new document under creditCards collection
            val creditCardRef = firestore.collection("users")
                .document(user.uid)
                .collection("creditCards")
                .document()

            // Create card data with Firestore document ID as IBAN
            val cardData = hashMapOf(
                "cardNumber" to cardNumber,
                "cardHolderName" to cardHolderName,
                "expirationDate" to expirationDate,
                "balance" to balance,
                "iban" to creditCardRef.id // Use the document ID as IBAN
            )

            // Save card data to Firestore
            creditCardRef.set(cardData)
                .addOnSuccessListener {
                    Toast.makeText(this, "Credit card added successfully!", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to add card: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } ?: run {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show()
        }
    }
}
