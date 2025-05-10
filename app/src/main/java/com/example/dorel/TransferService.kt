package com.example.dorel

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import android.util.Log

class TransferService : Service() {

    companion object {
        const val CHANNEL_ID = "TransferServiceChannel"
    }

    override fun onCreate() {
        super.onCreate()
        Log.d("TransferService", "Service created")
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("TransferService", "Service started")

        // Retrieve data from intent
        val recipientName = intent?.getStringExtra("RECIPIENT_NAME") ?: "Unknown"
        val amount = intent?.getDoubleExtra("AMOUNT", 0.0) ?: 0.0
        val transferDate = intent?.getStringExtra("TRANSFER_DATE") ?: "Now"

        // Notification content
        val notificationContent = """
            Recipient: $recipientName
            Amount: $${"%.2f".format(amount)}
            Date: $transferDate
        """.trimIndent()

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Transfer Completed")
            .setContentText("You transferred $$amount to $recipientName.")
            .setStyle(NotificationCompat.BigTextStyle().bigText(notificationContent)) // Expandable content
            .setSmallIcon(R.drawable.ic_transfer) // Replace with your actual icon
            .setPriority(NotificationCompat.PRIORITY_HIGH) // Ensures visibility
            .build()

        startForeground(1, notification)
        Log.d("TransferService", "Notification displayed")

        // Stop the service after showing the notification
        stopSelf()

        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.d("TransferService", "Creating notification channel")
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Transfer Notifications",
                NotificationManager.IMPORTANCE_HIGH // Ensures visibility
            ).apply {
                description = "Notifications for completed transfers."
            }

            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
            Log.d("TransferService", "Notification channel created")
        }
    }
}
