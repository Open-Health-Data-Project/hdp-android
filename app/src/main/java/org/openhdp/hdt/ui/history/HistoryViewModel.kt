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

    private fun displayHistory(stopwatch: Stopwatch? = null, expandedItemIndex: Int = -1) {
        viewModelScope.launch(Dispatchers.Main) {
            runCatching {
                provideHistoricEntriesUseCase.execute(stopwatch?.id)
            }.onSuccess { timestamps ->
                pushState<HistoryViewState> {
                    if (stopwatch != null) {
                        if (timestamps.isEmpty()) {
                            HistoryViewState.NoStopwatchTimestampsSoFar(stopwatch)
                        } else {
                            HistoryViewState.StopwatchesResult(
                                stopwatch,
                                items = timestamps.mapIndexed { index, item ->
                                    item.copy(isExpanded = index == expandedItemIndex)
                                })
                        }
                    } else {
                        if (timestamps.isEmpty()) {
                            HistoryViewState.NoStopwatchTimestampsSoFar(stopwatch)
                        } else {
                            HistoryViewState.StopwatchesResult(items = timestamps.mapIndexed { index, item ->
                                item.copy(isExpanded = index == expandedItemIndex)
                            })
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
        pushState<HistoryViewState.StopwatchesResult> { currentState ->
            currentState.copy(items = currentState.items.map(mapper))
        }
    }

    fun editTimestamp(entry: TimestampEntry) {
        pushState<HistoryViewState.StopwatchesResult> { currentState ->
            currentState.copy(timestampToEdit = entry)
        }
    }

    fun onDelete(entry: TimestampEntry) {
        val currentState = _viewState.value
        if (currentState is HistoryViewState.StopwatchesResult) {
            val stopwatch = currentState.stopwatch
            val expandedItemIndex = currentState.items.indexOfFirst { it.isExpanded }
            val shouldExpandEntry = currentState.items[expandedItemIndex].entries.isNotEmpty()

            viewModelScope.launch {
                runCatching {
                    stopwatchRepository.deleteTimestamp(entry.timestamp)
                    displayHistory(
                        stopwatch = stopwatch,
                        expandedItemIndex = if (shouldExpandEntry) expandedItemIndex else -1
                    )
                }
            }
        }
    }

    fun onEditFinished() {
        pushState<HistoryViewState.StopwatchesResult> {
            it.copy(timestampToEdit = null)
        }
    }
}

