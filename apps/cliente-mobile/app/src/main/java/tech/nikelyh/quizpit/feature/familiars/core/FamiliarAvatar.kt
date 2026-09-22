package tech.nikelyh.quizpit.feature.familiars.core

import tech.nikelyh.quizpit.R

/**
 * Representa una capa visual de un familiar.
 */
sealed class FamiliarLayer {
    abstract val resId: Int

    data class Static(override val resId: Int) : FamiliarLayer()
    data class Tail(override val resId: Int) : FamiliarLayer()
    data class Wing(override val resId: Int, val isFront: Boolean) : FamiliarLayer()
}

/**
 * Fuente de verdad para la estructura de un Familiar.
 * Define qué recursos usa y si tiene comportamiento especial.
 */
sealed class FamiliarAvatar(val id: String) {
    abstract val headRes: Int
    open val layers: List<FamiliarLayer> = emptyList()

    object Fox : FamiliarAvatar("fox") {
        override val headRes = R.drawable.ic_fox_head
        override val layers = listOf(FamiliarLayer.Tail(R.drawable.ic_fox_tail))
    }

    object Dragon : FamiliarAvatar("dragon") {
        override val headRes = R.drawable.ic_dragon_body
        override val layers = listOf(
            FamiliarLayer.Tail(R.drawable.ic_dragon_tail),
            FamiliarLayer.Wing(R.drawable.ic_dragon_wing_back, isFront = false),
            FamiliarLayer.Wing(R.drawable.ic_dragon_wing_front, isFront = true),
            FamiliarLayer.Static(R.drawable.ic_dragon_flame)
        )
    }

    object Medusa : FamiliarAvatar("medusa") {
        override val headRes = R.drawable.ic_medusa_head
    }

    object Duck : FamiliarAvatar("duck") {
        override val headRes = R.drawable.ic_duck
    }

    object Bat : FamiliarAvatar("bat") { override val headRes = R.drawable.ic_familiar_bat }
    object Gallo : FamiliarAvatar("gallo") { override val headRes = R.drawable.ic_familiar_gallo }
    object Peacock : FamiliarAvatar("peacock") { override val headRes = R.drawable.ic_familiar_peacock }
    object Chameleon : FamiliarAvatar("chameleon") { override val headRes = R.drawable.ic_familiar_chameleon }

    // Para los familiares que aún no tienen un diseño modular avanzado
    data class Legacy(val familiarId: String, override val headRes: Int) : FamiliarAvatar(familiarId)

    companion object {
        fun fromId(id: String, fallbackRes: Int): FamiliarAvatar {
            return when (id) {
                "fox" -> Fox
                "dragon" -> Dragon
                "medusa" -> Medusa
                "duck" -> Duck
                "bat" -> Bat
                "gallo" -> Gallo
                "peacock" -> Peacock
                "chameleon" -> Chameleon
                else -> Legacy(id, fallbackRes)
            }
        }
    }
}
