import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import java.awt.Dimension
import com.josephhopson.sprituum.App
import com.josephhopson.sprituum.di.AppModules
import com.josephhopson.sprituum.di.JvmPlatformModule
import org.jetbrains.compose.reload.DevelopmentEntryPoint
import org.koin.core.context.startKoin

fun main() {
    startKoin {
        modules(AppModules.allModules + JvmPlatformModule.module)
    }

    application {
        Window(
            title = "Spirituum",
            state = rememberWindowState(width = 800.dp, height = 600.dp),
            onCloseRequest = ::exitApplication,
        ) {
            window.minimumSize = Dimension(350, 600)
            DevelopmentEntryPoint {
                App()
            }
        }
    }
}

@Preview
@Composable
fun AppPreview() { App() }
