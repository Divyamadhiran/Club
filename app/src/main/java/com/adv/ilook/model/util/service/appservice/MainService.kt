package com.adv.ilook.model.util.service.appservice

import android.app.Activity
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ServiceInfo
import android.hardware.display.DisplayManager
import android.hardware.usb.UsbManager
import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaFormat
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Binder
import android.os.Build
import android.os.Handler
import android.os.HandlerThread
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.adv.ilook.R
import com.adv.ilook.model.util.receiver.UsbReceiver

import com.adv.ilook.view.ui.activities.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext
private const val TAG = "==>>MainService"
@AndroidEntryPoint
class MainService() : Service(), CoroutineScope {
    private var mediaProjection: MediaProjection?= null
    private var isServiceRunning = false
    private lateinit var notificationManager: NotificationManager


    companion object {
        const val CHANNEL_ID = "HearSightServiceChannel"
        const val NOTIFICATION_ID = 1
    }

    private lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private val binder = LocalBinder()
    inner class LocalBinder : Binder() {
        fun getService(): MainService = this@MainService
    }
    override fun onBind(intent: Intent): IBinder {
        return binder
    }
    override fun onCreate() {
        super.onCreate()
        job = Job()
        notificationManager = getSystemService(NotificationManager::class.java)


    }



    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let { incomingIntent ->
            when (incomingIntent.action) {
                MainServiceActions.INIT_SERVICE.name -> handleStartService(incomingIntent)
                MainServiceActions.STOP_SERVICE.name -> handleStopService(incomingIntent)
                MainServiceActions.HANDLE_PROJECTION.name -> handleMediaProjection(incomingIntent)

                else -> Unit
            }
        }
        return START_STICKY
    }

    private fun handleMediaProjection(incomingIntent: Intent) {
        val resultCode = incomingIntent.getIntExtra("resultCode", Activity.RESULT_CANCELED)
        val data: Intent? = incomingIntent.getParcelableExtra("data")
        if (resultCode == Activity.RESULT_OK && data != null) {
         //   startBackgroundTask(resultCode, data)
        }
    }

    private fun handleStopService(incomingIntent: Intent) {
        isServiceRunning = false
        job.cancel()
        stopSelf()
    }
/*
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }*/

    private fun handleStartService(incomingIntent: Intent) {
        if (!isServiceRunning) {
            isServiceRunning = true

            launch {
                withContext(Dispatchers.Main) {
                    startServiceWithNotification()
                }
            }
        } else {
            Log.d(TAG, "handleStartService: Service is already running")
        }
    }

    private fun startServiceWithNotification() {
        Log.d(TAG, "startServiceWithNotification: Service running state - $isServiceRunning")

        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent =
            PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Foreground Service")
            .setContentText("Service is running in the foreground")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(pendingIntent)
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {

                 startForeground(
                     NOTIFICATION_ID,
                     notification,
                     ServiceInfo.FOREGROUND_SERVICE_TYPE_CAMERA or ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION
                 )
              //   startForeground(NOTIFICATION_ID, notification)
               // ContextCompat.startForegroundService(this, notificationIntent)
            } else {
                startForeground(
                    NOTIFICATION_ID,
                    notification,
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION or ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION
                )
            }

        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
    }

    private fun startBackgroundTask(resultCode: Int, data: Intent) {
        launch {
            withContext(Dispatchers.IO) {
                Log.d(TAG, "Setting up media projection")
                val mediaProjectionManager =
                    getSystemService(MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
                mediaProjection = mediaProjectionManager.getMediaProjection(resultCode, data)

                val width = 1280
                val height = 720
                val dpi = resources.displayMetrics.densityDpi

                val mediaCodec = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_VIDEO_AVC)
                val mediaFormat =
                    MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, width, height)
                mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, 6000000)
                mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, 30)
                mediaFormat.setInteger(
                    MediaFormat.KEY_COLOR_FORMAT,
                    MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface
                )
                mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1)

                mediaCodec.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
                val inputSurface = mediaCodec.createInputSurface()
                mediaCodec.start()


                val virtualDisplay = mediaProjection?.createVirtualDisplay(
                    "ScreenCapture",
                    width,
                    height,
                    dpi,
                    DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                    inputSurface,
                    null,
                    null
                )

                val handlerThread = HandlerThread("CodecCallbackThread")
                handlerThread.start()
                val handler = Handler(handlerThread.looper)
                mediaCodec.setCallback(object : MediaCodec.Callback() {
                    override fun onInputBufferAvailable(codec: MediaCodec, index: Int) {
                        // No input buffers are available for an encoder
                    }

                    override fun onOutputBufferAvailable(
                        codec: MediaCodec,
                        index: Int,
                        info: MediaCodec.BufferInfo
                    ) {
                        val encodedData = codec.getOutputBuffer(index)
                        if (encodedData != null) {
                            // Process the encoded data, e.g., send it over the network
                            // Here we just log the data size
                            Log.d(TAG, "Encoded data size: ${info.size}")
                        }
                        codec.releaseOutputBuffer(index, false)
                    }

                    override fun onError(codec: MediaCodec, e: MediaCodec.CodecException) {
                        Log.e(TAG, "MediaCodec error: ${e.message}")
                    }

                    override fun onOutputFormatChanged(codec: MediaCodec, format: MediaFormat) {
                        Log.d(TAG, "Output format changed: $format")
                    }
                }, handler)
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
       // job.cancel()

        Log.d(TAG, "onDestroy: Service destroyed")
    }
}