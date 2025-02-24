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
import sample.currency.data.CurrencyRates
import sample.currency.data.CurrencyRepository
import timber.log.Timber

class ConverterViewModel(
    repository: CurrencyRepository
) : ViewModel() {

    private val _currencyRatesState = MutableStateFlow<CurrencyRatesState>(CurrencyRatesState.Loading)
    val currencyRatesState: StateFlow<CurrencyRatesState> = _currencyRatesState

    private val defaultCurrency = "EUR"
    private val _currencyList = MutableStateFlow(emptyList<ConvertCurrency>())
    val currencyList: StateFlow<List<ConvertCurrency>> = _currencyList

    private val interactor = CurrencyInteractor(repository)
    private var currencyRates = CurrencyRates(defaultCurrency, emptyList())
    private var fetchCurrenciesJob: Job? = null

    fun fetchCurrencies(lifecycleOwner: LifecycleOwner) {
        fetchCurrenciesJob?.cancel()
        fetchCurrenciesJob = viewModelScope.launch {
            lifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                interactor.fetchCurrencyRates(defaultCurrency).collect(::onResultState)
            }
        }
    }

    private fun onResultState(state: CurrencyRatesState) {
        _currencyRatesState.update { state }
        if (state !is CurrencyRatesState.Success) {
            return
        }
        val result = state.result
        if (currencyRates != result) {
            currencyRates = result
            updateCurrencyList()
        }
    }

    private fun updateCurrencyList() {
        val currencyRates = currencyRates
        val baseCurrency = interactor.findCurrency(currencyRates.baseCurrency) ?: return
        Timber.i("Update currency list (base: $baseCurrency)")

        val viewItems = currencyRates.values
            .sortedBy { it.currency }
            .mapNotNull { currencyRate ->
                val currency = interactor.findCurrency(currencyRate.currency)
                    ?: return@mapNotNull null
                val rateValue = currencyRate.value
                val amountValue = 1.0 * rateValue

                ConvertCurrency(currency, baseCurrency, amountValue, rateValue)
            }
        _currencyList.update { viewItems }
    }
}