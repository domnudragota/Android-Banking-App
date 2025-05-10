package com.example.dorel

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class TransferActivity : AppCompatActivity() {

    private var selectedDate: String? = null
    private val firestore = FirebaseFirestore.getInstance()
    private val currentUser = FirebaseAuth.getInstance().currentUser
    private lateinit var senderIbanSpinner: Spinner
    private var senderIban: String? = null

    @SuppressLint("InlinedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transfer)

        val recipientName: AutoCompleteTextView = findViewById(R.id.recipient_name)
        val ibanInput: EditText = findViewById(R.id.iban_input)
        val amountInput: EditText = findViewById(R.id.amount_input)
        val messageInput: EditText = findViewById(R.id.message_input)
        val timeGroup: RadioGroup = findViewById(R.id.time_group)
        val transferButton: Button = findViewById(R.id.transfer_button)
        senderIbanSpinner = findViewById(R.id.sender_iban_spinner)

        val recipientSuggestions = listOf("John Doe", "Jane Smith", "Mike Johnson")
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, recipientSuggestions)
        recipientName.setAdapter(adapter)

        requestNotificationPermission()
        fetchSenderIbans()

        timeGroup.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.time_schedule) {
                showDatePickerDialog()
            } else {
                selectedDate = "Now"
            }
        }

        transferButton.setOnClickListener {
            val name = recipientName.text.toString()
            val iban = ibanInput.text.toString()
            val amount = amountInput.text.toString().toDoubleOrNull()
            val message = messageInput.text.toString()
            val transferDate = selectedDate ?: "Now"
            senderIban = senderIbanSpinner.selectedItem?.toString()

            if (name.isBlank() || iban.isBlank() || amount == null || senderIban.isNullOrBlank()) {
                Toast.makeText(this, "Please fill in all mandatory fields.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            processTransfer(iban, amount, message, transferDate)
        }

        val receiver = TransferValidationReceiver()
        val filter = IntentFilter("com.example.dorel.TRANSFER_COMPLETE")
        registerReceiver(receiver, filter, RECEIVER_NOT_EXPORTED)
    }

    private fun fetchSenderIbans() {
        if (currentUser == null) {
            Toast.makeText(this, "User not logged in.", Toast.LENGTH_SHORT).show()
            return
        }

        val senderUserId = currentUser.uid
        firestore.collection("users")
            .document(senderUserId)
            .collection("creditCards")
            .get()
            .addOnSuccessListener { cards ->
                val ibans = cards.documents.mapNotNull { it.getString("iban") }
                val ibanAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, ibans)
                ibanAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                senderIbanSpinner.adapter = ibanAdapter
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to fetch sender IBANs.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun processTransfer(recipientIban: String, amount: Double, message: String?, transferDate: String) {
        if (currentUser == null || senderIban.isNullOrBlank()) {
            Toast.makeText(this, "User not logged in or IBAN not selected.", Toast.LENGTH_SHORT).show()
            return
        }

        val senderUserId = currentUser.uid
        val senderCardQuery = firestore.collection("users")
            .document(senderUserId)
            .collection("creditCards")
            .whereEqualTo("iban", senderIban)

        senderCardQuery.get().addOnSuccessListener { senderCards ->
            if (senderCards.isEmpty) {
                Toast.makeText(this, "No valid card found for sender with IBAN: $senderIban.", Toast.LENGTH_SHORT).show()
                return@addOnSuccessListener
            }

            val senderCard = senderCards.documents[0]
            val senderBalance = senderCard.getDouble("balance") ?: 0.0

            if (senderBalance < amount) {
                Toast.makeText(this, "Insufficient balance.", Toast.LENGTH_SHORT).show()
                return@addOnSuccessListener
            }

            findRecipientCard(recipientIban, amount, senderCard, message, transferDate)
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to retrieve sender card.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun findRecipientCard(
        recipientIban: String,
        amount: Double,
        senderCard: com.google.firebase.firestore.DocumentSnapshot,
        message: String?,
        transferDate: String
    ) {
        firestore.collection("users").get().addOnSuccessListener { users ->
            var recipientFound = false

            for (user in users.documents) {
                user.reference.collection("creditCards")
                    .whereEqualTo("iban", recipientIban)
                    .get()
                    .addOnSuccessListener { recipientCards ->
                        if (!recipientCards.isEmpty) {
                            recipientFound = true
                            val recipientCard = recipientCards.documents[0]

                            val recipientBalance = recipientCard.getDouble("balance") ?: 0.0

                            // Perform transfer
                            updateBalances(
                                senderCard,
                                recipientCard,
                                senderCard.getDouble("balance") ?: 0.0,
                                recipientBalance,
                                amount
                            )
                            sendTransferNotification(
                                recipientCard.getString("cardHolderName") ?: "Unknown",
                                amount
                            )
                            broadcastTransfer(senderIban!!, recipientIban, amount, transferDate)
                            return@addOnSuccessListener
                        }
                    }
            }

            if (!recipientFound) {
                Toast.makeText(this, "Recipient card not found.", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to search for recipient card.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateBalances(
        senderCard: com.google.firebase.firestore.DocumentSnapshot,
        recipientCard: com.google.firebase.firestore.DocumentSnapshot,
        senderBalance: Double,
        recipientBalance: Double,
        amount: Double
    ) {
        senderCard.reference.update("balance", senderBalance - amount).addOnFailureListener {
            Toast.makeText(this, "Failed to update sender's balance.", Toast.LENGTH_SHORT).show()
        }

        recipientCard.reference.update("balance", recipientBalance + amount).addOnFailureListener {
            Toast.makeText(this, "Failed to update recipient's balance.", Toast.LENGTH_SHORT).show()
        }

        Toast.makeText(this, "Transfer Successful!", Toast.LENGTH_SHORT).show()
    }

    private fun broadcastTransfer(senderIban: String, recipientIban: String, amount: Double, date: String) {
        val intent = Intent("com.example.dorel.TRANSFER_COMPLETE").apply {
            putExtra("SENDER_IBAN", senderIban)
            putExtra("RECIPIENT_IBAN", recipientIban)
            putExtra("AMOUNT", amount)
            putExtra("DATE", date)
        }
        intent.setPackage("com.example.dorel")
        sendBroadcast(intent)

        Log.d("TransferBroadcast", "Broadcast Sent: Sender=$senderIban, Recipient=$recipientIban, Amount=$amount, Date=$date")
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                calendar.set(year, month, dayOfMonth)
                selectedDate = dateFormat.format(calendar.time)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun sendTransferNotification(name: String, amount: Double) {
        val channelId = "TransferNotificationChannel"
        val notificationId = 1

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Transfer Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for transfer completion."
            }
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_transfer)
            .setContentTitle("Transfer Completed")
            .setContentText("You transferred $$amount to $name.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, notification)
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    101
                )
            }
        }
    }
}
