package org.openhdp.hdt.ui.settings

import android.net.Uri
import org.openhdp.hdt.data.entities.Stopwatch

sealed class SettingsEvent {

    object RequestExportStopwatches : SettingsEvent()

    object RequestStopwatchPicker : SettingsEvent()

    data class RequestExportStopwatchTimestamps(val stopwatch: Stopwatch) : SettingsEvent()

    data class ExportStopwatchTimestamps(val uri: Uri) : SettingsEvent()

    data class ExportStopwatches(val uri: Uri) : SettingsEvent()

    data class ChangeStartOfDay(val hourOfDay: Int, val minute: Int) : SettingsEvent()
}
