package tech.nikelyh.quizpit.feature.splash

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import tech.nikelyh.quizpit.core.data.repository.AvatarRepository

class SplashViewModel(application: Application) : AndroidViewModel(application) {
    private val _isLoaded = MutableStateFlow(false)
    val isLoaded: StateFlow<Boolean> = _isLoaded.asStateFlow()

    private val _loadingProgress = MutableStateFlow(0f)
    val loadingProgress: StateFlow<Float> = _loadingProgress.asStateFlow()

    init {
        // Carga inmediata de caché antes de intentar sincronizar
        AvatarRepository.initCache(application)
        syncData()
    }

    private fun syncData() {
        viewModelScope.launch {
            // Simulated loading for animation effect (Analog Sketchbook style)
            for (i in 1..5) {
                delay(200) // 1 second total mock delay
                _loadingProgress.value = i * 0.15f
            }

            // Intento de sincronización con el servidor
            val success = AvatarRepository.syncAvatars(getApplication())

            _loadingProgress.value = 1.0f
            delay(500) // Brief pause at 100%

            _isLoaded.value = true
        }
    }
}
