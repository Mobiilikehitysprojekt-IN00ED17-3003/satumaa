package fi.antero.satumaa.ui.components.story.create

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import fi.antero.satumaa.R
import fi.antero.satumaa.ui.theme.StorybookPaper

// --- Tietotyypit (Enumit) ---

enum class StoryLength(val labelRes: Int, val apiValue: String) {
    SHORT(R.string.story_length_short, "SHORT"),
    NORMAL(R.string.story_length_normal, "NORMAL"),
    LONG(R.string.story_length_long, "LONG")
}

enum class StoryStyle(val labelRes: Int, val icon: String, val apiValue: String, val color: Color) {
    DEFAULT(R.string.story_style_default, "📜", "DEFAULT", Color(0xFFCFD8DC)),
    EXCITING(R.string.story_style_exciting, "⚡", "EXCITING", Color(0xFFFFB74D)),
    CALMING(R.string.story_style_calming, "😴", "CALMING", Color(0xFF90CAF9)),
    FUNNY(R.string.story_style_funny, "🤪", "FUNNY", Color(0xFFF06292)),
    EDUCATIONAL(R.string.story_style_educational, "🦉", "EDUCATIONAL", Color(0xFFAED581)),
    ANDERSEN(R.string.story_style_andersen, "🦢", "ANDERSEN", Color(0xFFBA68C8)),
    GRIMM(R.string.story_style_grimm, "🏰", "GRIMM", Color(0xFFE57373)),
    JANSSON(R.string.story_style_jansson, "🍃", "JANSSON", Color(0xFF4DB6AC))
}

// --- Komponentit ---

@Composable
fun StoryLengthSelector(
    selectedLength: StoryLength,
    onLengthSelected: (StoryLength) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.story_selector_length_label),
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
                        text = stringResource(length.labelRes),
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
            text = stringResource(R.string.story_selector_style_label),
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
                    label = {
                        Text(
                            stringResource(style.labelRes),
                            color = if (isSelected) Color.Black else StorybookPaper
                        )
                    },
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