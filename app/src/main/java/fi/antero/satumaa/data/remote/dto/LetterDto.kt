package fi.antero.satumaa.data.remote.dto

import com.google.firebase.firestore.DocumentSnapshot

/**
 * DTO (Data Transfer Object) kirjeen siirtämiseen Firebasesta sovellukseen.
 */
data class LetterDto(
    val id: String,
    val userId: String,
    val childName: String,
    val letterText: String,
    val replyText: String?,
    val status: String,
    val createdAt: Long,
    val repliedAt: Long?
)

/**
 * Muuntaa Firestore-dokumentin DTO:ksi.
 */
fun DocumentSnapshot.toLetterDto(): LetterDto? {
    val userId = getString("userId")
    val letterText = getString("letterText")

    // Varmistetaan että pakolliset kentät löytyvät
    if (userId == null || letterText == null) return null

    val createdTs = getTimestamp("createdAt")
    val repliedTs = getTimestamp("repliedAt")

    return LetterDto(
        id = id,
        userId = userId,
        childName = getString("childName") ?: "",
        letterText = letterText,
        replyText = getString("replyText"),
        status = getString("status") ?: "replying",
        createdAt = createdTs?.toDate()?.time ?: System.currentTimeMillis(),
        repliedAt = repliedTs?.toDate()?.time
    )
}