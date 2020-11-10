package org.openhdp.hdt.ui.settings.export

import android.app.Application
import android.net.Uri
import androidx.core.content.FileProvider
import org.openhdp.hdt.data.entities.Timestamp
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class ExportTimestampsUseCase @Inject constructor(
    private val application: Application,
    private val exportCsvInteractor: ExportCsvInteractor
) {

    private val CSV_DATE_FORMAT = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.ENGLISH)

    fun export(stopWatchId: String, data: List<Timestamp>): Uri {
        val file = File.createTempFile("${stopWatchId}_timestamps", ".csv", application.cacheDir)
        val uri = FileProvider.getUriForFile(
            application,
            application.packageName.toString() + ".provider",
            file
        )
        val firstLine = "stopwatchId,startTime,stopTime"
        val lineWriter: (Timestamp) -> String = { timestamp ->
            val startTime = timestamp.startTime
            val stopTime = timestamp.stopTime
            val start = CSV_DATE_FORMAT.format(Date(startTime))
            val line = if (stopTime != null) {
                val end = CSV_DATE_FORMAT.format(Date(stopTime))
                "$stopWatchId,$start,$end"
            } else {
                "$stopWatchId,$start"
            }
            line
        }
        exportCsvInteractor.export(uri, data, firstLine, lineWriter)
        return uri
    }
}