package sample.currency.converter

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
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
        viewModel.fetchCurrencies(lifecycleOwner)
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        when (val state = currencyRates) {
            is CurrencyRatesState.Loading ->
                LoadingState(stringResource(R.string.loading))

            is CurrencyRatesState.Error ->
                LoadingState(
                    message = stringResource(id = R.string.error) + ": ${state.message}",
                    color = Color.Red
                )

            // Currency list
            is CurrencyRatesState.Success ->
                ConvertCurrencyList(state.result.values)
        }
    }
}

@Composable
private fun LoadingState(
    message: String,
    color: Color = MaterialTheme.colors.onBackground
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = message, color = color, textAlign = TextAlign.Center)
    }
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
