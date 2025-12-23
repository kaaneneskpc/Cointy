package com.kaaneneskpc.cointy.export.domain.model

sealed interface ExportResult {
    data class Success(val filePath: String, val fileName: String) : ExportResult
    data class Error(val message: String) : ExportResult
}


