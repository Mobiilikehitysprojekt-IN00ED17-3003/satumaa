package fi.antero.satumaa.data.remote.dto

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot

/**
 * Data Transfer Object (DTO) sadulle.
 * Tämä vastaa suoraan Firestoren tietorakennetta.
 */
data class StoryDto(
    val id: String,
    val title: String,
    val content: String,
    val childName: String,
    val style: String,
    val keywords: List<String>,
    val createdAt: Long,
    val isFavorite: Boolean
)

/**
 * Apufunktio, joka muuntaa Firestoren DocumentSnapshotin DTO:ksi.
 * Käsittelee puuttuvat kentät turvallisesti.
 */
fun DocumentSnapshot.toStoryDto(): StoryDto? {
    val title = getString("title")
    val content = getString("content")

    // Jos kriittiset tiedot puuttuvat, hylätään dokumentti
    if (title == null || content == null) return null

    val keywordsList = get("keywords") as? List<*>
    val keywordsStringList = keywordsList?.filterIsInstance<String>() ?: emptyList()

    val timestamp = getTimestamp("createdAt")
    val createdAtMillis = timestamp?.seconds?.times(1000) ?: System.currentTimeMillis()

    return StoryDto(
        id = id,
        title = title,
        content = content,
        childName = getString("childName") ?: "",
        style = getString("style") ?: "",
        keywords = keywordsStringList,
        createdAt = createdAtMillis,
        isFavorite = getBoolean("isFavorite") ?: false
    )
}