package tech.nikelyh.quizpit.feature.familiars.ui

import tech.nikelyh.quizpit.R

fun getFamiliarDrawableId(id: String): Int {
    return when (id) {
        "fox" -> R.drawable.ic_familiar_fox
        "owl" -> R.drawable.ic_familiar_owl
        "bear" -> R.drawable.ic_familiar_bear
        "cat" -> R.drawable.ic_familiar_cat
        "rabbit" -> R.drawable.ic_familiar_rabbit
        "dog" -> R.drawable.ic_familiar_dog
        "gallo" -> R.drawable.ic_familiar_gallo
        "peacock" -> R.drawable.ic_familiar_peacock
        "chameleon" -> R.drawable.ic_familiar_chameleon
        "bat" -> R.drawable.ic_familiar_bat
        "dragon" -> R.drawable.ic_familiar_dragon
        else -> R.drawable.ic_familiars // Fallback icon
    }
}
