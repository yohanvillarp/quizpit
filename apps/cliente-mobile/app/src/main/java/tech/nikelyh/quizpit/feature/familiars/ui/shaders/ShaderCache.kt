package tech.nikelyh.quizpit.feature.familiars.ui.shaders

import android.graphics.RuntimeShader
import android.os.Build
import androidx.annotation.RequiresApi

/**
 * Singleton que pre-compila y almacena en caché los Shaders AGSL
 * para evitar el costoso overhead de instanciarlos múltiples veces
 * en pantallas con grids como FamiliarsScreen.
 */
object ShaderCache {

    private var bioluminescenceShader: RuntimeShader? = null
    private var waterRippleShader: RuntimeShader? = null

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun getBioluminescenceShader(): RuntimeShader {
        if (bioluminescenceShader == null) {
            bioluminescenceShader = RuntimeShader(BIOLUMINESCENCE_SHADER_CODE)
        }
        return bioluminescenceShader!!
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun getWaterRippleShader(): RuntimeShader {
        if (waterRippleShader == null) {
            waterRippleShader = RuntimeShader(WATER_RIPPLE_SHADER_CODE)
        }
        return waterRippleShader!!
    }
}
