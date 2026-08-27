package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.JarablusViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceProviderPortfolioScreen(
    viewModel: JarablusViewModel,
    isDark: Boolean,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val selectedProvider by viewModel.selectedProvider.collectAsState()
    val context = LocalContext.current

    val provider: UserEntity = selectedProvider ?: currentUser
    val isOwner = currentUser.id == provider.id

    val photoList = remember(provider.portfolioImages, currentUser.portfolioImages) {
        val target = if (isOwner) currentUser else provider
        if (target.portfolioImages.isBlank()) emptyList() else target.portfolioImages.split("|").filter { it.isNotBlank() }
    }

    var viewingImageIndex by remember { mutableStateOf<Int?>(null) }
    var isAddingPhotoDialog by remember { mutableStateOf(false) }

    val sampleWorkPhotos = listOf(
        "https://images.unsplash.com/photo-1581578731548-c64695cc6952?w=600",
        "https://images.unsplash.com/photo-1621905251189-08b45d6a269e?w=600",
        "https://images.unsplash.com/photo-1504307651254-35680f356dfd?w=600",
        "https://images.unsplash.com/photo-1513694203232-719a280e022f?w=600",
        "https://images.unsplash.com/photo-1541888946425-d0fbb18086f6?w=600",
        "https://images.unsplash.com/photo-1581244277943-fe4a9c777189?w=600"
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = if (isDark) BgDark else BgLight,
        topBar = {
            TopAppBar(
                title = { Text("معرض أعمال مقدم الخدمة", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "رجوع")
                    }
                },
                actions = {
                    if (isOwner) {
                        FilledTonalButton(
                            onClick = {
                                if (photoList.size >= 50) {
                                    Toast.makeText(context, "الحد الأقصى لمعرض الأعمال هو 50 صورة", Toast.LENGTH_SHORT).show()
                                } else {
                                    val randomSample = sampleWorkPhotos.random()
                                    viewModel.addPortfolioImage(randomSample)
                                    Toast.makeText(context, "تمت إضافة صورة لمعرض أعمالك ✓", Toast.LENGTH_SHORT).show()
                                }
                            },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("إضافة صورة", fontSize = 12.sp)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = if (isDark) SurfaceDark else SurfaceLight
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Provider Profile Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = if (isDark) SurfaceDark else SurfaceLight),
                border = BorderStroke(1.dp, PurpleSecondary.copy(alpha = 0.35f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .clip(CircleShape)
                                .background(PurpleSecondary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Handyman,
                                contentDescription = null,
                                tint = PurpleSecondary,
                                modifier = Modifier.size(30.dp)
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = provider.name,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) TextPrimaryDark else TextPrimaryLight
                            )
                            Text(
                                text = "${provider.serviceCategory.ifBlank { "خدمات عامة" }} • ${provider.city}",
                                fontSize = 13.sp,
                                color = PurpleSecondary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = PurpleSecondary.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "${photoList.size} / 50 صورة",
                                color = PurpleSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    if (provider.storeDescription.isNotBlank()) {
                        Text(
                            text = provider.storeDescription,
                            fontSize = 13.sp,
                            color = if (isDark) TextPrimaryDark else TextPrimaryLight,
                            lineHeight = 18.sp
                        )
                    }

                    // WhatsApp Direct Contact Button
                    val whatsapp = provider.whatsapp.ifBlank { provider.phone }
                    Button(
                        onClick = {
                            val cleanNumber = whatsapp.replace("+", "").replace(" ", "").trim()
                            val uri = Uri.parse("https://wa.me/$cleanNumber?text=${Uri.encode("السلام عليكم، رأيت خدماتك على تطبيق جرابلس اليوم وأود الاستفسار.")}")
                            val intent = Intent(Intent.ACTION_VIEW, uri)
                            try {
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                Toast.makeText(context, "رقم الواتساب: $whatsapp", Toast.LENGTH_LONG).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Chat, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "تواصل مباشر عبر واتساب ($whatsapp)",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            // Gallery Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "معرض الأعمال المنجزة",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) TextPrimaryDark else TextPrimaryLight
                )

                Text(
                    text = "سعة المعرض حتى 50 صورة",
                    fontSize = 11.sp,
                    color = if (isDark) TextSecondaryDark else TextSecondaryLight
                )
            }

            if (photoList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Collections,
                            contentDescription = null,
                            tint = PurpleSecondary.copy(alpha = 0.5f),
                            modifier = Modifier.size(54.dp)
                        )
                        Text(
                            text = if (isOwner) "لم تقم بإضافة أي صور لأعمالك بعد.\nاضغط 'إضافة صورة' لعرض إنجازاتك للزبائن!" else "لم يقم مقدم الخدمة بإضافة صور للمعرض بعد.",
                            color = if (isDark) TextSecondaryDark else TextSecondaryLight,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                ) {
                    itemsIndexed(photoList) { index, photoUrl ->
                        Surface(
                            onClick = { viewingImageIndex = index },
                            shape = RoundedCornerShape(12.dp),
                            color = if (isDark) SurfaceDark else SurfaceLight,
                            border = BorderStroke(1.dp, if (isDark) BorderDark else BorderLight),
                            modifier = Modifier
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(12.dp))
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = Icons.Default.Image,
                                        contentDescription = null,
                                        tint = PurpleSecondary,
                                        modifier = Modifier.size(32.dp)
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "عمل #${index + 1}",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isDark) TextPrimaryDark else TextPrimaryLight
                                    )
                                }

                                if (isOwner) {
                                    IconButton(
                                        onClick = { viewModel.removePortfolioImage(index) },
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .size(26.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Cancel,
                                            contentDescription = "حذف الصورة",
                                            tint = Color(0xFFDC2626),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Fullscreen / Zoom Photo Dialog
    if (viewingImageIndex != null) {
        AlertDialog(
            onDismissRequest = { viewingImageIndex = null },
            confirmButton = {
                TextButton(onClick = { viewingImageIndex = null }) {
                    Text("إغلاق")
                }
            },
            title = { Text("معاينة عمل #${(viewingImageIndex ?: 0) + 1}") },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (isDark) Color(0xFF1E293B) else Color(0xFFF1F5F9),
                        border = BorderStroke(1.dp, PurpleSecondary.copy(alpha = 0.4f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.Handyman,
                                    contentDescription = null,
                                    tint = PurpleSecondary,
                                    modifier = Modifier.size(54.dp)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text("صورة من أعمال: ${provider.name}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("المجال: ${provider.serviceCategory}", fontSize = 11.sp, color = Color.Gray)
                            }
                        }
                    }
                }
            }
        )
    }
}
