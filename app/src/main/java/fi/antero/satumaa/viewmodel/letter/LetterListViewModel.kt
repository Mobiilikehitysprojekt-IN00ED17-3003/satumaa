package fi.antero.satumaa.ui.viewmodel.letter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fi.antero.satumaa.data.repository.LetterRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LetterListViewModel @Inject constructor(
    private val repository: LetterRepository
) : ViewModel() {

    val letters = repository.getLetters()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
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