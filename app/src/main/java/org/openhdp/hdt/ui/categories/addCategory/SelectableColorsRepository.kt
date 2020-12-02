package org.openhdp.hdt.ui.categories.addCategory

import android.app.Activity
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import org.openhdp.hdt.R
import javax.inject.Inject

class SelectableColorsRepository @Inject constructor(private val activity: Activity) {

    fun provideColors(): List<SelectableColor> {
        return arrayListOf(
            SelectableColor(R.color.colorYellow.toColorInt()),
            SelectableColor(R.color.colorOrange.toColorInt()),
            SelectableColor(R.color.colorPink.toColorInt()),
            SelectableColor(R.color.colorPurple.toColorInt()),
            SelectableColor(R.color.colorBlue.toColorInt()),
            SelectableColor(R.color.colorAccent.toColorInt())
        )
    }

    @ColorInt
    private fun Int.toColorInt(): Int {
        return ContextCompat.getColor(activity, this)
    }
}