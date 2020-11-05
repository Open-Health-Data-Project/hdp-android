package org.openhdp.hdt.ui.history

import org.openhdp.hdt.data.entities.Stopwatch
import org.openhdp.hdt.data.entities.Timestamp

sealed class HistoryViewState {

    object Loading : HistoryViewState()

    object NoStopwatchesSoFar : HistoryViewState()

    data class Error(val throwable: Throwable) : HistoryViewState()

    data class NoStopwatchTimestampsSoFar(val stopwatch: Stopwatch) : HistoryViewState()

    data class Stopwatches(val stopwatches: List<Stopwatch>) : HistoryViewState()

    data class StopwatchesResult(
        val stopwatch: Stopwatch,
        val timestamps: List<Timestamp>
    ) : HistoryViewState()

}