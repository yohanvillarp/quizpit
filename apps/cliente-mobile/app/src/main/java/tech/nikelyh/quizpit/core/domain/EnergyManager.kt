package tech.nikelyh.quizpit.core.domain

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object EnergyManager {
    private var prefs: SharedPreferences? = null
    
    private val _inkDrops = MutableStateFlow(0)
    val inkDrops: StateFlow<Int> = _inkDrops.asStateFlow()

    private val _isPro = MutableStateFlow(false)
    val isPro: StateFlow<Boolean> = _isPro.asStateFlow()

    fun init(context: Context) {
        if (prefs == null) {
            prefs = context.applicationContext.getSharedPreferences("quizpit_energy_prefs", Context.MODE_PRIVATE)
            
            _isPro.value = prefs!!.getBoolean("isPro", false)
            
            checkDailyReplenish()
            
            // Debug: give the user 10 drops
            addInk(10)
        }
    }

    fun addInk(amount: Int) {
        _inkDrops.value += amount
        prefs?.edit()?.putInt("inkDrops", _inkDrops.value)?.apply()
    }
    
    fun setPro(pro: Boolean) {
        _isPro.value = pro
        prefs?.edit()?.putBoolean("isPro", pro)?.apply()
        
        // Si mejoran a PRO, automáticamente les rellenamos a su nuevo máximo como regalo
        val maxInks = if (pro) 10 else 4
        if (_inkDrops.value < maxInks) {
            _inkDrops.value = maxInks
            prefs?.edit()?.putInt("inkDrops", maxInks)?.apply()
        }
    }
    
    fun spendInk(): Boolean {
        if (_inkDrops.value > 0) {
            _inkDrops.value -= 1
            prefs?.edit()?.putInt("inkDrops", _inkDrops.value)?.apply()
            return true
        }
        return false
    }

    // Lógica para recargar a medianoche hasta el máximo, rellenando solo lo faltante
    fun checkDailyReplenish() {
        val prefs = this.prefs ?: return
        val todayStr = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
        val lastDate = prefs.getString("lastReplenishDate", "")
        var currentInks = prefs.getInt("inkDrops", 0)
        
        val maxInks = if (_isPro.value) 10 else 4
        
        if (lastDate != todayStr) {
            // ¡Nuevo Día! Recarga a medianoche
            if (currentInks < maxInks) {
                currentInks = maxInks
            }
            prefs.edit()
                .putString("lastReplenishDate", todayStr)
                .putInt("inkDrops", currentInks)
                .apply()
        }
        
        _inkDrops.value = currentInks
    }
}
