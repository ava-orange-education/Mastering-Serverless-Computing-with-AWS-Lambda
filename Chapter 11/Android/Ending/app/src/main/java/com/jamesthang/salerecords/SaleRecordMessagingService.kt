package com.jamesthang.salerecords

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase. messaging.FirebaseMessagingService
import com.google. firebase. messaging. RemoteMessage

class SaleRecordMessagingService : FirebaseMessagingService() {

    companion object {
        private const val TAG = "SaleRecordMessagingService"
        private const val CHANNEL_ID = "SaleRecordMessagingService_CHANNEL_ID"
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "SaleRecord Channel"
            val descriptionText = "Channel for SaleRecord notifications"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")
        // If necessary, send the token to our server
        sendRegistrationToServer(token)
    }

    private fun sendRegistrationToServer (token: String) {
        // Implement this method to send token to our app server
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "From: ${remoteMessage.from}")
        // Handle FCM messages here
        remoteMessage.notification?.let {
            Log.d(TAG, "Message Notification Body: ${it.body}")
            it.body?.let { body ->
                sendNotification(body)
            }
        }
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Message data payload: ${remoteMessage.data}")
        }
    }

    private fun sendNotification(messageBody: String) {
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.firebase) // Replace with your app's icon
            .setContentTitle("SaleRecord Notification")
            .setContentText(messageBody)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        with(NotificationManagerCompat.from(this)) {
            if (ActivityCompat.checkSelfPermission(
                    this@SaleRecordMessagingService,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                notify(0, builder.build())
            }
        }
    }

}