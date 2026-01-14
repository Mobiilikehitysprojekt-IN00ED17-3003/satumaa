/*package fi.antero.satumaa.ui.screens.story

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import fi.antero.satumaa.ui.components.AppPageLayout
import fi.antero.satumaa.ui.components.AppTopBar
import fi.antero.satumaa.ui.navigation.RootRoute
import fi.antero.satumaa.ui.theme.LocalAppImages
import fi.antero.satumaa.ui.theme.StorybookPaper
import fi.antero.satumaa.ui.viewmodel.story.StoryViewModel

@Composable
fun StoryListScreen(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    viewModel: StoryViewModel = hiltViewModel()
) {
    val stories by viewModel.stories.collectAsState()

    AppPageLayout(
        backgroundImageRes = LocalAppImages.current.storyListBackground,
        topBar = {
            AppTopBar(
                showBack = true,
                onBack = { onNavigate(RootRoute.Menu.route) },
                onOpenProfile = { onNavigate(RootRoute.Profile.route) },
                overrideTitle = "Omat sadut"
            )
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (stories.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Ei vielä satuja.\nLuo uusi painamalla +",
                        style = MaterialTheme.typography.bodyLarge,
                        color = StorybookPaper,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(stories) { story ->
                        StoryListItem(
                            title = story.title,
                            preview = story.content.take(80).replace("\n", " ") + "...",
                            childName = story.childName,
                            onClick = {
                                onNavigate(RootRoute.StoryReader.createRoute(story.id))
                            }
                        )
                    }
                }
            }


            FloatingActionButton(
                onClick = { onNavigate(RootRoute.StoryCreate.route) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Luo uusi")
            }
        }
    }
}

@Composable
fun StoryListItem(title: String, preview: String, childName: String, onClick: () -> Unit) {
    val shape = RoundedCornerShape(20.dp)
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .clickable(onClick = onClick)
            .border(1.dp, Color.White.copy(alpha = 0.2f), shape),
        color = Color.White.copy(alpha = 0.15f),
        shape = shape
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.AutoAwesome, null, tint = StorybookPaper.copy(0.8f), modifier = Modifier.size(32.dp))
            Spacer(Modifier.width(16.dp))
            Column {
                Text(title, style = MaterialTheme.typography.titleMedium, color = StorybookPaper)
                Text("Lapselle: $childName", style = MaterialTheme.typography.labelSmall, color = StorybookPaper.copy(0.7f))
                Spacer(Modifier.height(4.dp))
                Text(preview, style = MaterialTheme.typography.bodySmall, maxLines = 2, overflow = TextOverflow.Ellipsis, color = StorybookPaper.copy(0.9f))
            }
        }
    }
}*/