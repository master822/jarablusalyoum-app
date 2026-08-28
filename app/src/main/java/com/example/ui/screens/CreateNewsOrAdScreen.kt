package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserRole
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.JarablusViewModel

enum class ContentCreationType(val titleAr: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    NEWS("نشر خبر", Icons.Default.Newspaper),
    ANNOUNCEMENT("إعلان مبوب", Icons.Default.Campaign),
    PROPERTY("عقار (بيع/إيجار)", Icons.Default.HomeWork)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateNewsOrAdScreen(
    viewModel: JarablusViewModel,
    isDark: Boolean,
    onNavigateBack: () -> Unit,
    onNavigateProperty: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val context = LocalContext.current

    var selectedType by remember { mutableStateOf(ContentCreationType.NEWS) }

    // News state
    var newsTitle by remember { mutableStateOf("") }
    var newsContent by remember { mutableStateOf("") }
    var newsCategory by remember { mutableStateOf("أخبار المدينة") }

    val newsCategories = listOf("أخبار المدينة", "خدمات عامة", "أحداث ومجتمع", "تعليم ومدارس", "صحة ومشافي", "فعاليات")

    // Announcement state
    var adTitle by remember { mutableStateOf("") }
    var adContent by remember { mutableStateOf("") }
    var adCategory by remember { mutableStateOf("إعلانات عامة") }
    var adPhone by remember { mutableStateOf(currentUser.phone) }

    val adCategories = listOf("إعلانات عامة", "مفقودات ومعثورات", "طلب وظيفة / عمال", "أجهزة وإلكترونيات", "سيارات ومعدات", "خدمات سريعة")

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = if (isDark) BgDark else BgLight,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "نشر محتوى جديد",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "رجوع")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = if (isDark) SurfaceDark else SurfaceLight
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "اختر نوع المحتوى المراد نشره في المنصة:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = if (isDark) TextSecondaryDark else TextSecondaryLight
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ContentCreationType.values().forEach { type ->
                        val isSelected = selectedType == type
                        Surface(
                            onClick = {
                                if (type == ContentCreationType.PROPERTY) {
                                    onNavigateProperty()
                                } else {
                                    selectedType = type
                                }
                            },
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) GoldPrimary else (if (isDark) SurfaceDark else SurfaceLight),
                            border = BorderStroke(1.dp, if (isSelected) GoldPrimary else (if (isDark) BorderDark else BorderLight)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 12.dp, horizontal = 6.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = type.icon,
                                    contentDescription = null,
                                    tint = if (isSelected) Color.Black else (if (isDark) GoldLight else GoldDark),
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = type.titleAr,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.Black else (if (isDark) TextPrimaryDark else TextPrimaryLight)
                                )
                            }
                        }
                    }
                }
            }

            // Moderation Info Box
            item {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF0284C7).copy(alpha = 0.1f),
                    border = BorderStroke(1.dp, Color(0xFF0284C7).copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.VerifiedUser,
                            contentDescription = null,
                            tint = Color(0xFF0284C7),
                            modifier = Modifier.size(24.dp)
                        )
                        Column {
                            Text(
                                text = "تدقيق ومراجعة الإدارة (خلال 24 ساعة)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = Color(0xFF0284C7)
                            )
                            Text(
                                text = "وفقاً لشروط المنصة، يتم إرسال المنشور إلى الإدارة للمراجعة والموافقة لمنع المخالفات، ويصلك إشعار فوري بنتيجة الاعتماد.",
                                fontSize = 11.sp,
                                color = if (isDark) TextSecondaryDark else TextSecondaryLight
                            )
                        }
                    }
                }
            }

            if (selectedType == ContentCreationType.NEWS) {
                // News Form
                item {
                    Text(
                        text = "تفاصيل الخبر الصحفي / المحلي",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                item {
                    OutlinedTextField(
                        value = newsTitle,
                        onValueChange = { newsTitle = it },
                        label = { Text("عنوان الخبر") },
                        placeholder = { Text("مثال: افتتاح مركز خدمات جديد في جرابلس") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    Text("تصنيف الخبر:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        newsCategories.take(3).forEach { cat ->
                            FilterChip(
                                selected = newsCategory == cat,
                                onClick = { newsCategory = cat },
                                label = { Text(cat, fontSize = 11.sp) }
                            )
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        newsCategories.drop(3).forEach { cat ->
                            FilterChip(
                                selected = newsCategory == cat,
                                onClick = { newsCategory = cat },
                                label = { Text(cat, fontSize = 11.sp) }
                            )
                        }
                    }
                }

                item {
                    OutlinedTextField(
                        value = newsContent,
                        onValueChange = { newsContent = it },
                        label = { Text("نص الخبر والتفاصيل الكاملة") },
                        placeholder = { Text("اكتب تفاصيل الخبر ومصادره هنا...") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 5
                    )
                }

                item {
                    Button(
                        onClick = {
                            if (newsTitle.isBlank() || newsContent.isBlank()) {
                                Toast.makeText(context, "يرجى تعبئة عنوان الخبر وتفاصيله", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            viewModel.submitNews(
                                title = newsTitle.trim(),
                                content = newsContent.trim(),
                                category = newsCategory
                            )
                            Toast.makeText(
                                context,
                                "تم إرسال الخبر للإدارة وسيصلك إشعار عند المراجعة والاعتماد",
                                Toast.LENGTH_LONG
                            ).show()
                            onNavigateBack()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("submit_news_btn")
                    ) {
                        Icon(imageVector = Icons.Default.Send, contentDescription = null, tint = Color.Black)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("إرسال الخبر للمراجعة والنشر", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            } else if (selectedType == ContentCreationType.ANNOUNCEMENT) {
                // Announcement Form
                item {
                    Text(
                        text = "تفاصيل الإعلان المبوب",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                item {
                    OutlinedTextField(
                        value = adTitle,
                        onValueChange = { adTitle = it },
                        label = { Text("عنوان الإعلان") },
                        placeholder = { Text("مثال: مطلوب فني كهرباء ذو خبرة") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        value = adPhone,
                        onValueChange = { adPhone = it },
                        label = { Text("رقم الهاتف أو الواتساب للتواصل") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    Text("تصنيف الإعلان:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        adCategories.take(3).forEach { cat ->
                            FilterChip(
                                selected = adCategory == cat,
                                onClick = { adCategory = cat },
                                label = { Text(cat, fontSize = 11.sp) }
                            )
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        adCategories.drop(3).forEach { cat ->
                            FilterChip(
                                selected = adCategory == cat,
                                onClick = { adCategory = cat },
                                label = { Text(cat, fontSize = 11.sp) }
                            )
                        }
                    }
                }

                item {
                    OutlinedTextField(
                        value = adContent,
                        onValueChange = { adContent = it },
                        label = { Text("نص الإعلان وشروطه") },
                        placeholder = { Text("اكتب تفاصيل إعلانك والمواصفات هنا...") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 4
                    )
                }

                item {
                    Button(
                        onClick = {
                            if (adTitle.isBlank() || adContent.isBlank() || adPhone.isBlank()) {
                                Toast.makeText(context, "يرجى ملء جميع الحقول المطلوبة", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            viewModel.submitAnnouncement(
                                title = adTitle.trim(),
                                content = adContent.trim(),
                                category = adCategory,
                                phone = adPhone.trim()
                            )
                            Toast.makeText(
                                context,
                                "تم إرسال إعلانك للإدارة وسيصلك إشعار فور اعتماده",
                                Toast.LENGTH_LONG
                            ).show()
                            onNavigateBack()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("submit_ad_btn")
                    ) {
                        Icon(imageVector = Icons.Default.Send, contentDescription = null, tint = Color.Black)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("إرسال الإعلان للمراجعة والاعتماد", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
