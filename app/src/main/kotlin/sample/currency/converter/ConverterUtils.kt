/*
 * Copyright (c) 2025, Vitaliy Levonyuk
 * Licensed under the MIT License.
 */
package sample.currency.converter

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.ParseException

object ConverterUtils {

    private val integerFormat = DecimalFormat("#,##0")
    private val amountFormat = DecimalFormat("#,##0.00").apply {
        maximumFractionDigits = 2
        val symbols = DecimalFormatSymbols()
        symbols.groupingSeparator = ','
        symbols.decimalSeparator = '.'
        decimalFormatSymbols = symbols
    }

    fun parseAmount(newText: String): Double? {
        if (newText.isEmpty()) {
            return null
        }
        return try {
            amountFormat.parse(newText)?.toDouble()
        } catch (e: ParseException) {
            null
        }
    }

    fun formatAmount(amount: Double): String {
        return amountFormat.format(amount).replace(".00", "")
    }

    fun reformatInputAmount(
        input: String,
        decimalDelimiter: Char = '.',
        maxIntegerLength: Int = 12,
        maxDecimalLength: Int = 2,
    ): String {
        // Filter digit characters and decimal delimiter
        val cleanInput = input.filter { it.isDigit() || it == decimalDelimiter }.let {
            val dotCount = it.count { char -> char == decimalDelimiter }
            if (dotCount > 1) it.dropLast(1) else it
        }
        if (cleanInput.isEmpty()) return ""

        // Process integer and decimal parts
        val parts = cleanInput.split(decimalDelimiter)
        val integerPart = parts[0]
            .let { if (it.length > 1) it.removePrefix("0") else it }
            .let { if (it.length > maxIntegerLength) it.take(maxIntegerLength) else it }
            .toLong()
            .let<Long, String>(integerFormat::format)
        val decimalPart = (if (parts.size > 1) parts[1] else "")
            .let { if (it.length > maxDecimalLength) it.take(maxDecimalLength) else it }

        // Format the result
        val buf = StringBuilder(integerPart)
        if (decimalPart.isNotEmpty() || cleanInput.endsWith(decimalDelimiter)) {
            buf.append(decimalDelimiter)
        }
        buf.append(decimalPart)
        return buf.toString()
    }
}
