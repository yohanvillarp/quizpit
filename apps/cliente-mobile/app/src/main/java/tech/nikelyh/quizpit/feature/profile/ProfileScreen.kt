package tech.nikelyh.quizpit.feature.profile

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.launch
import tech.nikelyh.quizpit.R
import tech.nikelyh.quizpit.core.sensor.ProvideAvatarTilt
import tech.nikelyh.quizpit.feature.familiars.FamiliarsViewModel
import tech.nikelyh.quizpit.feature.familiars.ui.FamiliarTheme
import tech.nikelyh.quizpit.feature.familiars.ui.avatars.AnimatedFamiliarIcon
import tech.nikelyh.quizpit.ui.components.SketchbookButton
import tech.nikelyh.quizpit.ui.components.sketchbookBackground
import tech.nikelyh.quizpit.ui.components.sketchbookColoring
import tech.nikelyh.quizpit.ui.components.sketchbookBounceIn

private val Navy = Color(0xFF1a1f3a)
private val PinkBg = Color(0xFFf4d7e8)
private val Paper = Color(0xFFFAF9F5)
private val YellowCTA = Color(0xFFffeb99)

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = viewModel(),
    familiarsViewModel: FamiliarsViewModel = viewModel()
) {
    ProvideAvatarTilt {
        val uiState by viewModel.uiState.collectAsState()
        val equippedFamiliar by familiarsViewModel.equippedFamiliar.collectAsState()
        val context = LocalContext.current
        val coroutineScope = rememberCoroutineScope()
        val scrollState = rememberScrollState()

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Paper)
                .sketchbookBackground()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 24.dp, vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (uiState.isGuest) {
                    GuestProfileView(
                        onSignInClick = {
                            coroutineScope.launch {
                                try {
                                    val credentialManager = androidx.credentials.CredentialManager.create(context)
                                    val googleIdOption = GetGoogleIdOption.Builder()
                                        .setFilterByAuthorizedAccounts(false)
                                        .setServerClientId(ProfileViewModel.WEB_CLIENT_ID)
                                        .setAutoSelectEnabled(false)
                                        .build()
                                    val request = GetCredentialRequest.Builder()
                                        .addCredentialOption(googleIdOption)
                                        .build()
                                    val result = credentialManager.getCredential(context, request)
                                    val credential = result.credential
                                    if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                                        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                                        viewModel.authenticateWithGoogle(googleIdTokenCredential.idToken) { message ->
                                            android.widget.Toast.makeText(context, message, android.widget.Toast.LENGTH_LONG).show()
                                        }
                                    }
                                } catch (e: Exception) {
                                    android.widget.Toast.makeText(context, "Error: ${e.localizedMessage}", android.widget.Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                    )
                } else {
                    // Identity Card Section
                    IdentityCard(uiState)

                    Spacer(modifier = Modifier.height(32.dp))

                    // Familiar of Honor Section
                    Text(
                        text = "FAMILIAR DE HONOR",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Black, letterSpacing = 1.sp),
                        color = Navy.copy(alpha = 0.4f),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Start
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    EquippedFamiliarCard(equippedFamiliar)

                    Spacer(modifier = Modifier.height(32.dp))

                    // Stats Grid
                    Text(
                        text = "ESTADÍSTICAS DEL MAESTRO",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Black, letterSpacing = 1.sp),
                        color = Navy.copy(alpha = 0.4f),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Start
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    StatsGrid(uiState)

                    Spacer(modifier = Modifier.height(48.dp))

                    Text(
                        text = "QuizPit v1.0 • Miembro desde ${uiState.memberSince}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Navy.copy(alpha = 0.3f)
                    )
                }
            }

            // Currency Top Bar
            tech.nikelyh.quizpit.ui.components.CurrencyTopBar(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .zIndex(10f)
            )
        }
    }
}

@Composable
fun IdentityCard(state: ProfileState) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer { rotationZ = -2f }
            .background(Color.White, RoundedCornerShape(24.dp))
            .border(3.dp, Navy, RoundedCornerShape(24.dp))
            .padding(20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Google Avatar with ink border
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(Navy, CircleShape)
                    .padding(3.dp)
                    .clip(CircleShape)
            ) {
                AsyncImage(
                    model = state.avatarUrl,
                    contentDescription = "Avatar",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    fallback = androidx.compose.ui.res.painterResource(R.drawable.logo_quizpit) // Use logo as fallback
                )
            }

            Spacer(modifier = Modifier.width(20.dp))

            Column {
                Text(
                    text = state.name.uppercase(),
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black),
                    color = Navy
                )
                Surface(
                    color = YellowCTA,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Text(
                        text = state.rank.uppercase(),
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Black),
                        color = Navy,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
                Text(
                    text = state.email,
                    style = MaterialTheme.typography.bodySmall,
                    color = Navy.copy(alpha = 0.5f),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        Icon(
            imageVector = Icons.Rounded.Verified,
            contentDescription = null,
            tint = Color(0xFF4CAF50),
            modifier = Modifier.align(Alignment.TopEnd).size(24.dp)
        )
    }
}

@Composable
fun EquippedFamiliarCard(familiar: tech.nikelyh.quizpit.core.model.Familiar) {
    val themeColor = FamiliarTheme.getColorForPower(familiar.powerType)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .border(3.dp, Navy, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Paper)
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(themeColor.copy(alpha = 0.3f), CircleShape)
                    .border(2.dp, Navy.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                AnimatedFamiliarIcon(
                    id = familiar.id,
                    modifier = Modifier.size(60.dp),
                    isAnimationReady = true
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = familiar.name.uppercase(),
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                    color = Navy
                )
                Text(
                    text = familiar.powerName,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = Navy.copy(alpha = 0.6f)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Icon(
                imageVector = Icons.Rounded.AutoAwesome,
                contentDescription = null,
                tint = themeColor,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Composable
fun StatsGrid(state: ProfileState) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            StatBlock(Modifier.weight(1f), "PARTIDAS", state.totalGames.toString(), Icons.Rounded.History, Color(0xFFD1E9FF))
            StatBlock(Modifier.weight(1f), "VICTORIAS", "${state.winRate}%", Icons.Rounded.EmojiEvents, Color(0xFFffeb99))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            StatBlock(Modifier.weight(1f), "ACIERTOS", state.correctAnswers.toString(), Icons.Rounded.CheckCircle, Color(0xFFD1FFD7))
            StatBlock(Modifier.weight(1f), "COLECCIÓN", "${state.familiarsUnlocked}/${state.totalFamiliars}", Icons.Rounded.Pets, Color(0xFFF0D1FF))
        }
    }
}

@Composable
fun StatBlock(modifier: Modifier, label: String, value: String, icon: ImageVector, color: Color) {
    Box(
        modifier = modifier
            .height(100.dp)
            .background(Paper, RoundedCornerShape(20.dp))
            .sketchbookColoring(color)
            .border(2.dp, Navy, RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        Column {
            Icon(imageVector = icon, contentDescription = null, tint = Navy, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.weight(1f))
            Text(text = value, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black), color = Navy)
            Text(text = label, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = Navy.copy(alpha = 0.5f))
        }
    }
}

@Composable
fun GuestProfileView(onSignInClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(top = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(160.dp)
                .background(Color.White, CircleShape)
                .border(4.dp, Navy, CircleShape)
                .padding(24.dp)
                .sketchbookBounceIn(),
            contentAlignment = Alignment.Center
        ) {
            AnimatedFamiliarIcon(id = "chameleon", isLocked = true, modifier = Modifier.fillMaxSize())
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "REGISTRA TU DIARIO",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black),
            color = Navy
        )

        Text(
            text = "Vincula tu cuenta para guardar tu progreso, estadísticas y familiares míticos.",
            style = MaterialTheme.typography.bodyLarge,
            color = Navy.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        SketchbookButton(
            onClick = onSignInClick,
            backgroundColor = Color.White,
            contentColor = Navy,
            modifier = Modifier.fillMaxWidth().height(64.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Rounded.AccountCircle, contentDescription = null)
                Spacer(modifier = Modifier.width(12.dp))
                Text("INICIAR CON GOOGLE", fontWeight = FontWeight.Black)
            }
        }
    }
}
