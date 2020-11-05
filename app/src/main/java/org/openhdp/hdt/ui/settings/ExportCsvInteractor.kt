package org.openhdp.hdt.ui.settings

import android.app.Application
import android.net.Uri
import org.openhdp.hdt.ui.base.NotEmpty
import java.io.FileOutputStream
import javax.inject.Inject

class ExportCsvInteractor @Inject constructor(private val application: Application) {

    fun <T : Any> export(
        fileUri: Uri,
        @NotEmpty dataSet: List<T>,
        firstLine: String? = null,
        lineWriter: (T) -> String = { it.toString() }
    ) {
        val contentResolver = application.contentResolver

        val fileDescriptor = contentResolver.openFileDescriptor(fileUri, "w")?.fileDescriptor
        val writeStream = FileOutputStream(fileDescriptor)

        if (firstLine != null) {
            writeStream.writeLine(firstLine)
        }
        dataSet.forEach { item ->
            writeStream.writeLine(lineWriter.invoke(item))
        }
        writeStream.flush()
        writeStream.close()
    }

    private fun FileOutputStream.writeLine(line: String) {
        write("$line\n".toByteArray(Charsets.UTF_8))
    }
}