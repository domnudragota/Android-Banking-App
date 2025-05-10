package com.example.dorel

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat

class VisualizeSpendingService : Service() {

    companion object {
        const val CHANNEL_ID = "VisualizeSpendingServiceChannel"
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Create an intent to open the VisualizeSpendingActivity
        val activityIntent = Intent(this, VisualizeSpendingActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            activityIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE // Ensure compatibility
        )

        // Build the notification
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Generating Spending Chart")
            .setContentText("Your spending data is being processed...")
            .setSmallIcon(R.drawable.ic_pie_chart)
            .setContentIntent(pendingIntent) // Add the PendingIntent here
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true) // Remove the notification after it's tapped
            .build()

        // Start the foreground service
        startForeground(1, notification)

        // Simulate processing (e.g., generating chart)
        Thread {
            try {
                Thread.sleep(10000) // Simulated delay
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            stopSelf() // Stop the service after processing
        }.start()

        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Visualize Spending Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for spending visualization"
            }

            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }
}
