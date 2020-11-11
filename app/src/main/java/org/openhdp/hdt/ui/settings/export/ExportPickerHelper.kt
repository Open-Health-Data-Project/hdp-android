package org.openhdp.hdt.ui.settings.export

import android.app.Activity
import android.app.Dialog
import android.view.View
import android.view.Window
import android.widget.PopupMenu
import org.openhdp.hdt.R
import org.openhdp.hdt.data.entities.Stopwatch
import javax.inject.Inject


class ExportPickerHelper @Inject constructor(private val activity: Activity) {
    private var popupMenu: PopupMenu? = null

    fun displayExportPicker(
        stopwatches: List<Stopwatch>,
        onStopwatches: () -> Unit,
        onCategories: () -> Unit,
        onStopwatchClick: (stopwatch: Stopwatch) -> Unit,
        onAllTimestamps: () -> Unit
    ) {
        val dialog = Dialog(activity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_export_data)
        dialog.setCancelable(true)

        val stopwatchesButton = dialog.findViewById<View>(R.id.stopwatches)

        stopwatchesButton.setOnClickListener {
            onStopwatches()
            dialog.dismiss()
        }
        val categoriesButton = dialog.findViewById<View>(R.id.categories)

        categoriesButton.setOnClickListener {
            onCategories()
            dialog.dismiss()
        }
        val timestampsButton = dialog.findViewById<View>(R.id.timestamps)

        timestampsButton.setOnClickListener { anchorView ->
            onTimestampsButtonClick(anchorView, stopwatches) { stopwatch ->
                onStopwatchClick(stopwatch)
                dialog.dismiss()
            }
        }

        val allTimestampsButton = dialog.findViewById<View>(R.id.all_timestamps)

        allTimestampsButton.setOnClickListener {
            onAllTimestamps()
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun onTimestampsButtonClick(
        button: View,
        stopwatches: List<Stopwatch>,
        onClick: (Stopwatch) -> Unit
    ) {
        popupMenu?.dismiss()
        val menu = PopupMenu(button.context, button)
        stopwatches.forEach { stopwatch ->
            menu.menu.add(stopwatch.name)
        }
        menu.setOnMenuItemClickListener { item ->
            val stopwatch = stopwatches.firstOrNull { it.name == item?.title }

            if (stopwatch != null) {
                onClick(stopwatch)
            }
            true
        }
        popupMenu = menu
        popupMenu?.show()
    }
}