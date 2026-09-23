package tech.nikelyh.quizpit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import tech.nikelyh.quizpit.core.navigation.AppNavigation
import tech.nikelyh.quizpit.ui.theme.QuizpitTheme

import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.PurchasesConfiguration
import com.revenuecat.purchases.LogLevel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        
        Purchases.logLevel = LogLevel.DEBUG
        if (BuildConfig.REVENUECAT_API_KEY.isNotEmpty()) {
            Purchases.configure(PurchasesConfiguration.Builder(this, BuildConfig.REVENUECAT_API_KEY).build())
        }
        
        tech.nikelyh.quizpit.core.domain.EnergyManager.init(this)
        volumeControlStream = android.media.AudioManager.STREAM_MUSIC
        enableEdgeToEdge()

        setContent {
            QuizpitTheme {
                AppNavigation()
            }
        }
    }
}
