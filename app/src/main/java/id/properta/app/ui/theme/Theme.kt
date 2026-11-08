package id.properta.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = RoyalBlue,
    onPrimary = Color.White,
    secondary = Navy,
    onSecondary = Color.White,
    tertiary = CyanGlow,
    background = LightBackground,
    onBackground = DeepNavy,
    surface = Color.White,
    onSurface = DeepNavy,
    error = ErrorCoral
)

private val DarkColors = darkColorScheme(
    primary = ElectricBlue,
    onPrimary = Color.White,
    secondary = CyanGlow,
    onSecondary = DeepNavy,
    tertiary = RoyalBlue,
    background = DarkBackground,
    onBackground = Color(0xFFE8EEF8),
    surface = Color(0xFF0D1D33),
    onSurface = Color(0xFFE8EEF8),
    error = ErrorCoral
)

@Composable
fun PropertaTheme(
    darkTheme: Boolean = false,
    accentSkin: AccentSkin = PropertaAccentSkins.default,
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        darkColorScheme(
            primary = accentSkin.primary,
            onPrimary = Color.White,
            secondary = accentSkin.glow,
            onSecondary = DeepNavy,
            tertiary = CyanGlow,
            background = DarkBackground,
            onBackground = Color(0xFFE8EEF8),
            surface = Color(0xFF0D1D33),
            onSurface = Color(0xFFE8EEF8),
            error = ErrorCoral
        )
    } else {
        lightColorScheme(
            primary = accentSkin.primary,
            onPrimary = Color.White,
            secondary = Navy,
            onSecondary = Color.White,
            tertiary = accentSkin.glow,
            background = LightBackground,
            onBackground = DeepNavy,
            surface = Color.White,
            onSurface = DeepNavy,
            error = ErrorCoral
        )
    }

    MaterialTheme(
        colorScheme = colors,
        typography = MaterialTheme.typography,
        shapes = Shapes(
            small = RoundedCornerShape(12.dp),
            medium = RoundedCornerShape(20.dp),
            large = RoundedCornerShape(26.dp)
        ),
        shapes = Shapes(
            small = RoundedCornerShape(12.dp),
            medium = RoundedCornerShape(20.dp),
            large = RoundedCornerShape(26.dp)
        ),
        content = content
    )
}
