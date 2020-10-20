package org.openhdp.hdt.ui.categories.addCategory

import androidx.annotation.ColorRes

data class SelectableColor(@ColorRes val color: Int, val selected: Boolean = false)