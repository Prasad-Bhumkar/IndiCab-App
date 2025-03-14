package com.example.indicab.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.indicab.models.*
import com.example.indicab.ui.MainActivity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MockNotificationService @Inject constructor(
    private val context: Context
) : NotificationService {

    private val notificationManager = NotificationManagerCompat.from(context)

    init {
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channels = listOf(
                    NotificationChannel(
                        CHANNEL_EMERGENCY,
                        "Emergency Alerts",
                        NotificationManager.IMPORTANCE_HIGH
                    ).apply {
                        enableVibration(true)
                        enableLights(true)
                        setBypassDnd(true)
                    },
                    NotificationChannel(
                        CHANNEL_SAFETY,
                        "Safety Checks",
                        NotificationManager.IMPORTANCE_HIGH
                    ).apply {
                        enableVibration(true)
                        enableLights(true)
                    },
                    NotificationChannel(
                        CHANNEL_LOCATION,
                        "Location Sharing",
                        NotificationManager.IMPORTANCE_DEFAULT
                    )
                )

                notificationManager.createNotificationChannels(channels)
            }
        } catch (e: Exception) {
            Log.e("MockNotificationService", "Failed to create notification channels", e)
        }
    }

    override fun showEmergencyNotification(alert: SOSAlert) {
        try {
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                putExtra(EXTRA_ALERT_ID, alert.id)
            }

            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val notification = NotificationCompat.Builder(context, CHANNEL_EMERGENCY)
                .setSmallIcon(android.R.drawable.ic_dialog_alert)
                .setContentTitle("Emergency Alert")
                .setContentText(createEmergencyMessage(alert))
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setAutoCancel(true)
                .setOngoing(true)
                .setContentIntent(pendingIntent)
                .addAction(
                    android.R.drawable.ic_menu_call,
                    "Call Emergency",
                    createEmergencyCallIntent()
                )
                .addAction(
                    android.R.drawable.ic_menu_share,
                    "Share Location",
                    createShareLocationIntent(alert)
                )
                .build()

            notificationManager.notify(NOTIFICATION_ID_EMERGENCY, notification)
        } catch (e: Exception) {
            Log.e("MockNotificationService", "Failed to show emergency notification", e)
            // Optionally, implement a retry mechanism or fallback behavior here
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_EMERGENCY)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("Emergency Alert")
            .setContentText(createEmergencyMessage(alert))
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)
            .setOngoing(true)
            .setContentIntent(pendingIntent)
            .addAction(
                android.R.drawable.ic_menu_call,
                "Call Emergency",
                createEmergencyCallIntent()
            )
            .addAction(
                android.R.drawable.ic_menu_share,
                "Share Location",
                createShareLocationIntent(alert)
            )
            .build()

        notificationManager.notify(NOTIFICATION_ID_EMERGENCY, notification)
    }

    override fun showSafetyCheckNotification(check: SafetyCheck) {
        val respondIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra(EXTRA_CHECK_ID, check.id)
        }

        // TODO: Implement logic to process and handle user responses to safety check notifications
        // This could involve setting up a listener or callback mechanism

        val respondPendingIntent = PendingIntent.getActivity(
            context,
            1,
            respondIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_SAFETY)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Safety Check")
            .setContentText("Please confirm your safety")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setAutoCancel(true)
            .setTimeoutAfter(300000) // 5 minutes
            .setContentIntent(respondPendingIntent)
            .addAction(
                android.R.drawable.ic_menu_compass,
                "I'm Safe",
                createSafetyResponseIntent(check.id, true)
            )
            .addAction(
                android.R.drawable.ic_dialog_alert,
                "Need Help",
                createSafetyResponseIntent(check.id, false)
            )
            .build()

        notificationManager.notify(NOTIFICATION_ID_SAFETY + check.hashCode(), notification)
    }

    fun showLocationSharingNotification(share: LocationShare) {
        try {
            val notification = NotificationCompat.Builder(context, CHANNEL_LOCATION)
                .setSmallIcon(android.R.drawable.ic_menu_mylocation)
                .setContentTitle("Location Sharing Active")
                .setContentText("Your location is being shared")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .setOngoing(true)
                .addAction(
                    android.R.drawable.ic_menu_close_clear_cancel,
                    "Stop Sharing",
                    createStopSharingIntent(share.id)
                )
                .build()

            notificationManager.notify(NOTIFICATION_ID_LOCATION + share.hashCode(), notification)
        } catch (e: Exception) {
            Log.e("MockNotificationService", "Failed to show location sharing notification", e)
            // Consider fallback behavior here
        }
    }

    fun cancelEmergencyNotification() {
        notificationManager.cancel(NOTIFICATION_ID_EMERGENCY)
    }

    fun cancelSafetyCheckNotification(checkId: String) {
        notificationManager.cancel(NOTIFICATION_ID_SAFETY + checkId.hashCode())
    }

    fun cancelLocationSharingNotification(shareId: String) {
        notificationManager.cancel(NOTIFICATION_ID_LOCATION + shareId.hashCode())
    }

    private fun createEmergencyMessage(alert: SOSAlert): String {
        // Localization of emergency messages
        return when (alert.type) {
            SOSType.MEDICAL -> context.getString(R.string.emergency_medical)
            SOSType.SECURITY -> context.getString(R.string.emergency_security)
            SOSType.ACCIDENT -> context.getString(R.string.emergency_accident)
            SOSType.BREAKDOWN -> context.getString(R.string.emergency_breakdown)
            SOSType.OTHER -> context.getString(R.string.emergency_other)
        }
    }

    private fun createEmergencyCallIntent(): PendingIntent {
        try {
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = android.net.Uri.parse("tel:112") // Emergency number
            }
            return PendingIntent.getActivity(
                context,
                2,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        } catch (e: Exception) {
            Log.e("MockNotificationService", "Failed to create emergency call intent", e)
            // Return a fallback intent or handle the error as needed
            return PendingIntent.getActivity(
                context,
                2,
                Intent(context, MainActivity::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }
    }

    private fun createShareLocationIntent(alert: SOSAlert): PendingIntent {
        // Validate necessary location data
        if (alert.location.latitude == 0.0 && alert.location.longitude == 0.0) {
            Log.e("MockNotificationService", "Invalid location data for sharing")
            return PendingIntent.getActivity(
                context,
                3,
                Intent(context, MainActivity::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, "Emergency location: " +
                "https://maps.google.com/?q=${alert.location.latitude},${alert.location.longitude}")
        }
        return PendingIntent.getActivity(
            context,
            3,
            Intent.createChooser(intent, "Share Location"),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun createSafetyResponseIntent(checkId: String, isSafe: Boolean): PendingIntent {
        try {
            val intent = Intent(context, MainActivity::class.java).apply {
                action = if (isSafe) ACTION_SAFETY_RESPONSE_SAFE else ACTION_SAFETY_RESPONSE_HELP
                putExtra(EXTRA_CHECK_ID, checkId)
            }
            return PendingIntent.getActivity(
                context,
                if (isSafe) 4 else 5,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        } catch (e: Exception) {
            Log.e("MockNotificationService", "Failed to create safety response intent", e)
            // Return a fallback intent or handle the error as needed
            return PendingIntent.getActivity(
                context,
                if (isSafe) 4 else 5,
                Intent(context, MainActivity::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }
    }

    private fun createStopSharingIntent(shareId: String): PendingIntent {
        try {
            val intent = Intent(context, MainActivity::class.java).apply {
                action = ACTION_STOP_LOCATION_SHARING
                putExtra(EXTRA_SHARE_ID, shareId)
            }
            return PendingIntent.getActivity(
                context,
                6,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        } catch (e: Exception) {
            Log.e("MockNotificationService", "Failed to create stop sharing intent", e)
            // Return a fallback intent or handle the error as needed
            return PendingIntent.getActivity(
                context,
                6,
                Intent(context, MainActivity::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }
    }

    companion object {
        private const val CHANNEL_EMERGENCY = "emergency_channel"
        private const val CHANNEL_SAFETY = "safety_channel"
        private const val CHANNEL_LOCATION = "location_channel"

        private const val NOTIFICATION_ID_EMERGENCY = 1001
        private const val NOTIFICATION_ID_SAFETY = 2001
        private const val NOTIFICATION_ID_LOCATION = 3001

        const val EXTRA_ALERT_ID = "extra_alert_id"
        const val EXTRA_CHECK_ID = "extra_check_id"
        const val EXTRA_SHARE_ID = "extra_share_id"

        const val ACTION_SAFETY_RESPONSE_SAFE = "action_safety_response_safe"
        const val ACTION_SAFETY_RESPONSE_HELP = "action_safety_response_help"
        const val ACTION_STOP_LOCATION_SHARING = "action_stop_location_sharing"
    }
}
