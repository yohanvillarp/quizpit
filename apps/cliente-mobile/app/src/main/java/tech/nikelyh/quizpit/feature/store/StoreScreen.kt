package tech.nikelyh.quizpit.feature.store

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Pets
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.revenuecat.purchases.ui.revenuecatui.PaywallDialog
import com.revenuecat.purchases.ui.revenuecatui.PaywallDialogOptions
import tech.nikelyh.quizpit.R
import tech.nikelyh.quizpit.core.sensor.ProvideAvatarTilt
import tech.nikelyh.quizpit.feature.familiars.ui.avatars.AnimatedFamiliarIcon
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.Info
import androidx.compose.ui.window.Dialog
import tech.nikelyh.quizpit.core.data.repository.AvatarRepository
import tech.nikelyh.quizpit.core.model.PowerType
import tech.nikelyh.quizpit.feature.familiars.ui.FamiliarTheme
import tech.nikelyh.quizpit.ui.components.CurrencyTopBar
import tech.nikelyh.quizpit.ui.components.SketchbookButton
import tech.nikelyh.quizpit.ui.components.sketchbookBackground
import tech.nikelyh.quizpit.ui.components.sketchbookColoring

// Analog Sketchbook Colors
private val Navy = Color(0xFF1a1f3a)
private val HighYellow = Color(0xFFffeb99)
private val HighPink = Color(0xFFf4d7e8)
private val Paper = Color(0xFFFAF9F5)

@Composable
fun StoreScreen(
    onBackClick: () -> Unit = {}
) {
    ProvideAvatarTilt {
        var showPaywall by remember { mutableStateOf(false) }

        if (showPaywall) {
            PaywallDialog(
                paywallDialogOptions = PaywallDialogOptions.Builder()
                    .setDismissRequest { showPaywall = false }
                    .build()
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .sketchbookBackground()
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 24.dp, bottom = 40.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                item {
                    Text(
                        text = stringResource(R.string.store_header_title),
                        style = MaterialTheme.typography.headlineLarge.copy(fontSize = 32.sp),
                        color = Navy,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                item {
                    ProSubscriptionBanner(onGetProClick = { showPaywall = true })
                }

                item {
                    SectionHeader(title = stringResource(R.string.store_ink_title))
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            StoreItemCard(modifier = Modifier.weight(1f), title = "+15 DROPS", iconRes = R.drawable.ic_ink, price = "$0.99", bgColor = HighPink)
                            StoreItemCard(modifier = Modifier.weight(1f), title = "+40 DROPS", iconRes = R.drawable.ic_ink, price = "$1.99", bgColor = HighPink)
                        }
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            StoreItemCard(modifier = Modifier.weight(1f), title = "+100 DROPS", iconRes = R.drawable.ic_ink, price = "$3.99", bgColor = HighPink)
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }

            CurrencyTopBar(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .zIndex(10f)
            )
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge.copy(
            fontSize = 20.sp,
            fontWeight = FontWeight.Black
        ),
        color = Navy,
        modifier = Modifier.padding(bottom = 12.dp)
    )
}

@Composable
private fun ProSubscriptionBanner(onGetProClick: () -> Unit) {
    var showMedusaSkills by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Paper)
            .sketchbookColoring(HighYellow)
            .border(3.dp, Navy, RoundedCornerShape(24.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.store_pro_title),
                style = MaterialTheme.typography.headlineLarge.copy(fontSize = 28.sp),
                color = Navy
            )

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ProBenefitRow(icon = Icons.Rounded.Pets, text = stringResource(R.string.store_pro_benefit_mythic))
                ProBenefitRow(icon = Icons.Rounded.Star, text = stringResource(R.string.store_pro_benefit_ink))
                ProBenefitRow(icon = Icons.Rounded.Description, text = "Archivos de hasta 10MB (PDF)")
                ProBenefitRow(icon = Icons.Rounded.AutoAwesome, text = stringResource(R.string.store_pro_benefit_ads))
            }

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .size(100.dp)
                    .padding(bottom = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                // Familiar Preview
                AnimatedFamiliarIcon(
                    id = "medusa",
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    isLocked = false
                )

                // Icono para ver habilidades
                IconButton(
                    onClick = { showMedusaSkills = true },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = 12.dp, y = 12.dp)
                        .background(Navy, RoundedCornerShape(20.dp))
                        .size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Info,
                        contentDescription = "Ver habilidades",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            SketchbookButton(
                onClick = onGetProClick,
                backgroundColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "${stringResource(R.string.store_pro_btn)} - $3.99/mo",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }
    }

    if (showMedusaSkills) {
        val medusaDto = AvatarRepository.avatars.value.find { it.id == "medusa" }
        medusaDto?.let { dto ->
            val medusa = tech.nikelyh.quizpit.core.model.Familiar(
                id = dto.id,
                name = dto.name,
                powerName = dto.powerName,
                description = dto.powerDescription,
                isMythic = dto.isMythic,
                powerType = try { PowerType.valueOf(dto.powerType) } catch(e: Exception) { PowerType.SPECIAL },
                isUnlocked = dto.isUnlocked
            )
            MedusaSkillPreviewDialog(familiar = medusa, onDismiss = { showMedusaSkills = false })
        }
    }
}

@Composable
fun MedusaSkillPreviewDialog(
    familiar: tech.nikelyh.quizpit.core.model.Familiar,
    onDismiss: () -> Unit
) {
    val themeColor = FamiliarTheme.getColorForPower(familiar.powerType)

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .border(4.dp, Navy, RoundedCornerShape(32.dp)),
            shape = RoundedCornerShape(32.dp),
            colors = CardDefaults.cardColors(containerColor = Paper)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "RECOMPENSA PRO",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Black),
                    color = Navy.copy(alpha = 0.4f)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .background(themeColor.copy(alpha = 0.2f), RoundedCornerShape(70.dp))
                        .border(3.dp, Navy, RoundedCornerShape(70.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    AnimatedFamiliarIcon(
                        id = familiar.id,
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        isAnimationReady = true
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = familiar.name.uppercase(),
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black),
                    color = Navy
                )

                Text(
                    text = familiar.powerName,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = themeColor
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = familiar.description,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    color = Navy.copy(alpha = 0.8f)
                )

                Spacer(modifier = Modifier.height(24.dp))

                SketchbookButton(
                    onClick = onDismiss,
                    backgroundColor = Navy,
                    contentColor = Color.White,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("CERRAR", fontWeight = FontWeight.Black)
                }
            }
        }
    }
}

@Composable
private fun StoreItemCard(
    modifier: Modifier = Modifier,
    title: String,
    iconRes: Int,
    price: String,
    bgColor: Color
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Paper)
            .sketchbookColoring(bgColor)
            .border(3.dp, Navy, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                modifier = Modifier.size(48.dp).padding(bottom = 12.dp)
            )

            Text(
                text = title.uppercase(),
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Black
                ),
                color = Navy,
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            SketchbookButton(
                onClick = { /* TODO: Handle purchase */ },
                backgroundColor = Paper,
                contentColor = Navy,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = price,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }
    }
}

@Composable
private fun ProBenefitRow(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Navy,
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            ),
            color = Navy.copy(alpha = 0.8f)
        )
    }
}
