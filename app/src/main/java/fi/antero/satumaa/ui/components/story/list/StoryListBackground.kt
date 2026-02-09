package fi.antero.satumaa.ui.components.story.list

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import fi.antero.satumaa.ui.theme.LocalAppImages

/**
 * Satujen listausnäkymän taustakuva.
 */
@Composable
fun StoryListBackground() {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = LocalAppImages.current.storyListBackground),
            contentDescription = null, // Dekoratiivinen tausta
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}