import androidx.compose.ui.window.ComposeUIViewController
import com.nestoleh.light.App
import com.nestoleh.light.di.KoinInitializer

fun MainViewController() = ComposeUIViewController(
    configure = {
        KoinInitializer().init()
    }
) { App() }