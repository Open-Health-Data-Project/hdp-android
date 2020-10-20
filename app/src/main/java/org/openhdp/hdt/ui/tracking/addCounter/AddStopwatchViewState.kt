package org.openhdp.hdt.ui.tracking.addCounter

import org.openhdp.hdt.R
import org.openhdp.hdt.ui.tracking.TrackState
import org.openhdp.hdt.ui.tracking.TrackingItem
import java.util.*

data class AddStopwatchViewState(
    val name: String = "",
    val isLoading: Boolean = false,
    val categories: List<SelectableCategory> = emptyList(),
    val addButtonEnabled: Boolean = false,
    val added: Boolean = false,
    val cancelled: Boolean = false
) {
    fun asTrackingItem(): TrackingItem {
        return TrackingItem(
            id = UUID.randomUUID().toString(),
            name,
            "",
            0,
            TrackState.INACTIVE,
            R.color.colorBlue
        )
    }
}