package com.example.dorel
import android.view.View

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val firestore = FirebaseFirestore.getInstance()

    // UI Components for the card details
    private lateinit var cardNumberTextView: TextView
    private lateinit var cardHolderNameTextView: TextView
    private lateinit var expirationDateTextView: TextView
    private lateinit var balanceAmountTextView: TextView
    private lateinit var cardSelectorSpinner: Spinner

    private var selectedCardIBAN: String? = null // To track the currently selected card

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase
        FirebaseApp.initializeApp(this)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Check if the user is logged in
        if (auth.currentUser == null) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        // Set content view after checking user authentication
        setContentView(R.layout.activity_main)
        setupUI()
        fetchAndPopulateCards()
    }

    private fun setupUI() {
        // Initialize UI components
        cardNumberTextView = findViewById(R.id.card_number)
        cardHolderNameTextView = findViewById(R.id.card_holder_name)
        expirationDateTextView = findViewById(R.id.expiration_date)
        balanceAmountTextView = findViewById(R.id.balance_amount)
        cardSelectorSpinner = findViewById(R.id.card_selector_spinner)

        val visualizeSpendingButton: ImageView = findViewById(R.id.go_to_visualize_spending_button)
        visualizeSpendingButton.setOnClickListener {
            val serviceIntent = Intent(this, VisualizeSpendingService::class.java)
            startForegroundService(serviceIntent)
        }

        val transactionsButton: ImageView = findViewById(R.id.view_transactions_icon)
        transactionsButton.setOnClickListener {
            val intent = Intent(this, TransactionsActivity::class.java)
            startActivity(intent)
        }

        val budgetButton: ImageView = findViewById(R.id.manage_budget_icon)
        budgetButton.setOnClickListener {
            val intent = Intent(this, BudgetManagementActivity::class.java)
            startActivity(intent)
        }

        val transferButton: Button = findViewById(R.id.go_to_transfer_button)
        transferButton.setOnClickListener {
            val intent = Intent(this, TransferActivity::class.java)
            startActivity(intent)
        }

        val logoutButton: Button = findViewById(R.id.logout_button)
        logoutButton.setOnClickListener {
            auth.signOut()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        val viewProfileButton: Button = findViewById(R.id.view_profile_button)
        viewProfileButton.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        val currencyConverterButton: ImageView = findViewById(R.id.go_to_currency_converter)
        currencyConverterButton.setOnClickListener {
            val intent = Intent(this, CurrencyConverterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun fetchAndPopulateCards() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show()
            return
        }

        firestore.collection("users")
            .document(currentUser.uid)
            .collection("creditCards")
            .get()
            .addOnSuccessListener { documents ->
                val cards = documents.mapNotNull { it.toObject(CreditCard::class.java) }
                if (cards.isNotEmpty()) {
                    populateCardSelector(cards)
                    setupRealTimeListener(currentUser.uid) // Add a listener for balance updates
                } else {
                    Toast.makeText(this, "No cards found.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to fetch cards.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun populateCardSelector(cards: List<CreditCard>) {
        val cardLabels = cards.map { it.cardNumber } // Show card numbers in the Spinner

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, cardLabels)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        cardSelectorSpinner.adapter = adapter

        // Set the first card details as default
        if (cards.isNotEmpty()) {
            updateCardUI(cards[0])
            selectedCardIBAN = cards[0].iban
        }

        cardSelectorSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedCard = cards[position]
                updateCardUI(selectedCard)
                selectedCardIBAN = selectedCard.iban
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // No action needed
            }
        }
    }

    private fun updateCardUI(card: CreditCard) {
        cardNumberTextView.text = card.cardNumber
        cardHolderNameTextView.text = card.cardHolderName
        expirationDateTextView.text = card.expirationDate
        balanceAmountTextView.text = "$${card.balance}"
    }

    private fun setupRealTimeListener(userId: String) {
        firestore.collection("users")
            .document(userId)
            .collection("creditCards")
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    Toast.makeText(this, "Error listening for balance updates: ${error.message}", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                snapshots?.documentChanges?.forEach { change ->
                    val updatedCard = change.document.toObject(CreditCard::class.java)
                    if (updatedCard.iban == selectedCardIBAN) {
                        // Update UI if the changed card is the currently selected card
                        updateCardUI(updatedCard)
                    }
                }
            }
    }
}
