package sample.currency.converter

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource

@Composable
fun MainScreen(converterViewModel: ConverterViewModel) {
    Scaffold(
        topBar = { TopBarImpl() }
    ) { padding ->
        ContentImpl(padding, converterViewModel)
    }
}

@Composable
private fun TopBarImpl() {
    TopAppBar(
        title = {
            Text(
                text = stringResource(R.string.app_name),
                color = MaterialTheme.colors.onPrimary
            )
        },
        backgroundColor = MaterialTheme.colors.primary,
    )
}

@Composable
private fun ContentImpl(
    padding: PaddingValues,
    converterViewModel: ConverterViewModel,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
    ) {
        ConverterScreen(converterViewModel)
    }
}
