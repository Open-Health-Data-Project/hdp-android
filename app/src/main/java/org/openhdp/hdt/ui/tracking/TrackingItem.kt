package org.openhdp.hdt.ui.tracking

import androidx.annotation.ColorRes


data class TrackingItem(
    val id: String,
    val name: String,
    val description: String,
    val timestamp: Long,
    val state: TrackState,
    @ColorRes val color: Int
) {
    fun toggled(): TrackingItem {
        val newState = if (state == TrackState.ACTIVE) {
            TrackState.INACTIVE
        } else {
            TrackState.ACTIVE
        }
        return this.copy(state = newState)
    }

    fun isRunning(): Boolean {
        return state == TrackState.ACTIVE
    }
}

enum class TrackState {
    ACTIVE, INACTIVE
}
