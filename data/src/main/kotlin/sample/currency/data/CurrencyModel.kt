/*
 * Copyright (c) 2025, Vitaliy Levonyuk
 * Licensed under the MIT License.
 */
package sample.currency.data

data class CurrencyRate(val currency: String, val value: Double)

data class CurrencyRates(val baseCurrency: String, val values: List<CurrencyRate>)
