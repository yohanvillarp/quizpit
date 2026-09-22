package tech.nikelyh.quizpit.feature.familiars

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import tech.nikelyh.quizpit.core.model.Familiar
import tech.nikelyh.quizpit.core.model.PowerType
import tech.nikelyh.quizpit.core.data.repository.AvatarRepository
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class FamiliarsViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("roulette_prefs", Context.MODE_PRIVATE)

    private val _localUnlocks = MutableStateFlow(prefs.getBoolean("unlocked_familiar_dragon", false))

    val familiars: StateFlow<List<Familiar>> = AvatarRepository.avatars
        .combine(_localUnlocks) { avatars, isDragonUnlocked ->
            if (avatars.isEmpty()) {
                // Si la caché está vacía (primer inicio), mostrar algo por defecto
                listOf(defaultFamiliar)
            } else {
                avatars.map { dto ->
                    Familiar(
                        id = dto.id,
                        name = dto.name,
                        powerName = dto.powerName,
                        description = dto.powerDescription,
                        isMythic = dto.isMythic,
                        powerType = try { PowerType.valueOf(dto.powerType) } catch(e: Exception) { PowerType.SPECIAL },
                        isUnlocked = if (dto.id == "dragon") isDragonUnlocked else dto.isUnlocked
                    )
                }
            }
        }.stateIn(viewModelScope, kotlinx.coroutines.flow.SharingStarted.Lazily, emptyList())

    private val defaultFamiliar = Familiar(
        id = "fox",
        name = "Zorro",
        powerName = "Ladrón Astuto",
        description = "Ve la respuesta de tu rival y cópiala.",
        isMythic = false,
        powerType = PowerType.OFFENSIVE,
        isUnlocked = true
    )

    // Persistencia del familiar equipado
    private val _equippedFamiliar = MutableStateFlow<Familiar>(loadEquippedFamiliar())
    val equippedFamiliar: StateFlow<Familiar> = _equippedFamiliar.asStateFlow()

    private fun loadEquippedFamiliar(): Familiar {
        val id = prefs.getString("equipped_familiar_id", "fox") ?: "fox"
        // Intentar buscarlo en la lista actual o devolver el default
        return familiars.value.find { it.id == id } ?: defaultFamiliar
    }

    fun refreshUnlocks() {
        _localUnlocks.value = prefs.getBoolean("unlocked_familiar_dragon", false)
    }

    fun equipFamiliar(familiar: Familiar) {
        _equippedFamiliar.value = familiar
        prefs.edit().putString("equipped_familiar_id", familiar.id).apply()
    }
}
