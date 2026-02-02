package fi.antero.satumaa.data.repository

import fi.antero.satumaa.data.model.Story
import kotlinx.coroutines.flow.Flow

interface StoryRepository {
    fun getStories(): Flow<List<Story>>

    suspend fun getStory(id: String): Story?

    suspend fun refreshStories()

    suspend fun generateStoryPreview(
        childName: String,
        keywords: List<String>,
        length: String,
        style: String
    ): Result<Story>

    suspend fun saveStory(story: Story): Result<String>

    suspend fun deleteStory(storyId: String)
}