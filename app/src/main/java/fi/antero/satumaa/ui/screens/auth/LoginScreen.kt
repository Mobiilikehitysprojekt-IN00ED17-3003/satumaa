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

    // Näytetään Google-sign-in virheet myös UI:ssa
    var googleError by remember { mutableStateOf<String?>(null) }

    val googleSignInClient = remember {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        GoogleSignIn.getClient(context, gso)
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        Log.d("AUTH", "Google resultCode=${result.resultCode}, dataNull=${result.data == null}")

        // Tyhjennä aiempi virhe, kun saadaan uusi tulos
        googleError = null

        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            Log.d("AUTH", "Google account ok email=${account.email}")

            val idToken = account.idToken
            if (idToken.isNullOrBlank()) {
                googleError = "Kirjautuminen epäonnistui: idToken puuttuu."
                Log.e("AUTH", "idToken was null/blank")
                return@rememberLauncherForActivityResult
            }

            // Tämä siirtyy ViewModeliin (Firebase signInWithCredential tms)
            viewModel.signInWithGoogle(idToken)

        } catch (e: ApiException) {
            // TÄRKEÄ: älä niele virhettä -> näytä ja loggaa
            val msg = "Google Sign-In epäonnistui (koodi ${e.statusCode})."
            googleError = msg
            Log.e("AUTH", msg, e)
        } catch (t: Throwable) {
            val msg = "Google Sign-In epäonnistui tuntemattomalla virheellä."
            googleError = msg
            Log.e("AUTH", msg, t)
        }
    }

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
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(4f))

            Box(
                modifier = Modifier
                    .weight(2f)
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                when (uiState) {
                    is AuthUiState.Loading -> {
                        LoadingView()
                    }

                    is AuthUiState.Error -> {
                        ErrorView(
                            message = (uiState as AuthUiState.Error).message,
                            onRetry = { launcher.launch(googleSignInClient.signInIntent) }
                        )
                    }

                    else -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {

                            Button(
                                onClick = {
                                    Log.d("AUTH", "Google sign-in click")
                                    googleError = null
                                    launcher.launch(googleSignInClient.signInIntent)
                                },
                                modifier = Modifier.padding(bottom = 8.dp)
                            ) {
                                Text("Kirjaudu Google-tilillä")
                            }

                            // Näytetään Google Sign-In virheet käyttäjälle
                            googleError?.let {
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    text = it,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }

                            TextButton(onClick = onLoginSuccess) {
                                Text(
                                    "Jatka kirjautumatta (Kehitys)",
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
