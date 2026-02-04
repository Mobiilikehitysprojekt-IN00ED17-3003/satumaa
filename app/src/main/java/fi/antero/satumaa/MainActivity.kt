package fi.antero.satumaa

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import dagger.hilt.android.AndroidEntryPoint
import fi.antero.satumaa.notifications.NotificationHelper
import fi.antero.satumaa.notifications.ReplyNotificationWatcher
import fi.antero.satumaa.ui.SatumaaApp
import fi.antero.satumaa.ui.theme.SatumaaTheme
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var replyNotificationWatcher: ReplyNotificationWatcher

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        NotificationHelper.ensureChannel(this)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                replyNotificationWatcher.run(this@MainActivity)
            }
        }

        setContent {
            SatumaaTheme {
                SatumaaApp()
            }
        }
    }
}
