package tech.nikelyh.quizpit.feature.profile

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.revenuecat.purchases.logInWith
import com.revenuecat.purchases.logOutWith
import tech.nikelyh.quizpit.BuildConfig
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class ProfileState(
    val isGuest: Boolean = true,
    val name: String = "",
    val email: String = "",
    val avatarUrl: String? = null,
    val rank: String = "Aprendiz de Tinta",
    val totalGames: Int = 24,
    val winRate: Int = 65,
    val correctAnswers: Int = 142,
    val familiarsUnlocked: Int = 3,
    val totalFamiliars: Int = 13,
    val memberSince: String = ""
)

private val _globalUiState = MutableStateFlow(ProfileState())

class ProfileViewModel(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : ViewModel() {
    val uiState: StateFlow<ProfileState> = _globalUiState.asStateFlow()

    companion object {
        val WEB_CLIENT_ID = BuildConfig.GOOGLE_WEB_CLIENT_ID
    }

    init {
        auth.addAuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {
                val isAnon = user.isAnonymous
                if (!isAnon) {
                    com.revenuecat.purchases.Purchases.sharedInstance.logInWith(
                        user.uid,
                        onError = { error -> android.util.Log.e("RevenueCat", "Error logging in: ${error.message}") },
                        onSuccess = { _, created -> android.util.Log.d("RevenueCat", "Logged in to RC. Created: $created") }
                    )
                }

                val dateStr = user.metadata?.creationTimestamp?.let {
                    SimpleDateFormat("MMM yyyy", Locale.getDefault()).format(Date(it))
                } ?: "Reciente"

                _globalUiState.update {
                    it.copy(
                        isGuest = isAnon,
                        name = user.displayName ?: "Maestro Misterioso",
                        email = user.email ?: "",
                        avatarUrl = user.photoUrl?.toString(),
                        memberSince = dateStr
                    )
                }
            } else {
                auth.signInAnonymously()
            }
        }
    }

    fun authenticateWithGoogle(idToken: String, onResult: (String) -> Unit = {}) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        val currentUser = auth.currentUser

        if (currentUser != null && currentUser.isAnonymous) {
            currentUser.linkWithCredential(credential).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult("¡Diario vinculado con éxito!")
                } else {
                    auth.signInWithCredential(credential).addOnCompleteListener { signInTask ->
                        if (signInTask.isSuccessful) {
                            onResult("¡Sesión iniciada con éxito!")
                        } else {
                            onResult("Error: ${signInTask.exception?.message}")
                        }
                    }
                }
            }
        } else {
            auth.signInWithCredential(credential).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult("¡Sesión iniciada con éxito!")
                } else {
                    onResult("Error: ${task.exception?.message}")
                }
            }
        }
    }

    fun signOut() {
        auth.signOut()
        com.revenuecat.purchases.Purchases.sharedInstance.logOutWith(
            onError = { /* Log error */ },
            onSuccess = { /* Log success */ }
        )
    }
}
