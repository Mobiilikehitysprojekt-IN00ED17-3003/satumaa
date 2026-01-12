package fi.antero.satumaa.ui.screens.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import fi.antero.satumaa.R

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {

        Image(
            painter = painterResource(id = R.drawable.kohti_taikalinnaa),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Button(
            onClick = onLoginSuccess,
            modifier = Modifier.align(Alignment.Center)
        ) {
            Text("Kirjaudu sisään")
        }
    }
}
