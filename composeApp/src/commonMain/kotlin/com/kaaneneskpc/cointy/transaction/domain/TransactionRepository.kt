package com.kaaneneskpc.cointy.transaction.domain

import com.kaaneneskpc.cointy.core.domain.DataError
import com.kaaneneskpc.cointy.core.domain.EmptyResult
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    suspend fun saveTransaction(transaction: TransactionModel): EmptyResult<DataError.Local>
    fun getAllTransactions(): Flow<List<TransactionModel>>
    fun getTransactionsByCoinId(coinId: String): Flow<List<TransactionModel>>
}


