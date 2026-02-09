package fi.antero.satumaa.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import fi.antero.satumaa.MainActivity
import fi.antero.satumaa.R

/**
 * Apuluokka Android-ilmoitusten luomiseen ja näyttämiseen.
 *
 * Hoitaa:
 * 1. Ilmoituskanavan (Notification Channel) luonnin (vaatimus Android 8.0+).
 * 2. Intentin luonnin, jotta ilmoitusta klikkaamalla aukeaa oikea kirje.
 * 3. Ilmoituksen näyttämisen.
 */
object NotificationHelper {

    const val CHANNEL_ID = "santa_replies"
    const val EXTRA_LETTER_ID = "extra_letter_id" // Avain, jolla MainActivity tunnistaa avattavan kirjeen

    /**
     * Varmistaa, että ilmoituskanava on luotu.
     * Tätä on turvallista kutsua useasti; Android ei luo kanavaa uudestaan, jos se on jo olemassa.
     */
    fun ensureChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = context.getString(R.string.notif_channel_name)
            val desc = context.getString(R.string.notif_channel_desc)

            val channel = NotificationChannel(
                CHANNEL_ID,
                name,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = desc
            }
            val nm = context.getSystemService(NotificationManager::class.java)
            nm.createNotificationChannel(channel)
        }
    }

    /**
     * Näyttää ilmoituksen saapuneesta vastauksesta.
     *
     * @param context Sovelluskonteksti.
     * @param letterId Kirjeen ID. Tätä käytetään sekä Intentissä että ilmoituksen ID:nä.
     */
    fun showSantaReplyNotification(context: Context, letterId: String) {
        ensureChannel(context)

        // Android 13+ (Tiramisu) vaatii runtime-luvan ilmoituksille.
        // Jos lupaa ei ole, emme voi näyttää ilmoitusta.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!granted) return
        }

        // Luodaan intent, joka avaa MainActivityn
        val intent = Intent(context, MainActivity::class.java).apply {
            // SINGLE_TOP: Jos sovellus on jo auki, ei luoda uutta Activitya vaan kutsutaan onNewIntent.
            // CLEAR_TOP: Siivoaa backstackin.
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra(EXTRA_LETTER_ID, letterId)
        }

        // PendingIntent tarvitaan, jotta ilmoitus voi käynnistää sovelluksen myöhemmin.
        // letterId.hashCode() varmistaa, että jokaisella kirjeellä on uniikki RequestCode.
        // Jos käyttäisimme vakiota (esim. 0), uusi ilmoitus ylikirjoittaisi edellisen Intentin extrat.
        val pendingIntent = PendingIntent.getActivity(
            context,
            letterId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Varmista, että tämä resurssi on olemassa
            .setContentTitle(context.getString(R.string.notif_title))
            .setContentText(context.getString(R.string.notif_body))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true) // Ilmoitus katoaa klikattaessa
            .build()

        // Näytetään ilmoitus. ID:nä käytetään hashcodea, jotta useampi eri kirjeen
        // ilmoitus voi näkyä yhtä aikaa ilman, että ne korvaavat toisensa.
        NotificationManagerCompat.from(context).notify(letterId.hashCode(), notification)
    }
}