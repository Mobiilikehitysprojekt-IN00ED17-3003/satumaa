package fi.antero.satumaa.data.repository

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference

interface LetterRepository {
    fun sendLetter(letterText: String): Task<DocumentReference>
}
