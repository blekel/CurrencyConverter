package sample.currency.converter

import sample.currency.data.CurrencyRates

sealed class CurrencyRatesState {
    data object Loading : CurrencyRatesState()
    data class Success(val result: CurrencyRates) : CurrencyRatesState()
    data class Error(val message: String) : CurrencyRatesState()
}
