package tech.nikelyh.quizpit.feature.familiars

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import tech.nikelyh.quizpit.R
import tech.nikelyh.quizpit.core.model.Familiar
import tech.nikelyh.quizpit.core.model.PowerType
import tech.nikelyh.quizpit.core.sensor.ProvideAvatarTilt
import tech.nikelyh.quizpit.feature.familiars.ui.FamiliarTheme
import tech.nikelyh.quizpit.feature.familiars.ui.avatars.AnimatedFamiliarIcon
import tech.nikelyh.quizpit.ui.components.SketchbookButton
import tech.nikelyh.quizpit.ui.components.sketchbookBackground
import tech.nikelyh.quizpit.ui.components.sketchbookColoring
import tech.nikelyh.quizpit.ui.components.sketchbookBounceIn

// Nueva Identidad Visual (Estilo Inicio)
val Navy = Color(0xFF1a1f3a)
val PinkBg = Color(0xFFf4d7e8)
val YellowCTA = Color(0xFFffeb99)
val GrayLight = Color(0xFFf5f5f5)
val GrayText = Color(0xFF666666)

enum class PowerFilter { ALL, OFFENSIVE, DEFENSIVE, TACTICAL, SPECIAL }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FamiliarsScreen(
    onBackClick: () -> Unit,
    viewModel: FamiliarsViewModel = viewModel()
) {
    ProvideAvatarTilt {
        val familiars by viewModel.familiars.collectAsState()
        val equippedFamiliar by viewModel.equippedFamiliar.collectAsState()

        val scrollState = rememberLazyGridState()
        val coroutineScope = rememberCoroutineScope()
        val context = LocalContext.current

        var draggedFamiliar by remember { mutableStateOf<Familiar?>(null) }
        var dragPosition by remember { mutableStateOf(Offset.Zero) }
        var dropZoneBounds by remember { mutableStateOf(Rect.Zero) }
        var isHoveringDropZone by remember { mutableStateOf(false) }
        var selectedFamiliarForDetails by remember { mutableStateOf<Familiar?>(null) }
        var currentPowerFilter by remember { mutableStateOf(PowerFilter.ALL) }

        // Deferred Rendering State
        var animationsReady by remember { mutableStateOf(false) }
        LaunchedEffect(Unit) {
            delay(300)
            animationsReady = true
        }

        val screenHeightPx = with(LocalDensity.current) { LocalConfiguration.current.screenHeightDp.dp.toPx() }

        // Efecto de Auto-Scroll cuando se arrastra a los bordes
        LaunchedEffect(dragPosition, draggedFamiliar) {
            if (draggedFamiliar != null) {
                val topThreshold = 350f
                val bottomThreshold = screenHeightPx - 350f

                if (dragPosition.y < topThreshold) {
                    while (draggedFamiliar != null && dragPosition.y < topThreshold) {
                        scrollState.scrollBy(-20f)
                        delay(16) // ~60fps
                    }
                } else if (dragPosition.y > bottomThreshold) {
                    while (draggedFamiliar != null && dragPosition.y > bottomThreshold) {
                        scrollState.scrollBy(20f)
                        delay(16)
                    }
                }
            }
        }

        val unequippedFamiliars = familiars.filter { it.id != equippedFamiliar.id }

        Box(modifier = Modifier.fillMaxSize()) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                state = scrollState,
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 40.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .sketchbookBackground()
            ) {
                item(span = { GridItemSpan(2) }) {
                    Column {
                        // Header Principal
                        Column(modifier = Modifier.padding(bottom = 8.dp)) {
                            Text(
                                text = stringResource(R.string.familiars_active_title),
                                style = MaterialTheme.typography.headlineLarge.copy(fontSize = 24.sp),
                                color = Navy
                            )
                            Text(
                                text = stringResource(R.string.familiars_active_subtitle),
                                style = MaterialTheme.typography.titleLarge.copy(fontSize = 16.sp, fontWeight = FontWeight.SemiBold),
                                color = Navy.copy(alpha = 0.8f)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        // Contenido: Personaje Activo
                        Box(
                            modifier = Modifier.fillMaxWidth().onGloballyPositioned { dropZoneBounds = it.boundsInWindow() }
                        ) {
                            AnimatedContent(
                                targetState = equippedFamiliar,
                                transitionSpec = {
                                    (fadeIn() + scaleIn(initialScale = 0.8f, animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)))
                                        .togetherWith(fadeOut() + scaleOut(targetScale = 0.8f))
                                        .using(SizeTransform(clip = false))
                                },
                                contentAlignment = Alignment.Center,
                                label = "ActiveFamiliarAnimation"
                            ) { animatedFamiliar ->
                                FamiliarGridCard(
                                    familiar = animatedFamiliar,
                                    isEquipped = true,
                                    onViewDetails = {
                                        tech.nikelyh.quizpit.core.audio.SoundManager.playOpeningSound(context)
                                        tech.nikelyh.quizpit.core.audio.SoundManager.playCharacterSound(context, animatedFamiliar.id)
                                        selectedFamiliarForDetails = animatedFamiliar
                                    },
                                    isFullWidth = true,
                                    isAnimationReady = animationsReady
                                )
                            }

                            if (isHoveringDropZone) {
                                Box(
                                    modifier = Modifier
                                        .matchParentSize()
                                        .background(Color.White.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        // Separador y Header de Colección
                        Column(modifier = Modifier.padding(bottom = 16.dp)) {
                            Text(
                                text = stringResource(R.string.familiars_collection_title),
                                style = MaterialTheme.typography.headlineLarge.copy(fontSize = 24.sp),
                                color = Navy
                            )
                            Text(
                                text = stringResource(R.string.familiars_collection_subtitle),
                                style = MaterialTheme.typography.titleLarge.copy(fontSize = 16.sp, fontWeight = FontWeight.SemiBold),
                                color = Navy.copy(alpha = 0.8f)
                            )
                        }

                        // Filtros
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            FilterIconTab(
                                icon = Icons.Rounded.Apps,
                                text = stringResource(R.string.familiars_filter_all),
                                isSelected = currentPowerFilter == PowerFilter.ALL,
                                onClick = { currentPowerFilter = PowerFilter.ALL }
                            )
                            FilterIconTab(
                                icon = Icons.Rounded.FlashOn,
                                text = stringResource(R.string.familiars_filter_offensive),
                                isSelected = currentPowerFilter == PowerFilter.OFFENSIVE,
                                powerType = PowerType.OFFENSIVE,
                                onClick = { currentPowerFilter = PowerFilter.OFFENSIVE }
                            )
                            FilterIconTab(
                                icon = Icons.Rounded.Shield,
                                text = stringResource(R.string.familiars_filter_defensive),
                                isSelected = currentPowerFilter == PowerFilter.DEFENSIVE,
                                powerType = PowerType.DEFENSIVE,
                                onClick = { currentPowerFilter = PowerFilter.DEFENSIVE }
                            )
                            FilterIconTab(
                                icon = Icons.Rounded.Visibility,
                                text = stringResource(R.string.familiars_filter_tactical),
                                isSelected = currentPowerFilter == PowerFilter.TACTICAL,
                                powerType = PowerType.TACTICAL,
                                onClick = { currentPowerFilter = PowerFilter.TACTICAL }
                            )
                            FilterIconTab(
                                icon = Icons.Rounded.Star,
                                text = stringResource(R.string.familiars_filter_special),
                                isSelected = currentPowerFilter == PowerFilter.SPECIAL,
                                powerType = PowerType.SPECIAL,
                                onClick = { currentPowerFilter = PowerFilter.SPECIAL }
                            )
                        }
                    }
                }

                // Cards de los Personajes
                val filteredFamiliars = unequippedFamiliars.filter {
                    when (currentPowerFilter) {
                        PowerFilter.ALL -> true
                        PowerFilter.OFFENSIVE -> it.powerType == PowerType.OFFENSIVE
                        PowerFilter.DEFENSIVE -> it.powerType == PowerType.DEFENSIVE
                        PowerFilter.TACTICAL -> it.powerType == PowerType.TACTICAL
                        PowerFilter.SPECIAL -> it.powerType == PowerType.SPECIAL
                    }
                }.sortedWith(compareByDescending<Familiar> { it.isUnlocked }.thenByDescending { it.isMythic })

                items(filteredFamiliars, key = { it.id }) { familiar ->
                    FamiliarGridCard(
                        familiar = familiar,
                        isEquipped = false,
                        onViewDetails = {
                            tech.nikelyh.quizpit.core.audio.SoundManager.playOpeningSound(context)
                            tech.nikelyh.quizpit.core.audio.SoundManager.playCharacterSound(context, familiar.id)
                            selectedFamiliarForDetails = familiar
                        },
                        isFullWidth = false,
                        isAnimationReady = animationsReady,
                        isDragged = draggedFamiliar?.id == familiar.id,
                        onDragStart = if (!familiar.isUnlocked) null else { globalOffset: Offset ->
                            draggedFamiliar = familiar
                            dragPosition = globalOffset
                        },
                        onDrag = { dragAmount: Offset ->
                            dragPosition += dragAmount
                            isHoveringDropZone = dropZoneBounds.contains(dragPosition)
                        },
                        onDragEnd = {
                            if (isHoveringDropZone) {
                                viewModel.equipFamiliar(familiar)
                                coroutineScope.launch { scrollState.animateScrollToItem(0) }
                            }
                            draggedFamiliar = null
                            isHoveringDropZone = false
                        },
                        onDragCancel = {
                            draggedFamiliar = null
                            isHoveringDropZone = false
                        }
                    )
                }
            }

            // Drag Overlay
            draggedFamiliar?.let { familiar ->
                Box(
                    modifier = Modifier
                        .offset {
                            androidx.compose.ui.unit.IntOffset(
                                dragPosition.x.toInt() - 250,
                                dragPosition.y.toInt() - 350
                            )
                        }
                        .width(180.dp)
                        .graphicsLayer { alpha = 0.9f; rotationZ = 5f; scaleX = 1.05f; scaleY = 1.05f }
                ) {
                    FamiliarGridCard(
                        familiar = familiar,
                        isEquipped = false,
                        onViewDetails = {},
                        isFullWidth = false,
                        isAnimationReady = true
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

        // Modal Details
        if (selectedFamiliarForDetails != null) {
            val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            ModalBottomSheet(
                onDismissRequest = { selectedFamiliarForDetails = null },
                sheetState = sheetState,
                containerColor = FamiliarTheme.getColorForPower(selectedFamiliarForDetails!!.powerType),
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            ) {
                FamiliarDetailsSheet(
                    familiar = selectedFamiliarForDetails!!,
                    isEquipped = selectedFamiliarForDetails!!.id == equippedFamiliar.id,
                    isAnimationReady = true,
                    onEquip = {
                        tech.nikelyh.quizpit.core.audio.SoundManager.playEquipSound(context)
                        viewModel.equipFamiliar(selectedFamiliarForDetails!!)
                        coroutineScope.launch {
                            sheetState.hide()
                            selectedFamiliarForDetails = null
                            scrollState.animateScrollToItem(0)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun FamiliarGridCard(
    familiar: Familiar,
    isEquipped: Boolean,
    onViewDetails: () -> Unit,
    isFullWidth: Boolean,
    isAnimationReady: Boolean = true,
    isDragged: Boolean = false,
    onDragStart: ((Offset) -> Unit)? = null,
    onDrag: ((Offset) -> Unit)? = null,
    onDragEnd: (() -> Unit)? = null,
    onDragCancel: (() -> Unit)? = null
) {
    var globalBounds by remember { mutableStateOf(Rect.Zero) }
    val haptic = LocalHapticFeedback.current
    var isPressed by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 4.dp)
            .onGloballyPositioned { globalBounds = it.boundsInWindow() }
            .then(
                if (onDragStart != null) {
                    Modifier.pointerInput(familiar) {
                        detectDragGesturesAfterLongPress(
                            onDragStart = { localOffset ->
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                onDragStart(globalBounds.topLeft + localOffset)
                            },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                onDrag?.invoke(dragAmount)
                            },
                            onDragEnd = { onDragEnd?.invoke() },
                            onDragCancel = { onDragCancel?.invoke() }
                        )
                    }
                } else Modifier
            )
    ) {
        if (isDragged) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(Color.Gray.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
                    .border(2.dp, Navy.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
            )
        }

        Box(
            modifier = Modifier.graphicsLayer { alpha = if (isDragged) 0f else 1f }
        ) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .offset(x = 0.dp, y = 4.dp)
                    .background(Navy.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .then(if (!isFullWidth) Modifier.aspectRatio(1f) else Modifier)
                    .background(Color(0xFFFAF9F5), RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp))
                    .sketchbookColoring(FamiliarTheme.getColorForPower(familiar.powerType))
                    .border(3.dp, Navy, RoundedCornerShape(16.dp))
                    .clickable {
                        isPressed = !isPressed
                        onViewDetails()
                    },
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                shape = RoundedCornerShape(16.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(if (isFullWidth) 110.dp else 85.dp)
                                .padding(bottom = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            AnimatedFamiliarIcon(
                                id = familiar.id,
                                contentDescription = familiar.name,
                                modifier = Modifier.fillMaxSize(),
                                isLocked = !familiar.isUnlocked,
                                isAnimationReady = isAnimationReady,
                                isPressed = isPressed
                            )
                        }

                        Text(
                            text = familiar.name.uppercase(),
                            style = MaterialTheme.typography.headlineLarge.copy(fontSize = 16.sp),
                            color = Navy
                        )

                        Text(
                            text = if (!familiar.isUnlocked) stringResource(R.string.familiars_unknown_power) else familiar.powerName,
                            style = MaterialTheme.typography.titleLarge.copy(fontSize = 13.sp),
                            color = Navy.copy(alpha = 0.8f),
                            textAlign = TextAlign.Center
                        )
                    }
                    if (isEquipped) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .offset(x = (-8).dp, y = 8.dp)
                                .graphicsLayer { rotationZ = 12f }
                                .background(Color(0xFFFAF9F5))
                                .sketchbookColoring(YellowCTA)
                                .border(2.dp, Navy)
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = stringResource(R.string.familiars_badge_active),
                                style = MaterialTheme.typography.headlineLarge.copy(
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Black
                                ),
                                color = Navy
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FamiliarDetailsSheet(
    familiar: Familiar,
    isEquipped: Boolean,
    isAnimationReady: Boolean = true,
    onEquip: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(160.dp)
                .background(Color.White, RoundedCornerShape(32.dp))
                .border(4.dp, Navy, RoundedCornerShape(32.dp))
                .padding(16.dp)
                .sketchbookBounceIn(),
            contentAlignment = Alignment.Center
        ) {
            AnimatedFamiliarIcon(
                id = familiar.id,
                contentDescription = familiar.name,
                modifier = Modifier.fillMaxSize(),
                isLocked = !familiar.isUnlocked,
                isAnimationReady = isAnimationReady
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = familiar.name.uppercase(),
            style = MaterialTheme.typography.headlineLarge.copy(fontSize = 28.sp),
            color = Navy
        )
        Text(
            text = if (!familiar.isUnlocked) stringResource(R.string.familiars_unknown_power) else familiar.powerName,
            style = MaterialTheme.typography.titleLarge.copy(fontSize = 18.sp, fontWeight = FontWeight.SemiBold),
            color = Navy.copy(alpha = 0.8f)
        )

        Spacer(modifier = Modifier.height(24.dp))

        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = false)
                .verticalScroll(scrollState)
        ) {
            Text(
                text = if (!familiar.isUnlocked) stringResource(R.string.familiars_unknown_desc) else familiar.description,
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 15.sp, lineHeight = 20.sp),
                color = Navy.copy(alpha = 0.9f),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        val isLocked = !familiar.isUnlocked
        SketchbookButton(
            onClick = onEquip,
            enabled = !isEquipped && !isLocked,
            backgroundColor = YellowCTA,
            contentColor = Navy,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Text(
                text = if (isLocked) stringResource(R.string.familiars_btn_locked) else if (isEquipped) stringResource(R.string.familiars_btn_equipped) else stringResource(R.string.familiars_btn_equip),
                fontSize = 20.sp,
                fontWeight = FontWeight.Black
            )
        }
    }
}

@Composable
fun RowScope.FilterIconTab(
    icon: ImageVector,
    text: String,
    isSelected: Boolean,
    powerType: PowerType? = null,
    onClick: () -> Unit
) {
    val selectedColor = powerType?.let { FamiliarTheme.getColorForPower(it) } ?: YellowCTA
    val bgColor = if (isSelected) selectedColor else Color(0xFFFAF9F5)
    val iconColor = if (isSelected) Navy else GrayText
    val borderColor = if (isSelected) Navy else Navy.copy(alpha = 0.3f)

    Box(
        modifier = Modifier
            .weight(1f)
            .padding(horizontal = 2.dp)
            .clickable(onClick = onClick)
            .background(Color(0xFFFAF9F5), RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .sketchbookColoring(bgColor)
            .border(if (isSelected) 2.dp else 1.dp, borderColor, RoundedCornerShape(12.dp))
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(24.dp).padding(bottom = 4.dp)
            )
            Text(
                text = text.uppercase(),
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 9.sp,
                    fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold
                ),
                color = iconColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
