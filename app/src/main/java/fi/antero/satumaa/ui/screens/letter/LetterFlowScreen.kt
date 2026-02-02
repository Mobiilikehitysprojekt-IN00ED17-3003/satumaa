package fi.antero.satumaa.ui.screens.letter

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import fi.antero.satumaa.ui.components.AppPageLayout
import fi.antero.satumaa.ui.components.AppTopBar
import fi.antero.satumaa.ui.components.ErrorView
import fi.antero.satumaa.ui.navigation.LetterRoutes
import fi.antero.satumaa.ui.navigation.RootRoute
import fi.antero.satumaa.ui.theme.AppDimensions
import fi.antero.satumaa.ui.theme.LocalAppImages
import fi.antero.satumaa.ui.theme.StorybookPaper
import fi.antero.satumaa.viewmodel.letter.LetterViewModel

@Composable
fun LetterFlowScreen(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    userName: String,
    letterId: String? = null,
    vm: LetterViewModel = hiltViewModel()
) {
    val state by vm.uiState.collectAsState()
    val scrollState = rememberScrollState()

    var isLoadingInitial by remember { mutableStateOf(true) }

    LaunchedEffect(letterId) {
        isLoadingInitial = true
        if (letterId != null) {
            vm.loadLetter(letterId)
        } else {
            val isActiveProcess = state.status == "replying" || (state.status == "replied" && !state.isViewMode)
            if (!isActiveProcess) {
                vm.resetToNewLetter()
            }
        }
        isLoadingInitial = false
    }

    AppPageLayout(
        backgroundImageRes = LocalAppImages.current.letterBackground,
        topBar = {
            AppTopBar(
                showBack = true,
                onBack = { onNavigate(RootRoute.Menu.route) },
                onOpenProfile = { onNavigate(RootRoute.Profile.route) },
                onOpenLibrary = { onNavigate(RootRoute.LetterList.route) },
                libraryLabel = "Omat kirjeet"
            )
        }
    ) { padding ->

        if (isLoadingInitial) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = StorybookPaper)
            }
            return@AppPageLayout
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
                .padding(
                    horizontal = AppDimensions.ScreenPadding,
                    vertical = 24.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            val isWritingNew = !state.isViewMode && state.status != "replied" && state.status != "replying"
            val isWaiting = state.status == "replying"

            // Näytetään vastausnäkymä jos vastattu TAI katsellaan vanhaa TAI kirje on avattu
            val isReadyOrViewing = state.status == "replied" || (state.isViewMode && state.sentText.isNotEmpty()) || state.isOpened

            // --- VAIHE 1: KIRJOITA UUSI KIRJE ---
            if (isWritingNew) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Black.copy(0.5f), RoundedCornerShape(16.dp))
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Kirje Joulupukille",
                        style = MaterialTheme.typography.headlineSmall,
                        color = StorybookPaper
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Hei $userName! Kirjoita kirjeesi tähän ja lähetä se Korvatunturille.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = StorybookPaper.copy(alpha = 0.9f)
                    )
                }

                Spacer(Modifier.height(24.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Black.copy(0.5f), RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    val maxChar = 200
                    val charsRemaining = maxChar - state.text.length

                    OutlinedTextField(
                        value = state.text,
                        onValueChange = { newText ->
                            if (newText.length <= maxChar) {
                                vm.onTextChange(newText)
                            }
                        },
                        label = { Text("Rakas Joulupukki...", color = StorybookPaper.copy(0.7f)) },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 6,
                        enabled = !state.isSending,
                        shape = RoundedCornerShape(12.dp),
                        supportingText = {
                            Text(
                                text = "$charsRemaining merkkiä jäljellä",
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = androidx.compose.ui.text.style.TextAlign.End,
                                color = if (charsRemaining < 20) MaterialTheme.colorScheme.error else StorybookPaper.copy(0.7f),
                                style = MaterialTheme.typography.labelSmall
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.Black.copy(alpha = 0.3f),
                            unfocusedContainerColor = Color.Black.copy(alpha = 0.3f),
                            disabledContainerColor = Color.Black.copy(alpha = 0.2f),
                            focusedTextColor = StorybookPaper,
                            unfocusedTextColor = StorybookPaper,
                            focusedBorderColor = StorybookPaper,
                            unfocusedBorderColor = StorybookPaper.copy(0.5f),
                            cursorColor = StorybookPaper
                        )
                    )

                    Spacer(Modifier.height(16.dp))

                    Button(
                        onClick = {
                            vm.sendLetter(userName, onSuccess = {
                                onNavigate(RootRoute.LetterMap.route)
                            })
                        },
                        enabled = state.text.trim().isNotEmpty() && !state.isSending,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = StorybookPaper,
                            contentColor = Color.Black
                        )
                    ) {
                        Text(if (state.isSending) "Lähetetään..." else "Lähetä")
                    }
                }
            }

            state.error?.let { msg ->
                Spacer(Modifier.height(16.dp))
                ErrorView(
                    message = msg,
                    onRetry = {
                        vm.sendLetter(userName, onSuccess = {
                            onNavigate(RootRoute.LetterMap.route)
                        })
                    }
                )
            }

            // --- VAIHE 2: ODOTETAAN VASTAUSTA ---
            if (isWaiting) {
                Spacer(Modifier.height(24.dp))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = StorybookPaper)
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = "Pukki miettii vastausta... 🎅",
                        style = MaterialTheme.typography.bodyLarge,
                        color = StorybookPaper
                    )
                }
            }

            // --- VAIHE 3: VASTAUS SAAPUI / VANHAN KIRJEEN KATSELU ---
            if (isReadyOrViewing) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Black.copy(0.4f), RoundedCornerShape(16.dp))
                        .padding(20.dp)
                ) {
                    Text(
                        text = "Sinun kirjeesi:",
                        style = MaterialTheme.typography.labelLarge,
                        color = StorybookPaper.copy(alpha = 0.7f)
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = state.sentText,
                        style = MaterialTheme.typography.bodyMedium.copy(fontStyle = FontStyle.Italic),
                        color = StorybookPaper.copy(alpha = 0.8f)
                    )
                }

                Spacer(Modifier.height(16.dp))

                // Tarkistetaan ENSIN onko avattu.
                if (state.isOpened) {
                    // A) KIRJE ON LÖYDETTY JA AVATTU
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.Black.copy(0.6f), RoundedCornerShape(16.dp))
                            .padding(24.dp)
                    ) {
                        Text(
                            text = "Pukin vastaus:",
                            style = MaterialTheme.typography.titleMedium,
                            color = StorybookPaper
                        )

                        Spacer(Modifier.height(12.dp))

                        Text(
                            text = state.replyText ?: "Kiitos kirjeestäsi! (Vastaustekstiä ei voitu ladata)",
                            style = MaterialTheme.typography.bodyLarge.copy(lineHeight = 24.sp),
                            color = StorybookPaper
                        )
                    }
                } else if (!state.replyText.isNullOrEmpty() || state.status == "replied") {
                    // B) KIRJE ON SAAPUNUT MUTTA SULJETTU (DUMMY)
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(StorybookPaper.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "📩",
                            style = MaterialTheme.typography.displayLarge
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = "Sinulle on saapunut vastaus!",
                            style = MaterialTheme.typography.titleMedium,
                            color = StorybookPaper
                        )
                        Text(
                            text = "Se on piilotettu huoneeseesi.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = StorybookPaper.copy(alpha = 0.8f)
                        )
                        Spacer(Modifier.height(24.dp))

                        Button(
                            onClick = {
                                val id = state.currentLetterId
                                if (id != null) {
                                    onNavigate("${LetterRoutes.CAMERA}/$id")
                                } else {
                                    onNavigate(LetterRoutes.CAMERA)
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = StorybookPaper,
                                contentColor = Color.Black
                            )
                        ) {
                            Text("Etsi ja avaa kirje (AR)")
                        }
                    }

                } else if (state.isViewMode) {
                    Text(
                        text = "Pukki lukee vielä kirjettäsi...",
                        color = StorybookPaper,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                Spacer(Modifier.height(24.dp))
                OutlinedButton(
                    onClick = {
                        vm.resetToNewLetter()
                        onNavigate(RootRoute.Letter.route)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = StorybookPaper),
                    border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(StorybookPaper))
                ) {
                    Text("Kirjoita uusi kirje")
                }

                Spacer(Modifier.height(32.dp))
            }
        }
    }
}