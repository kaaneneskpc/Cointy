package com.kaaneneskpc.cointy.coins.domain

import com.kaaneneskpc.cointy.core.domain.DataError
import com.kaaneneskpc.cointy.coins.domain.api.CoinRemoteDataSource
import com.kaaneneskpc.cointy.coins.domain.model.CoinModel
import com.kaaneneskpc.cointy.core.domain.map
import com.kaaneneskpc.cointy.coins.data.mapper.toCoinModel
import com.kaaneneskpc.cointy.core.domain.Result


class GetCoinDetailsUseCase(
    private val client: CoinRemoteDataSource,
) {

    suspend fun execute(coinId: String): Result<CoinModel, DataError.Remote> {
        return client.getCoinById(coinId).map { dto ->
            dto.data.coin.toCoinModel()
        }
    }
}