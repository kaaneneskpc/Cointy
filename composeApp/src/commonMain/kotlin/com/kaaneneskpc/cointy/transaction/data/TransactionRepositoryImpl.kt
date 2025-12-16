package com.kaaneneskpc.cointy.transaction.data

import androidx.sqlite.SQLiteException
import com.kaaneneskpc.cointy.core.domain.DataError
import com.kaaneneskpc.cointy.core.domain.EmptyResult
import com.kaaneneskpc.cointy.core.domain.Result
import com.kaaneneskpc.cointy.transaction.data.local.TransactionDao
import com.kaaneneskpc.cointy.transaction.data.mapper.toTransactionEntity
import com.kaaneneskpc.cointy.transaction.data.mapper.toTransactionModel
import com.kaaneneskpc.cointy.transaction.domain.TransactionModel
import com.kaaneneskpc.cointy.transaction.domain.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TransactionRepositoryImpl(
    private val transactionDao: TransactionDao
) : TransactionRepository {

    override suspend fun saveTransaction(transaction: TransactionModel): EmptyResult<DataError.Local> {
        return try {
            transactionDao.insertTransaction(transaction.toTransactionEntity())
            Result.Success(Unit)
        } catch (e: SQLiteException) {
            Result.Error(DataError.Local.DISK_FULL)
        }
    }

    override fun getAllTransactions(): Flow<List<TransactionModel>> {
        return transactionDao.getAllTransactions().map { entities ->
            entities.map { it.toTransactionModel() }
        }
    }

    override fun getTransactionsByCoinId(coinId: String): Flow<List<TransactionModel>> {
        return transactionDao.getTransactionsByCoinId(coinId).map { entities ->
            entities.map { it.toTransactionModel() }
        }
    }
}

