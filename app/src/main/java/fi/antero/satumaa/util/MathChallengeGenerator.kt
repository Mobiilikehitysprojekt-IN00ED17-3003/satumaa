package fi.antero.satumaa.util

import kotlin.random.Random

/**
 * Tietomalli yhdelle matikkahaasteelle.
 */
data class MathChallenge(
    val question: String,   // Esim. "5 + 3 = ?"
    val correctAnswer: Int  // Esim. 8
)

/**
 * Generoi yksinkertaisia laskutehtäviä "Parental Gate" -toimintoa varten.
 *
 * Tarkoitus on varmistaa, että käyttäjä on lukutaitoinen aikuinen (tai isompi sisarus),
 * eikä pikkulapsi, joka vahingossa avaa kirjeen tai poistuu sovelluksesta.
 */
object MathChallengeGenerator {

    /**
     * Luo uuden satunnaisen haasteen (yhteen- tai vähennyslasku).
     * Luvut pidetään pieninä (0-10), jotta laskeminen on nopeaa aikuiselle.
     */
    fun generateChallenge(): MathChallenge {
        val isAddition = Random.nextBoolean()

        if (isAddition) {
            // Yhteenlasku (summa max 10)
            val a = Random.nextInt(1, 6) // 1..5
            val b = Random.nextInt(1, 6) // 1..5
            return MathChallenge("$a + $b = ?", a + b)
        } else {
            // Vähennyslasku (tulos ei saa olla negatiivinen)
            val a = Random.nextInt(2, 11) // 2..10
            val b = Random.nextInt(1, a)  // b on aina pienempi kuin a -> tulos > 0
            return MathChallenge("$a - $b = ?", a - b)
        }
    }
}