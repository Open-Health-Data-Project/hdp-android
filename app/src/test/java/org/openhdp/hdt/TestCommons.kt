package org.openhdp.hdt

import org.openhdp.hdt.data.entities.Stopwatch
import org.openhdp.hdt.data.enums.PrivacyState

object TestCommons {

    fun testStopwatch(order: Int, name: String, categoryId: String): Stopwatch {
        return Stopwatch(
            customOrder = order, name = name, categoryId = categoryId,
            PrivacyState.PRIVATE, sharedName = null, extraDataType = null
        )
    }
}