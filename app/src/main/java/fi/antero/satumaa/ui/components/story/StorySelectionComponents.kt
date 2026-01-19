package fi.antero.satumaa.ui.components.story

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import fi.antero.satumaa.ui.theme.StorybookPaper

// --- Tietotyypit ---

enum class StoryLength(val label: String, val apiValue: String) {
    SHORT("Lyhyt", "SHORT"),
    NORMAL("Normaali", "NORMAL"),
    LONG("Pitkä", "LONG")
}

enum class StoryStyle(val label: String, val icon: String, val apiValue: String, val color: Color) {
    DEFAULT("Perus", "📜", "DEFAULT", Color(0xFFCFD8DC)), // Harmaa/Neutraali
    EXCITING("Jännittävä", "⚡", "EXCITING", Color(0xFFFFB74D)), // Oranssi
    CALMING("Rauhoittava", "😴", "CALMING", Color(0xFF90CAF9)), // Vaaleansininen
    FUNNY("Hassu", "🤪", "FUNNY", Color(0xFFF06292)), // Pinkki
    EDUCATIONAL("Opettavainen", "🦉", "EDUCATIONAL", Color(0xFFAED581)), // Vihreä
    ANDERSEN("Andersen", "🦢", "ANDERSEN", Color(0xFFBA68C8)), // Purppura
    GRIMM("Grimm", "🏰", "GRIMM", Color(0xFFE57373)), // Punainen
    JANSSON("Muumimainen", "🍃", "JANSSON", Color(0xFF4DB6AC)) // Vihreä
}

// --- Komponentit ---

@Composable
fun StoryLengthSelector(
    selectedLength: StoryLength,
    onLengthSelected: (StoryLength) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Sadun pituus",
            style = MaterialTheme.typography.labelMedium,
            color = StorybookPaper.copy(alpha = 0.8f),
            modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color.Black.copy(alpha = 0.3f))
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StoryLength.values().forEach { length ->
                val isSelected = length == selectedLength
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) StorybookPaper else Color.Transparent)
                        .clickable { onLengthSelected(length) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = length.label,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) Color.Black else StorybookPaper
                    )
                }
            }
        }
    }
}

@Composable
fun StoryStyleSelector(
    selectedStyle: StoryStyle,
    onStyleSelected: (StoryStyle) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Sadun tyyli",
            style = MaterialTheme.typography.labelMedium,
            color = StorybookPaper.copy(alpha = 0.8f),
            modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
        )
        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StoryStyle.values().forEach { style ->
                val isSelected = style == selectedStyle
                FilterChip(
                    selected = isSelected,
                    onClick = { onStyleSelected(style) },
                    label = { Text(style.label, color = if (isSelected) Color.Black else StorybookPaper) },
                    leadingIcon = { Text(style.icon) },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = Color.Black.copy(alpha = 0.3f),
                        selectedContainerColor = style.color,
                        labelColor = StorybookPaper
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        borderColor = StorybookPaper.copy(alpha = 0.3f),
                        selectedBorderColor = Color.Transparent,
                        enabled = true,
                        selected = isSelected
                    ),
                    shape = CircleShape
                )
            }
        }
    }
}