package tech.nikelyh.quizpit.core.data.repository

import android.content.Context
import com.google.gson.Gson
import java.lang.reflect.Type
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import tech.nikelyh.quizpit.core.network.AvatarDto
import tech.nikelyh.quizpit.core.network.RetrofitClient

object AvatarRepository {
    private const val PREFS_NAME = "avatar_cache"
    private const val KEY_AVATARS = "cached_avatars"
    private val gson = Gson()

    private val _avatars = MutableStateFlow<List<AvatarDto>>(emptyList())
    val avatars: StateFlow<List<AvatarDto>> = _avatars.asStateFlow()

    /**
     * Carga las mascotas desde la memoria local (caché).
     * Útil para que la app no dependa del servidor en cada inicio.
     */
    fun initCache(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(KEY_AVATARS, null)
        if (json != null) {
            try {
                val type = object : com.google.gson.reflect.TypeToken<List<AvatarDto>>() {}.type
                val cachedList: List<AvatarDto> = gson.fromJson(json, type)
                _avatars.value = cachedList
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Intenta sincronizar con el servidor. Si tiene éxito, actualiza la caché.
     */
    suspend fun syncAvatars(context: Context): Boolean {
        return try {
            val response = RetrofitClient.avatarApi.getAvatars()
            _avatars.value = response

            // Guardar en caché para futuros arranques offline
            saveToCache(context, response)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            // Si falla, mantenemos lo que haya en la lista (posiblemente lo cargado de initCache)
            false
        }
    }

    private fun saveToCache(context: Context, avatars: List<AvatarDto>) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = gson.toJson(avatars)
        prefs.edit().putString(KEY_AVATARS, json).apply()
    }

    fun getAvatarById(id: String): AvatarDto? {
        return _avatars.value.find { it.id == id }
    }
}
