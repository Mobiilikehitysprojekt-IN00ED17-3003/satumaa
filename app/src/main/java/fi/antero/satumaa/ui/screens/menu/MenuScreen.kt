// app/src/main/java/fi/antero/satumaa/ui/screens/menu/MenuScreen.kt
package fi.antero.satumaa.ui.screens.menu

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import fi.antero.satumaa.R
import fi.antero.satumaa.ui.components.AppTopBar
import fi.antero.satumaa.ui.navigation.RootRoute
import fi.antero.satumaa.ui.theme.Forest
import fi.antero.satumaa.ui.theme.StorybookPaper
import fi.antero.satumaa.ui.theme.Terracotta

@Composable
fun MenuScreen(
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    // Leijunta-animaatio
    val infiniteTransition = rememberInfiniteTransition(label = "floating")
    val floatAnim by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float"
    )

    Scaffold { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            // 1. Taustakuva
            Image(
                painter = painterResource(id = R.drawable.sade),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // 2. Yläpalkki
            AppTopBar(
                title = "Satumaa",
                onOpenProfile = { onNavigate(RootRoute.Profile.route) },
                modifier = Modifier.align(Alignment.TopCenter)
            )

            // 3. Kortit (Ilman ylimääräisiä tekstejä)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = 40.dp),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ModernMenuCard(
                    title = "Lue Iltasatu",
                    description = "Taikuutta jokaiseen iltaan",
                    icon = Icons.Default.AutoStories,
                    accentColor = Forest, // Käytetään teemasi Forest-väriä
                    offsetY = floatAnim,
                    onClick = { onNavigate(RootRoute.Story.route) }
                )

                Spacer(Modifier.height(20.dp))

                ModernMenuCard(
                    title = "Kirje Joulupukille",
                    description = "Lähetä terveisesi Korvatunturille",
                    icon = Icons.Default.Email,
                    accentColor = Terracotta, // Käytetään teemasi Terracotta-väriä
                    offsetY = floatAnim * 0.7f,
                    onClick = { onNavigate(RootRoute.Letter.route) }
                )

                Spacer(Modifier.height(60.dp)) // Tilaa alareunaan
            }
        }
    }
}

@Composable
fun ModernMenuCard(
    title: String,
    description: String,
    icon: ImageVector,
    accentColor: Color,
    offsetY: Float = 0f,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(28.dp)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
            .offset(y = offsetY.dp)
            .shadow(15.dp, shape)
            .clip(shape)
            .clickable(onClick = onClick)
            .border(1.dp, Color.White.copy(alpha = 0.2f), shape),
        color = Color.White.copy(alpha = 0.15f),
        shape = shape
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Ikoniosa
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(accentColor.copy(alpha = 0.25f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = StorybookPaper, // Ikonin väri on nyt paperinsävy
                    modifier = Modifier.size(30.dp)
                )
            }

            Spacer(Modifier.width(20.dp))

            // Tekstiosa
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = StorybookPaper, // Pääotsikko on nyt paperinsävy
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = StorybookPaper.copy(alpha = 0.8f) // Kuvausteksti on hieman haaleampi paperi
                )
            }
        }
    }
}