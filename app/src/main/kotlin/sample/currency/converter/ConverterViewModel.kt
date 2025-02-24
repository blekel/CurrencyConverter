package sample.currency.converter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import sample.currency.data.CurrencyRates
import sample.currency.data.CurrencyRepository
import timber.log.Timber

class ConverterViewModel(
    private val repository: CurrencyRepository
) : ViewModel() {

    private val _currencyRates = MutableStateFlow(CurrencyRates("", emptyList()))
    val currencyRates: StateFlow<CurrencyRates> = _currencyRates

    private val defaultCurrency = "EUR"
    private var fetchCurrenciesJob: Job? = null

    fun fetchCurrencies() {
        fetchCurrenciesJob?.cancel()
        fetchCurrenciesJob = viewModelScope.launch {
            Timber.d("fetch currency rates for $defaultCurrency")
            val response = repository.getCurrencyRates(defaultCurrency)
            Timber.d("response: $response")
            _currencyRates.update { response }
        }
    }
}