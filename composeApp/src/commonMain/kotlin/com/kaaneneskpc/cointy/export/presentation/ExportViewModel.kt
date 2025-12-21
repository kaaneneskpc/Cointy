package com.kaaneneskpc.cointy.export.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaaneneskpc.cointy.export.domain.ExportPortfolioDataUseCase
import com.kaaneneskpc.cointy.export.domain.GetExportDataUseCase
import com.kaaneneskpc.cointy.export.domain.model.ExportFormat
import com.kaaneneskpc.cointy.export.domain.model.ExportResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ExportViewModel(
    private val getExportDataUseCase: GetExportDataUseCase,
    private val exportPortfolioDataUseCase: ExportPortfolioDataUseCase
) : ViewModel() {
    private val _state: MutableStateFlow<ExportState> = MutableStateFlow(ExportState())
    val state: StateFlow<ExportState> = _state.asStateFlow()
    init {
        loadExportPreview()
    }
    private fun loadExportPreview() {
        viewModelScope.launch {
            getExportDataUseCase().collect { exportData ->
                _state.update { currentState ->
                    currentState.copy(
                        portfolioCoinCount = exportData.portfolioCoins.size,
                        transactionCount = exportData.transactions.size,
                        totalPortfolioValue = exportData.totalPortfolioValue,
                        cashBalance = exportData.cashBalance
                    )
                }
            }
        }
    }
    fun selectFormat(format: ExportFormat) {
        _state.update { currentState ->
            currentState.copy(selectedFormat = format)
        }
    }
    fun exportData() {
        viewModelScope.launch {
            _state.update { currentState ->
                currentState.copy(
                    isLoading = true,
                    errorMessage = null,
                    isExportSuccess = false
                )
            }
            val result = exportPortfolioDataUseCase(_state.value.selectedFormat)
            when (result) {
                is ExportResult.Success -> {
                    _state.update { currentState ->
                        currentState.copy(
                            isLoading = false,
                            isExportSuccess = true,
                            exportedFileName = result.fileName,
                            exportedFilePath = result.filePath
                        )
                    }
                }
                is ExportResult.Error -> {
                    _state.update { currentState ->
                        currentState.copy(
                            isLoading = false,
                            isExportSuccess = false,
                            errorMessage = result.message
                        )
                    }
                }
            }
        }
    }
    fun clearExportStatus() {
        _state.update { currentState ->
            currentState.copy(
                isExportSuccess = false,
                exportedFileName = null,
                exportedFilePath = null,
                errorMessage = null
            )
        }
    }
}

