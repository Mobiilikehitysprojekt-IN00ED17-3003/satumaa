package fi.antero.satumaa.data.repository

import com.google.firebase.auth.FirebaseUser

/**
 * Rajapinta käyttäjän tunnistautumiseen.
 *
 * Määrittelee sovelluksen tarvitsemat autentikaatiotoiminnot riippumatta toteutustavasta.
 * Tämä mahdollistaa esim. Mock-repositorion käytön testeissä tai
 * autentikaatiopalvelun vaihtamisen myöhemmin.
 */
interface AuthRepository {

    /**
     * Palauttaa tällä hetkellä kirjautuneen käyttäjän tai null, jos ketään ei ole kirjautunut.
     */
    val currentUser: FirebaseUser?

    /**
     * Kirjaa käyttäjän sisään Google-tunnuksilla.
     * @param idToken Googlen API:lta saatu ID-token.
     */
    suspend fun signInWithGoogle(idToken: String): Result<FirebaseUser>

    /**
     * Kirjaa käyttäjän sisään anonyymisti (pikakokeilu).

     */
    suspend fun signInAnonymously(): Result<FirebaseUser>

    /**
     * Kirjaa käyttäjän ulos.
     */
    fun signOut()
}