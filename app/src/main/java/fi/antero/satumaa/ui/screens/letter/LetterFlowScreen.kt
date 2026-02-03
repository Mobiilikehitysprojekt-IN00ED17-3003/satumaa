package fi.antero.satumaa.ui.screens.letter

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
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
import fi.antero.satumaa.util.MathChallenge
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

    if (state.isMathDialogVisible && state.mathChallenge != null) {
        MathChallengeDialog(
            challenge = state.mathChallenge!!,
            isError = state.mathError,
            onDismiss = { vm.dismissMathChallenge() },
            onSubmit = { answer -> vm.submitMathAnswer(answer) }
        )
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

            val isWaiting = state.status == "replying"
            val isReadyOrViewing = state.status == "replied" || (state.isViewMode && state.sentText.isNotEmpty()) || state.isOpened
            val isWritingNew = !isWaiting && !isReadyOrViewing

            if (isWritingNew) {
                Column(
                    modifier = Modifier.fillMaxWidth().background(Color.Black.copy(0.5f), RoundedCornerShape(16.dp)).padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "Kirje Joulupukille", style = MaterialTheme.typography.headlineSmall, color = StorybookPaper)
                    Spacer(Modifier.height(8.dp))
                    Text(text = "Hei $userName! Kirjoita kirjeesi tähän...", style = MaterialTheme.typography.bodyMedium, color = StorybookPaper.copy(alpha = 0.9f))
                }
                Spacer(Modifier.height(24.dp))
                Column(modifier = Modifier.fillMaxWidth().background(Color.Black.copy(0.5f), RoundedCornerShape(16.dp)).padding(16.dp)) {
                    val maxChar = 200
                    OutlinedTextField(
                        value = state.text,
                        onValueChange = { if (it.length <= maxChar) vm.onTextChange(it) },
                        label = { Text("Rakas Joulupukki...", color = StorybookPaper.copy(0.7f)) },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 6,
                        enabled = !state.isSending,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.Black.copy(alpha = 0.3f),
                            unfocusedContainerColor = Color.Black.copy(alpha = 0.3f),
                            focusedTextColor = StorybookPaper,
                            unfocusedTextColor = StorybookPaper,
                            cursorColor = StorybookPaper,
                            focusedBorderColor = StorybookPaper
                        )
                    )
                    Spacer(Modifier.height(16.dp))
                    Button(
                        onClick = { vm.sendLetter(userName, onSuccess = { id -> onNavigate("${RootRoute.LetterMap.route}/$id") }) },
                        enabled = state.text.trim().isNotEmpty() && !state.isSending,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = StorybookPaper, contentColor = Color.Black)
                    ) { Text(if (state.isSending) "Lähetetään..." else "Lähetä") }
                }
            }

            state.error?.let { msg ->
                Spacer(Modifier.height(16.dp))
                ErrorView(message = msg, onRetry = { vm.sendLetter(userName, onSuccess = { id -> onNavigate("${RootRoute.LetterMap.route}/$id") }) })
            }

            if (isWaiting) {
                Spacer(Modifier.height(24.dp))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = StorybookPaper)
                    Spacer(Modifier.height(16.dp))
                    Text(text = "Pukki miettii vastausta... 🎅", style = MaterialTheme.typography.bodyLarge, color = StorybookPaper)
                }
            }

            if (isReadyOrViewing) {
                Column(modifier = Modifier.fillMaxWidth().background(Color.Black.copy(0.4f), RoundedCornerShape(16.dp)).padding(20.dp)) {
                    Text(text = "Sinun kirjeesi:", style = MaterialTheme.typography.labelLarge, color = StorybookPaper.copy(alpha = 0.7f))
                    Spacer(Modifier.height(8.dp))
                    Text(text = state.sentText, style = MaterialTheme.typography.bodyMedium.copy(fontStyle = FontStyle.Italic), color = StorybookPaper.copy(alpha = 0.8f))
                }
                Spacer(Modifier.height(16.dp))

                if (state.isOpened) {
                    Column(modifier = Modifier.fillMaxWidth().background(Color.Black.copy(0.6f), RoundedCornerShape(16.dp)).padding(24.dp)) {
                        Text(text = "Pukin vastaus:", style = MaterialTheme.typography.titleMedium, color = StorybookPaper)
                        Spacer(Modifier.height(12.dp))

                        // UUSI: LATAUSINDIKAATTORI TEKSTILLE
                        if (state.replyText.isNullOrBlank()) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = StorybookPaper, strokeWidth = 2.dp)
                                Spacer(Modifier.width(12.dp))
                                Text("Avataan kirjettä...", color = StorybookPaper)
                            }
                        } else {
                            Text(text = state.replyText!!, style = MaterialTheme.typography.bodyLarge.copy(lineHeight = 24.sp), color = StorybookPaper)
                        }
                    }
                } else if (!state.replyText.isNullOrEmpty() || state.status == "replied") {
                    Column(
                        modifier = Modifier.fillMaxWidth().background(StorybookPaper.copy(alpha = 0.1f), RoundedCornerShape(16.dp)).padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "📩", style = MaterialTheme.typography.displayLarge)
                        Spacer(Modifier.height(16.dp))
                        Text(text = "Sinulle on saapunut vastaus!", style = MaterialTheme.typography.titleMedium, color = StorybookPaper)
                        Spacer(Modifier.height(32.dp))
                        Button(onClick = {
                            val id = state.currentLetterId
                            if (id != null) onNavigate("${LetterRoutes.CAMERA}/$id") else onNavigate(LetterRoutes.CAMERA)
                        }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = StorybookPaper, contentColor = Color.Black)) { Text("Etsi ja avaa kirje (AR)") }
                        Spacer(Modifier.height(12.dp))
                        Text(text = "Tai", color = StorybookPaper.copy(alpha = 0.9f), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(12.dp))
                        Button(onClick = { vm.showMathChallenge() }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = StorybookPaper, contentColor = Color.Black)) { Text("Avaa ratkaisemalla tehtävä 🧮") }
                    }
                } else if (state.isViewMode) {
                    Text(text = "Pukki lukee vielä kirjettäsi...", color = StorybookPaper, style = MaterialTheme.typography.bodyLarge)
                }

                Spacer(Modifier.height(24.dp))
                OutlinedButton(onClick = { vm.resetToNewLetter(); onNavigate(RootRoute.Letter.route) }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.outlinedButtonColors(contentColor = StorybookPaper), border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(StorybookPaper))) { Text("Kirjoita uusi kirje") }
                Spacer(Modifier.height(32.dp))
            }
        }
    }
}

// ... MathChallengeDialog pysyy samana ...
@Composable
fun MathChallengeDialog(
    challenge: MathChallenge,
    isError: Boolean,
    onDismiss: () -> Unit,
    onSubmit: (String) -> Unit
) {
    var answer by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = StorybookPaper,
        titleContentColor = Color(0xFF1B1B1F),
        textContentColor = Color(0xFF1B1B1F),
        title = { Text("Pukin pulmatehtävä", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Text("Avaa kirje ratkaisemalla tämä lasku:", fontSize = 16.sp)
                Spacer(Modifier.height(16.dp))
                Text(text = challenge.question, style = MaterialTheme.typography.displaySmall, modifier = Modifier.align(Alignment.CenterHorizontally), fontWeight = FontWeight.Bold, color = Color(0xFF2E6B5B))
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(
                    value = answer,
                    onValueChange = { if (it.length <= 3 && it.all { char -> char.isDigit() }) answer = it },
                    label = { Text("Vastaus") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = isError,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF2E6B5B),
                        focusedLabelColor = Color(0xFF2E6B5B),
                        cursorColor = Color(0xFF2E6B5B),
                        errorCursorColor = MaterialTheme.colorScheme.error
                    )
                )
            }
        },
        confirmButton = {
            Button(onClick = { onSubmit(answer) }, enabled = answer.isNotEmpty(), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E6B5B), contentColor = Color.White)) { Text("Avaa kirje") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFF1B1B1F))) { Text("Peruuta") }
        }
    )
}
