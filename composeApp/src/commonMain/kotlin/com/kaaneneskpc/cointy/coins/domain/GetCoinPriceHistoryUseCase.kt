package com.kaaneneskpc.cointy.coins.domain

import com.kaaneneskpc.cointy.core.domain.DataError
import com.kaaneneskpc.cointy.core.domain.Result
import com.kaaneneskpc.cointy.coins.domain.api.CoinRemoteDataSource
import com.kaaneneskpc.cointy.coins.domain.model.PriceModel
import com.kaaneneskpc.cointy.core.domain.map
import com.kaaneneskpc.cointy.coins.data.mapper.toPriceModel

class GetCoinPriceHistoryUseCase(
    private val client: CoinRemoteDataSource,
) {

    suspend fun execute(coinId: String): Result<List<PriceModel>, DataError.Remote> {
        return client.getPriceHistory(coinId).map { dto ->
            dto.data.history
                .filter { it.price != null }
                .map { it.toPriceModel() }
        }
    }
}