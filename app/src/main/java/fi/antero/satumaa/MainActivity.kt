package fi.antero.satumaa

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dagger.hilt.android.AndroidEntryPoint
import fi.antero.satumaa.notifications.ReplyNotificationWatcher
import fi.antero.satumaa.ui.SatumaaApp
import fi.antero.satumaa.ui.theme.SatumaaTheme
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var replyNotificationWatcher: ReplyNotificationWatcher

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Main pidetään siistinä: watcher hoitaa channel + permission + lifecycle + run()
        replyNotificationWatcher.start(this)

        setContent {
            SatumaaTheme {
                SatumaaApp()
            }
        }
    }
}
