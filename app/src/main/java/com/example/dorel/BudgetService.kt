package com.example.dorel

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import android.util.Log

class BudgetService : Service() {

    companion object {
        const val CHANNEL_ID = "BudgetServiceChannel"
    }

    override fun onCreate() {
        super.onCreate()
        Log.d("BudgetService", "Service created")
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("BudgetService", "Service started")

        // Retrieve data from intent
        val remainingFood = intent?.getDoubleExtra("FOOD_REMAINING", 0.0) ?: 0.0
        val remainingFinance = intent?.getDoubleExtra("FINANCE_REMAINING", 0.0) ?: 0.0
        val remainingMedia = intent?.getDoubleExtra("MEDIA_REMAINING", 0.0) ?: 0.0

        // Combine remaining budgets into one notification
        val notificationContent = """
            Food: $${"%.2f".format(remainingFood)}
            Finance: $${"%.2f".format(remainingFinance)}
            Media: $${"%.2f".format(remainingMedia)}
        """.trimIndent()

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Budget Plan Updated")
            .setContentText("You have updated your budget plan.")
            .setStyle(NotificationCompat.BigTextStyle().bigText(notificationContent)) // Expandable content
            .setSmallIcon(R.drawable.ic_budget) // Replace with your actual icon
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        startForeground(1, notification)
        Log.d("BudgetService", "Notification displayed")

        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.d("BudgetService", "Creating notification channel")
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Budget Notifications",
                NotificationManager.IMPORTANCE_HIGH // Ensures visibility
            ).apply {
                description = "Notifications for budget updates and remaining balances."
            }

            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
            Log.d("BudgetService", "Notification channel created")
        }
    }
}
