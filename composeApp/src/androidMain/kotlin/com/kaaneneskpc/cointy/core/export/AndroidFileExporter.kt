package com.kaaneneskpc.cointy.core.export

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import com.kaaneneskpc.cointy.export.domain.model.ExportFormat
import com.kaaneneskpc.cointy.export.domain.model.ExportResult
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.io.File
import java.io.FileOutputStream

class AndroidFileExporter(
    private val context: Context
) : FileExporter {
    override suspend fun exportFile(
        content: String,
        fileName: String,
        format: ExportFormat
    ): ExportResult {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                exportWithMediaStore(content, fileName, format)
            } else {
                exportToExternalStorage(content, fileName)
            }
        } catch (e: Exception) {
            ExportResult.Error(e.message ?: "Unknown error occurred while exporting")
        }
    }
    override fun generateFileName(prefix: String, format: ExportFormat): String {
        val now = Clock.System.now()
        val localDateTime = now.toLocalDateTime(TimeZone.currentSystemDefault())
        val timestamp = "${localDateTime.year}${localDateTime.monthNumber.toString().padStart(2, '0')}${localDateTime.dayOfMonth.toString().padStart(2, '0')}_" +
                "${localDateTime.hour.toString().padStart(2, '0')}${localDateTime.minute.toString().padStart(2, '0')}${localDateTime.second.toString().padStart(2, '0')}"
        return "${prefix}_$timestamp.${format.extension}"
    }
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun exportWithMediaStore(
        content: String,
        fileName: String,
        format: ExportFormat
    ): ExportResult {
        val contentValues = ContentValues().apply {
            put(MediaStore.Downloads.DISPLAY_NAME, fileName)
            put(MediaStore.Downloads.MIME_TYPE, format.mimeType)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }
        }
        val resolver = context.contentResolver
        val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
            ?: return ExportResult.Error("Failed to create file in Downloads folder")
        resolver.openOutputStream(uri)?.use { outputStream ->
            outputStream.write(content.toByteArray())
        } ?: return ExportResult.Error("Failed to write to file")
        return ExportResult.Success(
            filePath = uri.toString(),
            fileName = fileName
        )
    }
    @Suppress("DEPRECATION")
    private fun exportToExternalStorage(content: String, fileName: String): ExportResult {
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        if (!downloadsDir.exists()) {
            downloadsDir.mkdirs()
        }
        val file = File(downloadsDir, fileName)
        FileOutputStream(file).use { outputStream ->
            outputStream.write(content.toByteArray())
        }
        return ExportResult.Success(
            filePath = file.absolutePath,
            fileName = fileName
        )
    }
}

