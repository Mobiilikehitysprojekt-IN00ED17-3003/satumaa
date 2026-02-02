package fi.antero.satumaa.data.model

data class Story(
    val id: String,
    val title: String,
    val content: String,
    val childName: String,
    val style: String,
    val keywords: String,
    val createdAt: Long,
    val isFavorite: Boolean,
    val previewId: String? = null
)