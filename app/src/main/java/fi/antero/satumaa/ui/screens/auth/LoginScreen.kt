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

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val context = LocalContext.current // Saadaan context Compose-puolelta
    val uiState by viewModel.uiState.collectAsState()

    var googleError by remember { mutableStateOf<String?>(null) }

    val notifPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { Log.d("LoginScreen", "Notification permission: $it") }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= 33) {
            notifPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

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
        googleError = null
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                val idToken = account.idToken
                if (!idToken.isNullOrBlank()) {
                    viewModel.signInWithGoogle(idToken)
                } else {
                    // KORJATTU: Välitetään context mapErrorToUserMessage-funktiolle
                    googleError = "AUTH_GOOGLE_TOKEN_MISSING".mapErrorToUserMessage(context)
                }
            } catch (e: ApiException) {
                Log.e("LoginScreen", "Google Sign-In failed", e)
                // KORJATTU: Välitetään context
                googleError = "AUTH_GOOGLE_API_ERROR".mapErrorToUserMessage(context)
            }
        }
    }

    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Success) {
            onLoginSuccess()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AuthBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(3f))
            AuthHeader()
            Spacer(modifier = Modifier.weight(1f))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(2f),
                contentAlignment = Alignment.Center
            ) {
                when (val state = uiState) {
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