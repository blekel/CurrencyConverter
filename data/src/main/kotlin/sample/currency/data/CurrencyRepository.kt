package sample.currency.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.Currency

private data class CurrencyRatesResponseDto(val base: String, val rates: CurrencyRatesDto)

private typealias CurrencyRatesDto = Map<String, Double>

private interface CurrencyRatesService {
    @GET("latest")
    suspend fun fetchCurrencyRates(
        @Query("base") currency: String
    ): CurrencyRatesResponseDto
}

class CurrencyRepository {
    private val baseUrl = "https://api.frankfurter.app/"
    private val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val service = retrofit.create(CurrencyRatesService::class.java)

    private val currencyCodeMap = getCurrencies().associateBy(Currency::getCurrencyCode)

    fun findCurrency(currency: String): Currency? {
        return currencyCodeMap[currency]
    }

    private fun getCurrencies(): Set<Currency> {
        return Currency.getAvailableCurrencies()
    }

    suspend fun getCurrencyRates(currency: String): CurrencyRates {
        val response = service.fetchCurrencyRates(currency)
        return response.rates
            .map { CurrencyRate(it.key, it.value) }
            .let { CurrencyRates(response.base, it) }
    }
}
