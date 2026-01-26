package fi.antero.satumaa.ui.components.letter.camera

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import kotlin.math.PI

// Palauttaa puhelimen "kääntymissuunnan" (yaw/heading) asteina 0..360
@Composable
fun rememberHeadingDegrees(): State<Float> {
    val context = LocalContext.current

    // Tähän tallennetaan viimeisin heading (0..360)
    val headingState = remember { mutableFloatStateOf(0f) }

    DisposableEffect(Unit) {
        // Haetaan sensoripalvelu
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

        // Rotation vector antaa suuntatiedon ilman magnetometrin häiriöitä (hyvä sisätiloissa)
        val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
            ?: sensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR)

        // Matriisit/orientaatiot laskentaa varten
        val rotationMatrix = FloatArray(9)
        val orientation = FloatArray(3)

        // Kuunnellaan sensorimuutoksia
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                // Lasketaan rotaatiomatriisi sensorin vektorista
                SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)

                // Lasketaan orientaatio: [0]=azimuth(yaw), [1]=pitch, [2]=roll
                SensorManager.getOrientation(rotationMatrix, orientation)

                // Azimuth (yaw) on radiaaneina, muutetaan asteiksi
                val yawRad = orientation[0]
                val yawDeg = (yawRad * (180f / PI.toFloat()))

                // Normalisoidaan 0..360 välille
                headingState.floatValue = normalize360(yawDeg)
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
        }

        // Rekisteröidään kuuntelija
        if (sensor != null) {
            sensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_GAME)
        }

        // Vapautetaan sensori kun poistutaan näkymästä
        onDispose { sensorManager.unregisterListener(listener) }
    }

    return headingState
}

// Normalisoi asteet välille 0..360
private fun normalize360(deg: Float): Float {
    var d = deg % 360f
    if (d < 0f) d += 360f
    return d
}
