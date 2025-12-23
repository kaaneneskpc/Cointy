package com.kaaneneskpc.cointy.export.domain

import com.kaaneneskpc.cointy.export.domain.model.ExportData
import kotlinx.coroutines.flow.Flow

class GetExportDataUseCase(
    private val exportRepository: ExportRepository
) {
    operator fun invoke(): Flow<ExportData> {
        return exportRepository.getExportData()
    }
}


