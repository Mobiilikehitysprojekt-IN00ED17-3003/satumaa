package fi.antero.satumaa.util

import kotlin.random.Random

data class MathChallenge(
    val question: String,
    val correctAnswer: Int
)

object MathChallengeGenerator {

    // Generoi yksinkertaisen laskutoimituksen (0-10 alueella)
    fun generateChallenge(): MathChallenge {
        val isAddition = Random.nextBoolean()

        if (isAddition) {
            // Yhteenlasku (summa max 10)
            val a = Random.nextInt(1, 6) // 1..5
            val b = Random.nextInt(1, 6) // 1..5
            return MathChallenge("$a + $b = ?", a + b)
        } else {
            // Vähennyslasku (ei negatiivisia, max vähennettävä 10)
            val a = Random.nextInt(2, 11) // 2..10
            val b = Random.nextInt(1, a)  // b on aina pienempi kuin a
            return MathChallenge("$a - $b = ?", a - b)
        }
    }
}