package fi.antero.satumaa.ui.components.story.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import fi.antero.satumaa.R
import fi.antero.satumaa.data.model.Story
import fi.antero.satumaa.ui.theme.Ink
import fi.antero.satumaa.ui.theme.InkSoft
import fi.antero.satumaa.ui.theme.StorybookPaper
import java.text.SimpleDateFormat
import java.util.*

/**
 * Kortti, joka esittää yksittäisen sadun tiedot listassa.
 *
 * @param story Näytettävä satu.
 * @param onClick Callback sadun avaamiseen.
 * @param onDelete Callback sadun poistamiseen.
 */
@Composable
fun StoryCard(
    story: Story,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            // Käytetään StorybookPaperia, jotta erottuu taustasta
            containerColor = StorybookPaper.copy(alpha = 0.9f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = story.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = Ink
                )
                Text(
                    text = SimpleDateFormat("d.M.yyyy HH:mm", Locale.getDefault()).format(Date(story.createdAt)),
                    style = MaterialTheme.typography.bodySmall,
                    color = InkSoft
                )
            }
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = stringResource(R.string.story_list_cd_delete),
                    tint = InkSoft
                )
            }
        }
    }
}