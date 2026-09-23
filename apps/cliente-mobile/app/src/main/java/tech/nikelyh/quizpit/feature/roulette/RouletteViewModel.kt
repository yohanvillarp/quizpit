package tech.nikelyh.quizpit.feature.roulette

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import tech.nikelyh.quizpit.core.domain.EnergyManager
import tech.nikelyh.quizpit.feature.roulette.domain.PrizeType
import tech.nikelyh.quizpit.feature.roulette.domain.PrizeWheel

class RouletteViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("roulette_prefs", Context.MODE_PRIVATE)

    // DESCONECTADO: Siempre hay giros para pruebas
    private val _hasFreeSpin = MutableStateFlow(true)
    val hasFreeSpin: StateFlow<Boolean> = _hasFreeSpin.asStateFlow()

    private val _hasAdSpin = MutableStateFlow(true)
    val hasAdSpin: StateFlow<Boolean> = _hasAdSpin.asStateFlow()

    private val _nextResetTimeMs = MutableStateFlow(0L)
    val nextResetTimeMs: StateFlow<Long> = _nextResetTimeMs.asStateFlow()

    private val _currentServerTimeMs = MutableStateFlow(0L)
    val currentServerTimeMs: StateFlow<Long> = _currentServerTimeMs.asStateFlow()

    private val _isSimulatingAd = MutableStateFlow(false)
    val isSimulatingAd: StateFlow<Boolean> = _isSimulatingAd.asStateFlow()

    private val _prizeObtained = MutableStateFlow<PrizeType?>(null)
    val prizeObtained: StateFlow<PrizeType?> = _prizeObtained.asStateFlow()

    // Sistema de Piedad (Pity)
    private val _spinsWithoutRarePrize = MutableStateFlow(prefs.getInt("spins_without_rare", 0))
    val spinsWithoutRarePrize: StateFlow<Int> = _spinsWithoutRarePrize.asStateFlow()

    // Estado del Dragón
    private val _isMythicUnlocked = MutableStateFlow(prefs.getBoolean("unlocked_familiar_dragon", false))
    val isMythicUnlocked: StateFlow<Boolean> = _isMythicUnlocked.asStateFlow()

    init {
        // Validación de servidor desconectada temporalmente
        _currentServerTimeMs.value = System.currentTimeMillis()
        _nextResetTimeMs.value = System.currentTimeMillis() + 86400000 // Mañana
    }

    fun onSpinComplete(prize: PrizeType) {
        // Lógica de Pity
        if (prize.isRare) {
            _spinsWithoutRarePrize.value = 0
        } else {
            _spinsWithoutRarePrize.value += 1
        }

        // Persistencia
        prefs.edit().putInt("spins_without_rare", _spinsWithoutRarePrize.value).apply()

        _prizeObtained.value = prize
        grantPrize(prize)

        // MANTENER GIROS ILIMITADOS (No restamos nada aquí por ahora)
        _hasFreeSpin.value = true
        _hasAdSpin.value = true
    }

    fun clearPrize() {
        _prizeObtained.value = null
    }

    private fun grantPrize(prize: PrizeType) {
        when(prize) {
            PrizeType.INK_1 -> EnergyManager.addInk(1)
            PrizeType.INK_2 -> EnergyManager.addInk(2)
            PrizeType.INK_3 -> EnergyManager.addInk(3)
            PrizeType.INK_4 -> EnergyManager.addInk(4)
            PrizeType.INK_5 -> EnergyManager.addInk(5)
            PrizeType.MYTHIC_FAMILIAR -> {
                prefs.edit().putBoolean("unlocked_familiar_dragon", true).apply()
                _isMythicUnlocked.value = true
            }
            PrizeType.NOTHING -> {}
        }
    }

    fun simulateAd() {
        viewModelScope.launch {
            _isSimulatingAd.value = true
            delay(1000)
            _isSimulatingAd.value = false
        }
    }
}
