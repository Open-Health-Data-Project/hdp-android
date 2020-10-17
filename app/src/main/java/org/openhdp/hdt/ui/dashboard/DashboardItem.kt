package org.openhdp.hdt.ui.dashboard


data class DashboardItem(
    val id: String,
    val name: String,
    val description: String,
    val timestamp: Long,
    val state: DashboardItemState
) {
    fun toggled(): DashboardItem {
        val newState = if (state == DashboardItemState.ACTIVE) {
            DashboardItemState.INACTIVE
        } else {
            DashboardItemState.ACTIVE
        }
        return this.copy(state = newState)
    }

    fun isRunning(): Boolean {
        return state == DashboardItemState.ACTIVE
    }
}

enum class DashboardItemState {
    ACTIVE, INACTIVE
}
