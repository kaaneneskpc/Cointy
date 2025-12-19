package com.kaaneneskpc.cointy.transaction.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "TransactionEntity")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: String, // "BUY" or "SELL"
    val coinId: String,
    val coinName: String,
    val coinSymbol: String,
    val coinIconUrl: String,
    val amountInFiat: Double,
    val amountInUnit: Double,
    val price: Double,
    val timestamp: Long
)


