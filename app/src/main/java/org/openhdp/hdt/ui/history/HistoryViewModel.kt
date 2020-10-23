package org.openhdp.hdt.ui.history

import android.net.Uri
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.openhdp.hdt.data.StopwatchRepository
import org.openhdp.hdt.data.entities.Stopwatch
import org.openhdp.hdt.data.entities.Timestamp
import timber.log.Timber


class HistoryViewModel @ViewModelInject constructor(
    val stopwatchRepository: StopwatchRepository,
    private val exportToCsvInteractor: ExportToCsvInteractor
) : ViewModel() {

    val viewState = MutableLiveData<HistoryViewState>()

    fun initialize() {
        viewModelScope.launch(Dispatchers.Main) {
            runCatching {
                stopwatchRepository
                    .stopwatchDAO
                    .getAllStopwatchesInOrder()
            }
                .onSuccess {
                    if (it.isEmpty()) {
                        viewState.value = HistoryViewState.NoStopwatchesSoFar
                    } else {
                        viewState.value = HistoryViewState.Stopwatches(it)
                    }
                }
                .onFailure {
                    viewState.value = HistoryViewState.Error(it)
                }
        }
    }

    fun onStopwatchClick(stopwatch: Stopwatch) {
        viewModelScope.launch(Dispatchers.Main) {
            runCatching {
                stopwatchRepository
                    .timestampDAO
                    .getTimestampsFrom(stopwatch.id)
            }.onSuccess {
                if (it.isEmpty()) {
                    viewState.value = HistoryViewState.NoStopwatchesTimestampsSoFar(stopwatch)
                } else {
                    viewState.value = HistoryViewState.StopwatchesResult(stopwatch, it)
                }
            }.onFailure {
                viewState.value = HistoryViewState.Error(it)
            }
        }
    }

    fun doExport(fileUri: Uri): String? {
        (viewState.value as? HistoryViewState.StopwatchesResult)?.let { result ->

            val name = result.stopwatch.name
            exportToCsvInteractor.export(fileUri, name, result.timestamps)

            return name
        } ?: run {
            Timber.e("export failed")

        }
        return null
    }

}

