/*package fi.antero.satumaa.ui.screens.story

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import fi.antero.satumaa.ui.theme.StorybookPaper
import fi.antero.satumaa.ui.viewmodel.story.StoryReaderUiState
import fi.antero.satumaa.ui.viewmodel.story.StoryViewModel

@Composable
fun StoryReaderScreen(
    storyId: String,
    onNavigate: (String) -> Unit,
    viewModel: StoryViewModel = hiltViewModel()
) {
    LaunchedEffect(storyId) {
        viewModel.loadStory(storyId)
    }

    val uiState by viewModel.readerUiState.collectAsState()

    AppPageLayout(
        backgroundImageRes = LocalAppImages.current.storyListBackground,
        topBar = {
            AppTopBar(
                overrideTitle = "Lukuhetki", // Korjattu
                showBack = true,
                onBack = { onNavigate(RootRoute.StoryList.route) }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (val state = uiState) {
                is StoryReaderUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = StorybookPaper)
                }
                is StoryReaderUiState.Error -> {
                    ErrorView(message = state.message, onRetry = { viewModel.loadStory(storyId) }, modifier = Modifier.align(Alignment.Center))
                }
                is StoryReaderUiState.Success -> {
                    val story = state.story
                    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp)) {
                        Text(story.title, style = MaterialTheme.typography.headlineMedium, color = StorybookPaper)
                        Text("Lapselle: ${story.childName}", style = MaterialTheme.typography.labelLarge, color = StorybookPaper)
                        // Vaihdettu HorizontalDivider (Material3)
                        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = StorybookPaper.copy(alpha = 0.3f))
                        Text(text = story.content, style = MaterialTheme.typography.bodyLarge, color = StorybookPaper, lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.5)
                        Spacer(modifier = Modifier.height(48.dp))
                    }
                }
            }
        }
    }
}*/