package org.openhdp.hdt.ui.history

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.openhdp.hdt.data.StopwatchRepository
import org.openhdp.hdt.data.entities.Stopwatch


class HistoryViewModel @ViewModelInject constructor(
    val stopwatchRepository: StopwatchRepository
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

            }
        }
    }
}

