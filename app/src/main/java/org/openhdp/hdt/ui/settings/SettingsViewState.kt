package org.openhdp.hdt.ui.settings

import android.net.Uri
import org.openhdp.hdt.data.entities.Stopwatch
import org.openhdp.hdt.ui.base.NotEmpty

sealed class SettingsViewState {

    data class DisplayExportPicker(
        val stopwatches: List<Stopwatch>
    ) : SettingsViewState()

    data class Display(val hours: Int, val minutes: Int) : SettingsViewState()

    data class Error(val issue: Throwable) : SettingsViewState()

    data class Share(val uri: Uri) : SettingsViewState()
}