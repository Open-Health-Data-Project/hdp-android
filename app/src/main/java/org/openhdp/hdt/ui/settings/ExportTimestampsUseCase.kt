package org.openhdp.hdt.ui.settings

import android.net.Uri
import org.openhdp.hdt.data.entities.Timestamp
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class ExportTimestampsUseCase @Inject constructor(private val exportCsvInteractor: ExportCsvInteractor) {

    private val CSV_DATE_FORMAT = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.ENGLISH)

    fun export(fileUri: Uri, stopWatchId: String, data: List<Timestamp>) {
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
        exportCsvInteractor.export(fileUri, data, firstLine, lineWriter)
    }
}