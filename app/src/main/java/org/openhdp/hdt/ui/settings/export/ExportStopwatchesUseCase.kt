package org.openhdp.hdt.ui.settings.export

import android.app.Application
import android.net.Uri
import androidx.core.content.FileProvider
import org.openhdp.hdt.data.entities.Stopwatch
import java.io.File
import javax.inject.Inject

class ExportStopwatchesUseCase @Inject constructor(
    private val application: Application,
    private val interactor: ExportCsvInteractor
) {

    fun export(stopwatches: List<Stopwatch>): Uri {

        val file = File.createTempFile("stopwatches", ".csv", application.cacheDir)
        val uri = FileProvider.getUriForFile(
            application,
            application.packageName.toString() + ".provider",
            file
        )
        val lineWriter: (Stopwatch) -> String = { stopwatch ->
            "${stopwatch.id},${stopwatch.name},${stopwatch.categoryId}"
        }
        interactor.export(
            uri,
            stopwatches,
            "stopwatchId,stopwatchName,categoryName",
            lineWriter
        )
        return uri
    }
}