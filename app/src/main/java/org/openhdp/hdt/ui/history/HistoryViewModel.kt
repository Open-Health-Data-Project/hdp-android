package org.openhdp.hdt.ui.history

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.openhdp.hdt.data.StopwatchRepository
import org.openhdp.hdt.data.entities.Stopwatch
import org.openhdp.hdt.data.entities.Timestamp
import org.openhdp.hdt.data.enums.PrivacyState
import org.openhdp.hdt.ui.base.BaseViewModel

class HistoryViewModel @ViewModelInject constructor(
    val stopwatchRepository: StopwatchRepository,
    private val provideHistoricEntriesUseCase: ProvideHistoricEntriesUseCase
) : BaseViewModel<HistoryViewState>(
    initialViewState = HistoryViewState.Loading
) {
    fun initialize(stopwatchId: String?) {
        viewModelScope.launch(Dispatchers.Main) {
            runCatching {
                val stopwatch = stopwatchRepository.findStopwatch(stopwatchId)
                if (stopwatch == null) {
                    renderAllStopwatches()
                    displayHistory()
                } else {
                    onStopwatchClick(stopwatch)
                }
            }.onFailure { throwable ->
                pushState<HistoryViewState> { HistoryViewState.Error(throwable) }
            }
        }
    }

    private fun renderAllStopwatches() {
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
        displayHistory(stopwatch)
    }

    private fun displayHistory(stopwatch: Stopwatch? = null) {
        viewModelScope.launch(Dispatchers.Main) {
            runCatching {
                provideHistoricEntriesUseCase.execute(stopwatch?.id)
            }.onSuccess { timestamps ->
                pushState<HistoryViewState> {
                    if (stopwatch != null) {
                        if (timestamps.isEmpty()) {
                            HistoryViewState.NoStopwatchTimestampsSoFar(stopwatch)
                        } else {
                            HistoryViewState.StopwatchesResult(stopwatch, timestamps)
                        }
                    } else {
                        if (timestamps.isEmpty()) {
                            HistoryViewState.NoStopwatchTimestampsSoFar(stopwatch)
                        } else {
                            HistoryViewState.HistoryResult(timestamps)
                        }
                    }
                }
            }.onFailure { throwable ->
                pushState<HistoryViewState> { HistoryViewState.Error(throwable) }
            }
        }
    }

    fun toggleExpand(header: DayHeader) {
        val mapper: (DayHeader) -> DayHeader = {
            if (it == header) {
                it.copy(isExpanded = !it.isExpanded)
            } else {
                it
            }
        }
        val currentState = viewState.value
        if (currentState is HistoryViewState.StopwatchesResult) {
            _viewState.value = currentState.copy(items = currentState.items.map(mapper))
        } else if (currentState is HistoryViewState.HistoryResult) {
            _viewState.value = currentState.copy(items = currentState.items.map(mapper))
        }
    }
}

