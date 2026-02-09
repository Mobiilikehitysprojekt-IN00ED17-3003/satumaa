package fi.antero.satumaa.ui.components.letter.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.google.firebase.Timestamp
import fi.antero.satumaa.R
import fi.antero.satumaa.data.model.Letter
import fi.antero.satumaa.ui.theme.Forest
import fi.antero.satumaa.ui.theme.Ink
import fi.antero.satumaa.ui.theme.InkSoft
import fi.antero.satumaa.ui.theme.StorybookPaper
import fi.antero.satumaa.ui.theme.Terracotta
import java.text.SimpleDateFormat
import java.util.*

/**
 * LetterCard esittää yhden kirjeen tiivistetyt tiedot listassa.
 *
 * @param letter Näytettävä kirje-olio.
 * @param onClick Toiminto, kun käyttäjä napauttaa korttia (avaa kirjeen).
 * @param onDelete Toiminto, kun käyttäjä painaa roskakoria.
 */
@Composable
fun LetterCard(
    letter: Letter,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    // Määritetään visuaalinen indikaattori kirjeen tilalle.
    val isReplied = letter.status == "replied"
    val statusColor = if (isReplied) Forest else Terracotta

    // Päivämäärän muotoilu.
    val dateString = remember(letter.createdAt) {
        try {
            val raw: Any? = letter.createdAt
            val dateToFormat: Date = when (raw) {
                is Timestamp -> raw.toDate() // Firestoren tyyppi
                is Date -> raw               // Javan vakiotyyppi
                else -> Date()               // Fallback: nykyhetki
            }
            // Muotoillaan suomalaiseen tyyliin "p.k.vvvv klo:mm"
            SimpleDateFormat("d.M.yyyy HH:mm", Locale.getDefault()).format(dateToFormat)
        } catch (e: Exception) {
            ""
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }, // Koko kortti on klikattava
        colors = CardDefaults.cardColors(
            containerColor = StorybookPaper.copy(alpha = 0.95f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .height(IntrinsicSize.Min) // Pakottaa rivin korkeuden sisällön mukaan
        ) {
            // Vasemman reunan väripalkki (statusindikaattori)
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .fillMaxHeight()
                    .background(statusColor, MaterialTheme.shapes.small)
            )

            Spacer(Modifier.width(12.dp))

            // Kortin tekstisisältö
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = dateString,
                    style = MaterialTheme.typography.labelSmall,
                    color = InkSoft
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = letter.letterText,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = Ink.copy(alpha = 0.8f)
                )
            }

            // Poisto-painike
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = stringResource(R.string.letter_list_cd_delete),
                    tint = InkSoft
                )
            }
        }
    }
}