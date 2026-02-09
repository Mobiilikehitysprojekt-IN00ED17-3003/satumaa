plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("kotlin-kapt") // Tarvitaan Hiltille ja Roomille koodin generointiin
    alias(libs.plugins.hilt) // Dependency Injection
    alias(libs.plugins.google.services) // TÄRKEÄ: Yhdistää sovelluksen Firebaseen (lukee json-tiedoston)
    alias(libs.plugins.firebase.crashlytics) // Raportoi kaatumiset pilveen
}

android {
    namespace = "fi.antero.satumaa"
    compileSdk = 36 // Käyttää uusimpia Android-työkaluja

    defaultConfig {
        applicationId = "fi.antero.satumaa"
        minSdk = 29 // Android 10. Tätä vanhemmissa puhelimissa sovellus ei toimi.
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            // Pienentää ja optimoi koodin julkaisuversiossa.
            // Debuggauksessa false, jotta virheilmoitukset ovat selkeitä.
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true // Ottaa Jetpack Composen käyttöön
        buildConfig = true // Mahdollistaa "BuildConfig.DEBUG" -tarkistuksen koodissa (käytettiin AppCheckissä)
    }
    kapt {
        correctErrorTypes = true // Auttaa Hiltiä ymmärtämään tyypit paremmin virhetilanteissa
    }
}

dependencies {
    // --- KÄYTTÖLIITTYMÄ (COMPOSE) ---
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom)) // Pitää Compose-versiot synkassa
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)

    // --- NAVIGOINTI ---
    implementation(libs.androidx.navigation.compose)

    // --- DEPENDENCY INJECTION (HILT) ---
    // Nämä hoitavat @Inject, @HiltViewModel ja @HiltWorker -anotaatiot
    implementation(libs.hilt.android)
    implementation(libs.androidx.compose.runtime.saveable)
    implementation(libs.androidx.lifecycle.process) // ProcessLifecycleOwner (käytettiin ilmoituksissa)
    kapt(libs.hilt.compiler) // Generoi Hilt-koodin käännöksen aikana
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.hilt.work) // WorkManager-tuki Hiltille
    kapt(libs.androidx.hilt.compiler)

    // --- PAIKALLINEN TIETOKANTA (ROOM) ---
    // Tallentaa sadut ja kirjeet puhelimeen (Offline-First)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx) // Coroutine-tuki Roomille
    kapt(libs.androidx.room.compiler)

    // --- FIREBASE ---
    // BOM (Bill of Materials) hallitsee versiot automaattisesti
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.auth) // Kirjautuminen
    implementation(libs.firebase.firestore) // Pilvitietokanta

    // Firebase Cloud Functions -asiakasrajapinta palvelinpuolen logiikan kutsumiseen.
    implementation(libs.firebase.functions)

    // --- TIETOTURVA (APP CHECK) ---
    // Varmistaa, että pyynnöt tulevat oikeasta sovelluksesta eikä hakkereilta.
    implementation(libs.firebase.appcheck.playintegrity) // Tuotanto
    debugImplementation(libs.firebase.appcheck.debug) // Kehitys (Debug)

    // --- KAMERA ---
    // Kirjeen etsintä AR-tyylisesti
    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)

    // --- TAUSTATYÖT JA SIJAINTI ---
    implementation(libs.play.services.location) // GPS-sijainti
    implementation(libs.androidx.work.runtime.ktx) // WorkManager (synkkaus ja poistot taustalla)
    implementation(libs.play.services.auth) // Google Sign-In -tuki


    //implementation(platform("com.squareup.okhttp3:okhttp-bom:4.12.0"))
    //implementation("com.squareup.okhttp3:okhttp")
    //implementation("com.squareup.okhttp3:logging-interceptor")

    // --- KARTAT JA KAAVIOT ---
    implementation(libs.osmdroid.android) // Karttanäkymä (OpenStreetMap)
    implementation(libs.ycharts) // Pylväsdiagrammit ja graafit
    implementation(libs.mathparser) // Tilastojen laskenta

    // --- TESTAUS ---
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}