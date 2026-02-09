package fi.antero.satumaa.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt-moduuli Firebase-palveluille.
 *
 * Eristää Firebase-kirjaston alustamisen muusta koodista.
 * Jos Firebase-versiot tai alustuslogiikka muuttuu, muutos tehdään vain tänne.
 */
@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    /**
     * Tarjoaa Firebase Authentication -instanssin.
     * Käytetään käyttäjien kirjautumiseen ja tunnistautumiseen.
     */
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = Firebase.auth

    /**
     * Tarjoaa Cloud Firestore -tietokantainstanssin.
     * Käytetään datan tallentamiseen ja synkronointiin pilvessä.
     */
    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore = Firebase.firestore

    /**
     * Tarjoaa Cloud Functions -instanssin.
     * Käytetään tekoäly-backendin kutsumiseen (esim. sadun generointi).
     *
     * TÄRKEÄÄ: Määritellään alueeksi "us-central1" (tai mikä onkaan backendin alue),
     * jotta kutsut menevät oikeaan datakeskukseen.
     */
    @Provides
    @Singleton
    fun provideFirebaseFunctions(): FirebaseFunctions {
        return Firebase.functions("us-central1")
    }
}