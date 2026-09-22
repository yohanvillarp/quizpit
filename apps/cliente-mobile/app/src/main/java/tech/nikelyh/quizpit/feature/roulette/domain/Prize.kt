package tech.nikelyh.quizpit.feature.roulette.domain

import androidx.compose.ui.graphics.Color
import tech.nikelyh.quizpit.R
import kotlin.random.Random

enum class PrizeType(
    val displayName: String,
    val shortName: String,
    val iconRes: Int?,
    val baseWeight: Int,
    val color: Color,
    val isRare: Boolean = false
) {
    MYTHIC_FAMILIAR("Mythic Familiar", "Mítico", R.drawable.ic_familiar_dragon, 20, Color(0xFFffeb99), true),
    NOTHING("Mejor Suerte", "Mejor\nSuerte", null, 280, Color(0xFFf5f5f5)),
    INK_1("1 Gota de Tinta", "+1", R.drawable.ic_ink, 300, Color(0xFFf4d7e8)),
    INK_2("2 Gotas de Tinta", "+2", R.drawable.ic_ink, 200, Color(0xFFD1E9FF)),
    INK_3("3 Gotas de Tinta", "+3", R.drawable.ic_ink, 100, Color(0xFFD1FFD7)),
    INK_4("4 Gotas de Tinta", "+4", R.drawable.ic_ink, 70, Color(0xFFF0D1FF)),
    INK_5("5 Gotas de Tinta", "+5", R.drawable.ic_ink, 30, Color(0xFF99F5E5), true)
}

class PrizeWheel(val isMythicUnlocked: Boolean = false) {

    // Generamos la lista de items dinámicamente
    val items: List<PrizeType> = buildWheelItems()

    private fun buildWheelItems(): List<PrizeType> {
        val pool = mutableListOf(
            PrizeType.INK_1,
            PrizeType.INK_2,
            PrizeType.INK_3,
            PrizeType.INK_4,
            PrizeType.INK_5,
            PrizeType.NOTHING,
            PrizeType.NOTHING
        )

        // Si no está desbloqueado, añadimos al dragón, si no, añadimos otra "Suerte"
        if (!isMythicUnlocked) {
            pool.add(PrizeType.MYTHIC_FAMILIAR)
        } else {
            pool.add(PrizeType.NOTHING)
        }

        // Ordenar para evitar que haya 3 "NOTHING" juntos
        // Con 8 espacios y 3 "NOTHING", un ordenamiento fijo o intercalado funciona mejor
        return balanceWheel(pool)
    }

    /**
     * Asegura que los items estén distribuidos de forma que no haya 3 "NOTHING" juntos
     */
    private fun balanceWheel(pool: MutableList<PrizeType>): List<PrizeType> {
        val nonNothing = pool.filter { it != PrizeType.NOTHING }.toMutableList()
        val nothingItems = pool.filter { it == PrizeType.NOTHING }.toMutableList()

        val result = mutableListOf<PrizeType>()
        // Intercalamos: 2 normales, 1 nada, 2 normales, 1 nada...
        var nIdx = 0
        var mIdx = 0

        for (i in 0 until 8) {
            if (i % 3 == 1 && nIdx < nothingItems.size) {
                result.add(nothingItems[nIdx++])
            } else if (mIdx < nonNothing.size) {
                result.add(nonNothing[mIdx++])
            } else if (nIdx < nothingItems.size) {
                result.add(nothingItems[nIdx++])
            }
        }
        return result
    }

    /**
     * Calcula los pesos dinámicos basados en el "Pity Bonus".
     */
    fun getDynamicWeights(pityBonus: Int): List<Int> {
        return items.map { prize ->
            if (prize.isRare) {
                prize.baseWeight + (pityBonus * 5)
            } else if (prize == PrizeType.NOTHING) {
                maxOf(50, prize.baseWeight - (pityBonus * 10))
            } else {
                prize.baseWeight
            }
        }
    }

    fun spin(pityBonus: Int): PrizeType {
        val currentWeights = getDynamicWeights(pityBonus)
        val totalWeight = currentWeights.sum()
        var randomVal = Random.nextInt(totalWeight)

        for (i in items.indices) {
            randomVal -= currentWeights[i]
            if (randomVal < 0) {
                return items[i]
            }
        }
        return items.last()
    }
}
