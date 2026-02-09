package fi.antero.satumaa.ui.screens.story

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import fi.antero.satumaa.R
import fi.antero.satumaa.data.model.Story
import fi.antero.satumaa.ui.components.AppPageLayout
import fi.antero.satumaa.ui.components.AppTopBar
import fi.antero.satumaa.ui.components.story.list.DeleteStoryDialog
import fi.antero.satumaa.ui.components.story.list.EmptyStoryListView
import fi.antero.satumaa.ui.components.story.list.StoryCard
import fi.antero.satumaa.ui.components.story.list.StoryListBackground
import fi.antero.satumaa.ui.viewmodel.story.StoryListViewModel

/**
 * StoryListScreen näyttää listan käyttäjän tallentamista saduista.
 *
 * Toiminnallisuudet:
 * 1. Hakee sadut ViewModelista.
 * 2. Mahdollistaa listan päivityksen (Pull-to-refresh).
 * 3. Hallinnoi sadun poistamista.
 * 4. Käyttää erillisiä komponentteja (Background, Card, Dialog) selkeyden vuoksi.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoryListScreen(
    onNavigateBack: () -> Unit,
    onStoryClick: (String) -> Unit,
    viewModel: StoryListViewModel = hiltViewModel()
) {
    // UI-tila: Sadut
    val stories by viewModel.stories.collectAsState()

    // UI-tila: Virkistys ja poisto
    var isRefreshing by remember { mutableStateOf(false) }
    var storyToDelete by remember { mutableStateOf<Story?>(null) }

    // Käytetään AppPageLayoutia kustomoidulla taustalla
    AppPageLayout(
        background = { StoryListBackground() },
        topBar = {
            AppTopBar(
                overrideTitle = stringResource(R.string.story_list_title),
                showBack = true,
                onBack = onNavigateBack
            )
        }
    ) { padding ->

        // Pull-to-refresh -kontti
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = {
                isRefreshing = true
                viewModel.refreshStories()
                // Pieni viive käyttäjäkokemuksen parantamiseksi
                android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                    isRefreshing = false
                }, 1000)
            },
            modifier = Modifier.fillMaxSize().padding(padding)
        ) {
            if (stories.isEmpty()) {
                // Näytetään tyhjä tila
                EmptyStoryListView()
            } else {
                // Listataan sadut
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(stories, key = { it.id }) { story ->
                        StoryCard(
                            story = story,
                            onClick = { onStoryClick(story.id) },
                            onDelete = { storyToDelete = story } // Avaa poistodialogin
                        )
                    }
                }
            }
        }

        // Poistodialogi (näytetään vain kun storyToDelete ei ole null)
        storyToDelete?.let { story ->
            DeleteStoryDialog(
                story = story,
                onConfirm = {
                    viewModel.deleteStory(story.id)
                    storyToDelete = null
                },
                onDismiss = { storyToDelete = null }
            )
        }
    }
}