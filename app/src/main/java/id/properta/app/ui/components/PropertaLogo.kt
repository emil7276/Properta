package id.properta.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

@Composable
fun PropertaLogo(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val unit = size.minDimension / 10f
        val blueBrush = Brush.linearGradient(
            colors = listOf(
                Color(0xFF2684FF),
                Color(0xFF155EEF),
                Color(0xFF22D3EE)
            ),
            start = Offset(0f, 0f),
            end = Offset(size.width, size.height)
        )

        drawRoundRect(
            brush = blueBrush,
            topLeft = Offset(unit, unit),
            size = Size(unit * 2f, unit * 7f),
            cornerRadius = CornerRadius(unit * 0.25f)
        )
        drawRoundRect(
            brush = blueBrush,
            topLeft = Offset(unit * 2f, unit),
            size = Size(unit * 5f, unit * 2f),
            cornerRadius = CornerRadius(unit * 0.35f)
        )
        drawRoundRect(
            brush = blueBrush,
            topLeft = Offset(unit * 6f, unit * 2.5f),
            size = Size(unit * 1.5f, unit * 2f),
            cornerRadius = CornerRadius(unit * 0.25f)
        )
        drawRoundRect(
            brush = blueBrush,
            topLeft = Offset(unit * 2f, unit * 4f),
            size = Size(unit * 4.5f, unit * 1.5f),
            cornerRadius = CornerRadius(unit * 0.25f)
        )

        val square = unit * 0.75f
        val gap = unit * 0.3f
        val startX = unit * 3f
        val y = unit * 8.1f

        repeat(4) { i ->
            drawRoundRect(
                brush = blueBrush,
                topLeft = Offset(startX + i * (square + gap), y),
                size = Size(square, square),
                cornerRadius = CornerRadius(unit * 0.12f)
            )
        }
    }
}
