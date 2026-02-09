package fi.antero.satumaa.data.repository

import fi.antero.satumaa.data.model.Letter
import kotlinx.coroutines.flow.Flow

/**
 * LetterRepository määrittelee rajapinnan kirjeiden käsittelylle.
 *
 * Tämä abstraktiokerros eristää UI:n (ViewModel) tietolähteiden yksityiskohdista
 * (Room, Firestore, Cloud Functions). UI tietää vain, että se saa listan 'Letter'-objekteja.
 */
interface LetterRepository {

    /**
     * Hakee kaikki käyttäjän kirjeet reaktiivisena virtana.
     * Palauttaa Flow'n, joka päivittyy automaattisesti aina, kun paikallinen tietokanta muuttuu.
     */
    fun getLetters(): Flow<List<Letter>>

    /**
     * Hakee yksittäisen kirjeen tiedot.
     * Käytetään esimerkiksi, kun käyttäjä avaa kirjeen ilmoituksesta tai listasta.
     */
    suspend fun getLetterById(id: String): Letter?

    /**
     * Käynnistää synkronoinnin pilvestä.
     * Hakee uusimmat tiedot Firestoresta ja päivittää paikallisen tietokannan.
     */
    suspend fun refreshLetters()

    /**
     * Lähettää uuden kirjeen.
     * Palauttaa Resultin, joka sisältää onnistuessa uuden kirjeen ID:n.
     */
    suspend fun sendLetter(letterText: String, childName: String): Result<String>

    /**
     * Poistaa kirjeen.
     * Toteutus hoitaa sekä paikallisen poiston että pilvipoiston (mahdollisesti taustalla).
     */
    suspend fun deleteLetter(letterId: String)

    /**
     * Merkitsee kirjeen avatuksi (käyttäjä on nähnyt vastauksen).
     * Tämä tallennetaan vain paikallisesti (LetterLocalState).
     */
    suspend fun markAsOpened(id: String)
}