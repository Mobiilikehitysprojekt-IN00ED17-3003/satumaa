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
import fi.antero.satumaa.ui.components.ErrorView
import fi.antero.satumaa.ui.navigation.LetterRoutes
import fi.antero.satumaa.ui.navigation.RootRoute
import fi.antero.satumaa.ui.theme.AppDimensions
import fi.antero.satumaa.ui.theme.LocalAppImages
import fi.antero.satumaa.ui.theme.StorybookPaper
import fi.antero.satumaa.viewmodel.LetterViewModel

// Kirjeen kirjoitus- ja vastausnäkymä
@Composable
fun LetterFlowScreen(
    currentRoute: String?,              // Nykyinen reitti (ei käytössä tässä)
    onNavigate: (String) -> Unit         // Navigointifunktio
) {
    // ViewModel, joka hoitaa kirjeen tilan ja lähetyksen
    val vm: LetterViewModel = viewModel()

    // UI-tila ViewModelista
    val state by vm.uiState.collectAsState()

    // Sivupohja, jossa taustakuva ja yläpalkki
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
                .padding(
                    horizontal = AppDimensions.ScreenPadding,
                    vertical = 24.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Otsikko
            Text(
                text = "Kirje Joulupukille",
                style = MaterialTheme.typography.headlineSmall,
                color = StorybookPaper
            )

            Spacer(Modifier.height(8.dp))

            // Ohjeteksti käyttäjälle
            Text(
                text = "Kirjoita kirjeesi tähän ja lähetä se Korvatunturille.",
                style = MaterialTheme.typography.bodyMedium,
                color = StorybookPaper
            )

            Spacer(Modifier.height(16.dp))

            // Kirjeen tekstikenttä
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

            // Lähetyspainike
            Button(
                onClick = vm::sendLetter,
                enabled = state.text.trim().isNotEmpty() && !state.isSending,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (state.isSending) "Lähetetään..." else "Lähetä")
            }

            // Virheilmoitus jos lähetys epäonnistuu
            state.error?.let { msg ->
                Spacer(Modifier.height(10.dp))
                ErrorView(
                    message = msg,
                    onRetry = { vm.sendLetter() }
                )
            }

            Spacer(Modifier.height(16.dp))

            // Näytetään sisältö kirjeen tilan mukaan
            when (state.status) {

                // Pukki miettii vastausta
                "replying" -> {
                    Text(
                        text = "Pukki miettii vastausta... 🎅",
                        style = MaterialTheme.typography.bodyLarge,
                        color = StorybookPaper
                    )
                }

                // Vastaus on saatu
                "replied" -> {
                    Text(
                        text = "Pukin vastaus:",
                        style = MaterialTheme.typography.titleMedium,
                        color = StorybookPaper
                    )

                    Spacer(Modifier.height(6.dp))

                    // Pukin vastausteksti
                    Text(
                        text = state.replyText.orEmpty(),
                        style = MaterialTheme.typography.bodyLarge,
                        color = StorybookPaper
                    )

                    Spacer(Modifier.height(16.dp))

                    // Siirtyminen kameran AR-etsintään
                    Button(
                        onClick = { onNavigate(LetterRoutes.CAMERA) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Etsi vastaus kameralla (AR)")
                    }
                }

                // Ei vielä tilaa
                null -> Unit
                else -> Unit
            }
        }
    }
}
