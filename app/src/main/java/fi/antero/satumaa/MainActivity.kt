package fi.antero.satumaa

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dagger.hilt.android.AndroidEntryPoint
import fi.antero.satumaa.ui.SatumaaApp
import fi.antero.satumaa.ui.theme.SatumaaTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SatumaaTheme {
                SatumaaApp()
            }
        }
    }
}
