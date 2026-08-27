package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.components.RejectionReasonDialog
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.JarablusViewModel

enum class AdminSubPage {
    OVERVIEW,
    MODERATE_NEWS,
    MODERATE_ANNOUNCEMENTS,
    MODERATE_PROPERTIES,
    MODERATE_SUBSCRIPTIONS,
    MANAGE_USERS,
    MODERATE_COMMENTS,
    SHAMCASH_SETTINGS
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    viewModel: JarablusViewModel,
    isDark: Boolean,
    modifier: Modifier = Modifier
) {
    var currentPage by remember { mutableStateOf(AdminSubPage.OVERVIEW) }
    val context = LocalContext.current

    // Handle system back button to return to overview if in sub-page
    BackHandler(enabled = currentPage != AdminSubPage.OVERVIEW) {
        currentPage = AdminSubPage.OVERVIEW
    }

    val pendingNews by viewModel.pendingNews.collectAsState()
    val pendingAds by viewModel.pendingAnnouncements.collectAsState()
    val pendingProps by viewModel.pendingProperties.collectAsState()
    val pendingSubs by viewModel.pendingSubscriptions.collectAsState()
    val pendingComments by viewModel.pendingComments.collectAsState()
    val allUsers by viewModel.allUsers.collectAsState()
    val shamCashId by viewModel.shamCashCode.collectAsState()

    val totalPending = pendingNews.size + pendingAds.size + pendingProps.size + pendingSubs.size + pendingComments.size

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = if (isDark) BgDark else BgLight,
        topBar = {
            if (currentPage != AdminSubPage.OVERVIEW) {
                TopAppBar(
                    title = {
                        Text(
                            text = when (currentPage) {
                                AdminSubPage.MODERATE_NEWS -> "مراجعة وتدقيق الأخبار"
                                AdminSubPage.MODERATE_ANNOUNCEMENTS -> "مراجعة الإعلانات المبوبة"
                                AdminSubPage.MODERATE_PROPERTIES -> "مراجعة العقارات وإشعارات الدفع"
                                AdminSubPage.MODERATE_SUBSCRIPTIONS -> "اشتراكات التجار وشام كاش"
                                AdminSubPage.MANAGE_USERS -> "إدارة المستخدمين والحسابات"
                                AdminSubPage.MODERATE_COMMENTS -> "مراجعة التعليقات"
                                AdminSubPage.SHAMCASH_SETTINGS -> "إعدادات كود شام كاش"
                                else -> "لوحة التحكم"
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = { currentPage = AdminSubPage.OVERVIEW },
                            modifier = Modifier.testTag("admin_back_to_overview")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "العودة للرئيسية"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = if (isDark) SurfaceDark else SurfaceLight,
                        titleContentColor = if (isDark) TextPrimaryDark else TextPrimaryLight
                    )
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = currentPage,
                transitionSpec = {
                    if (targetState == AdminSubPage.OVERVIEW) {
                        slideInHorizontally { -it } + fadeIn() togetherWith slideOutHorizontally { it } + fadeOut()
                    } else {
                        slideInHorizontally { it } + fadeIn() togetherWith slideOutHorizontally { -it } + fadeOut()
                    }
                },
                label = "admin_page_transition"
            ) { targetPage ->
                when (targetPage) {
                    AdminSubPage.OVERVIEW -> AdminOverviewPage(
                        pendingNewsCount = pendingNews.size,
                        pendingAdsCount = pendingAds.size,
                        pendingPropsCount = pendingProps.size,
                        pendingSubsCount = pendingSubs.size,
                        pendingCommentsCount = pendingComments.size,
                        totalUsersCount = allUsers.size,
                        totalPending = totalPending,
                        shamCashCode = shamCashId,
                        isDark = isDark,
                        onNavigate = { currentPage = it }
                    )
                    AdminSubPage.MODERATE_NEWS -> AdminNewsPage(
                        viewModel = viewModel,
                        isDark = isDark
                    )
                    AdminSubPage.MODERATE_ANNOUNCEMENTS -> AdminAnnouncementsPage(
                        viewModel = viewModel,
                        isDark = isDark
                    )
                    AdminSubPage.MODERATE_PROPERTIES -> AdminPropertiesPage(
                        viewModel = viewModel,
                        isDark = isDark
                    )
                    AdminSubPage.MODERATE_SUBSCRIPTIONS -> AdminSubscriptionsPage(
                        viewModel = viewModel,
                        isDark = isDark
                    )
                    AdminSubPage.MANAGE_USERS -> AdminUsersPage(
                        viewModel = viewModel,
                        isDark = isDark
                    )
                    AdminSubPage.MODERATE_COMMENTS -> AdminCommentsPage(
                        viewModel = viewModel,
                        isDark = isDark
                    )
                    AdminSubPage.SHAMCASH_SETTINGS -> AdminShamCashPage(
                        currentCode = shamCashId,
                        onSave = { newCode ->
                            viewModel.updateShamCashCode(newCode)
                            Toast.makeText(context, "تم تحديث كود شام كاش بنجاح!", Toast.LENGTH_SHORT).show()
                        },
                        isDark = isDark
                    )
                }
            }
        }
    }
}

@Composable
fun AdminOverviewPage(
    pendingNewsCount: Int,
    pendingAdsCount: Int,
    pendingPropsCount: Int,
    pendingSubsCount: Int,
    pendingCommentsCount: Int,
    totalUsersCount: Int,
    totalPending: Int,
    shamCashCode: String,
    isDark: Boolean,
    onNavigate: (AdminSubPage) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Welcome & Executive Moderation Banner
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFFDC2626), Color(0xFF991B1B), Color(0xFF7F1D1D))
                        )
                    )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = Color.White.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = if (totalPending > 0) "$totalPending معاملات بانتظار المراجعة" else "جميع المعاملات مدققة ✓",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.AdminPanelSettings,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Text(
                        text = "مركز إدارة جرابلس اليوم",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "لوحة تحكم منفصلة وسلسة لمراجعة المحتوى، تدقيق إيصالات شام كاش، وتفعيل الاشتراكات والعقارات.",
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )

                    // Quick ShamCash preview widget
                    Surface(
                        color = Color.Black.copy(alpha = 0.25f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AccountBalanceWallet,
                                    contentDescription = null,
                                    tint = GoldLight,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "حساب شام كاش النشط:",
                                    color = Color.White,
                                    fontSize = 12.sp
                                )
                            }
                            Text(
                                text = shamCashCode.take(12) + "...",
                                color = GoldLight,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }

        // Section Title
        item {
            Text(
                text = "الأقسام الإدارية المستقلة",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDark) TextPrimaryDark else TextPrimaryLight
            )
        }

        // 1. Moderate Properties & Payments Card
        item {
            AdminSectionCard(
                title = "مراجعة العقارات وإشعارات الدفع (50 ل.ت)",
                subtitle = "تدقيق إيصالات الدفع لشام كاش وتفعيل العقار لمدة 3 أيام أوتوماتيكياً",
                icon = Icons.Default.RealEstateAgent,
                iconTint = Color(0xFFEA580C),
                pendingCount = pendingPropsCount,
                badgeColor = Color(0xFFEA580C),
                isDark = isDark,
                onClick = { onNavigate(AdminSubPage.MODERATE_PROPERTIES) }
            )
        }

        // 2. Moderate Merchant Subscriptions Card
        item {
            AdminSectionCard(
                title = "اشتراكات التجار وباقات شام كاش",
                subtitle = "مراجعة طلبات الترقية (10$ - 20$ - 35$) وإيصالات الدفع",
                icon = Icons.Default.MonetizationOn,
                iconTint = Color(0xFF2563EB),
                pendingCount = pendingSubsCount,
                badgeColor = Color(0xFF2563EB),
                isDark = isDark,
                onClick = { onNavigate(AdminSubPage.MODERATE_SUBSCRIPTIONS) }
            )
        }

        // 3. Moderate Classified Announcements Card
        item {
            AdminSectionCard(
                title = "مراجعة الإعلانات المبوبة",
                subtitle = "الموافقة على إعلانات المستخدمين أو رفضها مع سبب الرفض",
                icon = Icons.Default.Campaign,
                iconTint = Color(0xFF0D9488),
                pendingCount = pendingAdsCount,
                badgeColor = Color(0xFF0D9488),
                isDark = isDark,
                onClick = { onNavigate(AdminSubPage.MODERATE_ANNOUNCEMENTS) }
            )
        }

        // 4. Moderate City News Card
        item {
            AdminSectionCard(
                title = "مراجعة وتدقيق أخبار المدينة",
                subtitle = "تدقيق الأخبار المرفوعة من المراسلين والمستخدمين",
                icon = Icons.Default.Newspaper,
                iconTint = Color(0xFFDC2626),
                pendingCount = pendingNewsCount,
                badgeColor = Color(0xFFDC2626),
                isDark = isDark,
                onClick = { onNavigate(AdminSubPage.MODERATE_NEWS) }
            )
        }

        // 5. Users Management Card
        item {
            AdminSectionCard(
                title = "إدارة المستخدمين والتجار ومقدمي الخدمات",
                subtitle = "عرض الحسابات المسجلة ($totalUsersCount حساب)، تعديل الأدوار، ومتابعة النشاط",
                icon = Icons.Default.People,
                iconTint = PurpleSecondary,
                pendingCount = 0,
                customBadge = "$totalUsersCount حساب",
                badgeColor = PurpleSecondary,
                isDark = isDark,
                onClick = { onNavigate(AdminSubPage.MANAGE_USERS) }
            )
        }

        // 6. Comments Moderation Card
        item {
            AdminSectionCard(
                title = "مراجعة التعليقات والتفاعل",
                subtitle = "فلترة التعليقات الجديدة وحظر المحتوى المخالف",
                icon = Icons.Default.Forum,
                iconTint = Color(0xFF7C3AED),
                pendingCount = pendingCommentsCount,
                badgeColor = Color(0xFF7C3AED),
                isDark = isDark,
                onClick = { onNavigate(AdminSubPage.MODERATE_COMMENTS) }
            )
        }

        // 7. ShamCash Code Settings Card
        item {
            AdminSectionCard(
                title = "تعديل كود حساب شام كاش (ShamCash)",
                subtitle = "تحديث كود التحويل الخاص بالإدارة لاستقبال إشعارات الدفع فورياً",
                icon = Icons.Default.SettingsSuggest,
                iconTint = GoldDark,
                pendingCount = 0,
                customBadge = "إعدادات الدفع",
                badgeColor = GoldDark,
                isDark = isDark,
                onClick = { onNavigate(AdminSubPage.SHAMCASH_SETTINGS) }
            )
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
fun AdminSectionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconTint: Color,
    pendingCount: Int,
    customBadge: String? = null,
    badgeColor: Color,
    isDark: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDark) SurfaceDark else SurfaceLight
        ),
        border = BorderStroke(
            1.dp,
            if (pendingCount > 0) badgeColor.copy(alpha = 0.5f) else (if (isDark) BorderDark else BorderLight)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("admin_card_${title.take(6)}")
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconTint.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(28.dp)
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) TextPrimaryDark else TextPrimaryLight
                    )

                    if (pendingCount > 0) {
                        Surface(
                            shape = CircleShape,
                            color = badgeColor
                        ) {
                            Text(
                                text = "$pendingCount",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    } else if (customBadge != null) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = badgeColor.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = customBadge,
                                color = badgeColor,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = if (isDark) TextSecondaryDark else TextSecondaryLight,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 16.sp
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = null,
                tint = if (isDark) TextSecondaryDark else TextSecondaryLight,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

// ----------------------------------------------------
// DEDICATED SUB-PAGE 1: PROPERTIES MODERATION
// ----------------------------------------------------
@Composable
fun AdminPropertiesPage(
    viewModel: JarablusViewModel,
    isDark: Boolean
) {
    val pendingProps by viewModel.pendingProperties.collectAsState()
    val approvedProps by viewModel.approvedProperties.collectAsState()
    var selectedTab by remember { mutableStateOf(0) } // 0: بانتظار المراجعة, 1: المعروضة حالياً
    var viewingReceiptUrl by remember { mutableStateOf<String?>(null) }
    var rejectPropertyId by remember { mutableStateOf<Long?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Tab row
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = if (isDark) SurfaceDark else SurfaceLight,
            contentColor = Color(0xFFEA580C)
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("بانتظار المراجعة (${pendingProps.size})", fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("العقارات النشطة (${approvedProps.size})", fontWeight = FontWeight.Bold) }
            )
        }

        val displayedList = if (selectedTab == 0) pendingProps else approvedProps

        if (displayedList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF10B981),
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (selectedTab == 0) "لا توجد عقارات جديدة بانتظار الموافقة ✓" else "لا توجد عقارات معروضة حالياً",
                        color = if (isDark) TextSecondaryDark else TextSecondaryLight,
                        fontSize = 14.sp
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(displayedList, key = { it.id }) { prop ->
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = if (isDark) SurfaceDark else SurfaceLight),
                        border = BorderStroke(1.dp, if (prop.status == ModerationStatus.PENDING) Color(0xFFEA580C).copy(alpha = 0.4f) else (if (isDark) BorderDark else BorderLight)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = prop.title,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = if (isDark) TextPrimaryDark else TextPrimaryLight,
                                    modifier = Modifier.weight(1f)
                                )
                                StatusBadge(status = prop.status)
                            }

                            Text(
                                text = "${prop.type.arabicName} • ${prop.district} • ${prop.area} م² • ${prop.rooms} غرف",
                                fontSize = 13.sp,
                                color = if (isDark) TextSecondaryDark else TextSecondaryLight
                            )

                            Text(
                                text = prop.description,
                                fontSize = 13.sp,
                                color = if (isDark) TextPrimaryDark else TextPrimaryLight,
                                maxLines = 3,
                                overflow = TextOverflow.Ellipsis
                            )

                            // Price & Payment Info
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "السعر: ${prop.price} ${prop.currency}",
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFEA580C),
                                    fontSize = 14.sp
                                )

                                Surface(
                                    color = Color(0xFF10B981).copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "رسوم النشر: 50 ليرة تركي (3 أيام)",
                                        color = Color(0xFF10B981),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            // Publisher info & Receipt action
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "الناشر: ${prop.authorName} (${prop.phone})",
                                    fontSize = 12.sp,
                                    color = if (isDark) TextSecondaryDark else TextSecondaryLight
                                )

                                if (prop.paymentReceiptUrl.isNotBlank()) {
                                    OutlinedButton(
                                        onClick = { viewingReceiptUrl = prop.paymentReceiptUrl },
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ReceiptLong,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("معاينة إشعار الدفع", fontSize = 11.sp)
                                    }
                                }
                            }

                            // Action buttons for moderation
                            if (prop.status == ModerationStatus.PENDING) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = { viewModel.approveProperty(prop.id) },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("موافقة وتفعيل 3 أيام", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }

                                    OutlinedButton(
                                        onClick = { rejectPropertyId = prop.id },
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFDC2626)),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(imageVector = Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("رفض الإعلان", fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Receipt Preview Dialog
    if (viewingReceiptUrl != null) {
        AlertDialog(
            onDismissRequest = { viewingReceiptUrl = null },
            confirmButton = {
                TextButton(onClick = { viewingReceiptUrl = null }) {
                    Text("إغلاق")
                }
            },
            title = { Text("إشعار دفع شام كاش (50 ل.ت)") },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isDark) Color(0xFF1E293B) else Color(0xFFF1F5F9),
                        border = BorderStroke(1.dp, GoldDark.copy(alpha = 0.5f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.Receipt,
                                    contentDescription = null,
                                    tint = GoldDark,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text("إشعار تحويل عبر تطبيق ShamCash", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text("المبلغ: 50.00 TRY", color = Color(0xFF10B981), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("المعرف: ${viewingReceiptUrl}", fontSize = 10.sp, color = Color.Gray)
                            }
                        }
                    }
                    Text("ملاحظة: تأكد من وصول الحوالة لحساب شام كاش قبل الموافقة على التفعيل لمدة 3 أيام.", fontSize = 11.sp, color = Color.Gray)
                }
            }
        )
    }

    // Reject Dialog
    if (rejectPropertyId != null) {
        RejectionReasonDialog(
            title = "رفض إعلان العقار",
            onDismiss = { rejectPropertyId = null },
            onConfirm = { reason ->
                rejectPropertyId?.let { viewModel.rejectProperty(it, reason) }
                rejectPropertyId = null
            }
        )
    }
}

// ----------------------------------------------------
// DEDICATED SUB-PAGE 2: MERCHANT SUBSCRIPTIONS
// ----------------------------------------------------
@Composable
fun AdminSubscriptionsPage(
    viewModel: JarablusViewModel,
    isDark: Boolean
) {
    val pendingSubs by viewModel.pendingSubscriptions.collectAsState()
    val allSubs by viewModel.allSubscriptions.collectAsState()
    var viewingReceiptUrl by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF2563EB).copy(alpha = 0.12f))
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(imageVector = Icons.Default.Info, contentDescription = null, tint = Color(0xFF2563EB))
                Text(
                    text = "خطط اشتراك التجار: عادية (10$ - 30 منتج)، متوسطة (20$ - 80 منتج)، ذهبية (35$ - عدد مفتوح). التحويل عبر شام كاش.",
                    fontSize = 12.sp,
                    color = Color(0xFF2563EB),
                    lineHeight = 16.sp
                )
            }
        }

        if (allSubs.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "لا توجد طلبات اشتراك في الوقت الحالي",
                    color = if (isDark) TextSecondaryDark else TextSecondaryLight
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(allSubs, key = { it.id }) { sub ->
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = if (isDark) SurfaceDark else SurfaceLight),
                        border = BorderStroke(1.dp, if (sub.status == ModerationStatus.PENDING) Color(0xFF2563EB).copy(alpha = 0.5f) else (if (isDark) BorderDark else BorderLight)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = sub.planName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = if (isDark) TextPrimaryDark else TextPrimaryLight
                                )
                                StatusBadge(status = sub.status)
                            }

                            Text(
                                text = "المتجر: ${sub.storeName.ifBlank { sub.userName }} • سعة المنتجات: ${if (sub.productsLimit > 1000) "مفتوحة" else "${sub.productsLimit} منتج"}",
                                fontSize = 13.sp,
                                color = if (isDark) TextSecondaryDark else TextSecondaryLight
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "قيمة الاشتراك: ${sub.amount} ${sub.currency}",
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2563EB),
                                    fontSize = 14.sp
                                )

                                if (sub.receiptUrl.isNotBlank()) {
                                    OutlinedButton(
                                        onClick = { viewingReceiptUrl = sub.receiptUrl },
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.Receipt, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("معاينة إشعار الدفع", fontSize = 11.sp)
                                    }
                                }
                            }

                            if (sub.status == ModerationStatus.PENDING) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = { viewModel.approveSubscription(sub.id) },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("تفعيل الباقة فورياً", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }

                                    OutlinedButton(
                                        onClick = { viewModel.rejectSubscription(sub.id) },
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFDC2626)),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(imageVector = Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("رفض الطلب", fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (viewingReceiptUrl != null) {
        AlertDialog(
            onDismissRequest = { viewingReceiptUrl = null },
            confirmButton = {
                TextButton(onClick = { viewingReceiptUrl = null }) { Text("إغلاق") }
            },
            title = { Text("إشعار دفع اشتراك تاجر") },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isDark) Color(0xFF1E293B) else Color(0xFFF1F5F9),
                        border = BorderStroke(1.dp, Color(0xFF2563EB).copy(alpha = 0.5f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(imageVector = Icons.Default.ReceiptLong, contentDescription = null, tint = Color(0xFF2563EB), modifier = Modifier.size(44.dp))
                                Spacer(modifier = Modifier.height(6.dp))
                                Text("إشعار تحويل شام كاش", fontWeight = FontWeight.Bold)
                                Text("المعرف: ${viewingReceiptUrl}", fontSize = 10.sp, color = Color.Gray)
                            }
                        }
                    }
                }
            }
        )
    }
}

// ----------------------------------------------------
// DEDICATED SUB-PAGE 3: SHAMCASH SETTINGS
// ----------------------------------------------------
@Composable
fun AdminShamCashPage(
    currentCode: String,
    onSave: (String) -> Unit,
    isDark: Boolean
) {
    var codeInput by remember { mutableStateOf(currentCode) }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = if (isDark) SurfaceDark else SurfaceLight),
            border = BorderStroke(1.dp, GoldDark.copy(alpha = 0.4f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(GoldDark.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.AccountBalanceWallet, contentDescription = null, tint = GoldDark)
                    }

                    Column {
                        Text(
                            text = "حساب شام كاش (ShamCash) المعتمد",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) TextPrimaryDark else TextPrimaryLight
                        )
                        Text(
                            text = "هذا الحساب يظهر للمستخدمين عند دفع رسوم العقارات أو الاشتراكات",
                            fontSize = 12.sp,
                            color = if (isDark) TextSecondaryDark else TextSecondaryLight
                        )
                    }
                }

                OutlinedTextField(
                    value = codeInput,
                    onValueChange = { codeInput = it },
                    label = { Text("معرف محفظة شام كاش (ShamCash ID)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("ShamCash ID", codeInput))
                            Toast.makeText(context, "تم نسخ معرف شام كاش للحافظة", Toast.LENGTH_SHORT).show()
                        },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("نسخ المعرف", fontSize = 13.sp)
                    }

                    Button(
                        onClick = { onSave(codeInput) },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GoldDark),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(imageVector = Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("حفظ التغييرات", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Informational card
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = if (isDark) Color(0xFF1E293B) else Color(0xFFF8FAFC))
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(text = "معلومات هامة للإدارة:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text(
                    text = "• عند تغيير هذا المعرف، سينعكس فورياً في جميع صفحات الدفع (العقارات واشتراكات التجار).\n• المستخدم ينسخ هذا المعرف ويحول من تطبيق شام كاش، ثم يرفع صورة الإشعار للتحقق.\n• يمكنك التعديل في أي وقت بحسب الحسابات النشطة لدى الإدارة.",
                    fontSize = 12.sp,
                    color = if (isDark) TextSecondaryDark else TextSecondaryLight,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

// ----------------------------------------------------
// DEDICATED SUB-PAGE 4: ANNOUNCEMENTS MODERATION
// ----------------------------------------------------
@Composable
fun AdminAnnouncementsPage(
    viewModel: JarablusViewModel,
    isDark: Boolean
) {
    val pendingAds by viewModel.pendingAnnouncements.collectAsState()
    var rejectId by remember { mutableStateOf<Long?>(null) }

    if (pendingAds.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(48.dp))
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "لا توجد إعلانات مبوبة جديدة للمراجعة ✓", color = if (isDark) TextSecondaryDark else TextSecondaryLight)
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(pendingAds, key = { it.id }) { ad ->
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = if (isDark) SurfaceDark else SurfaceLight),
                    border = BorderStroke(1.dp, Color(0xFF0D9488).copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = ad.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            StatusBadge(status = ad.status)
                        }

                        Text(text = "القسم: ${ad.category} • المعلن: ${ad.authorName} (${ad.phone})", fontSize = 12.sp, color = Color.Gray)
                        Text(text = ad.content, fontSize = 13.sp)

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = { viewModel.approveAnnouncement(ad.id) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("موافقة ونشر", fontSize = 12.sp)
                            }
                            OutlinedButton(
                                onClick = { rejectId = ad.id },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFDC2626)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("رفض", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }

    if (rejectId != null) {
        RejectionReasonDialog(
            title = "سبب رفض الإعلان",
            onDismiss = { rejectId = null },
            onConfirm = { reason ->
                rejectId?.let { viewModel.rejectAnnouncement(it, reason) }
                rejectId = null
            }
        )
    }
}

// ----------------------------------------------------
// DEDICATED SUB-PAGE 5: NEWS MODERATION
// ----------------------------------------------------
@Composable
fun AdminNewsPage(
    viewModel: JarablusViewModel,
    isDark: Boolean
) {
    val pendingNews by viewModel.pendingNews.collectAsState()
    var rejectId by remember { mutableStateOf<Long?>(null) }

    if (pendingNews.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(48.dp))
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "لا توجد أخبار جديدة للمراجعة ✓", color = if (isDark) TextSecondaryDark else TextSecondaryLight)
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(pendingNews, key = { it.id }) { news ->
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = if (isDark) SurfaceDark else SurfaceLight),
                    border = BorderStroke(1.dp, Color(0xFFDC2626).copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = news.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            StatusBadge(status = news.status)
                        }

                        Text(text = "التصنيف: ${news.category} • الكاتب: ${news.authorName}", fontSize = 12.sp, color = Color.Gray)
                        Text(text = news.content, fontSize = 13.sp, maxLines = 4, overflow = TextOverflow.Ellipsis)

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = { viewModel.approveNews(news.id) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("اعتماد ونشر", fontSize = 12.sp)
                            }
                            OutlinedButton(
                                onClick = { rejectId = news.id },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFDC2626)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("رفض الخبر", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }

    if (rejectId != null) {
        RejectionReasonDialog(
            title = "سبب رفض الخبر",
            onDismiss = { rejectId = null },
            onConfirm = { reason ->
                rejectId?.let { viewModel.rejectNews(it, reason) }
                rejectId = null
            }
        )
    }
}

// ----------------------------------------------------
// DEDICATED SUB-PAGE 6: USERS MANAGEMENT
// ----------------------------------------------------
@Composable
fun AdminUsersPage(
    viewModel: JarablusViewModel,
    isDark: Boolean
) {
    val users by viewModel.allUsers.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(users, key = { it.id }) { user ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = if (isDark) SurfaceDark else SurfaceLight),
                border = BorderStroke(1.dp, if (isDark) BorderDark else BorderLight),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(
                                when (user.role) {
                                    UserRole.ADMIN -> Color(0xFFDC2626).copy(alpha = 0.15f)
                                    UserRole.MERCHANT -> GoldDark.copy(alpha = 0.15f)
                                    UserRole.SERVICE_PROVIDER -> PurpleSecondary.copy(alpha = 0.15f)
                                    UserRole.USER -> Color(0xFF0284C7).copy(alpha = 0.15f)
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (user.role) {
                                UserRole.ADMIN -> Icons.Default.AdminPanelSettings
                                UserRole.MERCHANT -> Icons.Default.Store
                                UserRole.SERVICE_PROVIDER -> Icons.Default.Handyman
                                UserRole.USER -> Icons.Default.Person
                            },
                            contentDescription = null,
                            tint = when (user.role) {
                                UserRole.ADMIN -> Color(0xFFDC2626)
                                UserRole.MERCHANT -> GoldDark
                                UserRole.SERVICE_PROVIDER -> PurpleSecondary
                                UserRole.USER -> Color(0xFF0284C7)
                            },
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = user.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(text = "${user.role.arabicName} • ${user.phone} • ${user.city}", fontSize = 12.sp, color = Color.Gray)
                        if (user.storeName.isNotBlank()) {
                            Text(text = "المتجر: ${user.storeName}", fontSize = 11.sp, color = GoldDark)
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (user.isActive) Color(0xFF10B981).copy(alpha = 0.15f) else Color.Gray.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = if (user.isActive) "نشط" else "معطل",
                            color = if (user.isActive) Color(0xFF10B981) else Color.Gray,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------
// DEDICATED SUB-PAGE 7: COMMENTS MODERATION
// ----------------------------------------------------
@Composable
fun AdminCommentsPage(
    viewModel: JarablusViewModel,
    isDark: Boolean
) {
    val comments by viewModel.pendingComments.collectAsState()

    if (comments.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(48.dp))
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "لا توجد تعليقات جديدة معلقة ✓", color = if (isDark) TextSecondaryDark else TextSecondaryLight)
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(comments, key = { it.id }) { comment ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = if (isDark) SurfaceDark else SurfaceLight),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = comment.userName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(text = comment.contentType, fontSize = 11.sp, color = Color.Gray)
                        }
                        Text(text = "على: ${comment.contentTitle}", fontSize = 12.sp, color = Color.Gray)
                        Text(text = comment.commentText, fontSize = 13.sp)

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = { viewModel.approveComment(comment.id) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("نشر", fontSize = 11.sp)
                            }
                            OutlinedButton(
                                onClick = { viewModel.rejectComment(comment) },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFDC2626)),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("حذف", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
