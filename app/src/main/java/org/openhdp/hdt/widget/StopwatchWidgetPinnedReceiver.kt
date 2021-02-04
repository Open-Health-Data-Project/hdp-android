package org.openhdp.hdt.widget


import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import org.openhdp.hdt.widget.StopwatchWidgetHelper.EXTRA_VIEWSTATE
import org.openhdp.hdt.widget.StopwatchWidgetHelper.NO_ID
import timber.log.Timber

class StopwatchWidgetPinnedReceiver : BroadcastReceiver() {

    companion object {

        private const val BROADCAST_ID = 123456

        fun getPendingIntent(
            context: Context,
            partialState: StopwatchViewState
        ): PendingIntent {
            val callbackIntent = Intent(context, StopwatchWidgetPinnedReceiver::class.java)
            val bundle = Bundle()
            bundle.putSerializable(EXTRA_VIEWSTATE, partialState)

            callbackIntent.putExtras(bundle)
            return PendingIntent.getBroadcast(
                context, BROADCAST_ID, callbackIntent, PendingIntent.FLAG_UPDATE_CURRENT
            )
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        val partialState: StopwatchViewState? =
            intent.getSerializableExtra(EXTRA_VIEWSTATE) as? StopwatchViewState
        if (partialState == null) {
            Toast.makeText(context, "EMPTY WIDGET DATA", Toast.LENGTH_SHORT).show()
        } else {
            val widgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, NO_ID)
            if (widgetId == NO_ID) {
                Toast.makeText(context, "EMPTY WIDGET ID", Toast.LENGTH_SHORT).show()
            } else {
                Timber.d("widget id = $widgetId, partial state id = ${partialState.widgetId}")
                val widgetPreferences = WidgetPreferences(context)
                widgetPreferences.saveWidget(
                    partialState.stopwatchId,
                    partialState.copy(widgetId = widgetId)
                )
                StopwatchWidgetProvider.updateWidgets(context)
                Toast.makeText(context, "WIDGET UPDATED", Toast.LENGTH_SHORT).show()
            }
        }

    }
}