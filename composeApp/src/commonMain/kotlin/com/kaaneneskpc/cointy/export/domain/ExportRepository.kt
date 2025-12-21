package com.kaaneneskpc.cointy.export.domain

import com.kaaneneskpc.cointy.export.domain.model.ExportData
import com.kaaneneskpc.cointy.export.domain.model.ExportFormat
import com.kaaneneskpc.cointy.export.domain.model.ExportResult
import kotlinx.coroutines.flow.Flow

interface ExportRepository {
    fun getExportData(): Flow<ExportData>
    suspend fun exportToFormat(data: ExportData, format: ExportFormat): ExportResult
    fun generateCsvContent(data: ExportData): String
    fun generateJsonContent(data: ExportData): String
}

