package theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable

private val ThemeColors = lightColors(
    primary = Blue300,
    onPrimary = Grey50,
    surface = Blue300,
    onSurface = Grey50,
    secondary = Red400,
    background = Grey200,
    onBackground = Grey300,
    error = Red600
)

@Composable
fun ApplicationTheme(content: @Composable () -> Unit) {
    MaterialTheme(colors = ThemeColors, content = content)
}