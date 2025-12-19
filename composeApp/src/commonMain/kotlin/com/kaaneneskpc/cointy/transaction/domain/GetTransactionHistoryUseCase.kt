package com.kaaneneskpc.cointy.transaction.domain

import kotlinx.coroutines.flow.Flow

class GetTransactionHistoryUseCase(
    private val transactionRepository: TransactionRepository
) {
    fun getAllTransactions(): Flow<List<TransactionModel>> {
        return transactionRepository.getAllTransactions()
    }
    
    fun getTransactionsByCoinId(coinId: String): Flow<List<TransactionModel>> {
        return transactionRepository.getTransactionsByCoinId(coinId)
    }
}


