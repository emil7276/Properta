package id.properta.app.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Branded intro component. It is deliberately independent from the existing
 * PropertaLogo API so it can be integrated without changing the logo component.
 */
@Composable
fun PropertaAnimatedIntro(
    modifier: Modifier = Modifier,
    slogan: String = "Kelola properti dengan lebih mudah"
) {
    val transition = rememberInfiniteTransition(label = "properta_intro")
    val pulse = transition.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "logo_pulse"
    )
    val glow = transition.animateFloat(
        initialValue = 0.28f,
        targetValue = 0.72f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "logo_glow"
    )

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Canvas(
                modifier = Modifier
                    .size(116.dp)
                    .scale(pulse.value)
                    .alpha(glow.value)
            ) {
                val blue = Color(0xFF2684FF)
                val cyan = Color(0xFF22D3EE)
                val stroke = size.minDimension * 0.105f
                val left = size.width * 0.22f
                val top = size.height * 0.16f
                val right = size.width * 0.78f
                val bottom = size.height * 0.84f
                val path = Path().apply {
                    moveTo(left, bottom)
                    lineTo(left, top)
                    lineTo(size.width * 0.60f, top)
                    cubicTo(
                        size.width * 0.91f, top,
                        size.width * 0.91f, size.height * 0.52f,
                        size.width * 0.60f, size.height * 0.52f
                    )
                    lineTo(left, size.height * 0.52f)
                }
                drawPath(
                    path = path,
                    brush = Brush.linearGradient(listOf(cyan, blue)),
                    style = Stroke(width = stroke)
                )
                drawRoundRect(
                    brush = Brush.linearGradient(listOf(cyan, blue)),
                    topLeft = Offset(size.width * 0.62f, size.height * 0.66f),
                    size = Size(size.width * 0.13f, size.height * 0.13f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(5f)
                )
                drawRoundRect(
                    color = Color.White.copy(alpha = 0.85f),
                    topLeft = Offset(size.width * 0.78f, size.height * 0.66f),
                    size = Size(size.width * 0.08f, size.height * 0.08f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f)
                )
            }

            Spacer(Modifier.height(22.dp))
            Text(
                text = "PROPERTA",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 27.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 3.sp
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = slogan,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.68f),
                fontSize = 13.sp,
                letterSpacing = 0.25.sp
            )
        }
    }
}
