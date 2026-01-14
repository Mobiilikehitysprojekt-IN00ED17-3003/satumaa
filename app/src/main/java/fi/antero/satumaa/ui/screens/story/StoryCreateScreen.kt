/*package fi.antero.satumaa.ui.screens.story

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import fi.antero.satumaa.ui.components.AppPageLayout
import fi.antero.satumaa.ui.components.AppTopBar
import fi.antero.satumaa.ui.components.ErrorView
import fi.antero.satumaa.ui.navigation.RootRoute
import fi.antero.satumaa.ui.theme.LocalAppImages
import fi.antero.satumaa.ui.viewmodel.story.StoryCreateUiState
import fi.antero.satumaa.ui.viewmodel.story.StoryViewModel

@Composable
fun StoryCreateScreen(
    onStoryCreated: (String) -> Unit,
    onNavigate: (String) -> Unit, // Lisätty navigaatiota varten
    viewModel: StoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.createUiState.collectAsState()

    var childName by remember { mutableStateOf("") }
    var keywords by remember { mutableStateOf("") }
    var selectedLength by remember { mutableStateOf("NORMAL") }
    var selectedStyle by remember { mutableStateOf("FUNNY") }

    LaunchedEffect(uiState) {
        if (uiState is StoryCreateUiState.Success) {
            val id = (uiState as StoryCreateUiState.Success).storyId
            viewModel.resetCreateState() // Korjattu nimi
            onStoryCreated(id)
        }
    }

    AppPageLayout(
        backgroundImageRes = LocalAppImages.current.menuBackground,
        topBar = {
            AppTopBar(
                overrideTitle = "Luo uusi satu", // Käytetään sinun overrideTitle-parametriasi
                showBack = true,
                onBack = { onNavigate(RootRoute.StoryList.route) }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (uiState is StoryCreateUiState.Error) {
                val msg = (uiState as StoryCreateUiState.Error).message
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    ErrorView(message = msg, onRetry = { viewModel.resetCreateState() })
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedTextField(
                        value = childName,
                        onValueChange = { childName = it },
                        label = { Text("Lapsen nimi") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        enabled = uiState !is StoryCreateUiState.Loading
                    )
                    OutlinedTextField(
                        value = keywords,
                        onValueChange = { keywords = it },
                        label = { Text("Mistä satu kertoo?") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = uiState !is StoryCreateUiState.Loading
                    )
                    Button(
                        onClick = { viewModel.createStory(childName, keywords, selectedLength, selectedStyle) },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        enabled = uiState !is StoryCreateUiState.Loading
                    ) {
                        if (uiState is StoryCreateUiState.Loading) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp))
                        } else {
                            Text("Luo satu")
                        }
                    }
                }
            }
        }
    }
}  */