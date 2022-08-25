package theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.runtime.Composable

private val ThemeColors = darkColors(primary = Colors.closedFieldBackground)

@Composable
fun ApplicationTheme(content: @Composable () -> Unit) {
    MaterialTheme(colors = ThemeColors, content = content)
}