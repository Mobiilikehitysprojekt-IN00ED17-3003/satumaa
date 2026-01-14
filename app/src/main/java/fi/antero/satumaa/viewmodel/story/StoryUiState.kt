package fi.antero.satumaa.ui.viewmodel.story

import fi.antero.satumaa.data.local.entity.StoryEntity

// Tila sadun luomiselle (CreateScreen)
sealed interface StoryCreateUiState {
    data object Idle : StoryCreateUiState
    data object Loading : StoryCreateUiState
    data class Success(val storyId: String) : StoryCreateUiState
    data class Error(val message: String) : StoryCreateUiState
}

// Tila sadun lukemiselle (ReaderScreen)
sealed interface StoryReaderUiState {
    data object Loading : StoryReaderUiState
    data class Success(val story: StoryEntity) : StoryReaderUiState
    data class Error(val message: String) : StoryReaderUiState
}