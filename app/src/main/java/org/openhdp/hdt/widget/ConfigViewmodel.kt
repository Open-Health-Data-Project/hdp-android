package org.openhdp.hdt.widget

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.openhdp.hdt.data.StopwatchRepository
import org.openhdp.hdt.data.entities.Category
import org.openhdp.hdt.data.entities.Stopwatch
import org.openhdp.hdt.data.entities.Timestamp


class ConfigViewmodel @ViewModelInject constructor(
    private val stopwatchRepository: StopwatchRepository,

    ) : ViewModel() {

    fun requestStopwatchesList(onLoaded: (List<Stopwatch>) -> Unit) {
        viewModelScope.launch(Dispatchers.Main) {
            runCatching {
                stopwatchRepository.stopwatches()
            }.onSuccess(onLoaded)
        }
    }

    fun requestCategoryWithTimestamp(
        stopwatch: Stopwatch,
        callback: (Category, Timestamp) -> Unit
    ) {
        viewModelScope.launch {
            stopwatchRepository.categories().firstOrNull {
                it.id == stopwatch.categoryId
            }?.let { category ->
                stopwatchRepository.lastTimestampOf(stopwatch.id)?.let { lastTimestamp ->
                    callback.invoke(category, lastTimestamp)
                }
            }
        }
    }
}

