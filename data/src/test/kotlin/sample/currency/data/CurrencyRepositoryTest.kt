package sample.currency.data

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CurrencyRepositoryTest {

    private val repository = CurrencyRepository()

    @Test
    fun getCurrencyRates() {
        runBlocking {
            val response = repository.getCurrencyRates("EUR")
            println(response)

            assertEquals("EUR", response.baseCurrency)
            assertTrue(response.values.isNotEmpty())
            assertEquals("AUD", response.values.first().currency)
            assertEquals("ZAR", response.values.last().currency)
        }
    }
}
