package fi.antero.satumaa.ui.screens.story

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import fi.antero.satumaa.R
import fi.antero.satumaa.ui.components.AppPageLayout
import fi.antero.satumaa.ui.components.AppTopBar
import fi.antero.satumaa.ui.components.ErrorView
// TÄRKEÄÄ: Nämä importit osoittavat nyt uuteen 'create'-pakettiin
import fi.antero.satumaa.ui.components.story.create.MagicWordInput
import fi.antero.satumaa.ui.components.story.create.StoryBackground
import fi.antero.satumaa.ui.components.story.create.StoryLength
import fi.antero.satumaa.ui.components.story.create.StoryLengthSelector
import fi.antero.satumaa.ui.components.story.create.StoryResultView
import fi.antero.satumaa.ui.components.story.create.StoryStyle
import fi.antero.satumaa.ui.components.story.create.StoryStyleSelector
import fi.antero.satumaa.ui.navigation.RootRoute
import fi.antero.satumaa.ui.theme.StorybookPaper
import fi.antero.satumaa.ui.viewmodel.story.StoryUiState
import fi.antero.satumaa.ui.viewmodel.story.StoryViewModel

/**
 * StoryScreen on näkymä, jossa luodaan uusia satuja tekoälyn avulla tai luetaan vanhoja.
 *
 * Toiminnallisuudet:
 * 1. Taikasanojen syöttö (3 kpl).
 * 2. Sadun asetusten valinta (Pituus ja Tyyli).
 * 3. Sadun generointi (kutsuu ViewModelia).
 * 4. Valmiin sadun esittäminen ja tallennusmahdollisuus.
 */
@Composable
fun StoryScreen(
    userName: String,
    storyId: String? = null,
    onNavigate: (String) -> Unit,
    viewModel: StoryViewModel = hiltViewModel()
) {
    // UI-tila ViewModelista (Loading, Success, Error)
    val uiState by viewModel.uiState.collectAsState()

    // UI-hallinta
    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    // Paikalliset tilat syötteille
    var word1 by remember { mutableStateOf("") }
    var word2 by remember { mutableStateOf("") }
    var word3 by remember { mutableStateOf("") }

    // Oletusvalinnat
    var selectedLength by remember { mutableStateOf(StoryLength.NORMAL) }
    var selectedStyle by remember { mutableStateOf(StoryStyle.DEFAULT) }

    // Ladataan vanha satu, jos ID on annettu (tullaan esim. listasta)
    LaunchedEffect(storyId) {
        if (storyId != null) {
            viewModel.loadStory(storyId)
        }
    }

    // Scrollataan automaattisesti ylös, kun satu valmistuu (Success-tila)
    LaunchedEffect(uiState) {
        if (uiState is StoryUiState.Success) {
            scrollState.scrollTo(0)
        }
    }

    // Päälayout
    AppPageLayout(
        // Käytetään omaa taustakomponenttia
        background = { StoryBackground() },
        topBar = {
            AppTopBar(
                overrideTitle = stringResource(R.string.story_create_title),
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

            // --- TILA 1: SYÖTTÖNÄKYMÄ ---
            // Näytetään syöttökentät, jos ei olla lataamassa eikä katsomassa valmista satua.
            // Näytetään myös virhetilanteessa, jotta käyttäjä voi yrittää uudelleen.
            if (uiState !is StoryUiState.Success && uiState !is StoryUiState.Loading) {

                // Virheilmoitus (esim. verkkovirhe)
                if (uiState is StoryUiState.Error) {
                    ErrorView(
                        message = (uiState as StoryUiState.Error).message,
                        onRetry = { viewModel.resetState() }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Ohjeteksti ("Kirjoita kolme taikasanaa...")
                Text(
                    text = stringResource(R.string.story_create_instruction, userName),
                    style = MaterialTheme.typography.titleMedium,
                    color = StorybookPaper,
                    textAlign = TextAlign.Center
                )

                // Taikasanat (käytetään erillisiä komponentteja)
                MagicWordInput(
                    value = word1,
                    onValueChange = { word1 = it },
                    label = stringResource(R.string.story_create_word_1),
                    imeAction = ImeAction.Next
                )
                MagicWordInput(
                    value = word2,
                    onValueChange = { word2 = it },
                    label = stringResource(R.string.story_create_word_2),
                    imeAction = ImeAction.Next
                )
                MagicWordInput(
                    value = word3,
                    onValueChange = { word3 = it },
                    label = stringResource(R.string.story_create_word_3),
                    imeAction = ImeAction.Done,
                    onDone = {
                        focusManager.clearFocus()
                        keyboardController?.hide()
                    }
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Valitsimet (Pituus ja Tyyli)
                // Nämä on tuotu 'create'-paketista
                StoryLengthSelector(
                    selectedLength = selectedLength,
                    onLengthSelected = { selectedLength = it }
                )

                StoryStyleSelector(
                    selectedStyle = selectedStyle,
                    onStyleSelected = { selectedStyle = it }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Generointipainike
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = StorybookPaper,
                        contentColor = Color.Black
                    )
                ) {
                    Icon(Icons.Default.AutoAwesome, null)
                    Spacer(Modifier.width(12.dp))
                    Text(
                        text = stringResource(R.string.story_create_button_magic),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
            }

            // --- TILA 2: LATAUS ---
            if (uiState is StoryUiState.Loading) {
                CircularProgressIndicator(color = StorybookPaper)
                Text(
                    text = stringResource(R.string.story_create_loading),
                    color = StorybookPaper
                )
            }

            // --- TILA 3: VALMIS SATU ---
            // Animoidaan sadun ilmestyminen
            AnimatedVisibility(uiState is StoryUiState.Success, enter = fadeIn()) {
                if (uiState is StoryUiState.Success) {
                    val story = (uiState as StoryUiState.Success).story
                    val isSaved = story.id.isNotEmpty() // Jos ID löytyy, se on tallennettu tietokantaan

                    // Näytetään tuloskortti
                    StoryResultView(
                        story = story,
                        isSaved = isSaved,
                        onSave = { viewModel.saveCurrentStory() },
                        onDiscard = {
                            // "Hylkää" tai "Tee uusi" tyhjentää tilan ja palaa syöttönäkymään
                            viewModel.resetState()
                            word1 = ""; word2 = ""; word3 = ""
                        }
                    )
                }
            }
            // Tyhjä tila scrollauksen loppuun, jotta sisältö ei jää piiloon
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}