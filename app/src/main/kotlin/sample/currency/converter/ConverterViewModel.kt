package sample.currency.converter

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import sample.currency.data.CurrencyRepository

class ConverterViewModel(
    repository: CurrencyRepository
) : ViewModel() {

    private val _currencyRates = MutableStateFlow<CurrencyRatesState>(CurrencyRatesState.Loading)
    val currencyRates: StateFlow<CurrencyRatesState> = _currencyRates

    private val defaultCurrency = "EUR"
    private val interactor = CurrencyInteractor(repository)
    private var fetchCurrenciesJob: Job? = null

    fun fetchCurrencies(lifecycleOwner: LifecycleOwner) {
        fetchCurrenciesJob?.cancel()
        fetchCurrenciesJob = viewModelScope.launch {
            lifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                interactor.fetchCurrencyRates(defaultCurrency).collect { state ->
                    _currencyRates.update { state }
                }
            }
        }
    }
}