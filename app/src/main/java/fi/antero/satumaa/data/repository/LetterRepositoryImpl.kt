package fi.antero.satumaa.data.repository

import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class LetterRepositoryImpl(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : LetterRepository {

    override fun sendLetter(letterText: String): Task<DocumentReference> {
        val uid = auth.currentUser?.uid
            ?: return Tasks.forException(IllegalStateException("User not logged in"))

        val data = hashMapOf(
            "userId" to uid,
            "letterText" to letterText,
            "status" to "replying",
            "createdAt" to FieldValue.serverTimestamp()
        )

        return db.collection("letters").add(data)
    }
}
