package org.openhdp.hdt.ui.history

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.openhdp.hdt.data.StopwatchRepository
import org.openhdp.hdt.data.entities.Stopwatch
import org.openhdp.hdt.ui.base.BaseViewModel

class HistoryViewModel @ViewModelInject constructor(
    val stopwatchRepository: StopwatchRepository
) : BaseViewModel<HistoryViewState>(
    initialViewState = HistoryViewState.Loading
) {

    fun initialize() {
        viewModelScope.launch(Dispatchers.Main) {
            runCatching {
                stopwatchRepository
                    .stopwatches()
            }.onSuccess { stopwatches ->
                pushState<HistoryViewState> {
                    if (stopwatches.isEmpty()) {
                        HistoryViewState.NoStopwatchesSoFar
                    } else {
                        HistoryViewState.Stopwatches(stopwatches)
                    }
                }
            }.onFailure { throwable ->
                pushState<HistoryViewState> { HistoryViewState.Error(throwable) }
            }
        }
    }

    fun onStopwatchClick(stopwatch: Stopwatch) {
        viewModelScope.launch(Dispatchers.Main) {
            runCatching {
                stopwatchRepository
                    .timestamps(stopwatch.id)
            }.onSuccess { timestamps ->
                pushState<HistoryViewState> {
                    if (timestamps.isEmpty()) {
                        HistoryViewState.NoStopwatchTimestampsSoFar(stopwatch)
                    } else {
                        HistoryViewState.StopwatchesResult(stopwatch, timestamps)
                    }
                }

            }.onFailure { throwable ->
                pushState<HistoryViewState> { HistoryViewState.Error(throwable) }
            }
        }
    }
}

