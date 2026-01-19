package fi.antero.satumaa.ui.screens.letter

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import fi.antero.satumaa.ui.components.AppPageLayout
import fi.antero.satumaa.ui.components.AppTopBar
import fi.antero.satumaa.ui.navigation.RootRoute
import fi.antero.satumaa.ui.theme.AppDimensions
import fi.antero.satumaa.ui.theme.LocalAppImages
import fi.antero.satumaa.ui.theme.StorybookPaper
import fi.antero.satumaa.viewmodel.LetterViewModel

@Composable
fun LetterFlowScreen(
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    val vm: LetterViewModel = viewModel()
    val state by vm.uiState.collectAsState()

    AppPageLayout(
        backgroundImageRes = LocalAppImages.current.letterBackground,
        topBar = {
            AppTopBar(
                showBack = true,
                onBack = { onNavigate(RootRoute.Menu.route) },
                onOpenProfile = { onNavigate(RootRoute.Profile.route) }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = AppDimensions.ScreenPadding, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Kirje Joulupukille",
                style = MaterialTheme.typography.headlineSmall,
                color = StorybookPaper
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Kirjoita kirjeesi tähän ja lähetä se Korvatunturille.",
                style = MaterialTheme.typography.bodyMedium,
                color = StorybookPaper
            )

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = state.text,
                onValueChange = vm::onTextChange,
                label = { Text("Kirjoita kirje") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 6,
                enabled = !state.isSending,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White.copy(alpha = 0.95f),
                    unfocusedContainerColor = Color.White.copy(alpha = 0.90f),
                    disabledContainerColor = Color.White.copy(alpha = 0.60f),
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    disabledTextColor = Color.DarkGray,
                    focusedLabelColor = Color.DarkGray,
                    unfocusedLabelColor = Color.DarkGray,
                    cursorColor = Color.Black
                )
            )

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = vm::sendLetter,
                enabled = state.text.trim().isNotEmpty() && !state.isSending,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (state.isSending) "Lähetetään..." else "Lähetä")
            }

            state.error?.let {
                Spacer(Modifier.height(10.dp))
                Text(it, color = MaterialTheme.colorScheme.error)
            }

            Spacer(Modifier.height(16.dp))

            when (state.status) {
                "replying" -> Text(
                    text = "Pukki miettii vastausta... 🎅",
                    style = MaterialTheme.typography.bodyLarge,
                    color = StorybookPaper
                )

                "replied" -> {
                    Text(
                        text = "Pukin vastaus:",
                        style = MaterialTheme.typography.titleMedium,
                        color = StorybookPaper
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = state.replyText.orEmpty(),
                        style = MaterialTheme.typography.bodyLarge,
                        color = StorybookPaper
                    )
                }

                "error" -> Text(
                    text = "Tapahtui virhe. Yritä uudelleen.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.error
                )

                null -> Unit
            }
        }
    }
}
