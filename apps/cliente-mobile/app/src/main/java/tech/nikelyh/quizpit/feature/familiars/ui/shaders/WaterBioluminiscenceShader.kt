package tech.nikelyh.quizpit.feature.familiars.ui.shaders

import android.os.Build
import androidx.annotation.RequiresApi
import android.graphics.RuntimeShader

const val BIOLUMINESCENCE_SHADER_CODE = """
    uniform float2 resolution;
    uniform float time;
    uniform shader contents;
    
    half4 main(float2 fragCoord) {
        float2 uv = fragCoord / resolution.xy;
        half4 color = contents.eval(fragCoord);
        
        // Dynamic bioluminescent glow
        float distanceToCenter = distance(uv, float2(0.5, 0.4));
        float pulse = (sin(time * 2.0) * 0.5 + 0.5) * 0.5 + 0.5; // range 0.5 to 1.0
        
        if (color.a < 0.1 && distanceToCenter < 0.35) {
            float glowAlpha = (0.35 - distanceToCenter) * 2.5 * pulse;
            // Mix toxic green and high pink for the "Neuro-Toxina" vibe
            half3 magicColor = mix(half3(0.0, 1.0, 0.6), half3(0.95, 0.84, 0.91), sin(time)*0.5+0.5);
            return half4(magicColor * glowAlpha, glowAlpha * 0.6);
        }
        
        return color;
    }
"""

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
val bioluminescenceRuntimeShader = RuntimeShader(BIOLUMINESCENCE_SHADER_CODE)
