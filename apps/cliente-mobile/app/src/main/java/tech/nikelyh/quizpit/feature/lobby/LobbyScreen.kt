package tech.nikelyh.quizpit.feature.lobby

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.json.JSONObject
import tech.nikelyh.quizpit.R
import tech.nikelyh.quizpit.core.network.SocketClient
import tech.nikelyh.quizpit.ui.components.SketchbookButton
import tech.nikelyh.quizpit.ui.components.sketchbookBackground
import androidx.activity.compose.BackHandler
import tech.nikelyh.quizpit.core.designsystem.component.SketchbookDialog

// Colors matching Analog Sketchbook design
private val Navy = Color(0xFF1a1f3a)
private val Paper = Color(0xFFFAF9F5)
private val HostYellow = Color(0xFFffeb99)
private val OtherPlayerWhite = Color.White

data class Player(
    val id: String, 
    val name: String, 
    val isHost: Boolean = false,
    val avatarId: String = "fox"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LobbyScreen(
    roomId: String,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val deviceId = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    val socket = remember { SocketClient.getSocket() }
    
    var players by remember { mutableStateOf<List<Player>>(emptyList()) }
    var isHost by remember { mutableStateOf(false) }
    var showExitDialog by remember { mutableStateOf(false) }
    var isQuizReady by remember { mutableStateOf(false) }

    BackHandler {
        if (isHost) {
            showExitDialog = true
        } else {
            onNavigateBack()
        }
    }

    DisposableEffect(roomId) {
        SocketClient.connect()
        
        socket.on("room_state") { args ->
            if (args.isNotEmpty()) {
                val data = args[0] as JSONObject
                val hostId = data.optString("hostId", "")
                isHost = (hostId == deviceId)
                
                // Read the quizReady state if provided by the backend
                if (data.has("isQuizReady")) {
                    isQuizReady = data.optBoolean("isQuizReady", false)
                }
                
                val playersArray = data.optJSONArray("players")
                val updatedPlayers = mutableListOf<Player>()
                if (playersArray != null) {
                    for (i in 0 until playersArray.length()) {
                        val playerObj = playersArray.getJSONObject(i)
                        val id = playerObj.optString("deviceId", playerObj.optString("id", ""))
                        updatedPlayers.add(
                            Player(
                                id = id,
                                name = playerObj.optString("name", "Jugador ${i + 1}"),
                                isHost = (id == hostId),
                                avatarId = playerObj.optString("avatarId", "fox")
                            )
                        )
                    }
                }
                players = updatedPlayers
            }
        }
        
        // Listen to player_joined just in case game-engine emits this instead
        socket.on("player_joined") { args ->
            if (args.isNotEmpty()) {
                val data = args[0] as JSONObject
                val playersArray = data.optJSONArray("players")
                val updatedPlayers = mutableListOf<Player>()
                
                // Identify host dynamically if it's the first or by 'isHost' flag in payload
                var localHostId = ""
                
                if (playersArray != null) {
                    for (i in 0 until playersArray.length()) {
                        val playerObj = playersArray.getJSONObject(i)
                        val id = playerObj.optString("deviceId", playerObj.optString("id", ""))
                        val playerIsHost = playerObj.optBoolean("isHost", false)
                        if (playerIsHost) localHostId = id
                        
                        updatedPlayers.add(
                            Player(
                                id = id,
                                name = playerObj.optString("name", "Jugador ${i + 1}"),
                                isHost = playerIsHost,
                                avatarId = playerObj.optString("avatarId", "fox")
                            )
                        )
                    }
                }
                
                if (localHostId.isEmpty() && updatedPlayers.isNotEmpty()) {
                    // Fallback if isHost wasn't sent
                    localHostId = updatedPlayers.first().id
                    val modifiedPlayers = updatedPlayers.map { it.copy(isHost = it.id == localHostId) }
                    players = modifiedPlayers
                } else {
                    players = updatedPlayers
                }
                
                isHost = (localHostId == deviceId)
            }
        }
        
        socket.on("quiz_ready") {
            isQuizReady = true
        }
        
        // Join room logic
        val joinData = JSONObject().apply {
            put("roomId", roomId)
            put("deviceId", deviceId)
            put("name", "Jugador") // We should probably get this from preferences
            put("avatarId", "fox") // Fallback, could be fetched from prefs
        }
        socket.emit("join_room", joinData)
        
        onDispose {
            socket.emit("leave_room", JSONObject().apply { 
                put("roomId", roomId) 
                put("deviceId", deviceId)
            })
            socket.off("room_state")
            socket.off("player_joined")
            SocketClient.disconnect()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Inicio", fontWeight = FontWeight.Black) },
                navigationIcon = {
                    IconButton(onClick = {
                        if (isHost) showExitDialog = true else onNavigateBack()
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        containerColor = Color.Transparent
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Paper)
                .sketchbookBackground(gridColor = Navy.copy(alpha = 0.1f))
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Info Card de Sala
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                        .border(3.dp, Navy, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "CÓDIGO DE SALA",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Navy.copy(alpha = 0.5f),
                            letterSpacing = 1.sp
                        )
                        
                        Text(
                            text = roomId,
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Black,
                            color = Navy,
                            letterSpacing = 4.sp
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        
                        
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            SketchbookButton(
                                onClick = {
                                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                        type = "text/plain"
                                        putExtra(Intent.EXTRA_SUBJECT, "¡Únete a mi partida en QuizPit!")
                                        putExtra(
                                            Intent.EXTRA_TEXT,
                                            "¡Entra a mi sala para jugar! Código: $roomId\nO haz clic: https://quizpit.nikelyh.tech/lobby/$roomId"
                                        )
                                    }
                                    context.startActivity(Intent.createChooser(shareIntent, "Compartir sala vía..."))
                                },
                                backgroundColor = Color.White,
                                contentColor = Navy,
                                modifier = Modifier.weight(1f)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Compartir", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                }
                            }
                        }
                    }
                }
                
                
                // Grilla de Jugadores
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(players) { player ->
                        PlayerCard(
                            player = player,
                            isLocalPlayer = (player.id == deviceId)
                        )
                    }
                }
                
                // Start Button (Only for Host)
                if (isHost) {
                    Spacer(modifier = Modifier.height(16.dp))
                    SketchbookButton(
                        onClick = {
                            if (isQuizReady) {
                                socket.emit("start_game", JSONObject().apply { put("roomId", roomId) })
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp),
                        backgroundColor = if (isQuizReady) HostYellow else Color.LightGray,
                        contentColor = Navy
                    ) {
                        if (!isQuizReady) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = Navy,
                                    strokeWidth = 3.dp
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "GENERANDO PREGUNTAS...",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        } else {
                            Text(
                                text = "▶ INICIAR",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }
            }
        }
    }
    
    if (showExitDialog) {
        SketchbookDialog(
            title = "Cerrar Sala",
            message = "Si sales de la sala, esta se destruirá y todos los jugadores serán expulsados. Perderás la tinta invertida. ¿Estás seguro?",
            confirmText = "CONFIRMAR",
            cancelText = "CANCELAR",
            onConfirm = {
                showExitDialog = false
                socket.emit("destroy_room", JSONObject().apply { put("roomId", roomId) })
                onNavigateBack()
            },
            onCancel = { showExitDialog = false },
            onDismissRequest = { showExitDialog = false }
        )
    }
}

@Composable
fun PlayerCard(player: Player, isLocalPlayer: Boolean) {
    // Resolve familiar data from API Repository
    val familiar = tech.nikelyh.quizpit.core.data.repository.AvatarRepository.getAvatarById(player.avatarId)
    val title = familiar?.powerName?.uppercase() ?: "JUGADOR"
    
    // Resolve avatar drawable
    val avatarResId = when (player.avatarId) {
        "fox" -> R.drawable.ic_familiar_fox
        "owl" -> R.drawable.ic_familiar_owl
        "bear" -> R.drawable.ic_familiar_bear
        "cat" -> R.drawable.ic_familiar_cat
        "rabbit" -> R.drawable.ic_familiar_rabbit
        "dog" -> R.drawable.ic_familiar_dog
        "peacock" -> R.drawable.ic_familiar_peacock
        "dragon" -> R.drawable.ic_familiar_dragon
        "bat" -> R.drawable.ic_familiar_bat
        "chameleon" -> R.drawable.ic_familiar_chameleon
        "gallo" -> R.drawable.ic_familiar_gallo
        else -> R.drawable.ic_profile // fallback
    }

    val backgroundColor = if (player.isHost) HostYellow else OtherPlayerWhite

    Box(
        modifier = Modifier.width(100.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            
            // Espacio superior para la corona si existe
            Spacer(modifier = Modifier.height(if(player.isHost) 12.dp else 0.dp))
            
            // Avatar Box
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(backgroundColor, RoundedCornerShape(16.dp))
                    .border(3.dp, Navy, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = avatarResId),
                    contentDescription = "Avatar",
                    modifier = Modifier
                        .size(48.dp)
                        .align(Alignment.Center)
                )
            }
            
            // Name and Title Block
            Box(
                modifier = Modifier
                    .offset(y = (-8).dp)
                    .width(90.dp)
                    .background(Navy, RoundedCornerShape(6.dp))
                    .border(2.dp, Navy, RoundedCornerShape(6.dp)) // Same color just for rounded shape stability
                    .padding(vertical = 4.dp, horizontal = 2.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = player.name.uppercase(),
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 11.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (isLocalPlayer) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Box(
                                modifier = Modifier
                                    .background(HostYellow, RoundedCornerShape(4.dp))
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "TÚ",
                                    color = Navy,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 8.sp
                                )
                            }
                        }
                    }
                    Text(
                        text = title,
                        color = Color.White.copy(alpha = 0.7f),
                        fontWeight = FontWeight.Bold,
                        fontSize = 8.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
        
        // Crown Badge
        if (player.isHost) {
            Image(
                painter = painterResource(id = R.drawable.ic_crown),
                contentDescription = "Host Crown",
                modifier = Modifier
                    .size(32.dp)
                    .align(Alignment.TopCenter)
                    .offset(y = (-15).dp)
            )
        }
    }
}
