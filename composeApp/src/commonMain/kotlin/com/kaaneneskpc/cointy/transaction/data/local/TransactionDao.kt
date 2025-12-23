package com.kaaneneskpc.cointy.transaction.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    
    @Insert
    suspend fun insertTransaction(transaction: TransactionEntity)
    
    @Query("SELECT * FROM TransactionEntity ORDER BY timestamp DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>
    
    @Query("SELECT * FROM TransactionEntity WHERE coinId = :coinId ORDER BY timestamp DESC")
    fun getTransactionsByCoinId(coinId: String): Flow<List<TransactionEntity>>
}




