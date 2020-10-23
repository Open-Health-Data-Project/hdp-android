package org.openhdp.hdt.ui.tracking

import androidx.annotation.ColorInt


data class TrackingItem(
    val stopwatchId: String,
    val name: String,
    val description: String,
    val millisTracked: Long,
    val buttonState: PlaybackButtonState,
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
