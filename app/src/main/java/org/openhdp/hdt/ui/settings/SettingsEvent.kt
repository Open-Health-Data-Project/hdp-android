package org.openhdp.hdt.ui.settings

import org.openhdp.hdt.data.entities.Stopwatch

sealed class SettingsEvent {

    object RequestGenericExportDialog: SettingsEvent()

    object ExportStopwatches : SettingsEvent()

    object ExportCategories : SettingsEvent()

    data class ExportTimestamps(val stopwatch: Stopwatch) : SettingsEvent()

    data class ChangeStartOfDay(val hourOfDay: Int, val minute: Int) : SettingsEvent()
}
