package sample.currency.converter

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.LocalLifecycleOwner
import kotlinx.coroutines.flow.StateFlow
import sample.currency.converter.ConverterUtils.formatAmount
import sample.currency.converter.ConverterUtils.parseAmount
import sample.currency.converter.ConverterUtils.reformatInputAmount
import java.util.Currency

@Composable
fun ConverterScreen(
    viewModel: ConverterViewModel,
) {
    val currencyRatesState by viewModel.currencyRatesState.collectAsState()
    val selectedCurrency by viewModel.selectedCurrency.collectAsState()
    val currencyList by viewModel.currencyList.collectAsState()

    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(lifecycleOwner) {
        viewModel.fetchCurrencies(lifecycleOwner)
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // Selected currency
        SelectedCurrencyItem(
            amountFlow = viewModel.amount,
            currency = selectedCurrency,
            focusRequester = focusRequester,
            onAmountChanged = viewModel::onAmountChange,
        )

        when (val state = currencyRatesState) {
            is CurrencyRatesState.Loading ->
                LoadingState(stringResource(R.string.loading))

            is CurrencyRatesState.Error ->
                LoadingState(
                    message = stringResource(id = R.string.error) + ": ${state.message}",
                    color = Color.Red
                )

            // Currency list
            is CurrencyRatesState.Success ->
                ConvertCurrencyList(currencyList) {
                    focusRequester.requestFocus()
                    keyboardController?.show()

                    viewModel.onCurrencyClick(it, lifecycleOwner)
                }
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
private fun SelectedCurrencyItem(
    amountFlow: StateFlow<Double?>,
    currency: Currency,
    focusRequester: FocusRequester,
    onAmountChanged: (Double?) -> Unit,
) {
    Card(
        shape = RoundedCornerShape(8.dp),
        backgroundColor = MaterialTheme.colors.primaryVariant,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            DetailsItem(
                label = currency.currencyCode,
                labelColor = MaterialTheme.colors.onPrimary,
                description = currency.displayName,
                descriptionColor = Color.LightGray,
            )

            // Input amount
            InputAmountField(amountFlow, focusRequester, onAmountChanged)
        }
    }
}

@Composable
private fun ConvertCurrencyList(
    currencies: List<ConvertCurrency>,
    onItemClick: (ConvertCurrency) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(
            items = currencies,
            key = { item -> item.currency.currencyCode }
        ) { item ->
            CurrencyItem(item) { onItemClick(item) }
        }
        item {
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun InputAmountField(
    amountFlow: StateFlow<Double?>,
    focusRequester: FocusRequester,
    onAmountChanged: (Double?) -> Unit
) {
    val amount by amountFlow.collectAsState()
    var textFieldValue by remember { mutableStateOf(TextFieldValue("")) }

    LaunchedEffect(amount) {
        if (amount != null) {
            val formattedAmount = formatAmount(amount!!)
            if (textFieldValue.text != formattedAmount) {
                textFieldValue = TextFieldValue(
                    text = formattedAmount,
                    selection = TextRange(formattedAmount.length)
                )
            }
        }
    }

    TextField(
        value = textFieldValue,
        onValueChange = { value ->
            val newText = reformatInputAmount(value.text)
            val selection = TextRange(newText.length)
            textFieldValue = value.copy(text = newText, selection = selection)

            val newAmount = parseAmount(newText)
            onAmountChanged(newAmount)
        },
        textStyle = TextStyle(
            fontSize = 18.sp,
            color = MaterialTheme.colors.onPrimary,
            textAlign = TextAlign.End
        ),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            cursorColor = MaterialTheme.colors.onPrimary,
        ),
        modifier = Modifier
            .padding(0.dp)
            .focusRequester(focusRequester)
    )
}

@Composable
fun LazyItemScope.CurrencyItem(
    item: ConvertCurrency,
    onItemClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(8.dp),
        backgroundColor = MaterialTheme.colors.secondary,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .animateItem()
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            DetailsItem(
                label = item.currency.currencyCode,
                description = item.currency.displayName,
                horizontalAlignment = Alignment.Start,
            )
            if (item.amount != null && item.rate != null) {
                DetailsItem(
                    label = formatAmount(item.amount),
                    description = "1 ${item.baseCurrency.currencyCode} = ${formatAmount(item.rate)}",
                    horizontalAlignment = Alignment.End,
                )
            }
        }
    }
}

@Composable
fun DetailsItem(
    label: String,
    description: String,
    labelColor: Color = MaterialTheme.colors.onSecondary,
    descriptionColor: Color = Color.Gray,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
) {
    Column(
        horizontalAlignment = horizontalAlignment,
        modifier = Modifier.padding(16.dp)
    ) {
        Text(
            text = label,
            color = labelColor,
            style = TextStyle(fontSize = 18.sp),
        )
        Text(
            text = description,
            color = descriptionColor,
            style = TextStyle(fontSize = 16.sp),
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}
