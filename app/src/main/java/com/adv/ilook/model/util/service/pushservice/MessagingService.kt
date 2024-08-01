package com.adv.ilook.model.util.service.pushservice

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.*
import com.adv.ilook.R
import com.adv.ilook.model.util.assets.PrefImpl
import com.adv.ilook.model.util.assets.SharedPrefKey
import com.adv.ilook.view.base.BasicFunction.EndToEnd.encrypt
import com.adv.ilook.view.base.BasicFunction.EndToEnd.generateKeyPair
import com.adv.ilook.view.ui.activities.main.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.io.IOException
import java.net.URL
import javax.inject.Inject

private const val TAG = "==>>MessagingService"
class MessagingService: FirebaseMessagingService() {

    @Inject
    lateinit var sharedPreference: PrefImpl
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "Refreshed token: $token")
     // tokenEncrypt(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        message.notification?.let {
            Log.d(TAG, "Notification Title: ${it.title}")
            Log.d(TAG, "Notification Body: ${it.body}")
        }
        if (message.data.size > 0) {
            Log.d(TAG, "Message Data payload: " + message.data)
        }
        if (message.notification != null) {
            sendNotification(
                message.notification!!.body, message.notification!!.title, message.notification!!
                    .imageUrl
            )
        }
    }

    override fun onMessageSent(msgId: String) {
        super.onMessageSent(msgId)
    }

    override fun onDeletedMessages() {
        super.onDeletedMessages()
    }

    private fun sendNotification(messageBody:String?,title:String?,imgUrl: Uri?){
        val intent= Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this, 0 /* Request code */, intent,
            PendingIntent.FLAG_IMMUTABLE
        )
        var bmp: Bitmap? = null
        Log.d(TAG, "sendNotification: " + imgUrl.toString())
        try {
            val inStream = URL(imgUrl.toString()).openStream()
            bmp = BitmapFactory.decodeStream(inStream)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        val channelId = getString(R.string.default_notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder: Builder =
            Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
                .setStyle(BigPictureStyle().bigPicture(bmp))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build())
    }

    fun tokenEncrypt(token: String) {
           val keyPair = generateKeyPair()
            val publicKey = keyPair.public
            val encryptedToken = encrypt(token, publicKey!!)
        sharedPreference.put(
            SharedPrefKey.TOKEN_KEY,
            encryptedToken
        )

    }
}
