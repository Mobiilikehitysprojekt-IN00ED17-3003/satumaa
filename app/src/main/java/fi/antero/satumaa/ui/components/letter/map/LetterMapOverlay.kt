package fi.antero.satumaa.ui.components.letter.map

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.location.Location
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import fi.antero.satumaa.R
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline

/**
 * LetterMapOverlay vastaa OpenStreetMap-kartan piirtämisestä ja kirjeen liikuttamisesta.
 *
 * Tämä komponentti käyttää 'AndroidView'-käärettä, koska osmdroid on perinteinen View-pohjainen kirjasto.
 * Se piirtää reitin (Polyline) ja markkerit (Marker) käyttäjän ja Joulupukin välille.
 *
 * @param userGeoPoint Käyttäjän sijainti.
 * @param santaPoint Joulupukin sijainti (Korvatunturi).
 * @param progress Matkan edistyminen (0.0 - 1.0).
 * @param mapCentered Tila: onko kartta keskitetty alussa (ettei se hypi joka päivityksellä).
 * @param onUpdateMapCentered Callback keskitystilan päivitykseen.
 */
@Composable
fun LetterMapOverlay(
    userGeoPoint: GeoPoint,
    santaPoint: GeoPoint,
    progress: Float,
    mapCentered: Boolean,
    onUpdateMapCentered: (Boolean) -> Unit
) {
    val context = LocalContext.current

    // Haetaan tekstit resursseista, jotta niitä voidaan käyttää Markereissa
    val youTitle = context.getString(R.string.letter_map_marker_you)
    val santaTitle = context.getString(R.string.letter_map_marker_santa)
    val letterTitle = context.getString(R.string.letter_map_marker_letter)

    // Ladataan ikonit muistiin (remember), jotta niitä ei luoda raskaasti joka framella uudestaan
    val userIcon = remember { getResizedDrawable(context, R.drawable.user_icon, 45) }
    val santaIcon = remember { getResizedDrawable(context, R.drawable.santa_icon, 45) }

    AndroidView(
        factory = {
            MapView(it).apply {
                setTileSource(TileSourceFactory.MAPNIK)
                isVerticalMapRepetitionEnabled = false
                isHorizontalMapRepetitionEnabled = false
                // Tärkeää: Asetetaan UserAgent OSM-palvelimia varten (ilman tätä kartta ei välttämättä lataudu)
                Configuration.getInstance().userAgentValue = context.packageName
                setMultiTouchControls(true)
                minZoomLevel = 3.0
            }
        },
        modifier = Modifier.fillMaxSize(),
        update = { view ->
            // --- KARTAN PIIRTOLOGIIKKA (Suoritetaan joka kerta kun 'progress' muuttuu) ---

            // Lasketaan kirjeen sijainti pikseleinä ja projisoidaan koordinaateiksi
            val projection = view.projection
            val startPoint = android.graphics.Point()
            val endPoint = android.graphics.Point()

            projection.toPixels(userGeoPoint, startPoint)
            projection.toPixels(santaPoint, endPoint)

            // Lineaarinen interpolaatio (LERP) pikseleiden välillä
            val currentX = startPoint.x + (endPoint.x - startPoint.x) * progress
            val currentY = startPoint.y + (endPoint.y - startPoint.y) * progress
            val currentPos = projection.fromPixels(currentX.toInt(), currentY.toInt()) as GeoPoint

            // Tyhjennetään vanhat overlayt (tämä on raskasta mutta OSMdroidilla usein tarpeen dynaamisessa animaatiossa)
            view.overlays.clear()

            // 1. Reittiviiva (Punainen viiva)
            val line = Polyline().apply {
                setPoints(listOf(userGeoPoint, santaPoint))
                outlinePaint.color = android.graphics.Color.RED
                outlinePaint.strokeWidth = 8f
                isGeodesic = false // Piirretään suora viiva (Mercator-projektiossa)
            }
            view.overlays.add(line)

            // 2. Käyttäjän merkki
            view.overlays.add(Marker(view).apply {
                position = userGeoPoint
                title = youTitle
                icon = userIcon
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            })

            // 3. Joulupukin merkki
            view.overlays.add(Marker(view).apply {
                position = santaPoint
                title = santaTitle
                icon = santaIcon
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            })

            // 4. Liikkuva kirje ja sen suunta
            // Lasketaan suuntima (bearing), jotta kirje-ikoni osoittaa oikeaan suuntaan
            val userLoc = Location("user").apply { latitude = userGeoPoint.latitude; longitude = userGeoPoint.longitude }
            val santaLoc = Location("santa").apply { latitude = santaPoint.latitude; longitude = santaPoint.longitude }
            val bearing = userLoc.bearingTo(santaLoc)

            val deliveryMarker = Marker(view).apply {
                position = currentPos
                title = letterTitle
                icon = ContextCompat.getDrawable(context, android.R.drawable.ic_menu_send)
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                rotation = -bearing + 90f // Käännetään ikoni reitin suuntaan
            }
            view.overlays.add(deliveryMarker)

            // Keskitetään kartta kirjeeseen, jos matka on kesken
            if (progress < 1f) {
                view.controller.setCenter(currentPos)
            }

            // Alustava zoomaus ja keskitys (suoritetaan vain kerran alussa)
            if (!mapCentered) {
                view.controller.setZoom(7.0)
                view.controller.setCenter(currentPos)
                onUpdateMapCentered(true)
            }

            view.invalidate() // Pakotetaan uudelleenpiirto
        }
    )
}

/**
 * Apufunktio: Muuttaa drawable-resurssin koon halutuksi (dp -> px) ja palauttaa Drawable-olion.
 * Tarvitaan OSMdroidin Markereita varten, koska ne eivät tue vektorikuvia suoraan samalla tavalla kuin Compose.
 */
private fun getResizedDrawable(context: Context, drawableRes: Int, sizeDp: Int): Drawable? {
    val sourceDrawable = ContextCompat.getDrawable(context, drawableRes) ?: return null
    val density = context.resources.displayMetrics.density
    val sizePx = (sizeDp * density).toInt()

    val bitmap = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
    val canvas = android.graphics.Canvas(bitmap)

    sourceDrawable.setBounds(0, 0, sizePx, sizePx)
    sourceDrawable.draw(canvas)

    return BitmapDrawable(context.resources, bitmap).apply {
        isFilterBitmap = true // Pehmennetään skaalaus
        setAntiAlias(true)
    }
}