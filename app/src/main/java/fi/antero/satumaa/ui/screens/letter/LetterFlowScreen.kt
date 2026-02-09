package fi.antero.satumaa.ui.screens.letter

import android.Manifest
import android.content.Intent
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import fi.antero.satumaa.R
import fi.antero.satumaa.ui.components.AppPageLayout
import fi.antero.satumaa.ui.components.AppTopBar
import fi.antero.satumaa.ui.components.ErrorView
import fi.antero.satumaa.ui.components.letter.flow.*
import fi.antero.satumaa.ui.navigation.LetterRoutes
import fi.antero.satumaa.ui.navigation.RootRoute
import fi.antero.satumaa.ui.theme.AppDimensions
import fi.antero.satumaa.ui.theme.Ink
import fi.antero.satumaa.ui.theme.LocalAppImages
import fi.antero.satumaa.ui.theme.StorybookPaper
import fi.antero.satumaa.util.PermissionUtils
import fi.antero.satumaa.viewmodel.letter.LetterViewModel

/**
 * Määrittelee, missä tilassa näkymä avautuu.
 * Käytetään esimerkiksi deeplink-navigoinnissa tai kun palataan tähän näkymään.
 */
private enum class StartMode {
    NEW_LETTER,   // Aloitetaan uusi kirje heti
    KEEP_CURRENT, // Pidetään nykyinen tila (esim. rotaation jälkeen)
    VIEW_LETTER   // Katsellaan tiettyä vanhaa kirjettä
}

/**
 * LetterFlowScreen on kirjeen kirjoittamisen ja vastauksen odottamisen "pääorkestraattori".
 *
 * Tämän komponentin tehtävät:
 * 1. Hallita koko prosessin tilaa (ViewModelin avulla).
 * 2. Koordinoida eri vaiheita: Kirjoitus -> Lähetys -> Odotus -> Vastaus.
 * 3. Hoitaa Android-spesifiset asiat kuten luvat (Permission) ja elinkaari (Lifecycle).
 * 4. Delegoida varsinainen UI:n piirtäminen alikomponenteille (esim. LetterInputView).
 *
 * @param currentRoute Nykyinen reitti navigoinnissa.
 * @param onNavigate Callback navigointiin (esim. kartalle tai menuhun).
 * @param userName Käyttäjän nimi, näytetään tervehdyksessä.
 * @param letterId (Valinnainen) Jos avataan vanha kirje, sen ID.
 * @param mode (Valinnainen) Avaustila ("new", "view" jne).
 * @param vm ViewModel, joka säilyttää sovelluslogiikan.
 */
@Composable
fun LetterFlowScreen(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    userName: String,
    letterId: String? = null,
    mode: String? = null,
    vm: LetterViewModel
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Kerätään ViewModelin tila Compose-tilaksi
    val state by vm.uiState.collectAsState()
    val scrollState = rememberScrollState()

    // --- Sijaintilupien ja GPS:n hallinta ---

    // Tila, jolla estetään lupapyynnön toistuva spämmäys
    var autoRequestedPermission by rememberSaveable { mutableStateOf(false) }

    // Launcher luvan pyytämiseen järjestelmältä
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ ->
        // Kun käyttäjä vastaa lupapyyntöön, tarkistetaan tilanne uudelleen
        val hasPerm = PermissionUtils.hasFineLocationPermission(context)
        val enabled = PermissionUtils.isLocationEnabled(context)
        vm.onFlowEntered(hasPerm, enabled)
    }

    // Apufunktio sijaintitilanteen tarkistamiseen
    fun refreshLocationCapability() {
        val hasPerm = PermissionUtils.hasFineLocationPermission(context)
        val enabled = PermissionUtils.isLocationEnabled(context)
        vm.onFlowEntered(hasPerm, enabled)
    }

    // Tarkistetaan sijainti heti näkymän avautuessa
    LaunchedEffect(Unit) { refreshLocationCapability() }

    // Tarkistetaan sijainti myös aina kun sovellus palaa aktiiviseksi (onResume),
    // siltä varalta että käyttäjä kävi asetuksissa muuttamassa niitä.
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) refreshLocationCapability()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    // Pyydetään lupa automaattisesti kerran, jos sitä ei ole
    LaunchedEffect(state.hasLocationPermission) {
        if (!state.hasLocationPermission && !autoRequestedPermission) {
            autoRequestedPermission = true
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    // --- Aloitustilan logiikka (Navigointiparametrit) ---

    val startMode = remember(mode, letterId) {
        when {
            mode == "new" -> StartMode.NEW_LETTER
            mode == "view" -> StartMode.VIEW_LETTER
            letterId != null -> StartMode.VIEW_LETTER
            else -> StartMode.KEEP_CURRENT
        }
    }

    LaunchedEffect(letterId, startMode) {
        when (startMode) {
            StartMode.VIEW_LETTER -> if (letterId != null) vm.loadLetter(letterId)
            StartMode.NEW_LETTER -> vm.beginNewLetter()
            StartMode.KEEP_CURRENT -> Unit // Ei tehdä mitään, säilytetään tila
        }
    }

    // --- Dialogit (Pop-up ikkunat) ---

    // Matikkatehtävä dialogi (jos aktivoitu)
    if (state.isMathDialogVisible && state.mathChallenge != null) {
        MathChallengeDialog(
            challenge = state.mathChallenge!!,
            isError = state.mathError,
            onDismiss = { vm.dismissMathChallenge() },
            onSubmit = { answer -> vm.submitMathAnswer(answer) }
        )
    }

    // Sijainti (GPS) pois päältä -dialogi
    // Näytetään vain, jos ollaan kirjoittamassa uutta kirjettä ja lupa on saatu,
    // mutta GPS on pois päältä.
    val showEnableLocationDialog = !state.isViewMode && state.hasLocationPermission && !state.isLocationEnabled
    if (showEnableLocationDialog) {
        AlertDialog(
            onDismissRequest = { }, // Pakotetaan valinta
            title = { Text(stringResource(R.string.letter_loc_dialog_title)) },
            text = { Text(stringResource(R.string.letter_loc_dialog_desc)) },
            confirmButton = {
                Button(onClick = { context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)) }) {
                    Text(stringResource(R.string.letter_loc_dialog_settings))
                }
            },
            dismissButton = {
                // Mahdollisuus perua ja palata menuhun
                TextButton(onClick = { onNavigate(RootRoute.Menu.route) }) {
                    Text(stringResource(R.string.letter_back))
                }
            }
        )
    }

    // --- Varsinainen näkymän asettelu ---

    AppPageLayout(
        // Taustakuva. Huom: Emme käytä erillistä LetterBackground-komponenttia tässä,
        // koska AppPageLayout vaatii resurssi-ID:n (backgroundImageRes).
        // Jos AppPageLayout päivitettäisiin tukemaan composable-taustaa, voisimme vaihtaa tämän.
        backgroundImageRes = LocalAppImages.current.letterBackground,
        topBar = {
            AppTopBar(
                showBack = true,
                onBack = { onNavigate(RootRoute.Menu.route) },
                onOpenProfile = { onNavigate(RootRoute.Profile.route) },
                onOpenLibrary = { onNavigate(RootRoute.LetterList.route) },
                libraryLabel = stringResource(R.string.letter_my_letters)
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState) // Mahdollistaa skrollauksen pienillä näytöillä
                .padding(horizontal = AppDimensions.ScreenPadding, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // 1. Sijaintilupien tilan näyttäminen (Bannerit ylhäällä)
            if (!state.hasLocationPermission) {
                LocationPermissionView(
                    onRequestPermission = { permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION) }
                )
                Spacer(Modifier.height(16.dp))
            } else if (state.isLocating && state.userLocation == null) {
                LocationLoadingView()
                Spacer(Modifier.height(16.dp))
            }

            // 2. Päätellään, mitä sisältöä näytetään tilan perusteella
            val isWritingNew = !state.isViewMode && state.isNewLetterMode
            val isWaiting = !state.isViewMode && !state.isNewLetterMode && state.status == "replying"
            val isReadyOrViewing = !state.isNewLetterMode && (
                    state.status == "replied" ||
                            (state.isViewMode && state.sentText.isNotEmpty()) ||
                            state.isOpened
                    )

            // 3. Näytetään oikea sisältökomponentti

            if (isWritingNew) {
                // Uuden kirjeen kirjoitusnäkymä
                val canGoMap = state.hasLocationPermission && state.isLocationEnabled && state.userLocation != null
                LetterInputView(
                    userName = userName,
                    text = state.text,
                    isSending = state.isSending,
                    canGoMap = canGoMap,
                    onTextChange = { vm.onTextChange(it) },
                    onSend = {
                        // Lähetys onnistui -> Navigoidaan kartalle
                        vm.sendLetter(userName) { id ->
                            onNavigate("${RootRoute.LetterMap.route}/$id")
                        }
                    }
                )
            }

            // Virheilmoitukset (yleinen virhe, esim. verkko-ongelma)
            state.error?.let { msg ->
                Spacer(Modifier.height(16.dp))
                ErrorView(
                    message = msg,
                    onRetry = {
                        vm.sendLetter(userName) { id ->
                            onNavigate("${RootRoute.LetterMap.route}/$id")
                        }
                    }
                )
            }

            // Odotustila (Pukki miettii...)
            if (isWaiting) {
                LetterStatusView()
            }

            // Valmis kirje tai vastauksen katselu
            if (isReadyOrViewing) {
                LetterResultView(
                    sentText = state.sentText,
                    replyText = state.replyText,
                    status = state.status,
                    isOpened = state.isOpened,
                    isViewMode = state.isViewMode,
                    onOpenAR = {
                        val id = state.currentLetterId
                        // Navigoidaan AR-kameraan joko ID:llä tai ilman
                        if (id != null) onNavigate("${LetterRoutes.CAMERA}/$id") else onNavigate(LetterRoutes.CAMERA)
                    },
                    onOpenMath = { vm.showMathChallenge() },
                    onNewLetter = {
                        vm.beginNewLetter()
                        onNavigate(RootRoute.Letter.route + "?mode=new")
                    }
                )
            }

            // Fallback: Jos tila on epämääräinen (esim. tyhjä), tarjotaan nappi uuden kirjeen aloitukseen
            if (!state.isViewMode && !state.isNewLetterMode && state.currentLetterId == null && state.status == null) {
                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = { onNavigate(RootRoute.Letter.route + "?mode=new") },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = StorybookPaper,
                        contentColor = Ink
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) { Text(stringResource(R.string.letter_new_button)) }
            }
        }
    }
}