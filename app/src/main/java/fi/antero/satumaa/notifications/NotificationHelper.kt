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

object NotificationHelper {

    const val CHANNEL_ID = "santa_replies"
    const val EXTRA_LETTER_ID = "extra_letter_id" // Tärkeä: avain tiedon siirtoon

    fun ensureChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Joulupukin vastaukset",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Ilmoitukset kun Joulupukki vastaa kirjeeseen"
            }
            val nm = context.getSystemService(NotificationManager::class.java)
            nm.createNotificationChannel(channel)
        }
    }

    fun showSantaReplyNotification(context: Context, letterId: String) {
        ensureChannel(context)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!granted) return
        }

        // Luodaan intent, joka avaa MainActivityn
        val intent = Intent(context, MainActivity::class.java).apply {
            // SINGLE_TOP: Jos sovellus on auki, käytä olemassa olevaa instanssia (kutsuu onNewIntent)
            // CLEAR_TOP: Poistaa mahdolliset päällä olevat muut aktiviteetit
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra(EXTRA_LETTER_ID, letterId)
        }

        // Uniikki RequestCode (letterId.hashCode()) on kriittinen, jotta
        // eri kirjeiden PendingIntentit eivät ylikirjoita toisiaan.
        val pendingIntent = PendingIntent.getActivity(
            context,
            letterId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Joulupukki vastasi 🎅")
            .setContentText("Avaa sovellus lukeaksesi vastauksen")
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        // Käytetään ID:tä notifikaatiossa, jotta useampi voi näkyä kerralla
        NotificationManagerCompat.from(context).notify(letterId.hashCode(), notification)
    }
}