package com.seongenie.handycoin.controller.baseCoin

import com.seongenie.handycoin.collector.exchange.upbit.UpbitRetrofitBuilder
import com.seongenie.handycoin.model.BaseCoin
import com.seongenie.handycoin.service.BaseCoinService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/base")
class BaseCoinController {

    @Autowired
    lateinit var baseCoinService: BaseCoinService

    @Autowired
    lateinit var upbitRetrofitBuilder: UpbitRetrofitBuilder

    @RequestMapping(value = ["/getBaseCoins"], method = [RequestMethod.GET])
    fun getBaseCoinList() : ExchangeCoinResponse {
        return baseCoinService.getBaseCoinList()
    }

    @RequestMapping(value = ["/getCoins/{exchange}"], method = [RequestMethod.GET])
    fun getCoins(@PathVariable("exchange") exchange : String) : List<String> {
        return baseCoinService.getBaseCoins(exchange).map{it -> it.coin}
    }

    @RequestMapping(value = ["/getExchanges/{coin}"], method = [RequestMethod.GET])
    fun getExchanges(@PathVariable("coin") coin : String) : List<String> {
        return baseCoinService.getBaseExchanges(coin).map{it -> it.exchange}
    }

    @RequestMapping(value = ["/upbit/ticker/collect"], method = [RequestMethod.GET])
    fun collectUpbitTicker() {
        upbitRetrofitBuilder.buildApiService().apply {
            val baseCoinList : List<BaseCoin> = baseCoinService.getBaseCoins("UPBIT")
            val builder = StringBuilder()
            baseCoinList.forEach { it ->
                builder.append(it.currency)
                builder.append("-")
                builder.append(it.coin)
                builder.append(",")
            }
            builder.setLength(builder.length - 1)
            var markets = builder.toString()
            println("업비트 마켓수 : ${baseCoinList.size}, market : ${markets}")
            tickerProcess(markets)
        }
    }
}