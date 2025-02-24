package sample.currency.converter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import org.koin.androidx.viewmodel.ext.android.viewModel
import sample.currency.converter.ui.theme.CurrencyConverterTheme

class MainActivity : ComponentActivity() {

    private val converterViewModel: ConverterViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CurrencyConverterTheme {
                MainScreen(converterViewModel)
            }
        }
    }
}
