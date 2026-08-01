package tech.nikelyh.quizpit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import tech.nikelyh.quizpit.core.navigation.AppNavigation
import tech.nikelyh.quizpit.ui.theme.QuizpitTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            QuizpitTheme {
                AppNavigation()
            }
        }
    }
}
