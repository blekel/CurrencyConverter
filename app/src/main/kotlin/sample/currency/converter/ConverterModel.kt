package sample.currency.converter

import sample.currency.data.CurrencyRates
import java.util.Currency

sealed class CurrencyRatesState {
    data object Loading : CurrencyRatesState()
    data class Success(val result: CurrencyRates) : CurrencyRatesState()
    data class Error(val message: String) : CurrencyRatesState()
}

data class ConvertCurrency(
    val currency: Currency,
    val baseCurrency: Currency,
    val amount: Double?,
    val rate: Double?,
)
