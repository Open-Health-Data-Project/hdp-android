package org.openhdp.hdt.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.annotation.ColorInt
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.openhdp.hdt.data.StopwatchRepository
import org.openhdp.hdt.data.entities.Timestamp
import org.openhdp.hdt.widget.StopwatchWidgetHelper.ACTION_TOGGLE
import org.openhdp.hdt.widget.StopwatchWidgetHelper.EXTRA_VIEWSTATE
import org.openhdp.hdt.widget.StopwatchWidgetHelper.bindRemoteViews
import org.openhdp.hdt.widget.StopwatchWidgetHelper.bindWidgetUI
import timber.log.Timber
import java.io.Serializable
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class StopwatchWidgetProvider : AppWidgetProvider() {

    companion object {
        val Context.appWidgetManager: AppWidgetManager
            get() = AppWidgetManager.getInstance(this)

        fun updateWidgets(context: Context) {
            val intent = Intent(context, StopwatchWidgetProvider::class.java)
            intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            val widgetIDs = context.appWidgetManager
                .getAppWidgetIds(ComponentName(context, StopwatchWidgetProvider::class.java))
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, widgetIDs)
            context.sendBroadcast(intent)
        }
    }

    @Inject
    lateinit var stopwatchRepository: StopwatchRepository

    override fun onReceive(context: Context, intent: Intent) {
        if (ACTION_TOGGLE == intent.action) {
            val widgetId = intent.getIntExtra(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                StopwatchWidgetHelper.NO_ID
            )
            if (widgetId != StopwatchWidgetHelper.NO_ID) {
                runCatching {
                    val viewState: StopwatchViewState = Gson().fromJson(
                        intent.getStringExtra(EXTRA_VIEWSTATE),
                        StopwatchViewState::class.java
                    )
                    context.pushNewState(viewState.copy(isLoading = true, widgetId = widgetId))
                    toggleStopwatch(viewState) { newState ->
                        context.pushNewState(newState)
                    }

                }.onFailure {
                    Timber.e("onReceive $it")
                }
            }
        }
        super.onReceive(context, intent)
    }

    private fun Context.pushNewState(newState: StopwatchViewState) {
        val remoteViews = bindRemoteViews(this, newState)
        WidgetPreferences(this).saveWidget(newState.stopwatchId, newState)
        appWidgetManager.updateAppWidget(newState.widgetId, remoteViews)
    }

    private fun toggleStopwatch(
        viewState: StopwatchViewState,
        callback: (viewState: StopwatchViewState) -> Unit
    ) {
        GlobalScope.launch(Dispatchers.IO) {
            runCatching {
                val stopwatchId = viewState.stopwatchId
                val now = Date().time
                if (viewState.isRunning) {
                    val lastTimestamp = stopwatchRepository.lastTimestampOf(stopwatchId)
                    if (lastTimestamp != null) {
                        stopwatchRepository.updateTimestamp(lastTimestamp.id, now)
                    }
                    callback.invoke(
                        viewState.copy(
                            isRunning = false,
                            isLoading = false,
                            millis = now
                        )
                    )
                } else {
                    // create new timestamp
                    stopwatchRepository.createTimestamp(
                        Timestamp(
                            stopwatchId = stopwatchId,
                            startTime = now
                        )
                    )
                    callback.invoke(
                        viewState.copy(
                            isRunning = true,
                            isLoading = false,
                            millis = now
                        )
                    )
                }
            }.onFailure {
                Timber.d("on failure $it")
            }
        }
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        Timber.d("onUpdate ${appWidgetIds.joinToString { it.toString() }}")
        val widgetPreferences = WidgetPreferences(context)
        for (widgetId: Int in appWidgetIds) {
            widgetPreferences.getWidgetViewState(widgetId)?.let { viewState ->
                bindWidgetUI(context, appWidgetManager, viewState)
            }
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds)

    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        super.onDeleted(context, appWidgetIds)
        Timber.d("onDeleted ${appWidgetIds.joinToString { it.toString() }}")
        val widgetPreferences = WidgetPreferences(context)
        for (appWidgetId: Int in appWidgetIds) {
            widgetPreferences.removeWidget(appWidgetId)
        }
    }

    override fun onAppWidgetOptionsChanged(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        newOptions: Bundle?
    ) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions)
        Timber.d("onAppWidgetOptionsChanged")
        WidgetPreferences(context).getWidgetViewState(appWidgetId)?.let { viewState ->
            bindWidgetUI(context, appWidgetManager, viewState)
        }
    }

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        Timber.d("onEnabled")
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        Timber.d("onDisabled")
    }

    override fun onRestored(context: Context, oldWidgetIds: IntArray, newWidgetIds: IntArray) {
        super.onRestored(context, oldWidgetIds, newWidgetIds)
        Timber.d("onRestored")
    }
}


data class StopwatchViewState(
    val stopwatchId: String,
    val widgetId: Int,
    val name: String,
    val isRunning: Boolean,
    val isLoading: Boolean = false,
    @ColorInt val backgroundColor: Int,
    val millis: Long
) : Serializable
