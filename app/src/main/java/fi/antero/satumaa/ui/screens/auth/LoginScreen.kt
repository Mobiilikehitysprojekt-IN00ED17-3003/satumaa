package fi.antero.satumaa.ui.screens.auth

import android.app.Activity
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

    // HUOM: Pidetään tämä kovakoodattu ID, koska totesimme sen toimivan google-services.jsonin kanssa
    val googleSignInClient = remember {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("803857639736-1hu6vfr5v3rp03p8l2impkm4bfn3glu9.apps.googleusercontent.com")
            .requestEmail()
            .build()
        GoogleSignIn.getClient(context, gso)
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                val idToken = account.idToken
                if (idToken != null) {
                    viewModel.signInWithGoogle(idToken)
                }
            } catch (e: ApiException) {
                // Tähän voisi lisätä virheilmoituksen, jos Google-kirjautuminen peruutetaan
            }
        }
    }

    // Tämä seuraa tilaa: Kun kirjautuminen (Google tai Anonyymi) onnistuu,
    // siirrytään eteenpäin.
    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Success) {
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
                modifier = Modifier.weight(2f),
                contentAlignment = Alignment.TopCenter
            ) {
                when (uiState) {
                    is AuthUiState.Loading -> {
                        LoadingView()
                    }
                    is AuthUiState.Error -> {
                        ErrorView(
                            message = (uiState as AuthUiState.Error).message,
                            // Retry yrittää Google-kirjautumista oletuksena
                            onRetry = { launcher.launch(googleSignInClient.signInIntent) }
                        )
                    }
                    else -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Button(
                                onClick = { launcher.launch(googleSignInClient.signInIntent) },
                                modifier = Modifier.padding(bottom = 8.dp)
                            ) {
                                Text("Kirjaudu Google-tilillä")
                            }

                            // --- TÄMÄ ON MUUTETTU KOHTA ---
                            // Nyt tämä kutsuu ViewModelin anonyymiä kirjautumista.
                            // Kun se onnistuu, LaunchedEffect yllä hoitaa siirtymisen.
                            TextButton(onClick = { viewModel.signInAnonymously() }) {
                                Text(
                                    "Ohita Google (Anonyymi kirjautuminen)",
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