package fi.antero.satumaa.ui.screens.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import fi.antero.satumaa.R
import fi.antero.satumaa.ui.components.onboarding.OnboardingBackground
import fi.antero.satumaa.ui.components.onboarding.OnboardingInputCard
import fi.antero.satumaa.ui.theme.*

/**
 * OnboardingScreen kysyy käyttäjältä nimen ennen sovelluksen varsinaista käyttöä.
 * Koostuu taustakuvasta, syöttökortista ja toimintopainikkeesta.
 *
 * @param onNameSubmitted Callback, kun käyttäjä on syöttänyt nimen ja painaa jatka.
 */
@Composable
fun OnboardingScreen(
    onNameSubmitted: (String) -> Unit
) {
    // Paikallinen tila nimelle
    var name by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {

        // 1. Taustakuva (oma komponentti)
        OnboardingBackground()

        // 2. Sisältö
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = AppDimensions.ScreenPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // Syöttökortti (Otsikot ja tekstikenttä)
            OnboardingInputCard(
                name = name,
                onNameChange = { name = it }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Toimintopainike
            Button(
                onClick = { if (name.isNotBlank()) onNameSubmitted(name) },
                enabled = name.isNotBlank(),
                modifier = Modifier
                    .height(56.dp)
                    .fillMaxWidth(0.7f),
                colors = ButtonDefaults.buttonColors(
                    // Käytetään StorybookPaperia taustana, jotta se erottuu tummasta kuvasta
                    containerColor = StorybookPaper,
                    contentColor = Ink,
                    disabledContainerColor = StorybookPaper.copy(alpha = 0.5f),
                    disabledContentColor = Ink.copy(alpha = 0.5f)
                )
            ) {
                Text(
                    text = stringResource(R.string.onboarding_button_start),
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}