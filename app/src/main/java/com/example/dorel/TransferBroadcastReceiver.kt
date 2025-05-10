package com.example.dorel

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class TransferBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val senderIban = intent.getStringExtra("SENDER_IBAN")
        val recipientIban = intent.getStringExtra("RECIPIENT_IBAN")
        val amount = intent.getDoubleExtra("AMOUNT", 0.0)
        val date = intent.getStringExtra("DATE")

        Log.d("TransferBroadcast", "Transfer Completed:")
        Log.d("TransferBroadcast", "Sender IBAN: $senderIban")
        Log.d("TransferBroadcast", "Recipient IBAN: $recipientIban")
        Log.d("TransferBroadcast", "Amount: $amount")
        Log.d("TransferBroadcast", "Date: $date")
    }
}
