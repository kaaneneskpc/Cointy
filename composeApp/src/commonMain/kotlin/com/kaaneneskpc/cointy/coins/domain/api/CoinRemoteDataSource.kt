package com.kaaneneskpc.cointy.coins.domain.api

import com.kaaneneskpc.cointy.core.domain.DataError
import com.kaaneneskpc.cointy.core.domain.Result
import com.kaaneneskpc.cointy.coins.data.remote.dto.CoinDetailResponse
import com.kaaneneskpc.cointy.coins.data.remote.dto.CoinPriceHistoryResponse
import com.kaaneneskpc.cointy.coins.data.remote.dto.CoinResponse

interface CoinRemoteDataSource {
    suspend fun getListOfCoins(): Result<CoinResponse, DataError.Remote>
    suspend fun getPriceHistory(coinId: String): Result<CoinPriceHistoryResponse, DataError.Remote>
    suspend fun getCoinById(coinId: String): Result<CoinDetailResponse, DataError.Remote>
}