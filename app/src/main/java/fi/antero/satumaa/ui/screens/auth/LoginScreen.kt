package fi.antero.satumaa.ui.screens.auth

import android.Manifest
import android.app.Activity
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import fi.antero.satumaa.R
import fi.antero.satumaa.ui.components.ErrorView
import fi.antero.satumaa.ui.components.LoadingView
import fi.antero.satumaa.ui.components.auth.AuthBackground
import fi.antero.satumaa.ui.components.auth.AuthHeader
import fi.antero.satumaa.ui.components.auth.LoginButtons
import fi.antero.satumaa.util.mapErrorToUserMessage
import fi.antero.satumaa.viewmodel.auth.AuthUiState
import fi.antero.satumaa.viewmodel.auth.AuthViewModel

/**
 * LoginScreen hoitaa käyttäjän tunnistautumisen.
 *
 * Päävastuut:
 * 1. Pyytää tarvittavat luvat (Ilmoitukset).
 * 2. Alustaa Google Sign-In -asiakasohjelman.
 * 3. Käsittelee Google-kirjautumisen tuloksen (Activity Result).
 * 4. Tarkkailee kirjautumisen tilaa (Loading, Success, Error).
 */
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit, // Callback navigointia varten, kun kirjautuminen onnistuu
    viewModel: AuthViewModel = hiltViewModel()
) {
    val context = LocalContext.current // Tarvitaan Google Sign-In Clientille ja virheviesteille
    val uiState by viewModel.uiState.collectAsState()

    // Paikallinen tila Google-kirjautumisen API-virheille (ennen kuin ne ehtivät ViewModeliin)
    var googleError by remember { mutableStateOf<String?>(null) }

    // --- LUVAT ---
    // Android 13+ vaatii luvan ilmoitusten näyttämiseen. Pyydetään se heti tässä.
    val notifPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { Log.d("LoginScreen", "Notification permission: $it") }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= 33) {
            notifPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    // --- GOOGLE SIGN-IN KONFIGURAATIO ---

    // R.string.default_web_client_id generoituu automaattisesti google-services.json -tiedostosta.
    // TÄRKEÄÄ: Tarvitsemme nimenomaan Web Client ID:n, jotta saamme ID-tokenin backendille.
    val googleClientId = context.getString(R.string.default_web_client_id)

    val googleSignInClient = remember {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(googleClientId) // Pyydetään token Firebaselle
            .requestEmail()
            .build()
        GoogleSignIn.getClient(context, gso)
    }

    // --- KIRJAUTUMISEN TULOS ---
    // Tämä launcher kuuntelee, kun käyttäjä palaa Googlen valintaikkunasta.
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        googleError = null
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                // Yritetään kaivaa tilitiedot intentistä
                val account = task.getResult(ApiException::class.java)
                val idToken = account.idToken

                if (!idToken.isNullOrBlank()) {
                    // Jos token saatiin, lähetetään se ViewModelille validointia varten
                    viewModel.signInWithGoogle(idToken)
                } else {
                    // Jos token puuttuu (harvinaista), näytetään virhe
                    googleError = "AUTH_GOOGLE_TOKEN_MISSING".mapErrorToUserMessage(context)
                }
            } catch (e: ApiException) {
                Log.e("LoginScreen", "Google Sign-In failed", e)
                // API-virhe (esim. käyttäjä peruutti, ei verkkoa)
                googleError = "AUTH_GOOGLE_API_ERROR".mapErrorToUserMessage(context)
            }
        }
    }

    // --- NAVIGOINTI ---
    // Tarkkaillaan ViewModelin tilaa. Jos Success -> siirrytään eteenpäin.
    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Success) {
            onLoginSuccess()
        }
    }

    // --- UI LAYOUT ---
    Box(modifier = Modifier.fillMaxSize()) {
        // Taustakuva (Metsä)
        AuthBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Asettelu: Otsikko ylhäällä, napit keskellä/alhaalla
            Spacer(modifier = Modifier.weight(3f))
            AuthHeader() // Logo ja sovelluksen nimi
            Spacer(modifier = Modifier.weight(1f))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(2f),
                contentAlignment = Alignment.Center
            ) {
                // Vaihdetaan näkymää tilan mukaan
                when (val state = uiState) {
                    is AuthUiState.Loading -> LoadingView() // Spinneri

                    is AuthUiState.Error -> {
                        // Virhe ViewModelista (esim. Firebase alhaalla)
                        ErrorView(
                            message = state.message,
                            onRetry = {
                                googleError = null
                                viewModel.resetState()
                            }
                        )
                    }

                    else -> {
                        // Normaali tila: Näytetään napit
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {

                            // Jos tuli virhe suoraan Google API:sta (ei ViewModelista)
                            googleError?.let { msg ->
                                ErrorView(
                                    message = msg,
                                    onRetry = { googleError = null },
                                    modifier = Modifier.padding(bottom = 16.dp)
                                )
                            }

                            LoginButtons(
                                onGoogleClick = {
                                    googleError = null
                                    // Käynnistetään Googlen oma kirjautumis-Activity
                                    launcher.launch(googleSignInClient.signInIntent)
                                },
                                onAnonymousClick = {
                                    googleError = null
                                    // "Kokeile ilman tunnuksia"
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