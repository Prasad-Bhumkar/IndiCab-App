 package com.example.indicab.services
 
 import android.app.NotificationChannel
 import android.app.NotificationManager
 import android.content.Context
 import android.os.Build
 import androidx.core.app.NotificationCompat
 import androidx.core.app.NotificationManagerCompat
 import com.google.firebase.messaging.FirebaseMessaging
 import com.google.firebase.messaging.FirebaseMessagingService
 import com.google.firebase.messaging.RemoteMessage
 import dagger.hilt.android.AndroidEntryPoint
 import kotlinx.coroutines.CoroutineScope
 import kotlinx.coroutines.Dispatchers
 import kotlinx.coroutines.launch
 import javax.inject.Inject
 
 @AndroidEntryPoint
 class NotificationService : FirebaseMessagingService() {
 
     @Inject
     lateinit var monitoringService: MonitoringService
 
     companion object {
         private const val CHANNEL_ID = "indiCab_notifications"
         private const val CHANNEL_NAME = "IndiCab Notifications"
         private const val NOTIFICATION_ID = 1001
 
         fun createNotificationChannel(context: Context) {
             if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                 val channel = NotificationChannel(
                     CHANNEL_ID,
                     CHANNEL_NAME,
                     NotificationManager.IMPORTANCE_HIGH
                 ).apply {
                     description = "IndiCab notifications"
                 }
 
                 val notificationManager = context.getSystemService(
                     NotificationManager::class.java
                 )
                 notificationManager.createNotificationChannel(channel)
             }
         }
     }
 
     override fun onNewToken(token: String) {
         super.onNewToken(token)
         CoroutineScope(Dispatchers.IO).launch {
             monitoringService.trackEvent("fcm_token_refreshed")
            //  Send token to server
         }
     }
 
     override fun onMessageReceived(message: RemoteMessage) {
         super.onMessageReceived(message)
         
         message.notification?.let { notification ->
             showNotification(
                 title = notification.title ?: "IndiCab",
                 message = notification.body ?: "New notification"
             )
         }
 
         // Track notification received event
         monitoringService.trackEvent(
             "notification_received",
             mapOf(
                 "message_id" to message.messageId,
                 "from" to message.from,
                 "data" to message.data.toString()
             )
         )
     }
 
     private fun showNotification(title: String, message: String) {
         val notification = NotificationCompat.Builder(this, CHANNEL_ID)
             .setContentTitle(title)
             .setContentText(message)
             .setSmallIcon(android.R.drawable.ic_dialog_info)
             .setPriority(NotificationCompat.PRIORITY_HIGH)
             .build()
 
         NotificationManagerCompat.from(this).notify(NOTIFICATION_ID, notification)
     }
 
     fun subscribeToTopic(topic: String) {
         FirebaseMessaging.getInstance().subscribeToTopic(topic)
             .addOnCompleteListener { task ->
                 if (task.isSuccessful) {
                     monitoringService.trackEvent("notification_topic_subscribed", mapOf("topic" to topic))
                 } else {
                     monitoringService.logError(
                         Exception("Failed to subscribe to topic $topic"),
                         "Failed to subscribe to topic"
                     )
                 }
             }
     }
 
     fun unsubscribeFromTopic(topic: String) {
         FirebaseMessaging.getInstance().unsubscribeFromTopic(topic)
             .addOnCompleteListener { task ->
                 if (task.isSuccessful) {
                     monitoringService.trackEvent("notification_topic_unsubscribed", mapOf("topic" to topic))
                 } else {
                     monitoringService.logError(
                         Exception("Failed to unsubscribe from topic $topic"),
                         "Failed to unsubscribe from topic"
                     )
                 }
             }
     }
 }
