package sample.currency.data

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CurrencyRepositoryTest {

    private val repository = CurrencyRepository()

    @Test
    fun getCurrencyRates() {
        val response = runBlocking {
            repository.getCurrencyRates("EUR")
        }
        println(response)

        assertEquals("EUR", response.baseCurrency)
        assertTrue(response.values.isNotEmpty())
        assertEquals("AUD", response.values.first().currency)
        assertEquals("ZAR", response.values.last().currency)
    }

    @Test
    fun findCurrency() {
        val existsCurrencies = listOf("EUR", "USD", "JPY")
        val nonExistsCurrencies = listOf("", "ABC", "XYZ")

        existsCurrencies.forEach { currency ->
            val found = repository.findCurrency(currency)

            assertNotNull(found)
            assertEquals(currency, found!!.currencyCode)
        }
        nonExistsCurrencies.forEach { currency ->
            val found = repository.findCurrency(currency)

            assertNull(found)
        }
    }
}
