package fi.antero.satumaa.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import fi.antero.satumaa.ui.theme.StorybookPaper

@Composable
fun ModernMenuCard(
    title: String,
    description: String,
    icon: ImageVector,
    accentColor: Color,
    offsetY: Float = 0f,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(28.dp)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
            .offset(y = offsetY.dp)
            .shadow(15.dp, shape)
            .clip(shape)
            .clickable(onClick = onClick)
            .border(1.dp, Color.White.copy(alpha = 0.2f), shape),
        color = Color.White.copy(alpha = 0.15f),
        shape = shape
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Ikoniosa
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(accentColor.copy(alpha = 0.25f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = StorybookPaper,
                    modifier = Modifier.size(30.dp)
                )
            }

            Spacer(Modifier.width(20.dp))

            // Tekstiosa
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = StorybookPaper
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = StorybookPaper.copy(alpha = 0.8f)
                )
            }
        }
    }
}