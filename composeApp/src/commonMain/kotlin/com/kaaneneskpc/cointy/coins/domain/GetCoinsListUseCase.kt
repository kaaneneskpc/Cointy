package com.kaaneneskpc.cointy.coins.domain

import com.kaaneneskpc.cointy.core.domain.DataError
import com.kaaneneskpc.cointy.core.domain.Result
import com.kaaneneskpc.cointy.core.domain.map
import com.kaaneneskpc.cointy.coins.data.mapper.toCoinModel
import com.kaaneneskpc.cointy.coins.domain.api.CoinRemoteDataSource
import com.kaaneneskpc.cointy.coins.domain.model.CoinModel

class GetCoinsListUseCase(
    private val client: CoinRemoteDataSource
) {
    suspend fun execute(): Result<List<CoinModel>, DataError.Remote> {
        return client.getListOfCoins().map { dto ->
            dto.data.coins.map { it.toCoinModel() }
        }
    }
}