package com.sayanox.scanner.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.sayanox.scanner.ui.theme.NeonCyan
import com.sayanox.scanner.ui.theme.NeonMagenta
import com.sayanox.scanner.ui.theme.DarkCard

@Composable
fun NeonCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(12.dp),
    borderColor: Color = NeonCyan.copy(alpha = 0.45f),
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier
            .border(1.dp, borderColor, shape),
        shape = shape,
        color = DarkCard,
        content = content
    )
}

@Composable
fun NeonProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    height: Dp = 6.dp
) {
    val infinite = rememberInfiniteTransition(label = "glow")
    val glow by infinite.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAnim"
    )

    Box(modifier = modifier.fillMaxWidth()) {
        LinearProgressIndicator(
            progress = { progress.coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .drawBehind {
                    drawRect(
                        brush = Brush.horizontalGradient(
                            listOf(
                                NeonCyan.copy(alpha = glow * 0.3f),
                                NeonMagenta.copy(alpha = glow * 0.3f)
                            )
                        )
                    )
                },
            color = NeonCyan,
            trackColor = Color.White.copy(alpha = 0.08f)
        )
    }
}

@Composable
fun SectionHeader(title: String, modifier: Modifier = Modifier) {
    Text(
        text = title.uppercase(),
        style = MaterialTheme.typography.labelLarge,
        color = NeonCyan,
        fontWeight = FontWeight.Bold,
        modifier = modifier.padding(bottom = 8.dp)
    )
}

@Composable
fun StatChip(
    label: String,
    value: String,
    accent: Color = NeonCyan,
    modifier: Modifier = Modifier
) {
    NeonCard(
        modifier = modifier,
        borderColor = accent.copy(alpha = 0.35f)
    ) {
        Box(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
            Text(
                text = "$label\n$value",
                style = MaterialTheme.typography.labelMedium,
                color = accent
            )
        }
    }
}
