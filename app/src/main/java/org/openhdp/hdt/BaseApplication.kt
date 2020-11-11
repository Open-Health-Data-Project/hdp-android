package org.openhdp.hdt

import android.app.Application
import android.content.Context
import android.widget.Toast
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class BaseApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }
}

fun Context.showText(message: String, duration: Int = Toast.LENGTH_SHORT): Toast {
    val toast = Toast.makeText(applicationContext, message, duration)
    toast.show()
    return toast
}