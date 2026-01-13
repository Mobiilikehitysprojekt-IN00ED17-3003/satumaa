// app/src/main/java/fi/antero/satumaa/ui/components/LoadingView.kt
package fi.antero.satumaa.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fi.antero.satumaa.ui.theme.StorybookPaper

@Composable
fun LoadingView(
    modifier: Modifier = Modifier,
    text: String = "Taikuutta ladataan..."
) {

    val infiniteTransition = rememberInfiniteTransition(label = "loading")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        CircularProgressIndicator(
            modifier = Modifier
                .size(64.dp)
                .rotate(rotation),
            color = StorybookPaper,
            strokeWidth = 4.dp
        )

        Spacer(Modifier.height(24.dp))

        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 18.sp,
                letterSpacing = 1.sp
            ),
            color = StorybookPaper
        )
    }
}