package id.ismail.pasienapps.lib

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.provider.Settings
import androidx.core.app.NotificationManagerCompat


object NotificationPermissionHandler {
    fun checkNotificationPermission(context: Context) {
        if (!isNotificationEnabled(context)) {
            showNotificationPermissionDialog(context)
        }
    }

    private fun isNotificationEnabled(context: Context): Boolean {
        return NotificationManagerCompat.from(context).areNotificationsEnabled()
    }

    private fun showNotificationPermissionDialog(context: Context) {
        AlertDialog.Builder(context)
            .setTitle("Enable Notifications")
            .setMessage("To receive important updates, enable notifications for this app.")
            .setPositiveButton(
                "Enable"
            ) { dialog: DialogInterface?, which: Int ->
                openAppSystemSettings(
                    context
                )
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun openAppSystemSettings(context: Context) {
        val intent = Intent()
        intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
        intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
        context.startActivity(intent)
    }
}

