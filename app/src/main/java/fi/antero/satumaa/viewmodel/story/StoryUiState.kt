package fi.antero.satumaa.ui.viewmodel.story

import fi.antero.satumaa.data.model.Story

sealed interface StoryUiState {
    data object Idle : StoryUiState
    data object Loading : StoryUiState
    data class Success(val story: Story) : StoryUiState
    data class Error(val message: String) : StoryUiState
}