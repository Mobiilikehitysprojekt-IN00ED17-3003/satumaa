package fi.antero.satumaa.data.model

/**
 * Domain-malli sadulle ('Story').
 *
 * Edustaa yhtä generoituja satua sovelluksessa.
 * Tätä mallia käytetään sekä "Luo uusi satu" -näkymässä (esikatselu)
 * että "Omat sadut" -listauksessa (tallennetut).
 *
 * @param id Sadun yksilöllinen tunniste.
 * - Tyhjä (""), jos kyseessä on vasta generoitu esikatselu, jota ei ole tallennettu.
 * - UUID, jos satu on tallennettu tietokantaan.
 * @param title Sadun otsikko (generoitu tekoälyllä).
 * @param content Varsinainen sadun teksti.
 * @param childName Lapsen nimi, jolle satu on personoitu.
 * @param style Sadun tyyli (esim. "EXCITING", "FUNNY").
 * @param keywords Taikasanat, joita käytettiin generoinnissa.
 * @param createdAt Luontiaika (millisekunteina, Long).
 * @param isFavorite Onko satu merkitty suosikiksi (UI-toiminnallisuus).
 * @param previewId Väliaikainen tunniste generointivaiheessa (käytetään esim. virheenjäljitykseen).
 * Tämä on null, kun satu ladataan tietokannasta.
 */
data class Story(
    val id: String,
    val title: String,
    val content: String,
    val childName: String,
    val style: String,
    val keywords: String,
    val createdAt: Long,
    val isFavorite: Boolean,
    val previewId: String? = null
)