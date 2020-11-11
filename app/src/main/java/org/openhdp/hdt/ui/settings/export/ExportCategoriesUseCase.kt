package org.openhdp.hdt.ui.settings.export

import android.app.Application
import android.net.Uri
import androidx.core.content.FileProvider
import org.openhdp.hdt.data.entities.Category
import java.io.File
import javax.inject.Inject


class ExportCategoriesUseCase @Inject constructor(
    private val application: Application,
    private val interactor: ExportCsvInteractor
) {

    fun export(categories: List<Category>): Uri {
        val file = File.createTempFile("categories", ".csv", interactor.application.cacheDir)
        val uri = FileProvider.getUriForFile(
            application,
            application.packageName.toString() + ".provider",
            file
        )
        val lineWriter: (Category) -> String = { category ->
            "${category.id},${category.name},${category.color}"
        }
        interactor.export(
            uri,
            categories,
            "categoryId,categoryName,categoryColor",
            lineWriter
        )
        return uri
    }
}