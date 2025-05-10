package com.example.dorel

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class TransferValidationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("TransferValidationReceiver", "Receiver triggered")

        val senderIban = intent.getStringExtra("SENDER_IBAN")
        val recipientIban = intent.getStringExtra("RECIPIENT_IBAN")
        val amount = intent.getDoubleExtra("AMOUNT", 0.0)
        val date = intent.getStringExtra("DATE")

        Log.d(
            "TransferValidationReceiver",
            "Broadcast Data: Sender=$senderIban, Recipient=$recipientIban, Amount=$amount, Date=$date"
        )

        // Perform validation checks
        if (senderIban.isNullOrEmpty() || recipientIban.isNullOrEmpty() || amount <= 0) {
            Log.e("TransferValidationReceiver", "Validation failed: Missing or invalid data")
        } else {
            Log.d("TransferValidationReceiver", "Validation successful")
        }
    }
}
