package org.openhdp.hdt.widget

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import org.openhdp.hdt.R
import org.openhdp.hdt.data.entities.Category
import org.openhdp.hdt.data.entities.Stopwatch
import org.openhdp.hdt.data.entities.Timestamp


@AndroidEntryPoint
class AppWidgetConfigActivity : AppCompatActivity() {

    private val viewModel: ConfigViewmodel by viewModels()

    private var adapter: StopwatchesAdapter? = null

    private val preferences: WidgetPreferences by lazy { WidgetPreferences(applicationContext) }

    private val errorCallback: (Throwable) -> Unit = {
        Toast.makeText(
            this,
            "Oops! ${it.message}",
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stopwatch_widget_configuration)
        setResult(RESULT_CANCELED, intent)

        configureNewAppWidget()
    }

    private fun configureNewAppWidget() {
        val noId = StopwatchWidgetHelper.NO_ID
        val widgetId = intent.extras?.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, noId) ?: noId
        val label = findViewById<TextView>(R.id.label)
        label.text = "configure widget no. $widgetId"
        if (widgetId == noId) {
            finish()
        } else {
            setupRecyclerWith {
                if (it.widgetAttached) {
                    showStopwatchAttached(it.stopwatch)
                } else {
                    configureAppWidget(it.stopwatch, widgetId)
                }
            }
        }
    }

    private fun showStopwatchAttached(stopwatch: Stopwatch) {
        Toast.makeText(
            this,
            "Stopwatch already attached. You can remove it from home screen",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun configureAppWidget(stopwatch: Stopwatch, widgetId: Int) {
        viewModel.requestCategoryWithTimestamp(
            stopwatch,
            errorCallback = errorCallback
        ) { category, timestamp ->
            val viewState =
                StopwatchWidgetHelper.createViewState(stopwatch, category, timestamp, widgetId)
            preferences.saveWidget(viewState.stopwatchId, viewState)
            finishWithResult(widgetId)
        }
    }

    private fun finishWithResult(widgetId: Int) {
        val resultValue = Intent().apply {
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
        }
        setResult(Activity.RESULT_OK, resultValue)
        finish()
    }


    // todo: check samsungs in the future
    @RequiresApi(Build.VERSION_CODES.O)
    private fun FragmentActivity.pinStopwatch(
        stopwatch: Stopwatch,
        category: Category,
        timestamp: Timestamp,
    ) {
        val appWidgetManager = AppWidgetManager.getInstance(applicationContext)
        if (appWidgetManager.isRequestPinAppWidgetSupported) {
            val provider = ComponentName(applicationContext, StopwatchWidgetProvider::class.java)

            val state = StopwatchWidgetHelper.createViewState(stopwatch, category, timestamp)

            val successCallback = Intent().let {
                val id = it.getIntExtra(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    StopwatchWidgetHelper.NO_ID
                )
                StopwatchWidgetPinnedReceiver.getPendingIntent(
                    this,
                    state.copy(widgetId = id)
                )
            }

            val remoteViews = StopwatchWidgetHelper.bindRemoteViews(this, state)

            appWidgetManager.requestPinAppWidget(
                provider,
                bundleOf(
                    AppWidgetManager.EXTRA_APPWIDGET_PREVIEW to remoteViews
                ),
                successCallback
            )
        } else {
            showPinAppWidgetFromHomeScreen()
        }

    }

    private fun setupRecyclerWith(listener: (SelectableStopwatch) -> Unit) {
        val recycler = findViewById<RecyclerView>(R.id.recyclerview)
        adapter = StopwatchesAdapter(listener)
        recycler.adapter = adapter
        viewModel.requestStopwatchesList { stopwatches ->
            adapter?.submitList(stopwatches.map {
                val hasStopwatch = preferences.hasStopwatch(it.id)
                SelectableStopwatch(it, hasStopwatch)
            })
        }
    }

    private fun showPinAppWidgetFromHomeScreen() {
        Toast.makeText(
            this,
            "Long press on home screen to add widget!",
            Toast.LENGTH_SHORT
        ).show()
    }
}

val diff = object : DiffUtil.ItemCallback<ItemType>() {
    override fun areItemsTheSame(oldItem: ItemType, newItem: ItemType): Boolean {
        if (oldItem.stopwatch.id != oldItem.stopwatch.id) return false
        return oldItem.widgetAttached == newItem.widgetAttached
    }

    override fun areContentsTheSame(oldItem: ItemType, newItem: ItemType): Boolean {
        if (oldItem.stopwatch.id != oldItem.stopwatch.id) return false
        return oldItem.widgetAttached == newItem.widgetAttached
    }
}

class StopwatchesAdapter(private val listener: (SelectableStopwatch) -> Unit) :
    ListAdapter<ItemType, StopwatchViewHolder>(diff) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StopwatchViewHolder {
        return StopwatchViewHolder(
            ItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: StopwatchViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
        holder.itemView.setOnClickListener {
            listener.invoke(item)
        }
    }
}
typealias ItemType = SelectableStopwatch
typealias ItemBinding = org.openhdp.hdt.databinding.ItemAppWidgetEntryBinding

data class SelectableStopwatch(val stopwatch: Stopwatch, val widgetAttached: Boolean)

class StopwatchViewHolder(val binding: ItemBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(item: SelectableStopwatch) {
        binding.entryName.text = item.stopwatch.name
        if (item.widgetAttached) {
            binding.icon.setImageResource(R.drawable.ic_check)
        } else {
            binding.icon.setImageResource(R.drawable.ic_plus)
        }
    }
}