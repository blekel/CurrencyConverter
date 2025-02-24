package sample.currency.converter

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

object ConverterUtils {

    private val amountFormat = DecimalFormat("#,##0.00").apply {
        maximumFractionDigits = 2
        val symbols = DecimalFormatSymbols()
        symbols.groupingSeparator = ','
        symbols.decimalSeparator = '.'
        decimalFormatSymbols = symbols
    }

    fun formatAmount(amount: Double): String {
        return amountFormat.format(amount).replace(".00", "")
    }
}