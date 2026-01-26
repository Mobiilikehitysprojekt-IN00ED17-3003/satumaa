package fi.antero.satumaa.ui.screens.profile.math.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import fi.antero.satumaa.ui.screens.profile.math.TimeRange
import fi.antero.satumaa.ui.theme.StorybookPaper

@Composable
fun TimeRangeSelector(
    currentRange: TimeRange,
    onRangeSelected: (TimeRange) -> Unit
) {
    // UI-kontrolli: vaihtaa laskennan ja kaavioiden ajanjaksoa (viikko / kuukausi)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TimeRange.values().forEach { range ->
            val isSelected = range == currentRange

            FilterChip(
                selected = isSelected,
                onClick = { onRangeSelected(range) },
                label = {
                    Text(
                        text = range.label,
                        style = MaterialTheme.typography.labelLarge,
                        color = if (isSelected) Color.Black else StorybookPaper
                    )
                },
                modifier = Modifier.padding(horizontal = 4.dp),
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = Color.Black.copy(alpha = 0.3f),
                    selectedContainerColor = StorybookPaper,
                    labelColor = StorybookPaper
                ),
                border = FilterChipDefaults.filterChipBorder(
                    borderColor = StorybookPaper.copy(alpha = 0.5f),
                    selectedBorderColor = Color.Transparent,
                    selected = isSelected,
                    enabled = true
                )
            )
        }
    }
}
