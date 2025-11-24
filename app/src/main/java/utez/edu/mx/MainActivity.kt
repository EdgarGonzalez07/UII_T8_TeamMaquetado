package utez.edu.mx

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import utez.edu.mx.ui.AppNavigation

import utez.edu.mx.ui.theme.UII_T8_TeamMaquetadoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UII_T8_TeamMaquetadoTheme {
                AppNavigation()
            }
        }
    }
}
