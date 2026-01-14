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
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import fi.antero.satumaa.ui.components.AppPageLayout
import fi.antero.satumaa.ui.components.AppTopBar
import fi.antero.satumaa.ui.navigation.RootRoute
import fi.antero.satumaa.ui.theme.LocalAppImages
import fi.antero.satumaa.ui.theme.StorybookPaper
import fi.antero.satumaa.ui.viewmodel.story.StoryScreenState
import fi.antero.satumaa.ui.viewmodel.story.StoryViewModel

@Composable
fun StoryScreen(
    userName: String,
    onNavigate: (String) -> Unit,
    viewModel: StoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current

    // Kolme taikasanaa
    var word1 by remember { mutableStateOf("") }
    var word2 by remember { mutableStateOf("") }
    var word3 by remember { mutableStateOf("") }

    // Rullataan alas kun satu valmistuu
    LaunchedEffect(uiState) {
        if (uiState is StoryScreenState.Success) {
            scrollState.animateScrollTo(scrollState.maxValue)
        }
    }

    AppPageLayout(
        backgroundImageRes = LocalAppImages.current.storyListBackground, // Sama tausta koko ajan
        topBar = {
            AppTopBar(
                overrideTitle = "Sadun Taikaa",
                showBack = true,
                onBack = { onNavigate(RootRoute.Menu.route) }
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

            // --- OHJE ---
            if (uiState !is StoryScreenState.Success) {
                Text(
                    text = "Kirjoita kolme taikasanaa,\nniin $userName saa sadun!",
                    style = MaterialTheme.typography.titleMedium,
                    color = StorybookPaper,
                    textAlign = TextAlign.Center
                )
            }

            // --- SYÖTTEET ---
            // Näytetään syötteet vain jos ei olla lataamassa
            if (uiState !is StoryScreenState.Loading) {
                MagicWordInput(
                    value = word1,
                    onValueChange = { word1 = it },
                    label = "1. Taikasana (esim. Lohikäärme)",
                    imeAction = ImeAction.Next
                )
                MagicWordInput(
                    value = word2,
                    onValueChange = { word2 = it },
                    label = "2. Taikasana (esim. Linna)",
                    imeAction = ImeAction.Next
                )
                MagicWordInput(
                    value = word3,
                    onValueChange = { word3 = it },
                    label = "3. Taikasana (esim. Ystävyys)",
                    imeAction = ImeAction.Done,
                    onDone = { focusManager.clearFocus() }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // --- ISO NAPPI ---
                Button(
                    onClick = {
                        focusManager.clearFocus()
                        viewModel.generateStory(userName, word1, word2, word3)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(16.dp),
                    elevation = ButtonDefaults.buttonElevation(8.dp)
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(28.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        "TAIO SATU!",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }

            // --- LATAUSANIMAATIO ---
            if (uiState is StoryScreenState.Loading) {
                Box(modifier = Modifier.padding(40.dp)) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(60.dp),
                        color = StorybookPaper,
                        strokeWidth = 6.dp
                    )
                }
                Text("Taikuutta ilmassa...", color = StorybookPaper)
            }

            // --- VIRHEILMOITUS ---
            if (uiState is StoryScreenState.Error) {
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = (uiState as StoryScreenState.Error).message,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                }
                Button(onClick = { viewModel.resetState() }) {
                    Text("Yritä uudelleen")
                }
            }

            // --- VALMIS SATU ---
            AnimatedVisibility(
                visible = uiState is StoryScreenState.Success,
                enter = fadeIn()
            ) {
                if (uiState is StoryScreenState.Success) {
                    val story = (uiState as StoryScreenState.Success).story

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = Color.Black.copy(alpha = 0.6f),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(24.dp)
                    ) {
                        // Otsikko
                        Text(
                            text = story.title,
                            style = MaterialTheme.typography.headlineMedium,
                            color = StorybookPaper,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )

                        Divider(
                            color = StorybookPaper.copy(alpha = 0.5f),
                            modifier = Modifier.padding(vertical = 16.dp)
                        )

                        // Sisältö
                        Text(
                            text = story.content,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                lineHeight = 32.sp,
                                fontSize = 18.sp
                            ),
                            color = StorybookPaper.copy(alpha = 0.95f)
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        // Nappi uuden luomiseen
                        Button(
                            onClick = {
                                word1 = ""
                                word2 = ""
                                word3 = ""
                                viewModel.resetState()
                            },
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            colors = ButtonDefaults.buttonColors(containerColor = StorybookPaper, contentColor = Color.Black)
                        ) {
                            Text("Tee uusi satu")
                        }
                    }
                }
            }


            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}


@Composable
fun MagicWordInput(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    imeAction: ImeAction,
    onDone: () -> Unit = {}
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = StorybookPaper.copy(alpha = 0.8f)) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = imeAction),
        keyboardActions = KeyboardActions(onDone = { onDone() }),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = StorybookPaper,
            unfocusedBorderColor = StorybookPaper.copy(alpha = 0.5f),
            focusedTextColor = StorybookPaper,
            unfocusedTextColor = StorybookPaper,
            cursorColor = StorybookPaper
        ),
        leadingIcon = {
            Icon(Icons.Default.Star, contentDescription = null, tint = StorybookPaper.copy(alpha = 0.7f))
        },
        shape = RoundedCornerShape(12.dp)
    )
}