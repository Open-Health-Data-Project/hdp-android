package org.openhdp.hdt.ui.history

import android.app.Application
import android.net.Uri
import android.os.ParcelFileDescriptor
import org.openhdp.hdt.data.entities.Timestamp
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class ExportToCsvInteractor @Inject constructor(private val application: Application) {

    private val CSV_DATE_FORMAT = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.ENGLISH)

    fun export(fileUri: Uri, stopWatchName: String, data: List<Timestamp>) {
        val contentResolver = application.contentResolver

        val pfd: ParcelFileDescriptor = contentResolver.openFileDescriptor(fileUri, "w")!!
        //create write stream
        val writeStream = FileOutputStream(pfd.fileDescriptor)

        writeStream.writeLine("stopwatchName,startTime,stopTime")
        data.forEach { timestamp ->
            val startTime = timestamp.startTime
            val stopTime = timestamp.stopTime
            val start = CSV_DATE_FORMAT.format(Date(startTime))
            if (stopTime != null) {
                val end = CSV_DATE_FORMAT.format(Date(stopTime))
                writeStream.writeLine("$stopWatchName,$start,$end")
            } else {
                writeStream.writeLine("$stopWatchName,$start")
            }
        }

        writeStream.flush()
        writeStream.close()
    }

    private fun FileOutputStream.writeLine(line: String) {
        write("$line\n".toByteArray(Charsets.UTF_8))
    }
}