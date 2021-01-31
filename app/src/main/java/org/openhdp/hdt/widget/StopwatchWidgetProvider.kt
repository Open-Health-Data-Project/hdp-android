package org.openhdp.hdt.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.RemoteViews
import androidx.annotation.ColorInt
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import org.openhdp.hdt.R
import org.openhdp.hdt.data.entities.Category
import org.openhdp.hdt.data.entities.Stopwatch
import org.openhdp.hdt.data.entities.Timestamp
import timber.log.Timber
import java.io.Serializable
import java.util.*
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class StopwatchWidgetProvider : AppWidgetProvider() {

    companion object {

        private val KLAZZ = StopwatchWidgetProvider::class.java
        private const val REQUEST_CODE_TOGGLE = 233
        private const val EXTRA_VIEWSTATE = "EXTRA_VIEWSTATE"

        fun updateWidgets(context: Context) {
            val intent = Intent(context, KLAZZ)
            intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            val widgetIDs =
                AppWidgetManager.getInstance(context).getAppWidgetIds(ComponentName(context, KLAZZ))
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, widgetIDs)
            context.sendBroadcast(intent)
        }

        fun getRemoteViews(context: Context, widgetName: String): RemoteViews {
            val remoteViews = RemoteViews(context.packageName, R.layout.appwidget_stopwatch)
            remoteViews.setTextViewText(R.id.timer_name, widgetName)
            return remoteViews
        }

        fun bindRemoteViews(context: Context, state: StopwatchViewState): RemoteViews {
            val remoteViews = RemoteViews(context.packageName, R.layout.appwidget_stopwatch)

            remoteViews.setInt(R.id.appwidget_root, "setBackgroundColor", state.backgroundColor)

            remoteViews.setTextViewText(R.id.timer_name, state.name)

            val totalSeconds = TimeUnit.MILLISECONDS.toSeconds(Date().time - state.millis)
            val minutes = totalSeconds / 60
            val hours = totalSeconds / 3600
            remoteViews.setTextViewText(
                R.id.timer_time,
                String.format(
                    "%02d:%02d:%02d",
                    hours.rem(24),
                    minutes.rem(60),
                    totalSeconds.rem(60)
                )
            )
            val src = if (state.isRunning) R.drawable.ic_pause else R.drawable.ic_play
            remoteViews.setImageViewResource(R.id.play_or_pause, src)
            val clickPendingIntent = PendingIntent.getBroadcast(
                context,
                REQUEST_CODE_TOGGLE,
                Intent(context, KLAZZ).apply {
                    action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                    val widgetIDs = AppWidgetManager.getInstance(context)
                        .getAppWidgetIds(ComponentName(context, KLAZZ))
                    putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, widgetIDs)
                    val newState = Gson().toJson(state.copy(isRunning = !state.isRunning))
                    putExtra(EXTRA_VIEWSTATE, newState)
                },
                0
            )
            remoteViews.setOnClickPendingIntent(R.id.play_or_pause, clickPendingIntent)
            return remoteViews
        }

        fun updateWidgetUI(
            context: Context,
            appWidgetManager: AppWidgetManager,
            widgetPreferences: WidgetPreferences,
            widgetId: Int
        ) {
            val widgetName = widgetPreferences.getWidgetName(widgetId)
            if (widgetName != null) {
                appWidgetManager.updateAppWidget(widgetId, getRemoteViews(context, widgetName))
            }
        }

        fun bindWidgetUI(
            context: Context,
            appWidgetManager: AppWidgetManager,
            stopwatch: Stopwatch,
            lastTimestamp: Timestamp,
            category: Category,
            widgetId: Int
        ) {
            val viewState = StopwatchViewState(
                stopwatch.id,
                widgetId,
                stopwatch.name,
                isRunning = lastTimestamp.stopTime == null,
                category.color,
                lastTimestamp.startTime
            )

            val remoteViews = bindRemoteViews(context, viewState)
            appWidgetManager.updateAppWidget(widgetId, remoteViews)
        }
    }

    private var viewState: StopwatchViewState? = null

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
        Timber.d("onReceive ${intent?.getStringExtra(EXTRA_VIEWSTATE)}")
        try {
            viewState = Gson().fromJson(
                intent?.getStringExtra(EXTRA_VIEWSTATE),
                StopwatchViewState::class.java
            )
        } catch (t: Throwable) {
            Timber.e("failed to fetch viewstate...")
        }
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        Timber.d("onUpdate ${appWidgetIds.joinToString { it.toString() }}")
        if (viewState != null) {

        } else {
            val widgetPreferences = WidgetPreferences(context)
            for (widgetId: Int in appWidgetIds) {
                updateWidgetUI(context, appWidgetManager, widgetPreferences, widgetId)
            }
        }

    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        super.onDeleted(context, appWidgetIds)
        Timber.d("onUpdate ${appWidgetIds.joinToString { it.toString() }}")
        val widgetPreferences = WidgetPreferences(context)
        for (appWidgetId: Int in appWidgetIds) {
            widgetPreferences.removeWidget(appWidgetId)
        }
    }

    override fun onAppWidgetOptionsChanged(
        context: Context?,
        appWidgetManager: AppWidgetManager?,
        appWidgetId: Int,
        newOptions: Bundle?
    ) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions)
        Timber.d("onAppWidgetOptionsChanged")
    }

    override fun onEnabled(context: Context?) {
        super.onEnabled(context)
        Timber.d("onEnabled")
    }

    override fun onDisabled(context: Context?) {
        super.onDisabled(context)
        Timber.d("onDisabled")
    }

    override fun onRestored(context: Context?, oldWidgetIds: IntArray?, newWidgetIds: IntArray?) {
        super.onRestored(context, oldWidgetIds, newWidgetIds)
        Timber.d("onRestored")
    }
}

data class StopwatchViewState(
    val stopwatchId: String,
    val widgetId: Int,
    val name: String,
    val isRunning: Boolean,
    @ColorInt val backgroundColor: Int,
    val millis: Long
) : Serializable
