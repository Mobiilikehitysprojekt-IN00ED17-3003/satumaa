package fi.antero.satumaa.data.remote.dto

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot

/**
 * StoryDto (Data Transfer Object) saduille.
 *
 * Tämä luokka vastaa suoraan Firestore-tietokannan 'stories'-kokoelman rakennetta.
 * DTO eristää ulkoisen rajapinnan muutokset sovelluksen sisäisestä logiikasta.
 */
data class StoryDto(
    val id: String,
    val title: String,
    val content: String,
    val childName: String,
    val style: String,
    val keywords: List<String>, // Pilvessä lista, paikallisessa kannassa string
    val createdAt: Long,
    val isFavorite: Boolean
)

/**
 * Apufunktio, joka muuntaa Firestoren DocumentSnapshotin DTO:ksi.
 * * Sisältää logiikkaa datan puhdistamiseen ja tyyppimuunnoksiin (esim. List<Any> -> List<String>).
 */
fun DocumentSnapshot.toStoryDto(): StoryDto? {
    val title = getString("title")
    val content = getString("content")

    // Validointi: Otsikko ja sisältö ovat pakollisia
    if (title == null || content == null) return null

    // Turvallinen tyyppimuunnos listalle (Firestore palauttaa List<*>)
    val keywordsList = get("keywords") as? List<*>
    val keywordsStringList = keywordsList?.filterIsInstance<String>() ?: emptyList()

    // Aikaleiman käsittely
    val timestamp = getTimestamp("createdAt")
    // Muunnetaan sekunnit millisekuneiksi (* 1000)
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