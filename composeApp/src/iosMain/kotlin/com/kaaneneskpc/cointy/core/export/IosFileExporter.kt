package com.kaaneneskpc.cointy.core.export

import com.kaaneneskpc.cointy.export.domain.model.ExportFormat
import com.kaaneneskpc.cointy.export.domain.model.ExportResult
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSString
import platform.Foundation.NSURL
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.NSUserDomainMask
import platform.Foundation.create
import platform.Foundation.writeToURL

class IosFileExporter : FileExporter {
    @OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
    override suspend fun exportFile(
        content: String,
        fileName: String,
        format: ExportFormat
    ): ExportResult {
        return try {
            val fileManager = NSFileManager.defaultManager
            val documentsUrl = fileManager.URLsForDirectory(
                NSDocumentDirectory,
                NSUserDomainMask
            ).firstOrNull() as? NSURL
                ?: return ExportResult.Error("Could not access Documents directory")
            val fileUrl = documentsUrl.URLByAppendingPathComponent(fileName)
                ?: return ExportResult.Error("Could not create file URL")
            val nsString = NSString.create(string = content)
            val success = nsString.writeToURL(
                fileUrl,
                atomically = true,
                encoding = NSUTF8StringEncoding,
                error = null
            )
            if (success) {
                ExportResult.Success(
                    filePath = fileUrl.path ?: "",
                    fileName = fileName
                )
            } else {
                ExportResult.Error("Failed to write file")
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
}
