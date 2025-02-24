/*
 * Copyright (c) 2025, Vitaliy Levonyuk
 * Licensed under the MIT License.
 */
package sample.currency.converter

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import org.junit.Test
import sample.currency.converter.ui.theme.CurrencyConverterTheme
import sample.currency.data.CurrencyRepository

class ConverterScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testLoadingAndDisplayCurrencies() {
        composeTestRule.setContent {
            CurrencyConverterTheme {
                MainScreen(ConverterViewModel(CurrencyRepository()))
            }
        }
        composeTestRule.onNodeWithText("Loading…").assertIsDisplayed()

        composeTestRule.waitUntil {
            composeTestRule.onAllNodesWithText("BRL")
                .fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithText("AUD").assertIsDisplayed()
        composeTestRule.onNodeWithText("BGN").assertIsDisplayed()
        composeTestRule.onNodeWithText("CAD").assertIsDisplayed()
    }
}
