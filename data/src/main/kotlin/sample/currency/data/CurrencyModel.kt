package sample.currency.data

data class CurrencyRate(val currency: String, val value: Double)

data class CurrencyRates(val baseCurrency: String, val values: List<CurrencyRate>)
