package id.properta.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke

@Composable
fun PropertaLogo(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val u = size.minDimension / 10f
        val blue = Color(0xFF2684FF)
        val royal = Color(0xFF155EEF)
        val cyan = Color(0xFF22D3EE)
        val gradient = Brush.linearGradient(
            colors = listOf(cyan, blue, royal, cyan),
            start = Offset(0f, 0f),
            end = Offset(size.width, size.height)
        )

        // Soft ambient glow around the geometric P.
        val glow = Brush.radialGradient(
            colors = listOf(
                Color(0x552684FF),
                Color(0x2222D3EE),
                Color.Transparent
            ),
            center = Offset(u * 4.5f, u * 4.2f),
            radius = u * 6.2f
        )
        drawCircle(
            brush = glow,
            radius = u * 5.5f,
            center = Offset(u * 4.5f, u * 4.5f)
        )

        // Geometric P silhouette.
        drawRoundRect(
            brush = gradient,
            topLeft = Offset(u, u),
            size = Size(u * 1.65f, u * 7f),
            cornerRadius = CornerRadius(u * 0.18f)
        )
        drawRoundRect(
            brush = gradient,
            topLeft = Offset(u * 2f, u),
            size = Size(u * 4.8f, u * 1.65f),
            cornerRadius = CornerRadius(u * 0.22f)
        )
        drawRoundRect(
            brush = gradient,
            topLeft = Offset(u * 5.4f, u * 2.1f),
            size = Size(u * 1.4f, u * 1.65f),
            cornerRadius = CornerRadius(u * 0.2f)
        )
        drawRoundRect(
            brush = gradient,
            topLeft = Offset(u * 2f, u * 3.5f),
            size = Size(u * 4.4f, u * 1.45f),
            cornerRadius = CornerRadius(u * 0.2f)
        )

        // Fine neon highlight along the inner contour.
        val neon = Path().apply {
            moveTo(u * 2.05f, u * 1.5f)
            lineTo(u * 6.0f, u * 1.5f)
            quadraticTo(u * 6.5f, u * 1.5f, u * 6.5f, u * 2.0f)
            lineTo(u * 6.5f, u * 3.2f)
            quadraticTo(u * 6.5f, u * 3.65f, u * 6.0f, u * 3.65f)
            lineTo(u * 2.2f, u * 3.65f)
        }
        drawPath(
            path = neon,
            color = Color(0xAA9AEFFF),
            style = Stroke(width = u * 0.10f, cap = StrokeCap.Round)
        )
        drawLine(
            color = Color(0xBBB8F7FF),
            start = Offset(u * 1.25f, u * 2f),
            end = Offset(u * 1.25f, u * 7.3f),
            strokeWidth = u * 0.08f,
            cap = StrokeCap.Round
        )

        // Four small signature tiles at the lower-right.
        val tile = u * 0.62f
        val gap = u * 0.16f
        val startX = u * 5.0f
        val startY = u * 6.1f
        repeat(4) { i ->
            val x = startX + (i % 2) * (tile + gap)
            val y = startY + (i / 2) * (tile + gap)
            drawRoundRect(
                brush = gradient,
                topLeft = Offset(x, y),
                size = Size(tile, tile),
                cornerRadius = CornerRadius(u * 0.10f)
            )
            drawRoundRect(
                color = Color(0x8899F6FF),
                topLeft = Offset(x, y),
                size = Size(tile, tile),
                cornerRadius = CornerRadius(u * 0.10f),
                style = Stroke(width = u * 0.045f)
            )
        }
    }
}
