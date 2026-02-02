package fi.antero.satumaa.data.repository

import fi.antero.satumaa.data.model.Letter
import kotlinx.coroutines.flow.Flow

interface LetterRepository {
    // Hakee kirjeet paikallisesta kannasta (Room) -> UI päivittyy automaattisesti
    fun getLetters(): Flow<List<Letter>>

    // UUSI: Hakee yksittäisen kirjeen (View Mode)
    suspend fun getLetterById(id: String): Letter?

    // Synkkaa kirjeet pilvestä paikalliseen kantaan
    suspend fun refreshLetters()

    // Lähettää kirjeen
    suspend fun sendLetter(letterText: String, childName: String): Result<String>

    // UUSI: Poistaa kirjeen
    suspend fun deleteLetter(letterId: String)

    // Onko kirje avattu
    suspend fun markAsOpened(id: String)
}