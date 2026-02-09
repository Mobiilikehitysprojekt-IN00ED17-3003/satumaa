package fi.antero.satumaa.ui.viewmodel.story

import fi.antero.satumaa.data.model.Story

/**
 * StoryViewModelin UI-tilat (Sealed Interface).
 * Tämä mahdollistaa tyyppiturvallisen tilanhallinnan Composessa (when-lauseke).
 */
sealed interface StoryUiState {
    // Alkutila: Käyttäjä syöttää tietoja
    data object Idle : StoryUiState

    // Lataustila: Tekoäly generoi satua tai satua ladataan
    data object Loading : StoryUiState

    // Onnistunut tila: Satu on valmis näytettäväksi
    data class Success(val story: Story) : StoryUiState

    // Virhetila: Jotain meni pieleen (sisältää käännetyn viestin)
    data class Error(val message: String) : StoryUiState
}