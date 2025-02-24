/*
 * Copyright (c) 2025, Vitaliy Levonyuk
 * Licensed under the MIT License.
 */
package sample.currency.converter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import org.koin.androidx.viewmodel.ext.android.viewModel
import sample.currency.converter.ui.theme.CurrencyConverterTheme

class MainActivity : ComponentActivity() {

    private val converterViewModel: ConverterViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        setContent {
            CurrencyConverterTheme {
                MainScreen(converterViewModel)
            }
        }
    }
}
