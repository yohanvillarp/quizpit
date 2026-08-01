---
name: RevenueCat Android SDK
description: Guía e instrucciones para interactuar con la API de compras, suscripciones y Paywalls de RevenueCat en Kotlin.
---

# Instrucciones de RevenueCat (Kotlin / Android)

## 1. Mejores Prácticas (Best Practices)
- ✅ Haz que los paywalls sean dinámicos. Evita "hardcodear" IDs de productos específicos en el código.
- ✅ Usa las interfaces nativas de Jetpack Compose (`Paywall`, `PaywallDialog`) proporcionadas por `purchases-ui`.
- ❌ **NUNCA** llames a `getOfferings` o `getCustomerInfo` dentro del método `Application.onCreate()`, ya que esto despertará llamadas de red innecesarias cuando lleguen notificaciones push sin que el usuario esté usando la app activamente.

## 2. Obtener Ofertas (Offerings)
Para obtener los productos disponibles (Lifetime, Monthly, Yearly) configurados en el Dashboard de RevenueCat:
```kotlin
Purchases.sharedInstance.getOfferingsWith(
    onError = { error -> /* Manejar error */ },
    onSuccess = { offerings ->
        val packages = offerings.current?.availablePackages
        // `packages` contiene los productos a mostrar
    }
)
```

## 3. Comprobar Acceso (Entitlements)
Para saber si un usuario tiene acceso al plan "Nikelyh Pro":
```kotlin
Purchases.sharedInstance.getCustomerInfoWith(
    onError = { error -> Log.e("RevenueCat", "Error: ${error.message}") },
    onSuccess = { customerInfo ->
        val hasProAccess = customerInfo.entitlements.active["Nikelyh Pro"] != null
        // Activar o desactivar features basandose en `hasProAccess`
    }
)
```

## 4. UI del Paywall en Jetpack Compose
Si vas a presentar la pantalla de cobro nativa, usa la librería `purchases-ui` y el composable `Paywall`:
```kotlin
import com.revenuecat.purchases.ui.revenuecatui.Paywall
import com.revenuecat.purchases.ui.revenuecatui.PaywallOptions

@Composable
fun MiPantalla() {
    Paywall(
        options = PaywallOptions.Builder(dismissRequest = { /* Cerrar */ })
            .setShouldDisplayDismissButton(true)
            .build()
    )
}
```
