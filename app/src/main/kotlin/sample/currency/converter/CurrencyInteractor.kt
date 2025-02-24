package sample.currency.converter

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.retryWhen
import sample.currency.data.CurrencyRepository
import timber.log.Timber
import java.io.IOException

class CurrencyInteractor(
    private val repository: CurrencyRepository,
) {
    fun fetchCurrencyRates(currency: String, repeatDelayInSec: Int = 3): Flow<CurrencyRatesState> = flow {
        while (true) {
            Timber.d("Fetch currency rates for $currency")
            val rates = repository.getCurrencyRates(currency)

            emit(CurrencyRatesState.Success(rates) as CurrencyRatesState)
            delay(1000L * repeatDelayInSec)
        }
    }.retryWhen { cause, attempt ->
        if (cause is IOException) {
            Timber.w("Retrying after error: ${cause.message}")
            delay(1000L * repeatDelayInSec * (attempt + 1))
            true
        } else {
            false
        }
    }.catch { e ->
        Timber.e("Error fetching currency rates: ${e.message}")
        emit(CurrencyRatesState.Error("${e.message}"))
    }
}
