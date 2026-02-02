package fi.antero.satumaa.ui.screens.letter

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import fi.antero.satumaa.data.model.Letter
import fi.antero.satumaa.ui.components.AppPageLayout
import fi.antero.satumaa.ui.components.AppTopBar
import fi.antero.satumaa.ui.theme.LocalAppImages
import fi.antero.satumaa.ui.viewmodel.letter.LetterListViewModel
import java.text.SimpleDateFormat
import java.util.*
// TÄRKEÄ: Tämä import tarvitaan, jotta tunnistamme Timestamp-objektin
import com.google.firebase.Timestamp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LetterListScreen(
    onNavigateBack: () -> Unit,
    onLetterClick: (String) -> Unit,
    viewModel: LetterListViewModel = hiltViewModel()
) {
    val letters by viewModel.letters.collectAsState()
    var isRefreshing by remember { mutableStateOf(false) }
    var letterToDelete by remember { mutableStateOf<Letter?>(null) }

    AppPageLayout(
        backgroundImageRes = LocalAppImages.current.storyListBackground,
        topBar = {
            AppTopBar(
                overrideTitle = "Kirjeposti",
                showBack = true,
                onBack = onNavigateBack
            )
        }
    ) { padding ->

        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = {
                isRefreshing = true
                viewModel.refreshLetters()
                android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                    isRefreshing = false
                }, 1000)
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (letters.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        color = Color.Black.copy(alpha = 0.6f),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text(
                            "Postilaatikko on tyhjä.",
                            color = Color.White,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(letters, key = { it.id }) { letter ->
                        LetterCard(
                            letter = letter,
                            onClick = { onLetterClick(letter.id) },
                            onDelete = { letterToDelete = letter }
                        )
                    }
                }
            }
        }

        if (letterToDelete != null) {
            AlertDialog(
                onDismissRequest = { letterToDelete = null },
                title = { Text("Poista kirje?") },
                text = { Text("Haluatko varmasti poistaa tämän kirjeen? Sitä ei voi palauttaa.") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            letterToDelete?.let { viewModel.deleteLetter(it.id) }
                            letterToDelete = null
                        },
                        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Poista")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { letterToDelete = null }) {
                        Text("Peruuta")
                    }
                }
            )
        }
    }
}

@Composable
fun LetterCard(
    letter: Letter,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val isReplied = letter.status == "replied"
    val statusColor = if (isReplied) Color(0xFF2E6B5B) else Color(0xFFB4573A)

    // PÄIVÄMÄÄRÄN KÄSITTELY ---

    val dateString = remember(letter.createdAt) {
        try {
            // "Huijataan" Kotlinia käsittelemään tätä geneerisenä objektina hetken
            val raw: Any? = letter.createdAt

            val dateToFormat: Date = when (raw) {
                is Timestamp -> raw.toDate() // Jos Firestore palautti Timestampin -> muunna Dateksi
                is Date -> raw               // Jos se on jo Date -> käytä sellaisenaan
                else -> Date()               // Jos null tai tuntematon -> käytä nykyhetkeä
            }

            SimpleDateFormat("d.M.yyyy HH:mm", Locale.getDefault()).format(dateToFormat)
        } catch (e: Exception) {

            ""
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFF3E6).copy(alpha = 0.95f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .height(IntrinsicSize.Min)
        ) {
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .fillMaxHeight()
                    .background(statusColor, MaterialTheme.shapes.small)
            )

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = dateString,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = letter.letterText,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = Color.Black.copy(alpha = 0.8f)
                )
            }

            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Poista",
                    tint = Color.Gray
                )
            }
        }
    }
}