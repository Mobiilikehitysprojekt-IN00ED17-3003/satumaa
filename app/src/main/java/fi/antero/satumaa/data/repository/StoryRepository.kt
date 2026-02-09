package fi.antero.satumaa.data.repository

import fi.antero.satumaa.data.model.Story
import kotlinx.coroutines.flow.Flow

/**
 * Rajapinta tarinoiden (Story) hallintaan.
 *
 * Erottaa tietolähteet (Room, Firestore, Cloud Functions) sovelluslogiikasta.
 * UI-kerros (ViewModel) käyttää vain tätä rajapintaa tietämättä, mistä data tulee.
 */
interface StoryRepository {

    /**
     * Hakee kaikki tallennetut sadut reaktiivisena virtana.
     * Datalähde: Paikallinen Room-tietokanta.
     */
    fun getStories(): Flow<List<Story>>

    /**
     * Hakee yksittäisen sadun ID:n perusteella.
     * Datalähde: Paikallinen Room-tietokanta.
     */
    suspend fun getStory(id: String): Story?

    /**
     * Synkronoi sadut pilvestä paikalliseen kantaan.
     * Tätä kutsutaan sovelluksen käynnistyessä tai "Päivitä"-toiminnolla.
     */
    suspend fun refreshStories()

    /**
     * Generoi uuden sadun esikatselun tekoälyllä.
     * HUOM: Tämä EI vielä tallenna satua tietokantaan, vaan palauttaa
     * väliaikaisen Story-objektin esikatselua varten.
     *
     * Datalähde: Firebase Cloud Functions (AI).
     */
    suspend fun generateStoryPreview(
        childName: String,
        keywords: List<String>,
        length: String,
        style: String
    ): Result<Story>

    /**
     * Tallentaa esikatselussa olevan sadun pysyvästi.
     * 1. Lähettää tiedot pilveen tallennettavaksi.
     * 2. Tallentaa onnistuneen tuloksen paikalliseen kantaan.
     */
    suspend fun saveStory(story: Story): Result<String>

    /**
     * Poistaa sadun.
     * Poistaa heti paikallisesti ja asettaa taustatyön poistamaan pilvestä.
     */
    suspend fun deleteStory(storyId: String)
}