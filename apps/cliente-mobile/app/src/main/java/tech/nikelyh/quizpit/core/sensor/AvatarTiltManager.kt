package tech.nikelyh.quizpit.core.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import kotlin.math.abs

/**
 * Clase estable que contiene el estado del sensor.
 * Al usar MutableFloatState, podemos actualizar los valores sin disparar la recomposición
 * de los componentes que posean una referencia a este objeto, SIEMPRE Y CUANDO leamos
 * los valores dentro de una lambda (como en graphicsLayer).
 */
@Stable
class TiltState {
    var x by mutableFloatStateOf(0f)
    var y by mutableFloatStateOf(0f)
}

val LocalAvatarTilt = staticCompositionLocalOf { TiltState() }

@Composable
fun ProvideAvatarTilt(content: @Composable () -> Unit) {
    val context = LocalContext.current
    // Mantenemos la misma instancia del objeto para que sea "estable"
    val tiltState = remember { TiltState() }

    DisposableEffect(Unit) {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        val listener = object : SensorEventListener {
            private var lastX = 0f
            private var lastY = 0f
            private val threshold = 0.05f // Deadzone para evitar micro-ruido

            override fun onSensorChanged(event: SensorEvent) {
                val newX = -event.values[0] * 1.5f
                val newY = event.values[1] * 1.5f

                // Solo actualizamos si el cambio es relevante
                if (abs(newX - lastX) > threshold || abs(newY - lastY) > threshold) {
                    tiltState.x = newX
                    tiltState.y = newY
                    lastX = newX
                    lastY = newY
                }
            }
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        // SENSOR_DELAY_GAME es un buen equilibrio entre fluidez y ahorro de batería
        sensorManager.registerListener(listener, accelerometer, SensorManager.SENSOR_DELAY_GAME)

        onDispose {
            sensorManager.unregisterListener(listener)
        }
    }

    CompositionLocalProvider(LocalAvatarTilt provides tiltState) {
        content()
    }
}
