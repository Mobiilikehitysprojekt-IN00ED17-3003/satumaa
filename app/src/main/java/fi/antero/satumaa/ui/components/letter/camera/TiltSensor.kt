package fi.antero.satumaa.ui.components.letter.camera

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import kotlin.math.max
import kotlin.math.min

// Yksinkertainen data kallistukselle
data class Tilt(
    val x: Float, // vasen–oikea
    val y: Float  // ylös–alas
)

// Composable, joka kuuntelee puhelimen kallistusta
@Composable
fun rememberTilt(): State<Tilt> {
    val context = LocalContext.current
    val tiltState = remember { mutableStateOf(Tilt(0f, 0f)) }

    DisposableEffect(Unit) {
        // Haetaan sensoripalvelu
        val sensorManager =
            context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

        // Käytetään kiihtyvyysanturia
        val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        // Kuunnellaan sensorin muutoksia
        val listener = object : SensorEventListener {

            override fun onSensorChanged(event: SensorEvent) {
                // Muutetaan sensoridata yksinkertaiseksi kallistukseksi
                val nx = (event.values[0] / 9.81f).coerceIn(-1f, 1f)
                val ny = (event.values[1] / 9.81f).coerceIn(-1f, 1f)

                tiltState.value = Tilt(nx, ny)
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
        }

        // Rekisteröidään sensori
        if (sensor != null) {
            sensorManager.registerListener(
                listener,
                sensor,
                SensorManager.SENSOR_DELAY_GAME
            )
        }

        // Vapautetaan sensori kun poistutaan näkymästä
        onDispose {
            sensorManager.unregisterListener(listener)
        }
    }

    return tiltState
}

// Rajaa arvon tietylle välille
private fun Float.coerceIn(minV: Float, maxV: Float): Float =
    max(minV, min(this, maxV))
