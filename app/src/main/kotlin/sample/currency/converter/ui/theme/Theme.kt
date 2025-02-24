package sample.currency.converter.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val lightColors = Colors(
    primary = Color(0xFF43B6BC),
    primaryVariant = Color(0xFF34949a),
    secondary = Color(0xFFE6E6EB),
    secondaryVariant = Color(0xFFD1D1D6),
    background = Color(0xFFFAFAFA),
    surface = Color.White,
    error = Color.Red,
    onPrimary = Color(0xFFEEEEEE),
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    onError = Color.White,
    isLight = true,
)

// Dark theme is not provided yet
private val darkColors = lightColors

@Composable
fun CurrencyConverterTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) darkColors else lightColors
    MaterialTheme(
        colors = colors,
        content = content,
    )
}
