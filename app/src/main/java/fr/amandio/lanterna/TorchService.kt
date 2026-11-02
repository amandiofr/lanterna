package fr.amandio.lanterna

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.os.Handler
import android.os.IBinder
import android.os.Looper

/** Holds the torch on while running; stopping the service switches it off. */
class TorchService : Service() {

    companion object {
        @Volatile var isOn = false
        private const val CHANNEL_ID = "torch"
        private const val NOTIF_ID = 1
        private const val ACTION_STOP = "fr.amandio.lanterna.STOP"
        // Switches itself off if forgotten, to save the battery
        private const val AUTO_OFF_MS = 30 * 60 * 1000L
    }

    private val handler = Handler(Looper.getMainLooper())
    private val autoOff = Runnable { stopSelf() }

    private lateinit var cameraManager: CameraManager
    private var cameraId: String? = null

    // Stops the service if the torch is switched off elsewhere (quick settings, camera app...)
    private val torchCallback = object : CameraManager.TorchCallback() {
        override fun onTorchModeChanged(id: String, enabled: Boolean) {
            if (id == cameraId && !enabled && isOn) stopSelf()
        }
    }

    override fun onCreate() {
        super.onCreate()
        cameraManager = getSystemService(CameraManager::class.java)
        cameraId = cameraManager.cameraIdList.firstOrNull {
            cameraManager.getCameraCharacteristics(it).get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP) {
            stopSelf()
            return START_NOT_STICKY
        }
        startForeground(NOTIF_ID, buildNotification())
        val id = cameraId ?: run { stopSelf(); return START_NOT_STICKY }
        try {
            cameraManager.setTorchMode(id, true)
            isOn = true
            cameraManager.registerTorchCallback(torchCallback, handler)
            handler.removeCallbacks(autoOff)
            handler.postDelayed(autoOff, AUTO_OFF_MS)
        } catch (e: Exception) {
            stopSelf()
        }
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        handler.removeCallbacks(autoOff)
        cameraManager.unregisterTorchCallback(torchCallback)
        if (isOn) {
            isOn = false
            cameraId?.let { runCatching { cameraManager.setTorchMode(it, false) } }
        }
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun buildNotification(): Notification {
        val nm = getSystemService(NotificationManager::class.java)
        nm.createNotificationChannel(
            NotificationChannel(CHANNEL_ID, getString(R.string.channel_name), NotificationManager.IMPORTANCE_LOW)
        )
        val stop = PendingIntent.getService(
            this, 0, Intent(this, TorchService::class.java).setAction(ACTION_STOP),
            PendingIntent.FLAG_IMMUTABLE
        )
        return Notification.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_menu_view)
            .setContentTitle(getString(R.string.notif_title))
            .setContentText(getString(R.string.notif_text))
            .setContentIntent(stop)
            .setOngoing(true)
            .build()
    }
}
