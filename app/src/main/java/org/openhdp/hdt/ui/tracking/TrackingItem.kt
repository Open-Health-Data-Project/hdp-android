package org.openhdp.hdt.ui.tracking

import androidx.annotation.ColorInt
import org.openhdp.hdt.ui.settings.StartOfDay


data class TrackingItem(
    val stopwatchId: String,
    val name: String,
    val categoryName: String,
    val millisTracked: Long,
    val buttonState: PlaybackButtonState,
    val startOfDay: StartOfDay,
    @ColorInt val color: Int
) {
    fun toggled(isEnabled: Boolean = true): TrackingItem {
        val newState = if (buttonState.trackState == TrackState.ACTIVE) {
            TrackState.INACTIVE
        } else {
            TrackState.ACTIVE
        }
        return this.copy(
            buttonState = PlaybackButtonState(
                trackState = newState,
                isEnabled = isEnabled
            )
        )
    }

    fun isRunning(): Boolean {
        return buttonState.trackState == TrackState.ACTIVE
    }
}


data class PlaybackButtonState(val trackState: TrackState, val isEnabled: Boolean = true)

enum class TrackState {
    ACTIVE, INACTIVE
}
