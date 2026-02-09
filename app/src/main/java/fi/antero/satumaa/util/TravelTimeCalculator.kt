package fi.antero.satumaa.util

import kotlin.random.Random

/**
 * Laskee "matka-ajan" kirjeelle.
 *
 * Tämä luo illuusion siitä, että kirje oikeasti matkustaa Korvatunturille.
 * Ilman tätä vastaus tulisi liian nopeasti (muutamassa sekunnissa), mikä rikkoisi
 * taian lapselta.
 */
object TravelTimeCalculator {
    // Matkan kesto: min 15 sekuntia, max 30 sekuntia.
    // Tämä on tarpeeksi pitkä aika luomaan jännitystä, mutta tarpeeksi lyhyt,
    // ettei lapsi kyllästy odottamaan.
    private const val MIN_DURATION_MS = 15_000L
    private const val MAX_DURATION_MS = 30_000L

    /**
     * Palauttaa satunnaisen (mutta deterministisen) matka-ajan kirjeelle.
     *
     * @param letterId Kirjeen ID. Käytetään satunnaislukugeneraattorin siemenenä (seed).
     * Tämä tarkoittaa, että samalle kirjeelle arvotaan AINA sama matka-aika.
     * Jos sovellus käynnistetään uudelleen, matka-aika ei muutu.
     */
    fun getTravelDuration(letterId: String): Long {
        if (letterId.isEmpty()) return MIN_DURATION_MS

        // Käytetään ID:n hashcodea siemenenä -> Deterministinen lopputulos
        val seed = letterId.hashCode().toLong()
        val random = Random(seed)

        return random.nextLong(MIN_DURATION_MS, MAX_DURATION_MS)
    }

    /**
     * Laskee tarkan kellonajan (timestamp), jolloin matka on ohi.
     *
     * @param letterId Kirjeen ID (vaikuttaa kestoon).
     * @param createdAtMs Kirjeen luontihetki (millisekunteina).
     * @return Toimitusaika (Unix timestamp ms).
     */
    fun getDeliveryTime(letterId: String, createdAtMs: Long): Long {
        val duration = getTravelDuration(letterId)
        return createdAtMs + duration
    }
}