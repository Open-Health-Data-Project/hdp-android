package org.openhdp.hdt.widget

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import org.openhdp.hdt.R
import org.openhdp.hdt.data.entities.Stopwatch

@AndroidEntryPoint
class StopwatchWidgetConfigurationActivity : AppCompatActivity() {

    private var widgetId: Int = AppWidgetManager.INVALID_APPWIDGET_ID

    private val viewModel: ConfigViewmodel by viewModels()

    private var adapter: StopwatchesAdapter? = null

    private lateinit var preferences: WidgetPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stopwatch_widget_configuration)

        preferences = WidgetPreferences(applicationContext)

        val intent = intent
        val extras = intent.extras
        if (extras != null) {
            widgetId = extras.getInt(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID
            )
        }

        if (widgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            //something went wrong
            finish()
        } else {
            val recycler = findViewById<RecyclerView>(R.id.recyclerview)

            adapter = StopwatchesAdapter { save(it) }
            recycler.adapter = adapter
            viewModel.requestStopwatchesList { adapter?.submitList(it) }
        }

    }

    private fun save(stopwatch: Stopwatch) {
        preferences.setWidgetValues(widgetId, stopwatch.name)
        val appWidgetManager = AppWidgetManager.getInstance(this)


        viewModel.requestCategoryWithTimestamp(stopwatch) { category, timestamp ->
            StopwatchWidgetProvider.bindWidgetUI(
                this,
                appWidgetManager,
                stopwatch,
                timestamp,
                category,
                widgetId
            )
            finishWithResult()
        }


    }

    private fun finishWithResult() {
        val resultValue = Intent()
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
        setResult(Activity.RESULT_OK, resultValue)
        finish()
    }
}

val diff = object : DiffUtil.ItemCallback<Stopwatch>() {
    override fun areItemsTheSame(oldItem: Stopwatch, newItem: Stopwatch): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Stopwatch, newItem: Stopwatch): Boolean {
        return oldItem.id == newItem.id
    }

}

class StopwatchesAdapter(private val listener: (Stopwatch) -> Unit) :
    ListAdapter<Stopwatch, StopwatchViewHolder>(diff) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StopwatchViewHolder {
        return StopwatchViewHolder(
            org.openhdp.hdt.databinding.ItemTimeEntryBinding.inflate(
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

class StopwatchViewHolder(val binding: org.openhdp.hdt.databinding.ItemTimeEntryBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(stopwatch: Stopwatch) {
        binding.entryName.text = stopwatch.name
    }
}