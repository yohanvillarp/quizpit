package tech.nikelyh.quizpit.feature.familiars.ui.shaders

import android.os.Build
import androidx.annotation.RequiresApi
import android.graphics.RuntimeShader

const val WATER_RIPPLE_SHADER_CODE = """
    uniform float2 resolution;
    uniform float time;
    uniform shader contents;
    
    // Premium Mythical Water Aura
    half4 main(float2 fragCoord) {
        // Normalize coordinates
        float2 uv = fragCoord / resolution.xy;
        
        // Distort UVs based on sine wave to simulate water
        float distortion = sin(uv.y * 10.0 + time * 2.0) * 0.05;
        float2 distortedUv = float2(uv.x + distortion, uv.y);
        
        // Sample the original content with distorted UV
        half4 color = contents.eval(distortedUv * resolution.xy);
        
        // Add a magical mythical glow (High Pink + High Yellow tinting)
        // Only glow the transparent/semi-transparent areas to create an aura
        float distanceToCenter = distance(uv, float2(0.5, 0.5));
        float auraIntensity = (sin(time * 3.0) * 0.5 + 0.5) * 0.4;
        
        // If it's outside the duck's body, draw the aura
        if (color.a < 0.1 && distanceToCenter < 0.45) {
            float glowAlpha = (0.45 - distanceToCenter) * 2.0 * auraIntensity;
            // Mix between pink and yellow based on time
            half3 magicColor = mix(half3(0.98, 0.86, 0.3), half3(0.95, 0.84, 0.91), sin(time)*0.5+0.5);
            return half4(magicColor * glowAlpha, glowAlpha);
        }
        
        return color;
    }
"""

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
val waterRippleRuntimeShader = RuntimeShader(WATER_RIPPLE_SHADER_CODE)
