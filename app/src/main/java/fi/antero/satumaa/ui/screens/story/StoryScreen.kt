package fi.antero.satumaa.ui.screens.story

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import fi.antero.satumaa.ui.components.AppPageLayout
import fi.antero.satumaa.ui.components.AppTopBar
import fi.antero.satumaa.ui.components.ErrorView
import fi.antero.satumaa.ui.components.story.StoryLength
import fi.antero.satumaa.ui.components.story.StoryLengthSelector
import fi.antero.satumaa.ui.components.story.StoryStyle
import fi.antero.satumaa.ui.components.story.StoryStyleSelector
import fi.antero.satumaa.ui.navigation.RootRoute
import fi.antero.satumaa.ui.theme.LocalAppImages
import fi.antero.satumaa.ui.theme.StorybookPaper
// Varmistetaan, että importit osoittavat oikeaan pakettiin
import fi.antero.satumaa.ui.viewmodel.story.StoryUiState
import fi.antero.satumaa.ui.viewmodel.story.StoryViewModel

@Composable
fun StoryScreen(
    userName: String,
    storyId: String? = null,
    onNavigate: (String) -> Unit,
    viewModel: StoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    var word1 by remember { mutableStateOf("") }
    var word2 by remember { mutableStateOf("") }
    var word3 by remember { mutableStateOf("") }

    var selectedLength by remember { mutableStateOf(StoryLength.NORMAL) }
    var selectedStyle by remember { mutableStateOf(StoryStyle.DEFAULT) }

    LaunchedEffect(storyId) {
        if (storyId != null) {
            viewModel.loadStory(storyId)
        }
    }

    LaunchedEffect(uiState) {
        if (uiState is StoryUiState.Success) {
            scrollState.scrollTo(0)
        }
    }

    AppPageLayout(
        backgroundImageRes = LocalAppImages.current.storyListBackground,
        topBar = {
            AppTopBar(
                overrideTitle = "Sadun Taikaa",
                showBack = true,
                onBack = { onNavigate(RootRoute.Menu.route) },
                onOpenLibrary = { onNavigate(RootRoute.StoryList.route) }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            // Näytetään syöttökentät, jos ei olla lataamassa eikä katsomassa valmista satua
            // Myös Error-tilassa näytetään kentät, jotta käyttäjä voi yrittää uudelleen.
            if (uiState !is StoryUiState.Success && uiState !is StoryUiState.Loading) {

                if (uiState is StoryUiState.Error) {
                    ErrorView(
                        message = (uiState as StoryUiState.Error).message,
                        onRetry = { viewModel.resetState() }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Text(
                    text = "Kirjoita kolme taikasanaa,\nniin $userName saa sadun!",
                    style = MaterialTheme.typography.titleMedium,
                    color = StorybookPaper,
                    textAlign = TextAlign.Center
                )

                MagicWordInput(word1, { word1 = it }, "1. Taikasana", ImeAction.Next)
                MagicWordInput(word2, { word2 = it }, "2. Taikasana", ImeAction.Next)
                MagicWordInput(word3, { word3 = it }, "3. Taikasana", ImeAction.Done) {
                    focusManager.clearFocus()
                    keyboardController?.hide()
                }

                Spacer(modifier = Modifier.height(4.dp))

                StoryLengthSelector(
                    selectedLength = selectedLength,
                    onLengthSelected = { selectedLength = it }
                )

                StoryStyleSelector(
                    selectedStyle = selectedStyle,
                    onStyleSelected = { selectedStyle = it }
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        keyboardController?.hide()
                        focusManager.clearFocus()

                        viewModel.generateStory(
                            userName,
                            word1, word2, word3,
                            selectedLength,
                            selectedStyle
                        )
                    },
                    modifier = Modifier.fillMaxWidth().height(64.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = StorybookPaper,
                        contentColor = Color.Black
                    )
                ) {
                    Icon(Icons.Default.AutoAwesome, null)
                    Spacer(Modifier.width(12.dp))
                    Text("TAIO SATU!", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            }

            if (uiState is StoryUiState.Loading) {
                CircularProgressIndicator(color = StorybookPaper)
                Text("Taikuutta ilmassa...", color = StorybookPaper)
            }

            AnimatedVisibility(uiState is StoryUiState.Success, enter = fadeIn()) {
                if (uiState is StoryUiState.Success) {
                    val story = (uiState as StoryUiState.Success).story
                    Column(
                        modifier = Modifier
                            .background(Color.Black.copy(0.6f), RoundedCornerShape(16.dp))
                            .padding(24.dp)
                    ) {
                        Text(
                            text = story.title,
                            style = MaterialTheme.typography.headlineMedium,
                            color = StorybookPaper,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth(),
                            fontWeight = FontWeight.Bold
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 16.dp),
                            color = StorybookPaper.copy(0.5f)
                        )
                        Text(
                            text = story.content,
                            style = MaterialTheme.typography.bodyLarge.copy(lineHeight = 32.sp),
                            color = StorybookPaper
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        if (story.id.isEmpty()) {
                            // Satu on vasta esikatselussa (Preview)
                            Button(
                                onClick = { viewModel.saveCurrentStory() },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = fi.antero.satumaa.ui.theme.Forest,
                                    contentColor = Color.White
                                )
                            ) {
                                Icon(Icons.Default.AutoAwesome, null)
                                Spacer(Modifier.width(8.dp))
                                Text("Tallenna kirjahyllyyn")
                            }

                            Spacer(Modifier.height(12.dp))

                            OutlinedButton(
                                onClick = {
                                    viewModel.resetState()
                                    word1 = ""; word2 = ""; word3 = ""
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = StorybookPaper)
                            ) {
                                Text("Hylkää ja tee uusi")
                            }
                        } else {
                            // Satu on tallennettu tai ladattu kirjastosta
                            if (storyId == null) {
                                Text(
                                    "✓ Tallennettu kirjahyllyyn",
                                    color = fi.antero.satumaa.ui.theme.Forest,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.align(Alignment.CenterHorizontally)
                                )
                                Spacer(Modifier.height(16.dp))
                            }

                            Button(
                                onClick = {
                                    viewModel.resetState()
                                    word1 = ""; word2 = ""; word3 = ""
                                },
                                modifier = Modifier.align(Alignment.CenterHorizontally),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = StorybookPaper.copy(alpha = 0.2f),
                                    contentColor = StorybookPaper
                                )
                            ) {
                                Text("Tee uusi satu")
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
fun MagicWordInput(value: String, onValueChange: (String) -> Unit, label: String, imeAction: ImeAction, onDone: () -> Unit = {}) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = StorybookPaper.copy(0.8f)) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = imeAction),
        keyboardActions = KeyboardActions(onDone = { onDone() }),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = StorybookPaper,
            unfocusedTextColor = StorybookPaper,
            focusedBorderColor = StorybookPaper,
            unfocusedBorderColor = StorybookPaper.copy(0.5f)
        )
    )
}