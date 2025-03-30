import androidx.compose.ui.window.ComposeUIViewController
import com.josephhopson.sprituum.App
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController = ComposeUIViewController { App() }
