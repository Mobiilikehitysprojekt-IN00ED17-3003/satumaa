package fi.antero.satumaa.ui.screens.auth

import android.Manifest
import android.app.Activity
import android.os.Build
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

    // Google-login virheviesti UI:lle
    var googleError by remember { mutableStateOf<String?>(null) }

    // Android 13+ ilmoituslupa (näitä tarvitaan virallisiin notifikaatioihin)
    val notifPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        Log.d("AUTH", "Notification permission granted=$granted")
    }

    // Pyydetään lupa kerran kun ruutu avautuu (vain Android 13+)
    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= 33) {
            notifPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    // Google client id
    val googleClientId = context.getString(R.string.default_web_client_id)

    // Google sign-in client
    val googleSignInClient = remember {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(googleClientId)
            .requestEmail()
            .build()
        GoogleSignIn.getClient(context, gso)
    }

    // Google sign-in result launcher
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        googleError = null

        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                val idToken = account.idToken

                if (!idToken.isNullOrBlank()) {
                    Log.d("AUTH", "Google account ok, token found.")
                    viewModel.signInWithGoogle(idToken)
                } else {
                    Log.e("AUTH", "Google Sign-In: idToken puuttuu.")
                    googleError = "Google-kirjautuminen epäonnistui (tunniste puuttuu)."
                }

            } catch (e: ApiException) {
                Log.e("AUTH", "Google Sign-In epäonnistui (koodi ${e.statusCode}).", e)
                googleError = "Google-kirjautuminen epäonnistui. Yritä uudelleen."
            }
        } else {
            Log.d("AUTH", "Google Sign-In peruutettu/epäonnistui (resultCode=${result.resultCode})")
        }
    }

    // Kun Firebase-auth onnistuu, siirrytään eteenpäin
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
                    .weight(2f),
                contentAlignment = Alignment.Center
            ) {
                // lokaali muuttuja -> smart cast helpommin
                val state = uiState

                when (state) {
                    is AuthUiState.Loading -> LoadingView()

                    is AuthUiState.Error -> {
                        ErrorView(
                            message = state.message,
                            onRetry = {
                                googleError = null
                                viewModel.resetState()
                            }
                        )
                    }

                    else -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {

                            // Näytetään mahdollinen googleError omana laatikkona
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
