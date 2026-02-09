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
 * Arkkitehtuurinen huomio:
 * Vaikka luokan nimi on "FunctionsSource", tämä toteutus käyttää toistaiseksi
 * suoraa Firestore-kommunikaatiota client-puolelta (ns. "Client-side logic").
 * Rakenne on kuitenkin suunniteltu niin, että logiikka on helppo siirtää
 * Cloud Functions -funktion taakse (Server-side logic) tulevaisuudessa ilman,
 * että Repository-tasoa tarvitsee muuttaa.
 */
class LetterFunctionsSource @Inject constructor(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth
) {

    /**
     * Lähettää uuden kirjeen Firestoreen.
     *
     * Prosessi sisältää client-side validoinnin kustannusten ja turvallisuuden optimoimiseksi:
     * 1. **Postilaatikon koko:** Tarkistetaan, ettei käyttäjällä ole liikaa kirjeitä (max 10).
     * Tämä tehdään `count()`-kyselyllä, joka on halpa ja nopea.
     * 2. **Rate Limiting:** Tarkistetaan, ettei käyttäjä spämmää (max 1 kirje / min).
     * 3. **Kirjoitus:** Jos tarkistukset menevät läpi, kirje tallennetaan.
     *
     * @return Result<String>: Onnistuessa uuden kirjeen ID, epäonnistuessa Exception.
     */
    suspend fun sendLetter(letterText: String, childName: String): Result<String> {
        val uid = auth.currentUser?.uid
            ?: return Result.failure(IllegalStateException("AUTH_REQUIRED"))

        val collectionRef = db.collection("users").document(uid).collection("letters")

        try {
            // 1. Tarkista montako kirjettä käyttäjällä on (halpa server-side count)
            // AggregateSource.SERVER varmistaa, että laskenta tehdään palvelimella eikä ladata dokumentteja.
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
                    if (diff < 60 * 1000) { // 60 sekuntia
                        return Result.failure(Exception("RATE_LIMIT_LETTER"))
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // Strategia: Jos esitarkistus epäonnistuu (esim. verkkovirhe), sallimme yrityksen jatkua.
            // Firestoren Security Rules torjuu sen palvelimella, jos säännöt rikotaan.
        }

        // Valmistellaan data
        val data = hashMapOf(
            "userId" to uid,
            "childName" to childName,
            "letterText" to letterText,
            "status" to "replying", // Asetetaan tila odottamaan vastausta
            "createdAt" to FieldValue.serverTimestamp() // Palvelimen aika on luotettavin
        )

        return try {
            val docRef = collectionRef.add(data).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            val msg = e.message ?: ""
            // Tulkitaan Firestore Security Rules -virheet ymmärrettävämmiksi koodeiksi
            if (msg.contains("PERMISSION_DENIED", ignoreCase = true) ||
                msg.contains("Missing or insufficient permissions")) {
                // Oletetaan, että sääntövirhe johtuu postilaatikon täyttymisestä (yleisin syy)
                Result.failure(Exception("MAILBOX_FULL"))
            } else {
                Result.failure(e)
            }
        }
    }
}