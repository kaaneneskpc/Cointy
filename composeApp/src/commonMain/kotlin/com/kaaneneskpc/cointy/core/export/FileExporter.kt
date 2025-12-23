package com.kaaneneskpc.cointy.core.export

import com.kaaneneskpc.cointy.export.domain.model.ExportFormat
import com.kaaneneskpc.cointy.export.domain.model.ExportResult

interface FileExporter {
    suspend fun exportFile(content: String, fileName: String, format: ExportFormat): ExportResult
    fun generateFileName(prefix: String, format: ExportFormat): String
}


