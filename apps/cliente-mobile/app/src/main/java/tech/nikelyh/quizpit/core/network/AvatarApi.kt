package tech.nikelyh.quizpit.core.network

import retrofit2.http.GET

data class AvatarDto(
    val id: String,
    val name: String,
    val phrase: String,
    val powerName: String,
    val powerDescription: String,
    val effectType: String,
    val powerType: String = "SPECIAL",
    val isMythic: Boolean = false,
    val isUnlocked: Boolean = true
)

interface AvatarApi {
    @GET("avatars")
    suspend fun getAvatars(): List<AvatarDto>
}
