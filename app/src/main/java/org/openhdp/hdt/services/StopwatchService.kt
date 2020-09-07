package org.openhdp.hdt.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_LOW
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import org.openhdp.hdt.R
import org.openhdp.hdt.other.Constants.ACTION_START_SERVICE
import org.openhdp.hdt.other.Constants.ACTION_STOP_SERVICE
import org.openhdp.hdt.other.Constants.NOTIFICATION_CHANNEL_ID
import org.openhdp.hdt.other.Constants.NOTIFICATION_CHANNEL_NAME
import org.openhdp.hdt.other.Constants.STOPWATCH_ID
import timber.log.Timber

class StopwatchService : LifecycleService() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when(it.action){
                ACTION_START_SERVICE -> {
                    Timber.d("Started service ${it.getIntExtra(STOPWATCH_ID,0)}")
                }
                ACTION_STOP_SERVICE -> {
                    Timber.d("Stop service")
                }

            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun startForegroundService(stopwatchName: String) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }

        val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setAutoCancel(false)
            .setOngoing(true)
            .setSmallIcon(R.drawable.ic_stopwatch_black_24)
            .setContentTitle("Health Data Tracker")
            .setContentText("$stopwatchName - 00:00:00")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager){
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(channel)
    }

}