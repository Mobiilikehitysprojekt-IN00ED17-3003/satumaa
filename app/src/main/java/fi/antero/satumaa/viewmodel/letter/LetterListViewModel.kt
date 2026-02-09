package fi.antero.satumaa.ui.viewmodel.letter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fi.antero.satumaa.data.repository.LetterRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel kirjehistorialistalle (LetterListScreen).
 * Yksinkertainen ViewModel, joka vain välittää datan repositoriosta UI:lle.
 */
@HiltViewModel
class LetterListViewModel @Inject constructor(
    private val repository: LetterRepository
) : ViewModel() {

    // Muunnetaan Repositoryn Flow (joka päivittyy automaattisesti Roomista)
    // StateFlow'ksi, jota UI voi kuunnella tehokkaasti.
    val letters = repository.getLetters()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000), // Pidetään data muistissa 5s tilauksen loppumisen jälkeen
            initialValue = emptyList()
        )

    init {
        // Kun lista avataan, varmistetaan että meillä on tuorein data pilvestä
        refreshLetters()
    }

    fun refreshLetters() {
        viewModelScope.launch {
            try {
                repository.refreshLetters()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun deleteLetter(letterId: String) {
        viewModelScope.launch {
            repository.deleteLetter(letterId)
        }
    }
}