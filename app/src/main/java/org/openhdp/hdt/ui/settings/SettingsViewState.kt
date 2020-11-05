package org.openhdp.hdt.ui.settings

import org.openhdp.hdt.data.entities.Stopwatch
import org.openhdp.hdt.data.entities.Timestamp
import org.openhdp.hdt.ui.base.MightBeEmpty
import org.openhdp.hdt.ui.base.NotEmpty

sealed class SettingsViewState {

    data class DisplayStopwatchPicker(@NotEmpty val stopwatches: List<Stopwatch>) :
        SettingsViewState()

    data class ExportStopwatchesData(
        @NotEmpty val stopwatches: List<Stopwatch>
    ) : SettingsViewState()

    data class ExportStopwatchTimestamps(
        val stopwatch: Stopwatch,
        @NotEmpty val timestamps: List<Timestamp>
    ) : SettingsViewState()

    data class Error(val issue: Throwable) : SettingsViewState()
}