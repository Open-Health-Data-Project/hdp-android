package org.openhdp.hdt.ui.settings

import android.app.Activity
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import org.openhdp.hdt.ui.base.MightBeEmpty
import org.openhdp.hdt.ui.base.NotEmpty

class DialogHelper constructor(private val activity: Activity) {

    fun <T : Any> showMultiChoice(
        @MightBeEmpty title: String,
        @NotEmpty options: List<T>,
        onPicked: (T) -> Unit,
        renderer: (T) -> String = { it.toString() }
    ) {
        val builderSingle = AlertDialog.Builder(activity)
        //builderSingle.setIcon(R.drawable.ic_launcher)
        if (title.isNotEmpty()) {
            builderSingle.setTitle(title)
        }

        val arrayAdapter =
            ArrayAdapter<String>(activity, android.R.layout.select_dialog_singlechoice)
        arrayAdapter.addAll(options.map(renderer))

        builderSingle.setNegativeButton("cancel") { dialog, which -> dialog.dismiss() }
        builderSingle.setAdapter(arrayAdapter) { dialog, which ->
            onPicked.invoke(options[which])
            dialog.dismiss()
        }
        builderSingle.show()
    }
}