package fi.antero.satumaa.ui.screens.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import fi.antero.satumaa.ui.components.AppPageLayout
import fi.antero.satumaa.ui.theme.*

@Composable
fun OnboardingScreen(
    onNameSubmitted: (String) -> Unit
) {
    var name by remember { mutableStateOf("") }

    AppPageLayout(backgroundImageRes = LocalAppImages.current.authBackground) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = AppDimensions.ScreenPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                color = Color.Black.copy(alpha = 0.5f),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Tervetuloa seikkailuun!",
                        style = MaterialTheme.typography.headlineLarge,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Kuka tänään lähtee matkaan?",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White.copy(alpha = 0.9f),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    TextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Seikkailijan nimi", color = Color.White.copy(alpha = 0.7f)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Black.copy(alpha = 0.3f),
                            unfocusedContainerColor = Color.Black.copy(alpha = 0.2f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = StorybookPaper,
                            focusedIndicatorColor = StorybookPaper,
                            unfocusedIndicatorColor = Color.Transparent
                        )
                    )
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = { if (name.isNotBlank()) onNameSubmitted(name) },
                enabled = name.isNotBlank(),
                modifier = Modifier.height(56.dp).fillMaxWidth(0.7f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black.copy(alpha = 0.8f),
                    contentColor = Color.White
                )
            ) {
                Text("Aloita seikkailu", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}