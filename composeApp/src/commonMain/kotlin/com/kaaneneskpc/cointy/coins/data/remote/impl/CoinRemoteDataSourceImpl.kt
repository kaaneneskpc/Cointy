package com.kaaneneskpc.cointy.coins.data.remote.impl

import com.kaaneneskpc.cointy.coins.domain.api.CoinRemoteDataSource
import com.kaaneneskpc.cointy.coins.data.remote.dto.CoinDetailResponse
import com.kaaneneskpc.cointy.coins.data.remote.dto.CoinPriceHistoryResponse
import com.kaaneneskpc.cointy.coins.data.remote.dto.CoinResponse
import com.kaaneneskpc.cointy.core.domain.DataError
import com.kaaneneskpc.cointy.core.domain.Result
import com.kaaneneskpc.cointy.core.network.safeCall
import io.ktor.client.HttpClient
import io.ktor.client.request.get

private const val BASE_URL = "https://api.coinranking.com/v2"

class CoinRemoteDataSourceImpl(private val httpClient: HttpClient) : CoinRemoteDataSource {
    override suspend fun getListOfCoins(): Result<CoinResponse, DataError.Remote> {
        return safeCall {
            httpClient.get("$BASE_URL/coins")
        }
    }

    override suspend fun getPriceHistory(coinId: String): Result<CoinPriceHistoryResponse, DataError.Remote> {
        return safeCall {
            httpClient.get("$BASE_URL/coin/$coinId/history")
        }
    }

    override suspend fun getCoinById(coinId: String): Result<CoinDetailResponse, DataError.Remote> {
        return safeCall {
            httpClient.get("$BASE_URL/coin/$coinId")
        }
    }
}