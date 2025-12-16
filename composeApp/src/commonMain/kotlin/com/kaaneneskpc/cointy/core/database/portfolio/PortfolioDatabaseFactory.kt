package com.kaaneneskpc.cointy.core.database.portfolio

import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

@Suppress("NO_ACTUAL_FOR_EXPECT", "EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect object PortfolioDatabaseCreator : RoomDatabaseConstructor<PortfolioDatabase> {
    override fun initialize(): PortfolioDatabase
}

fun getPortfolioDatabase(
    builder: RoomDatabase.Builder<PortfolioDatabase>
): PortfolioDatabase {
    return builder
        //.addMigrations(MIGRATIONS)
        .fallbackToDestructiveMigration(dropAllTables = false)
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}