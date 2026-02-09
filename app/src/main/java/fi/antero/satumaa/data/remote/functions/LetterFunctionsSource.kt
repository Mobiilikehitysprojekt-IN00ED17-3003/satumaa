package fi.antero.satumaa.data.remote.functions

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import java.util.Date
import javax.inject.Inject

/**
 * LetterFunctionsSource hallinnoi kirjeiden lähettämistä pilveen.
 *
 * Tällä hetkellä käytämme suoraa Firestore-kirjoitusta, mutta rakenne on
 * valmis siirtymään Cloud Functions -kutsuun (kuten StoryFunctionsSource),
 * jos logiikka siirretään myöhemmin backendin puolelle.
 */
class LetterFunctionsSource @Inject constructor(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth
) {

    /**
     * Lähettää kirjeen Firestoreen.
     *
     * Tarkistaa ensin:
     * 1. Onko postilaatikko täynnä (max 10).
     * 2. Onko edellisestä kirjeestä kulunut tarpeeksi aikaa (Rate limit).
     */
    suspend fun sendLetter(letterText: String, childName: String): Result<String> {
        val uid = auth.currentUser?.uid
            ?: return Result.failure(IllegalStateException("AUTH_REQUIRED"))

        val collectionRef = db.collection("users").document(uid).collection("letters")

        try {
            // 1. Tarkista montako kirjettä käyttäjällä on (halpa server-side count)
            val countQuery = collectionRef.count().get(AggregateSource.SERVER).await()
            if (countQuery.count >= 10) {
                return Result.failure(Exception("MAILBOX_FULL"))
            }

            // 2. Tarkista milloin edellinen kirje lähetettiin (Rate limiting 1 min)
            val lastLetterQuery = collectionRef
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .await()

            if (!lastLetterQuery.isEmpty) {
                val lastDoc = lastLetterQuery.documents[0]
                val lastDate = lastDoc.getTimestamp("createdAt")?.toDate()
                if (lastDate != null) {
                    val diff = Date().time - lastDate.time
                    if (diff < 60 * 1000) {
                        return Result.failure(Exception("RATE_LIMIT_LETTER"))
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // Jos tarkistus epäonnistuu (esim. verkkovirhe), sallimme yrityksen jatkua.
        }

        val data = hashMapOf(
            "userId" to uid,
            "childName" to childName,
            "letterText" to letterText,
            "status" to "replying",
            "createdAt" to FieldValue.serverTimestamp()
        )

        return try {
            val docRef = collectionRef.add(data).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            val msg = e.message ?: ""
            // Firestore Security Rules -virheiden tulkinta
            if (msg.contains("PERMISSION_DENIED", ignoreCase = true) ||
                msg.contains("Missing or insufficient permissions")) {
                Result.failure(Exception("MAILBOX_FULL"))
            } else {
                Result.failure(e)
            }
        }
    }
}