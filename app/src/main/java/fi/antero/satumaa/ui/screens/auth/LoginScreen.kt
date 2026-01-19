package fi.antero.satumaa.ui.screens.auth

import android.app.Activity
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import fi.antero.satumaa.R
import fi.antero.satumaa.ui.components.ErrorView
import fi.antero.satumaa.ui.components.LoadingView
import fi.antero.satumaa.ui.theme.LocalAppImages
import fi.antero.satumaa.viewmodel.auth.AuthUiState
import fi.antero.satumaa.viewmodel.auth.AuthViewModel

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    // Paikallinen tila Google-kirjautumisen Intent-virheille (ennen kuin päästään ViewModeliin)
    var googleError by remember { mutableStateOf<String?>(null) }

    // TÄRKEÄ: Tämä id haetaan resursseista (Main branchin korjaus)
    val googleClientId = context.getString(R.string.default_web_client_id)

    val googleSignInClient = remember {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(googleClientId)
            .requestEmail()
            .build()

        GoogleSignIn.getClient(context, gso)
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        // Tyhjennetään vanhat virheet
        googleError = null

        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                // Yritetään kaivaa tili vastauksesta (Miron lisäys: tarkempi logitus)
                val account = task.getResult(ApiException::class.java)
                val idToken = account.idToken

                if (!idToken.isNullOrBlank()) {
                    Log.d("AUTH", "Google account ok, token found.")
                    viewModel.signInWithGoogle(idToken)
                } else {
                    Log.e("AUTH", "Kirjautuminen epäonnistui: idToken puuttuu.")
                    googleError = "Google-kirjautuminen epäonnistui (tunniste puuttuu)."
                }

            } catch (e: ApiException) {
                // TÄRKEÄ: Näytetään virhe (Miron lisäys)
                val msg = "Google Sign-In epäonnistui (koodi ${e.statusCode})."
                Log.e("AUTH", msg, e)
                googleError = "Google-kirjautuminen epäonnistui. Yritä uudelleen."
            }
        } else {
            Log.d("AUTH", "Google Sign-In peruutettu tai epäonnistui (resultCode=${result.resultCode})")
        }
    }

    // Kuunnellaan ViewModelin tilaa navigointia varten
    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Success) {
            Log.d("AUTH", "AuthUiState.Success -> navigate")
            onLoginSuccess()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = LocalAppImages.current.authBackground),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(3f))

            Text(
                text = "Tervetuloa Satumaahan",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White
            )

            Spacer(modifier = Modifier.weight(1f))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(2f), // Varmistetaan tila sisällölle
                contentAlignment = Alignment.Center
            ) {
                when (uiState) {
                    is AuthUiState.Loading -> LoadingView()
                    is AuthUiState.Error -> {
                        ErrorView(
                            message = (uiState as AuthUiState.Error).message,
                            onRetry = {
                                googleError = null
                                viewModel.resetState()
                            }
                        )
                    }
                    else -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            // Näytetään mahdolliset launcher-virheet tässä
                            googleError?.let {
                                Text(
                                    text = it,
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(bottom = 16.dp)
                                )
                            }

                            LoginButtons(
                                onGoogleClick = {
                                    googleError = null
                                    launcher.launch(googleSignInClient.signInIntent)
                                },
                                onAnonymousClick = {
                                    googleError = null
                                    viewModel.signInAnonymously()
                                }
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}

@Composable
private fun LoginButtons(
    onGoogleClick: () -> Unit,
    onAnonymousClick: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Button(
            onClick = onGoogleClick,
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .padding(bottom = 12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            // Huom: Varmista että R.drawable.ic_google löytyy, tai korvaa Icons.Defaultilla
            Icon(
                painter = painterResource(id = R.drawable.ic_google),
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text("Jatka Google-tilillä")
        }

        TextButton(onClick = onAnonymousClick) {
            Text(
                "Kokeile ilman tiliä",
                color = Color.White.copy(alpha = 0.8f),
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}