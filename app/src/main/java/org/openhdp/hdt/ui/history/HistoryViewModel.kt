package org.openhdp.hdt.ui.history

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.openhdp.hdt.data.StopwatchRepository
import org.openhdp.hdt.data.entities.Stopwatch
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
        viewModelScope.launch(Dispatchers.Main) {
            runCatching {
                provideHistoricEntriesUseCase.execute(stopwatch.id)
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

    fun toggleExpand(header: DayHeader) {
        pushState<HistoryViewState.StopwatchesResult> { result ->
            result.copy(
                items = result.items.map {
                    if (it is DayHeader && it == header) {
                        it.copy(isExpanded = !it.isExpanded)
                    } else {
                        it
                    }
                }
            )
        }
    }
}

