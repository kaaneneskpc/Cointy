package com.kaaneneskpc.cointy.export.presentation

import com.kaaneneskpc.cointy.export.domain.model.ExportFormat

data class ExportState(
    val isLoading: Boolean = false,
    val isExportSuccess: Boolean = false,
    val exportedFileName: String? = null,
    val exportedFilePath: String? = null,
    val errorMessage: String? = null,
    val selectedFormat: ExportFormat = ExportFormat.CSV,
    val portfolioCoinCount: Int = 0,
    val transactionCount: Int = 0,
    val totalPortfolioValue: Double = 0.0,
    val cashBalance: Double = 0.0
)


