package fi.antero.satumaa.ui.viewmodel.story

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fi.antero.satumaa.data.repository.StoryRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Hallinnoi satujen listanäkymää (StoryListScreen).
 */
@HiltViewModel
class StoryListViewModel @Inject constructor(
    private val repository: StoryRepository
) : ViewModel() {

    /**
     * Satulista "Hot Flow" -muodossa.
     *
     * repository.getStories() palauttaa Room-tietokannan Flow'n, joka päivittyy automaattisesti.
     * stateIn muuttaa sen StateFlow'ksi, joka säilyttää viimeisimmän arvon UI:lle.
     *
     * SharingStarted.WhileSubscribed(5000) pitää datan muistissa 5 sekuntia sen jälkeen,
     * kun viimeinen tilaaja (UI) poistuu. Tämä auttaa esim. näytön käännöksissä,
     * jottei tietokantahakua tehdä turhaan uudestaan.
     */
    val stories = repository.getStories()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        // Kun käyttäjä avaa listan, yritetään synkronoida tuoreimmat sadut pilvestä.
        // Tämä tapahtuu taustalla eikä estä paikallisen datan näyttämistä heti.
        refreshStories()
    }

    /**
     * Käskee repositorya hakemaan uusimmat tiedot pilvestä.
     */
    fun refreshStories() {
        viewModelScope.launch {
            repository.refreshStories()
        }
    }

    /**
     * Poistaa sadun.
     * Repository hoitaa paikallisen poiston heti ja pilvipoiston taustalla.
     */
    fun deleteStory(storyId: String) {
        viewModelScope.launch {
            repository.deleteStory(storyId)
        }
    }
}