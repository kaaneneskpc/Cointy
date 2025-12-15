package com.kaaneneskpc.cointy.core.database.portfolio

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import com.kaaneneskpc.cointy.portfolio.data.local.PortfolioCoinEntity
import com.kaaneneskpc.cointy.portfolio.data.local.PortfolioDao
import com.kaaneneskpc.cointy.portfolio.data.local.UserBalanceDao
import com.kaaneneskpc.cointy.portfolio.data.local.UserBalanceEntity

@ConstructedBy(PortfolioDatabaseCreator::class)
@Database(entities = [PortfolioCoinEntity::class, UserBalanceEntity::class], version = 2)
abstract class PortfolioDatabase: RoomDatabase() {
    abstract fun portfolioDao(): PortfolioDao
    abstract fun userBalanceDao(): UserBalanceDao
}


