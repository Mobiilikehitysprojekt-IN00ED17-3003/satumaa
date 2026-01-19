package fi.antero.satumaa.ui.screens.story

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState // LISÄTTY
import androidx.compose.foundation.verticalScroll // LISÄTTY
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox // LISÄTTY
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import fi.antero.satumaa.data.model.Story
import fi.antero.satumaa.ui.components.AppPageLayout
import fi.antero.satumaa.ui.components.AppTopBar
import fi.antero.satumaa.ui.theme.LocalAppImages
import fi.antero.satumaa.ui.viewmodel.story.StoryListViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoryListScreen(
    onNavigateBack: () -> Unit,
    onStoryClick: (String) -> Unit,
    viewModel: StoryListViewModel = hiltViewModel()
) {
    val stories by viewModel.stories.collectAsState()
    var isRefreshing by remember { mutableStateOf(false) }

    var storyToDelete by remember { mutableStateOf<Story?>(null) }

    AppPageLayout(
        backgroundImageRes = LocalAppImages.current.storyListBackground,
        topBar = {
            AppTopBar(
                overrideTitle = "Omat sadut",
                showBack = true,
                onBack = onNavigateBack
            )
        }
    ) { padding ->

        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = {
                isRefreshing = true
                viewModel.refreshStories()

                android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                    isRefreshing = false
                }, 1000)
            },
            modifier = Modifier.fillMaxSize().padding(padding)
        ) {
            if (stories.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text("Ei vielä satuja. Vedä alas päivittääksesi.", modifier = Modifier.padding(16.dp))
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(stories, key = { it.id }) { story ->
                        StoryCard(
                            story = story,
                            onClick = { onStoryClick(story.id) },
                            onDelete = { storyToDelete = story }
                        )
                    }
                }
            }
        }

        if (storyToDelete != null) {
            AlertDialog(
                onDismissRequest = { storyToDelete = null },
                title = { Text(text = "Poistetaanko satu?") },
                text = {
                    Text(text = "Haluatko varmasti poistaa sadun \"${storyToDelete?.title}\"? Tätä toimintoa ei voi kumota.")
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            storyToDelete?.let { viewModel.deleteStory(it.id) }
                            storyToDelete = null
                        },
                        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Poista")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { storyToDelete = null }) {
                        Text("Peruuta")
                    }
                }
            )
        }
    }
}

@Composable
fun StoryCard(
    story: Story,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(0.9f))
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = story.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = SimpleDateFormat("d.M.yyyy HH:mm", Locale.getDefault()).format(Date(story.createdAt)),
                    style = MaterialTheme.typography.bodySmall
                )
            }
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Poista",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}