package tech.nikelyh.quizpit.feature.familiars.ui

import androidx.compose.ui.graphics.Color
import tech.nikelyh.quizpit.core.model.PowerType

/**
 * Define los colores temáticos para cada tipo de poder de los Familiares.
 */
object FamiliarTheme {
    val Offensive = Color(0xFFFFD1D1) // Rojo suave / Coral
    val Defensive = Color(0xFFD1E9FF) // Azul cielo claro
    val Tactical = Color(0xFFD1FFD7)  // Verde menta claro
    val Special = Color(0xFFF0D1FF)   // Lavanda / Violeta suave
    val Default = Color(0xFFFAF9F5)   // Papel (color base)

    /**
     * Retorna el color de fondo correspondiente al tipo de poder.
     */
    fun getColorForPower(type: PowerType): Color {
        return when (type) {
            PowerType.OFFENSIVE -> Offensive
            PowerType.DEFENSIVE -> Defensive
            PowerType.TACTICAL -> Tactical
            PowerType.SPECIAL -> Special
        }
    }
}
