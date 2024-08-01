package com.adv.ilook.model.util.extension

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.BigPictureStyle
import androidx.core.app.NotificationCompat.Builder
import com.adv.ilook.R
import com.adv.ilook.view.ui.activities.main.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService.NOTIFICATION_SERVICE
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.URL

/**
 * Extension function to simplify setting an afterTextChanged action to EditText components.
 */


private const val TAG = "==>>AdvExtension"
fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}

val scope = CoroutineScope(Job() + Dispatchers.IO)
suspend fun Context.customNotification(
    messageBody: String?,
    title: String?,
    imgUrl: Uri? = null,
    bMap: Bitmap? = null,
    isNetworkConnected: Boolean
) {
    val intent = Intent(this, MainActivity::class.java)
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
    val pendingIntent = PendingIntent.getActivity(
        this, 0 /* Request code */, intent,
        PendingIntent.FLAG_IMMUTABLE
    )
    var bmp: Bitmap? = null
    Log.d(TAG, "sendNotification: " + imgUrl.toString())

    try {
        if (isNetworkConnected)
            if (imgUrl != null) {
                coroutineScope {
                    val inStream = scope.launchOnIO {
                        bmp = BitmapFactory.decodeStream(URL(imgUrl.toString()).openStream())
                    }

                }
            }
    } catch (e: IOException) {

        Toast.makeText(this, "Network Error", Toast.LENGTH_SHORT).show()
    }
    if (bMap != null)
        bmp = bMap

     val channelId = getString(R.string.default_notification_channel_id)
    val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
    val notificationBuilder: Builder =
        Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setGroupSummary(false)
            .setGroup("com.adv.ilook.updates")
            .setContentIntent(pendingIntent)
            .setStyle(BigPictureStyle().bigPicture(bmp))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
    val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager


    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            channelId,
            "version release....",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager.createNotificationChannel(channel)
    }
    val uniqueNotificationId = System.currentTimeMillis().toInt()
    notificationManager.notify(uniqueNotificationId /* ID of notification */, notificationBuilder.build())

/*    // Create the group summary notification
    val summaryNotificationBuilder: NotificationCompat.Builder =
        NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("You have updates")
            .setContentText("Check your updates.")
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setGroupSummary(true)
            .setGroup("com.adv.ilook.updates")
            .setPriority(NotificationCompat.PRIORITY_HIGH)

    // Issue the group summary notification
    notificationManager.notify(0, summaryNotificationBuilder.build())*/


}

fun CoroutineScope.launchOnMain(block: suspend () -> Unit) {
    launch(Dispatchers.Main) {
        block()
    }
}

fun CoroutineScope.launchOnIO(block: suspend () -> Unit) {
    launch(Dispatchers.IO) {
        block()
    }
}
