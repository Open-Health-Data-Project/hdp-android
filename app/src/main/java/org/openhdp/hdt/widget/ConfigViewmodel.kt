package org.openhdp.hdt.widget

import android.widget.Toast
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.openhdp.hdt.data.StopwatchRepository
import org.openhdp.hdt.data.entities.Category
import org.openhdp.hdt.data.entities.Stopwatch
import org.openhdp.hdt.data.entities.Timestamp
import timber.log.Timber
import java.lang.Exception


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
        errorCallback: (Throwable) -> Unit = {},
        callback: (Category, Timestamp) -> Unit,
    ) {
        viewModelScope.launch(Dispatchers.Main) {
            runCatching {
                val category = stopwatchRepository.categories().firstOrNull {
                    it.id == stopwatch.categoryId
                }
                if (category != null) {
                    val lastTimestamp = stopwatchRepository.lastTimestampOf(stopwatch.id)
                    if (lastTimestamp != null) {
                        callback.invoke(category, lastTimestamp)
                    } else {
                        errorCallback.invoke(Exception("null timestamp"))
                    }
                } else {
                    errorCallback.invoke(Exception("null category"))
                }
            }.onFailure(errorCallback)
        }
    }
}

