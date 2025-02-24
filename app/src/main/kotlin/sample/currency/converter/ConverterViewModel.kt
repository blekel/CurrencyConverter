/*
 * Copyright (c) 2025, Vitaliy Levonyuk
 * Licensed under the MIT License.
 */
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
import sample.currency.data.CurrencyRate
import sample.currency.data.CurrencyRates
import sample.currency.data.CurrencyRepository
import timber.log.Timber
import java.util.Currency

class ConverterViewModel(
    repository: CurrencyRepository
) : ViewModel() {

    private val _currencyRatesState = MutableStateFlow<CurrencyRatesState>(CurrencyRatesState.Loading)
    val currencyRatesState: StateFlow<CurrencyRatesState> = _currencyRatesState

    private var _amount = MutableStateFlow<Double?>(null)
    val amount: StateFlow<Double?> = _amount

    private val defaultCurrency = Currency.getInstance("EUR")
    private var _selectedCurrency = MutableStateFlow(defaultCurrency)
    val selectedCurrency: StateFlow<Currency> = _selectedCurrency

    private val _currencyList = MutableStateFlow(emptyList<ConvertCurrency>())
    val currencyList: StateFlow<List<ConvertCurrency>> = _currencyList

    private val interactor = CurrencyInteractor(repository, selectedCurrency)
    private val currencyCodes = sortedSetOf<String>()
    private var currencyRates = CurrencyRates("", emptyList())
    private var fetchCurrenciesJob: Job? = null

    fun fetchCurrencies(lifecycleOwner: LifecycleOwner) {
        fetchCurrenciesJob?.cancel()
        fetchCurrenciesJob = viewModelScope.launch {
            lifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                interactor.fetchCurrencyRates().collect(::onResultState)
            }
        }
    }

    private fun onResultState(state: CurrencyRatesState) {
        _currencyRatesState.update { state }
        if (state !is CurrencyRatesState.Success) {
            return
        }
        val result = state.result
        if (currencyCodes.isEmpty()) {
            populateCurrencyCodes(result)
        }
        if (currencyRates != result) {
            currencyRates = result
            updateCurrencyList()
        }
    }

    private fun populateCurrencyCodes(result: CurrencyRates) {
        currencyCodes.apply {
            add(result.baseCurrency)
            addAll(result.values.map(CurrencyRate::currency))
        }
    }

    fun onAmountChange(amount: Double?) {
        Timber.i("onAmountChange: $amount")
        _amount.update { amount }
        updateCurrencyList()
    }

    fun onCurrencyClick(item: ConvertCurrency, lifecycleOwner: LifecycleOwner) {
        Timber.i("onCurrencyClick: $item")
        //_amount.update { item.amount }
        _selectedCurrency.update { item.currency }
        updateCurrencyList()
        fetchCurrencies(lifecycleOwner)
    }

    private fun updateCurrencyList() {
        val amount = _amount.value
        val selectedCurrency = _selectedCurrency.value
        val currencyRates = currencyRates
        val baseCurrency = interactor.findCurrency(currencyRates.baseCurrency) ?: return
        Timber.i("updateCurrencyList: $amount, $baseCurrency, $selectedCurrency")

        val viewItems = currencyCodes
            .filterNot { it == selectedCurrency.currencyCode }
            .mapNotNull { code ->
                val currency = interactor.findCurrency(code) ?: return@mapNotNull null
                val currencyRate = currencyRates.values.find { it.currency == code }

                var amountValue: Double? = null
                var rateValue: Double? = null
                if (selectedCurrency == baseCurrency
                    && amount != null && currencyRate != null
                ) {
                    rateValue = currencyRate.value
                    amountValue = amount * rateValue
                }

                ConvertCurrency(currency, baseCurrency, amountValue, rateValue)
            }
        _currencyList.update { viewItems }
    }
}
