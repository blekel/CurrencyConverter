package sample.currency.converter

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.LocalLifecycleOwner
import sample.currency.data.CurrencyRate

@Composable
fun ConverterScreen(
    viewModel: ConverterViewModel,
) {
    val currencyRates by viewModel.currencyRates.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(lifecycleOwner) {
        viewModel.fetchCurrencies()
    }

    ConvertCurrencyList(currencyRates.values)
}

@Composable
private fun ConvertCurrencyList(
    currencies: List<CurrencyRate>,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(
            items = currencies,
            key = { item -> item.currency }
        ) { item ->
            Card(
                shape = RoundedCornerShape(8.dp),
                backgroundColor = MaterialTheme.colors.secondary,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "${item.currency} ${item.value}",
                    style = TextStyle(fontSize = 18.sp),
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}
