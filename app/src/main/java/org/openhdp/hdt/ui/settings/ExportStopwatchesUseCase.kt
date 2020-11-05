package org.openhdp.hdt.ui.settings

import android.net.Uri
import org.openhdp.hdt.data.entities.Stopwatch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class ExportStopwatchesUseCase @Inject constructor(
    private val interactor: ExportCsvInteractor
) {

    fun export(fileUri: Uri, stopwatches: List<Stopwatch>) {
        val lineWriter: (Stopwatch) -> String = { stopwatch ->
            "${stopwatch.id},${stopwatch.categoryId}"
        }
        interactor.export(fileUri, stopwatches, "stopwatchId,categoryName", lineWriter)
    }
}