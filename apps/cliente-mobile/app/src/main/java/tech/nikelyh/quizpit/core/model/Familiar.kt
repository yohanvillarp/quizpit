package tech.nikelyh.quizpit.core.model

enum class PowerType { OFFENSIVE, DEFENSIVE, TACTICAL, SPECIAL }

data class Familiar(
    val id: String,
    val name: String,
    val powerName: String,
    val description: String,
    val isMythic: Boolean,
    val powerType: PowerType,
    val isUnlocked: Boolean = true
)
