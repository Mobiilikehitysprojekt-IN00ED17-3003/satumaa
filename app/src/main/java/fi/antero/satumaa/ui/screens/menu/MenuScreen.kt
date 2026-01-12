// app/src/main/java/fi/antero/satumaa/ui/screens/menu/MenuScreen.kt
package fi.antero.satumaa.ui.screens.menu

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import fi.antero.satumaa.R
import fi.antero.satumaa.ui.components.AppTopBar
import fi.antero.satumaa.ui.navigation.RootRoute

@Composable
fun MenuScreen(
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    Scaffold { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Image(
                painter = painterResource(id = R.drawable.sade),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            AppTopBar(
                title = "Satumaa",
                onOpenProfile = { onNavigate(RootRoute.Profile.route) },
                modifier = Modifier.align(Alignment.TopCenter)
            )

            // Kevyt “tummennus” alaosaan, jotta tekstit erottuu kauniisti
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(240.dp)
                    .background(Color.Black.copy(alpha = 0.12f))
            )

            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Mitä haluaisit tehdä?",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    GlassTile(
                        title = "Iltasatu",
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(RootRoute.Story.route) }
                    )
                    GlassTile(
                        title = "Kirje\nJoulupukille",
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(RootRoute.Letter.route) }
                    )
                }
            }
        }
    }
}

@Composable
private fun GlassTile(
    title: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(18.dp)

    Surface(
        modifier = modifier
            .aspectRatio(1f) // neliö
            .sizeIn(minHeight = 120.dp) // varmistaa ettei mene liian pieneksi
            .clip(shape)
            .clickable(onClick = onClick),
        shape = shape,
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.35f),
        tonalElevation = 0.dp,
        shadowElevation = 6.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White.copy(alpha = 0.08f))
                .padding(14.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
