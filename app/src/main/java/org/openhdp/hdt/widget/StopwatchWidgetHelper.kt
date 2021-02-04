package org.openhdp.hdt.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import androidx.annotation.DrawableRes
import com.google.gson.Gson
import org.openhdp.hdt.R
import org.openhdp.hdt.data.entities.Category
import org.openhdp.hdt.data.entities.Stopwatch
import org.openhdp.hdt.data.entities.Timestamp
import org.openhdp.hdt.ui.MainActivity

object StopwatchWidgetHelper {

    const val EXTRA_VIEWSTATE = "EXTRA_VIEWSTATE"
    const val ACTION_TOGGLE = "ACTION_TOGGLE"
    const val NO_ID = AppWidgetManager.INVALID_APPWIDGET_ID

    fun createViewState(
        stopwatch: Stopwatch,
        category: Category,
        lastTimestamp: Timestamp,
        widgetId: Int = NO_ID
    ): StopwatchViewState {
        val viewState = StopwatchViewState(
            stopwatch.id,
            widgetId,
            stopwatch.name,
            isRunning = lastTimestamp.stopTime == null,
            isLoading = false,
            category.color,
            lastTimestamp.startTime
        )
        return viewState
    }

    fun bindWidgetUI(
        context: Context,
        appWidgetManager: AppWidgetManager,
        viewState: StopwatchViewState
    ) {
        val remoteViews = bindRemoteViews(context, viewState)
        appWidgetManager.updateAppWidget(viewState.widgetId, remoteViews)
    }

    fun bindRemoteViews(context: Context, state: StopwatchViewState): RemoteViews {
        val remoteViews = RemoteViews(context.packageName, R.layout.appwidget_stopwatch)

        remoteViews.setInt(R.id.appwidget_root, "setBackgroundColor", state.backgroundColor)
        remoteViews.setTextViewText(R.id.timer_name, state.name)

        val description = if (state.isRunning) {
            "Running"
        } else {
            "Paused"
        }
        remoteViews.setTextViewText(R.id.timer_time, description)
        remoteViews.setImageViewResource(R.id.play_or_pause, state.getButtonIcon())
        remoteViews.setInt(R.id.play_or_pause, "setBackgroundColor", state.backgroundColor)

        val browseStopwatchIntent = PendingIntent.getActivity(
            context, state.widgetId,
            MainActivity.refreshTracking(context),
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val clickPendingIntent = PendingIntent.getBroadcast(
            context, state.widgetId,
            Intent(context, StopwatchWidgetProvider::class.java).apply {
                action = ACTION_TOGGLE
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, state.widgetId)
                val newState = Gson().toJson(state.copy(isLoading = true))
                putExtra(EXTRA_VIEWSTATE, newState)
            },
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        if (!state.isLoading) {
            remoteViews.setOnClickPendingIntent(R.id.play_or_pause, clickPendingIntent)
            remoteViews.setOnClickPendingIntent(R.id.appwidget_root, browseStopwatchIntent)
        }
        return remoteViews
    }

    @DrawableRes
    private fun StopwatchViewState.getButtonIcon(): Int {
        return when {
            isLoading -> {
                R.drawable.ic_stopwatch_black_24
            }
            isRunning -> R.drawable.ic_pause
            else -> R.drawable.ic_play
        }
    }
}