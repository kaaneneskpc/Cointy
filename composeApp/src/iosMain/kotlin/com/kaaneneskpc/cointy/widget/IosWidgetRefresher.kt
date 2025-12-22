package com.kaaneneskpc.cointy.widget

import com.kaaneneskpc.cointy.core.domain.onSuccess
import com.kaaneneskpc.cointy.widget.domain.GetWidgetDataUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object IosWidgetRefresher : KoinComponent {
    private val getWidgetDataUseCase: GetWidgetDataUseCase by inject()
    private val scope = CoroutineScope(Dispatchers.IO)
    fun refreshWidgetData() {
        scope.launch {
            getWidgetDataUseCase.execute().onSuccess { data ->
                IosWidgetDataWriter.writePortfolioData(data.portfolioData)
                IosWidgetDataWriter.writeCoinData(data.coins)
            }
        }
    }
}
