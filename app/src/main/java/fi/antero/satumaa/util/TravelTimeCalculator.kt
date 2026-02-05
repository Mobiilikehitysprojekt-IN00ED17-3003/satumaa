package fi.antero.satumaa.util

import kotlin.random.Random

object TravelTimeCalculator {
    // Matkan kesto: min 15 sekuntia, max 30 sekuntia.
    // Tämä on "random", mutta ID:n perusteella aina sama kyseiselle kirjeelle.
    private const val MIN_DURATION_MS = 15_000L
    private const val MAX_DURATION_MS = 30_000L

    fun getTravelDuration(letterId: String): Long {
        if (letterId.isEmpty()) return MIN_DURATION_MS
        val seed = letterId.hashCode().toLong()
        val random = Random(seed)
        return random.nextLong(MIN_DURATION_MS, MAX_DURATION_MS)
    }

    // Laskee milloin matka on ohi (absoluuttinen aika millisekunteina)
    // Tarvitsee kirjeen luontiajan (createdAtMs)
    fun getDeliveryTime(letterId: String, createdAtMs: Long): Long {
        val duration = getTravelDuration(letterId)
        return createdAtMs + duration
    }
}
