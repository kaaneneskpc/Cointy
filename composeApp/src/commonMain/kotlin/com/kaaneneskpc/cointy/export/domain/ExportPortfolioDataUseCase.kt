package com.kaaneneskpc.cointy.export.domain

import com.kaaneneskpc.cointy.export.domain.model.ExportFormat
import com.kaaneneskpc.cointy.export.domain.model.ExportResult
import kotlinx.coroutines.flow.first

class ExportPortfolioDataUseCase(
    private val exportRepository: ExportRepository
) {
    suspend operator fun invoke(format: ExportFormat): ExportResult {
        val exportData = exportRepository.getExportData().first()
        return exportRepository.exportToFormat(exportData, format)
    }
}

