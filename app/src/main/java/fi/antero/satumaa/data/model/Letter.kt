package fi.antero.satumaa.data.model

import com.google.firebase.Timestamp

data class Letter(
    val id: String = "",
    val userId: String = "",
    val letterText: String = "",
    val status: String = "replying", // replying | replied | error
    val createdAt: Timestamp? = null,
    val replyText: String? = null,
    val repliedAt: Timestamp? = null,
    val errorMessage: String? = null,
    val isOpened: Boolean = false
)