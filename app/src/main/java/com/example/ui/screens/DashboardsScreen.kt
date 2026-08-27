package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.components.RejectionReasonDialog
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.JarablusViewModel

@Composable
fun DashboardsScreen(
    viewModel: JarablusViewModel,
    dashboardType: AppScreen,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val isDark by viewModel.isDarkMode.collectAsState()
    var isSidebarOpen by remember { mutableStateOf(false) }
    var selectedSection by remember { mutableStateOf(0) }

    // Intercept hardware/system back button to collapse sidebar on mobile
    BackHandler(enabled = isSidebarOpen) {
        isSidebarOpen = false
    }

    val pendingNews by viewModel.pendingNews.collectAsState()
    val pendingAds by viewModel.pendingAnnouncements.collectAsState()
    val pendingProps by viewModel.pendingProperties.collectAsState()
    val pendingSubs by viewModel.pendingSubscriptions.collectAsState()
    val activeDiscounts by viewModel.activeDiscounts.collectAsState()

    val pendingBadgeCount = when (dashboardType) {
        AppScreen.ADMIN_DASHBOARD -> pendingNews.size + pendingAds.size + pendingProps.size + pendingSubs.size
        AppScreen.MERCHANT_DASHBOARD -> activeDiscounts.size
        else -> 0
    }

    val accentColor = when (dashboardType) {
        AppScreen.ADMIN_DASHBOARD -> Color(0xFFDC2626)
        AppScreen.MERCHANT_DASHBOARD -> GoldDark
        AppScreen.SERVICE_DASHBOARD -> PurpleSecondary
        else -> Color(0xFF0284C7)
    }

    // Animated Backdrop Blur for Underlying Dashboard Content
    val blurRadius by animateDpAsState(
        targetValue = if (isSidebarOpen) 16.dp else 0.dp,
        animationSpec = tween(durationMillis = 260, easing = FastOutSlowInEasing),
        label = "dashboard_backdrop_blur"
    )

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(if (isDark) BgDark else BgLight)
    ) {
        val isSmallScreen = maxWidth < 600.dp
        val responsiveSidebarWidth = if (isSmallScreen) {
            (maxWidth * 0.82f).coerceIn(270.dp, 320.dp)
        } else {
            320.dp
        }

        // Main Dashboard Content with Responsive Backdrop Blur
        Column(
            modifier = Modifier
                .fillMaxSize()
                .blur(blurRadius)
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            // Sleek Dashboard Header with Sidebar Toggle & Notification Badge
            DashboardTopNav(
                dashboardType = dashboardType,
                user = currentUser,
                viewModel = viewModel,
                isDark = isDark,
                onToggleSidebar = { isSidebarOpen = !isSidebarOpen }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Main View based on role and selected section
            Box(modifier = Modifier.weight(1f)) {
                when (dashboardType) {
                    AppScreen.ADMIN_DASHBOARD -> AdminDashboardView(
                        viewModel = viewModel,
                        selectedSection = selectedSection,
                        onSelectSection = { selectedSection = it },
                        isDark = isDark
                    )
                    AppScreen.MERCHANT_DASHBOARD -> MerchantDashboardView(
                        viewModel = viewModel,
                        user = currentUser,
                        selectedSection = selectedSection,
                        onSelectSection = { selectedSection = it },
                        isDark = isDark
                    )
                    AppScreen.SERVICE_DASHBOARD -> ServiceProviderDashboardView(
                        viewModel = viewModel,
                        user = currentUser,
                        selectedSection = selectedSection,
                        onSelectSection = { selectedSection = it },
                        isDark = isDark
                    )
                    else -> UserDashboardView(
                        viewModel = viewModel,
                        user = currentUser,
                        selectedSection = selectedSection,
                        onSelectSection = { selectedSection = it },
                        isDark = isDark
                    )
                }
            }
        }

        // Floating Quick-Access Menu Button for phone/small screens
        // Ensures the menu button is ALWAYS easily accessible at thumb reach when scrolling!
        AnimatedVisibility(
            visible = !isSidebarOpen,
            enter = fadeIn(animationSpec = tween(200)) + scaleIn(initialScale = 0.8f),
            exit = fadeOut(animationSpec = tween(150)) + scaleOut(targetScale = 0.8f),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Surface(
                onClick = { isSidebarOpen = true },
                shape = RoundedCornerShape(22.dp),
                color = accentColor,
                shadowElevation = 8.dp,
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.35f)),
                modifier = Modifier.testTag("floating_quick_menu_toggle")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    BadgedBox(
                        badge = {
                            if (pendingBadgeCount > 0) {
                                Badge(
                                    containerColor = Color.White,
                                    contentColor = Color(0xFFDC2626)
                                ) {
                                    Text(
                                        text = if (pendingBadgeCount > 99) "+" else "$pendingBadgeCount",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 9.sp
                                    )
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.MenuOpen,
                            contentDescription = "فتح القائمة الجانبية",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Text(
                        text = "القائمة",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Translucent Glassmorphic Collapsible Sidebar Drawer with Responsive Width
        DashboardTranslucentSidebar(
            isOpen = isSidebarOpen,
            onClose = { isSidebarOpen = false },
            drawerWidth = responsiveSidebarWidth,
            dashboardType = dashboardType,
            currentUser = currentUser,
            selectedSection = selectedSection,
            onSelectSection = { section ->
                selectedSection = section
                isSidebarOpen = false
            },
            onNavigate = { screen ->
                isSidebarOpen = false
                viewModel.navigateTo(screen)
            },
            onSwitchRole = { newRole ->
                viewModel.switchUser(currentUser.copy(role = newRole))
                isSidebarOpen = false
            },
            viewModel = viewModel,
            isDark = isDark
        )
    }
}

// -------------------------------------------------------------
// TOP BAR / HEADER FOR DASHBOARD
// -------------------------------------------------------------
@Composable
fun DashboardTopNav(
    dashboardType: AppScreen,
    user: UserEntity,
    viewModel: JarablusViewModel,
    isDark: Boolean,
    onToggleSidebar: () -> Unit
) {
    val pendingNews by viewModel.pendingNews.collectAsState()
    val pendingAds by viewModel.pendingAnnouncements.collectAsState()
    val pendingProps by viewModel.pendingProperties.collectAsState()
    val pendingSubs by viewModel.pendingSubscriptions.collectAsState()
    val activeDiscounts by viewModel.activeDiscounts.collectAsState()

    val pendingBadgeCount = when (dashboardType) {
        AppScreen.ADMIN_DASHBOARD -> pendingNews.size + pendingAds.size + pendingProps.size + pendingSubs.size
        AppScreen.MERCHANT_DASHBOARD -> activeDiscounts.size
        else -> 0
    }

    val title = when (dashboardType) {
        AppScreen.ADMIN_DASHBOARD -> "لوحة تحكم الإدارة العليا"
        AppScreen.MERCHANT_DASHBOARD -> "لوحة تحكم التاجر والمتجر"
        AppScreen.SERVICE_DASHBOARD -> "لوحة تحكم مقدم الخدمات"
        else -> "لوحة التحكم الشخصية"
    }

    val subtitle = when (dashboardType) {
        AppScreen.ADMIN_DASHBOARD -> "إدارة المحتوى، التدقيق المالي وصلاحيات المستخدمين"
        AppScreen.MERCHANT_DASHBOARD -> "إدارة المنتجات، العروض الترويجية وتقارير المبيعات"
        AppScreen.SERVICE_DASHBOARD -> "إدارة الخدمات المعتمدة، المهام وتواصل العملاء"
        else -> "متابعة حالة منشوراتك، عقاراتك وإعلاناتك"
    }

    val accentGradient = when (dashboardType) {
        AppScreen.ADMIN_DASHBOARD -> listOf(Color(0xFFDC2626), Color(0xFF991B1B))
        AppScreen.MERCHANT_DASHBOARD -> listOf(GoldPrimary, GoldDark)
        AppScreen.SERVICE_DASHBOARD -> listOf(PurpleSecondary, PurpleDark)
        else -> listOf(Color(0xFF0284C7), Color(0xFF0369A1))
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(
                BorderStroke(
                    1.dp,
                    if (isDark) Color.White.copy(alpha = 0.12f) else Color(0xFFE2E8F0)
                ),
                RoundedCornerShape(16.dp)
            ),
        color = if (isDark) SurfaceDark.copy(alpha = 0.95f) else SurfaceLight.copy(alpha = 0.95f),
        tonalElevation = 3.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Enhanced 48dp Accessible Sidebar Menu Button with Notification Badge
                Box(contentAlignment = Alignment.TopEnd) {
                    Surface(
                        onClick = onToggleSidebar,
                        shape = RoundedCornerShape(12.dp),
                        color = if (isDark) Color.White.copy(alpha = 0.10f) else Color(0xFFF1F5F9),
                        border = BorderStroke(
                            1.dp,
                            if (isDark) Color.White.copy(alpha = 0.16f) else Color(0xFFCBD5E1)
                        ),
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("dashboard_sidebar_toggle")
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Icon(
                                imageVector = Icons.Default.MenuOpen,
                                contentDescription = "القائمة الجانبية",
                                tint = if (isDark) GoldLight else GoldDark,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    if (pendingBadgeCount > 0) {
                        Box(
                            modifier = Modifier
                                .offset(x = 4.dp, y = (-4).dp)
                                .size(18.dp)
                                .clip(CircleShape)
                                .background(if (dashboardType == AppScreen.ADMIN_DASHBOARD) Color(0xFFDC2626) else GoldPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (pendingBadgeCount > 99) "+" else "$pendingBadgeCount",
                                color = Color.White,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = if (isDark) TextPrimaryDark else TextPrimaryLight,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Brush.horizontalGradient(accentGradient))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = user.role.labelAr,
                                color = Color.White,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Text(
                        text = subtitle,
                        fontSize = 10.sp,
                        color = if (isDark) TextSecondaryDark else TextSecondaryLight,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// TRANSLUCENT GLASSMORPHIC SIDEBAR (ADMIN, MERCHANT, USER, SERVICE)
// -------------------------------------------------------------
@Composable
fun DashboardTranslucentSidebar(
    isOpen: Boolean,
    onClose: () -> Unit,
    drawerWidth: Dp = 310.dp,
    dashboardType: AppScreen,
    currentUser: UserEntity,
    selectedSection: Int,
    onSelectSection: (Int) -> Unit,
    onNavigate: (AppScreen) -> Unit,
    onSwitchRole: (UserRole) -> Unit,
    viewModel: JarablusViewModel,
    isDark: Boolean
) {
    val accentColor = when (dashboardType) {
        AppScreen.ADMIN_DASHBOARD -> Color(0xFFDC2626)
        AppScreen.MERCHANT_DASHBOARD -> GoldPrimary
        AppScreen.SERVICE_DASHBOARD -> PurpleSecondary
        else -> Color(0xFF0284C7)
    }

    AnimatedVisibility(
        visible = isOpen,
        enter = fadeIn(animationSpec = tween(220)) + slideInHorizontally(
            initialOffsetX = { it },
            animationSpec = tween(280, easing = FastOutSlowInEasing)
        ),
        exit = fadeOut(animationSpec = tween(180)) + slideOutHorizontally(
            targetOffsetX = { it },
            animationSpec = tween(240, easing = FastOutSlowInEasing)
        )
    ) {
        // Semi-transparent Backdrop Overlay with Backdrop Scrim Blur Effect
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    if (isDark) Color.Black.copy(alpha = 0.58f)
                    else Color(0xFF0F172A).copy(alpha = 0.40f)
                )
                .clickable { onClose() }
        ) {
            // Translucent Frosted Glass Sidebar Panel with Responsive Width
            Surface(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(drawerWidth)
                    .align(Alignment.CenterEnd)
                    .clickable(enabled = false) {}
                    .border(
                        BorderStroke(
                            1.dp,
                            Brush.verticalGradient(
                                listOf(
                                    Color.White.copy(alpha = if (isDark) 0.30f else 0.85f),
                                    Color.White.copy(alpha = if (isDark) 0.08f else 0.35f)
                                )
                            )
                        ),
                        RoundedCornerShape(topStart = 24.dp, bottomStart = 24.dp)
                    )
                    .shadow(
                        elevation = 18.dp,
                        shape = RoundedCornerShape(topStart = 24.dp, bottomStart = 24.dp),
                        ambientColor = accentColor.copy(alpha = 0.25f)
                    ),
                shape = RoundedCornerShape(topStart = 24.dp, bottomStart = 24.dp),
                color = if (isDark) Color(0xD90F172A) else Color(0xF2FFFFFF), // Translucent 85% Dark / 94% Light Glass
                tonalElevation = 8.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    if (isDark) Color.White.copy(alpha = 0.06f) else Color.White.copy(alpha = 0.45f),
                                    Color.Transparent
                                )
                            )
                        )
                ) {
                    when (dashboardType) {
                        AppScreen.ADMIN_DASHBOARD -> AdminSidebarContent(
                            onClose = onClose,
                            currentUser = currentUser,
                            selectedSection = selectedSection,
                            onSelectSection = onSelectSection,
                            onNavigate = onNavigate,
                            onSwitchRole = onSwitchRole,
                            viewModel = viewModel,
                            isDark = isDark
                        )
                        AppScreen.MERCHANT_DASHBOARD -> MerchantSidebarContent(
                            onClose = onClose,
                            currentUser = currentUser,
                            selectedSection = selectedSection,
                            onSelectSection = onSelectSection,
                            onNavigate = onNavigate,
                            onSwitchRole = onSwitchRole,
                            viewModel = viewModel,
                            isDark = isDark
                        )
                        AppScreen.SERVICE_DASHBOARD -> ServiceProviderSidebarContent(
                            onClose = onClose,
                            currentUser = currentUser,
                            selectedSection = selectedSection,
                            onSelectSection = onSelectSection,
                            onNavigate = onNavigate,
                            onSwitchRole = onSwitchRole,
                            viewModel = viewModel,
                            isDark = isDark
                        )
                        else -> UserSidebarContent(
                            onClose = onClose,
                            currentUser = currentUser,
                            selectedSection = selectedSection,
                            onSelectSection = onSelectSection,
                            onNavigate = onNavigate,
                            onSwitchRole = onSwitchRole,
                            viewModel = viewModel,
                            isDark = isDark
                        )
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 1. ADMIN SIDEBAR CONTENT
// -------------------------------------------------------------
@Composable
private fun AdminSidebarContent(
    onClose: () -> Unit,
    currentUser: UserEntity,
    selectedSection: Int,
    onSelectSection: (Int) -> Unit,
    onNavigate: (AppScreen) -> Unit,
    onSwitchRole: (UserRole) -> Unit,
    viewModel: JarablusViewModel,
    isDark: Boolean
) {
    val pendingNews by viewModel.pendingNews.collectAsState()
    val pendingAds by viewModel.pendingAnnouncements.collectAsState()
    val pendingProps by viewModel.pendingProperties.collectAsState()
    val pendingSubs by viewModel.pendingSubscriptions.collectAsState()
    val allUsers by viewModel.allUsers.collectAsState()

    val totalPending = pendingNews.size + pendingAds.size + pendingProps.size + pendingSubs.size
    val crimsonAccent = Color(0xFFDC2626)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(18.dp)
    ) {
        // Admin Header with Shield & System Status
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFFDC2626), Color(0xFF991B1B))
                            )
                        )
                        .border(1.dp, Color.White.copy(alpha = 0.35f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AdminPanelSettings,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "الإدارة العليا لجرابلس",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = if (isDark) TextPrimaryDark else TextPrimaryLight
                    )
                    Text(
                        text = "المدير: ${currentUser.name}",
                        fontSize = 11.sp,
                        color = crimsonAccent,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            SidebarHeaderCollapseButton(
                onClose = onClose,
                isDark = isDark,
                modifier = Modifier.testTag("admin_sidebar_collapse_top")
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Frosted Glass Quick Metrics Summary Card
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            color = if (isDark) Color(0x33DC2626) else Color(0x1ADC2626),
            border = BorderStroke(1.dp, crimsonAccent.copy(alpha = 0.35f))
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "طابور التدقيق الفوري",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = crimsonAccent
                    )
                    Surface(
                        color = if (totalPending > 0) Color(0xFFEF4444).copy(alpha = 0.2f) else Color(0xFF10B981).copy(alpha = 0.2f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = if (totalPending > 0) "$totalPending يتطلب إجراء" else "مكتمل ✓",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (totalPending > 0) Color(0xFFEF4444) else Color(0xFF059669),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "المستخدمين: ${allUsers.size}",
                        fontSize = 10.sp,
                        color = if (isDark) TextSecondaryDark else TextSecondaryLight
                    )
                    Text(
                        text = "النظام: آمن ومحمي 100%",
                        fontSize = 10.sp,
                        color = Color(0xFF10B981),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "أقسام الإدارة والتدقيق",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = if (isDark) TextSecondaryDark else TextSecondaryLight
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Navigation Sections with Live Count Badges
        val sections = listOf(
            NavSectionItem("نظرة عامة والتحليلات", Icons.Default.Analytics, 0, null),
            NavSectionItem("الأخبار والمقالات", Icons.Default.Newspaper, 1, pendingNews.size),
            NavSectionItem("الإعلانات قيد المراجعة", Icons.Default.Campaign, 2, pendingAds.size),
            NavSectionItem("العقارات والمخططات", Icons.Default.HomeWork, 3, pendingProps.size),
            NavSectionItem("اشتراكات المتاجر", Icons.Default.Payments, 4, pendingSubs.size),
            NavSectionItem("المستخدمين والصلاحيات", Icons.Default.People, 5, allUsers.size)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(sections) { item ->
                SidebarNavRow(
                    title = item.title,
                    icon = item.icon,
                    isSelected = selectedSection == item.index,
                    badgeCount = item.badgeCount,
                    badgeColor = crimsonAccent,
                    accentColor = crimsonAccent,
                    isDark = isDark,
                    onClick = { onSelectSection(item.index) }
                )
            }

            item {
                SidebarRoleSwitcherSection(
                    currentUser = currentUser,
                    onSwitchRole = onSwitchRole,
                    isDark = isDark
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        SidebarFooterActions(onClose = onClose, onNavigate = onNavigate, isDark = isDark)
    }
}

// -------------------------------------------------------------
// 2. MERCHANT SIDEBAR CONTENT
// -------------------------------------------------------------
@Composable
private fun MerchantSidebarContent(
    onClose: () -> Unit,
    currentUser: UserEntity,
    selectedSection: Int,
    onSelectSection: (Int) -> Unit,
    onNavigate: (AppScreen) -> Unit,
    onSwitchRole: (UserRole) -> Unit,
    viewModel: JarablusViewModel,
    isDark: Boolean
) {
    val myProducts by viewModel.allProducts.collectAsState()
    val myDiscounts by viewModel.activeDiscounts.collectAsState()
    val goldAccent = GoldDark

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(18.dp)
    ) {
        // Merchant Header with Store Identity
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Brush.linearGradient(listOf(GoldPrimary, GoldDark)))
                        .border(1.dp, Color.White.copy(alpha = 0.4f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Storefront,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "متجر: ${currentUser.storeName.ifBlank { currentUser.name }}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = if (isDark) TextPrimaryDark else TextPrimaryLight,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "تاجر معتمد ⭐ • ${currentUser.subscriptionTier}",
                        fontSize = 11.sp,
                        color = goldAccent,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            SidebarHeaderCollapseButton(
                onClose = onClose,
                isDark = isDark,
                modifier = Modifier.testTag("merchant_sidebar_collapse_top")
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Frosted Glass Store Capacity & Metric Card
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            color = if (isDark) Color(0x2ECA8A04) else Color(0x18CA8A04),
            border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.35f))
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "سعة كتالوج المتجر",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = goldAccent
                    )
                    Text(
                        text = "${myProducts.size} / ${currentUser.productLimit} منتج",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = goldAccent
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                val ratio = if (currentUser.productLimit > 0) (myProducts.size.toFloat() / currentUser.productLimit.toFloat()).coerceIn(0f, 1f) else 0.5f
                LinearProgressIndicator(
                    progress = { ratio },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(5.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = GoldPrimary,
                    trackColor = if (isDark) Color.White.copy(alpha = 0.1f) else Color(0xFFE2E8F0)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "العروض الفعالة: ${myDiscounts.size}",
                        fontSize = 10.sp,
                        color = if (isDark) TextSecondaryDark else TextSecondaryLight
                    )
                    Text(
                        text = "زيارات المتجر: 1,420",
                        fontSize = 10.sp,
                        color = PurpleSecondary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "أقسام إدارة المتجر",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = if (isDark) TextSecondaryDark else TextSecondaryLight
        )

        Spacer(modifier = Modifier.height(8.dp))

        val sections = listOf(
            NavSectionItem("نظرة عامة على المتجر", Icons.Default.Dashboard, 0, null),
            NavSectionItem("المنتجات والكتالوج", Icons.Default.ShoppingBag, 1, myProducts.size),
            NavSectionItem("التخفيضات والعروض", Icons.Default.LocalOffer, 2, myDiscounts.size),
            NavSectionItem("الاشتراك والترقية", Icons.Default.Star, 3, null)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(sections) { item ->
                SidebarNavRow(
                    title = item.title,
                    icon = item.icon,
                    isSelected = selectedSection == item.index,
                    badgeCount = item.badgeCount,
                    badgeColor = GoldPrimary,
                    accentColor = goldAccent,
                    isDark = isDark,
                    onClick = { onSelectSection(item.index) }
                )
            }

            // Quick Merchant Action Buttons
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Button(
                        onClick = { onNavigate(AppScreen.MARKET) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.Add, null, tint = Color.Black, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("منتج جديد", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = { onSelectSection(2) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = PurpleSecondary),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.LocalOffer, null, tint = Color.White, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("عرض تخفيض", color = Color.White, fontSize = 11.sp)
                    }
                }
            }

            item {
                SidebarRoleSwitcherSection(
                    currentUser = currentUser,
                    onSwitchRole = onSwitchRole,
                    isDark = isDark
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        SidebarFooterActions(onClose = onClose, onNavigate = onNavigate, isDark = isDark)
    }
}

// -------------------------------------------------------------
// 3. USER SIDEBAR CONTENT
// -------------------------------------------------------------
@Composable
private fun UserSidebarContent(
    onClose: () -> Unit,
    currentUser: UserEntity,
    selectedSection: Int,
    onSelectSection: (Int) -> Unit,
    onNavigate: (AppScreen) -> Unit,
    onSwitchRole: (UserRole) -> Unit,
    viewModel: JarablusViewModel,
    isDark: Boolean
) {
    val allNews by viewModel.approvedNews.collectAsState()
    val allPendingNews by viewModel.pendingNews.collectAsState()
    val allProps by viewModel.approvedProperties.collectAsState()
    val allPendingProps by viewModel.pendingProperties.collectAsState()

    val myApprovedNews = allNews.filter { it.authorId == currentUser.id }
    val myPendingNews = allPendingNews.filter { it.authorId == currentUser.id }
    val myApprovedProps = allProps.filter { it.authorId == currentUser.id }
    val myPendingProps = allPendingProps.filter { it.authorId == currentUser.id }

    val totalApproved = myApprovedNews.size + myApprovedProps.size
    val totalPending = myPendingNews.size + myPendingProps.size
    val cyanAccent = Color(0xFF0284C7)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(18.dp)
    ) {
        // User Profile Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Brush.linearGradient(listOf(Color(0xFF0284C7), Color(0xFF0369A1))))
                        .border(1.dp, Color.White.copy(alpha = 0.4f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = currentUser.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = if (isDark) TextPrimaryDark else TextPrimaryLight,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "مواطن نشط ⭐ • ${currentUser.phone}",
                        fontSize = 11.sp,
                        color = cyanAccent,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            SidebarHeaderCollapseButton(
                onClose = onClose,
                isDark = isDark,
                modifier = Modifier.testTag("user_sidebar_collapse_top")
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Frosted Glass Personal Summary Card
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            color = if (isDark) Color(0x2B0284C7) else Color(0x150284C7),
            border = BorderStroke(1.dp, cyanAccent.copy(alpha = 0.35f))
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "موقف مشاركاتي",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = cyanAccent
                    )
                    Surface(
                        color = Color(0xFF10B981).copy(alpha = 0.2f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "$totalApproved معتمد",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF047857),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "قيد المراجعة: $totalPending طلب",
                        fontSize = 10.sp,
                        color = if (totalPending > 0) GoldDark else (if (isDark) TextSecondaryDark else TextSecondaryLight),
                        fontWeight = if (totalPending > 0) FontWeight.Bold else FontWeight.Normal
                    )
                    Text(
                        text = "نقاط الولاء: 150 نقطة",
                        fontSize = 10.sp,
                        color = PurpleSecondary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "أقسام نشاطي اليومي",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = if (isDark) TextSecondaryDark else TextSecondaryLight
        )

        Spacer(modifier = Modifier.height(8.dp))

        val sections = listOf(
            NavSectionItem("نظرة عامة على نشاطي", Icons.Default.Person, 0, null),
            NavSectionItem("منشوراتي وإعلاناتي", Icons.Default.Article, 1, myApprovedNews.size + myPendingNews.size),
            NavSectionItem("عقاراتي المعروضة", Icons.Default.Apartment, 2, myApprovedProps.size + myPendingProps.size)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(sections) { item ->
                SidebarNavRow(
                    title = item.title,
                    icon = item.icon,
                    isSelected = selectedSection == item.index,
                    badgeCount = item.badgeCount,
                    badgeColor = cyanAccent,
                    accentColor = cyanAccent,
                    isDark = isDark,
                    onClick = { onSelectSection(item.index) }
                )
            }

            // Quick User Action Buttons
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Button(
                        onClick = { onNavigate(AppScreen.NEWS) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = cyanAccent),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.Add, null, tint = Color.White, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("إضافة خبر", color = Color.White, fontSize = 11.sp)
                    }
                    Button(
                        onClick = { onNavigate(AppScreen.PROPERTIES) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEA580C)),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.HomeWork, null, tint = Color.White, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("نشر عقار", color = Color.White, fontSize = 11.sp)
                    }
                }
            }

            item {
                SidebarRoleSwitcherSection(
                    currentUser = currentUser,
                    onSwitchRole = onSwitchRole,
                    isDark = isDark
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        SidebarFooterActions(onClose = onClose, onNavigate = onNavigate, isDark = isDark)
    }
}

// -------------------------------------------------------------
// 4. SERVICE PROVIDER SIDEBAR CONTENT
// -------------------------------------------------------------
@Composable
private fun ServiceProviderSidebarContent(
    onClose: () -> Unit,
    currentUser: UserEntity,
    selectedSection: Int,
    onSelectSection: (Int) -> Unit,
    onNavigate: (AppScreen) -> Unit,
    onSwitchRole: (UserRole) -> Unit,
    viewModel: JarablusViewModel,
    isDark: Boolean
) {
    val myServices by viewModel.allServices.collectAsState()
    val purpleAccent = PurpleSecondary

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(18.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Brush.linearGradient(listOf(PurpleSecondary, PurpleDark)))
                        .border(1.dp, Color.White.copy(alpha = 0.4f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Handyman,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = currentUser.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = if (isDark) TextPrimaryDark else TextPrimaryLight
                    )
                    Text(
                        text = "مهني معتمد ⭐ • تقييم 4.9",
                        fontSize = 11.sp,
                        color = purpleAccent,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            SidebarHeaderCollapseButton(
                onClose = onClose,
                isDark = isDark,
                modifier = Modifier.testTag("service_sidebar_collapse_top")
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            color = if (isDark) Color(0x2E8B5CF6) else Color(0x188B5CF6),
            border = BorderStroke(1.dp, purpleAccent.copy(alpha = 0.35f))
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("الخدمات المسجلة: ${myServices.size}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = purpleAccent)
                    Text("طلبات الحجز: 14 طلب", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF10B981))
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "أقسام الخدمات",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = if (isDark) TextSecondaryDark else TextSecondaryLight
        )

        Spacer(modifier = Modifier.height(8.dp))

        val sections = listOf(
            NavSectionItem("نظرة عامة والخدمات", Icons.Default.Handyman, 0, myServices.size),
            NavSectionItem("طلبات الحجز والاستفسار", Icons.Default.AssignmentTurnedIn, 1, 14),
            NavSectionItem("تقييمات العملاء", Icons.Default.StarRate, 2, null)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(sections) { item ->
                SidebarNavRow(
                    title = item.title,
                    icon = item.icon,
                    isSelected = selectedSection == item.index,
                    badgeCount = item.badgeCount,
                    badgeColor = purpleAccent,
                    accentColor = purpleAccent,
                    isDark = isDark,
                    onClick = { onSelectSection(item.index) }
                )
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { onNavigate(AppScreen.SERVICES) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = purpleAccent),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Add, null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("إضافة خدمة جديدة", fontSize = 11.sp)
                }
            }

            item {
                SidebarRoleSwitcherSection(
                    currentUser = currentUser,
                    onSwitchRole = onSwitchRole,
                    isDark = isDark
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        SidebarFooterActions(onClose = onClose, onNavigate = onNavigate, isDark = isDark)
    }
}

// -------------------------------------------------------------
// SIDEBAR SHARED REUSABLE COMPONENTS
// -------------------------------------------------------------
private data class NavSectionItem(
    val title: String,
    val icon: ImageVector,
    val index: Int,
    val badgeCount: Int? = null
)

@Composable
private fun SidebarNavRow(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    badgeCount: Int?,
    badgeColor: Color,
    accentColor: Color,
    isDark: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .clickable { onClick() },
        color = if (isSelected) {
            accentColor.copy(alpha = if (isDark) 0.22f else 0.12f)
        } else Color.Transparent,
        border = if (isSelected) BorderStroke(1.dp, accentColor.copy(alpha = 0.5f)) else null,
        shape = RoundedCornerShape(10.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isSelected) accentColor else (if (isDark) TextSecondaryDark else TextSecondaryLight),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = title,
                    fontSize = 12.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = if (isSelected) accentColor else (if (isDark) TextPrimaryDark else TextPrimaryLight)
                )
            }

            if (badgeCount != null && badgeCount > 0) {
                Surface(
                    color = badgeColor.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "$badgeCount",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = badgeColor,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun SidebarRoleSwitcherSection(
    currentUser: UserEntity,
    onSwitchRole: (UserRole) -> Unit,
    isDark: Boolean
) {
    Column {
        Spacer(modifier = Modifier.height(10.dp))
        HorizontalDivider(
            color = if (isDark) Color.White.copy(alpha = 0.08f) else Color(0xFFE2E8F0)
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "التبديل التجريبي للأدوار",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = if (isDark) TextSecondaryDark else TextSecondaryLight
        )
        Spacer(modifier = Modifier.height(6.dp))

        UserRole.values().forEach { role ->
            val isCurrentRole = currentUser.role == role
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onSwitchRole(role) },
                color = if (isCurrentRole) {
                    PurpleSecondary.copy(alpha = 0.15f)
                } else Color.Transparent,
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "دور: ${role.labelAr}",
                        fontSize = 11.sp,
                        fontWeight = if (isCurrentRole) FontWeight.Bold else FontWeight.Normal,
                        color = if (isCurrentRole) PurpleSecondary else (if (isDark) TextSecondaryDark else TextSecondaryLight)
                    )
                    if (isCurrentRole) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = PurpleSecondary,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SidebarHeaderCollapseButton(
    onClose: () -> Unit,
    isDark: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClose,
        shape = RoundedCornerShape(10.dp),
        color = if (isDark) Color.White.copy(alpha = 0.10f) else Color(0xFFF1F5F9),
        border = BorderStroke(
            1.dp,
            if (isDark) Color.White.copy(alpha = 0.16f) else Color(0xFFCBD5E1)
        ),
        modifier = modifier.defaultMinSize(minWidth = 48.dp, minHeight = 36.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "طي",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDark) TextPrimaryDark else TextPrimaryLight
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "طي القائمة الجانبية",
                tint = if (isDark) TextPrimaryDark else TextPrimaryLight,
                modifier = Modifier.size(15.dp)
            )
        }
    }
}

@Composable
private fun SidebarFooterActions(
    onClose: () -> Unit,
    onNavigate: (AppScreen) -> Unit,
    isDark: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedButton(
            onClick = onClose,
            modifier = Modifier
                .weight(1f)
                .testTag("sidebar_collapse_footer_btn"),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = if (isDark) GoldLight else GoldDark
            ),
            border = BorderStroke(
                1.dp,
                if (isDark) GoldLight.copy(alpha = 0.40f) else GoldDark.copy(alpha = 0.40f)
            ),
            contentPadding = PaddingValues(vertical = 10.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                modifier = Modifier.size(15.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "طي القائمة",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Button(
            onClick = { onNavigate(AppScreen.HOME) },
            modifier = Modifier
                .weight(1f)
                .testTag("sidebar_home_footer_btn"),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isDark) Color(0xFF1E293B) else Color(0xFFF1F5F9)
            ),
            shape = RoundedCornerShape(10.dp),
            contentPadding = PaddingValues(vertical = 10.dp)
        ) {
            Icon(
                Icons.Default.Home,
                contentDescription = null,
                tint = if (isDark) TextPrimaryDark else TextPrimaryLight,
                modifier = Modifier.size(15.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "الرئيسية",
                fontSize = 11.sp,
                color = if (isDark) TextPrimaryDark else TextPrimaryLight
            )
        }
    }
}

@Composable
private fun SidebarHomeButton(
    onNavigate: (AppScreen) -> Unit,
    isDark: Boolean
) {
    Button(
        onClick = { onNavigate(AppScreen.HOME) },
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isDark) Color(0xFF1E293B) else Color(0xFFF1F5F9)
        ),
        shape = RoundedCornerShape(10.dp)
    ) {
        Icon(
            Icons.Default.Home,
            contentDescription = null,
            tint = if (isDark) TextPrimaryDark else TextPrimaryLight,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = "العودة للرئيسية",
            fontSize = 12.sp,
            color = if (isDark) TextPrimaryDark else TextPrimaryLight
        )
    }
}

// -------------------------------------------------------------
// PROFESSIONAL SUMMARY CARDS & HERO COCKPIT CARDS
// -------------------------------------------------------------
@Composable
fun ProfessionalStatCard(
    title: String,
    value: String,
    icon: ImageVector,
    accentColor: Color,
    subtitle: String? = null,
    badgeText: String? = null,
    badgeColor: Color? = null,
    isPositiveTrend: Boolean = true,
    progress: Float? = null,
    progressLabel: String? = null,
    onClick: (() -> Unit)? = null,
    isDark: Boolean,
    modifier: Modifier = Modifier
) {
    val containerShape = RoundedCornerShape(16.dp)
    Surface(
        modifier = modifier
            .width(170.dp)
            .clip(containerShape)
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
            .border(
                BorderStroke(
                    1.dp,
                    if (isDark) accentColor.copy(alpha = 0.32f) else accentColor.copy(alpha = 0.22f)
                ),
                containerShape
            )
            .shadow(
                elevation = if (isDark) 6.dp else 2.dp,
                shape = containerShape,
                ambientColor = accentColor.copy(alpha = 0.15f)
            ),
        color = if (isDark) SurfaceDark.copy(alpha = 0.95f) else SurfaceLight,
        tonalElevation = 3.dp
    ) {
        Column(
            modifier = Modifier.padding(13.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(accentColor.copy(alpha = 0.25f), accentColor.copy(alpha = 0.08f))
                            )
                        )
                        .border(1.dp, accentColor.copy(alpha = 0.35f), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(19.dp)
                    )
                }

                if (badgeText != null) {
                    val finalBadgeColor = badgeColor ?: if (isPositiveTrend) Color(0xFF10B981) else Color(0xFFEF4444)
                    Surface(
                        color = finalBadgeColor.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        ) {
                            Icon(
                                imageVector = if (isPositiveTrend) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                                contentDescription = null,
                                tint = finalBadgeColor,
                                modifier = Modifier.size(10.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = badgeText,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = finalBadgeColor
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = value,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 21.sp,
                color = if (isDark) TextPrimaryDark else TextPrimaryLight,
                letterSpacing = 0.3.sp
            )

            Text(
                text = title,
                fontSize = 11.sp,
                color = if (isDark) TextSecondaryDark else TextSecondaryLight,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            if (subtitle != null) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = 10.sp,
                    color = accentColor.copy(alpha = 0.9f),
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (progress != null) {
                Spacer(modifier = Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = { progress.coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = accentColor,
                    trackColor = if (isDark) Color.White.copy(alpha = 0.1f) else Color(0xFFE2E8F0)
                )
                if (progressLabel != null) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = progressLabel,
                        fontSize = 9.sp,
                        color = if (isDark) TextSecondaryDark else TextSecondaryLight
                    )
                }
            }
        }
    }
}

// Backward compatible alias
@Composable
fun SleekStatCard(
    title: String,
    value: String,
    icon: ImageVector,
    accentColor: Color,
    trendText: String? = null,
    isPositiveTrend: Boolean = true,
    isDark: Boolean,
    modifier: Modifier = Modifier
) {
    ProfessionalStatCard(
        title = title,
        value = value,
        icon = icon,
        accentColor = accentColor,
        badgeText = trendText,
        isPositiveTrend = isPositiveTrend,
        isDark = isDark,
        modifier = modifier
    )
}

@Composable
fun HeroOverviewCard(
    title: String,
    subtitle: String,
    accentGradient: List<Color>,
    statusBadgeText: String,
    statusBadgeColor: Color,
    icon: ImageVector,
    isDark: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit = {}
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .border(
                BorderStroke(
                    1.dp,
                    if (isDark) accentGradient.first().copy(alpha = 0.35f) else accentGradient.first().copy(alpha = 0.25f)
                ),
                RoundedCornerShape(18.dp)
            )
            .shadow(
                elevation = if (isDark) 6.dp else 2.dp,
                shape = RoundedCornerShape(18.dp),
                ambientColor = accentGradient.first().copy(alpha = 0.15f)
            ),
        color = if (isDark) SurfaceDark else SurfaceLight,
        tonalElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(11.dp))
                            .background(Brush.linearGradient(accentGradient)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = if (isDark) TextPrimaryDark else TextPrimaryLight
                        )
                        Text(
                            text = subtitle,
                            fontSize = 11.sp,
                            color = if (isDark) TextSecondaryDark else TextSecondaryLight
                        )
                    }
                }

                Surface(
                    color = statusBadgeColor.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(statusBadgeColor)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = statusBadgeText,
                            color = statusBadgeColor,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            content()
        }
    }
}

// -------------------------------------------------------------
// 1. ADMIN DASHBOARD
// -------------------------------------------------------------
@Composable
fun AdminDashboardView(
    viewModel: JarablusViewModel,
    selectedSection: Int,
    onSelectSection: (Int) -> Unit,
    isDark: Boolean,
    modifier: Modifier = Modifier
) {
    val pendingNews by viewModel.pendingNews.collectAsState()
    val pendingAds by viewModel.pendingAnnouncements.collectAsState()
    val pendingProps by viewModel.pendingProperties.collectAsState()
    val pendingSubs by viewModel.pendingSubscriptions.collectAsState()
    val allUsers by viewModel.allUsers.collectAsState()

    val totalPending = pendingNews.size + pendingAds.size + pendingProps.size + pendingSubs.size
    val crimsonAccent = Color(0xFFDC2626)

    var rejectTarget by remember { mutableStateOf<Triple<String, Long, String>?>(null) } // type, id, title
    var searchQuery by remember { mutableStateOf("") }

    Column(modifier = modifier.fillMaxSize()) {
        // Executive Hero Moderation Card
        HeroOverviewCard(
            title = "مركز التحكم والتدقيق الشامل لمدينة جرابلس",
            subtitle = "طابور المراجعة المباشر: $totalPending عناصر بانتظار الإجراء",
            accentGradient = listOf(Color(0xFFDC2626), Color(0xFF991B1B)),
            statusBadgeText = if (totalPending > 0) "يتطلب إجراء فوري" else "جميع المعاملات معتمدة ✓",
            statusBadgeColor = if (totalPending > 0) Color(0xFFEF4444) else Color(0xFF10B981),
            icon = Icons.Default.AdminPanelSettings,
            isDark = isDark
        ) {
            Spacer(modifier = Modifier.height(10.dp))
            // Quick Filter Pills with Real-time Count Badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onSelectSection(1) },
                    color = if (selectedSection == 1) crimsonAccent.copy(alpha = 0.22f) else (if (isDark) Color.White.copy(alpha = 0.06f) else Color(0xFFF1F5F9)),
                    border = if (selectedSection == 1) BorderStroke(1.dp, crimsonAccent.copy(alpha = 0.5f)) else null,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(vertical = 6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "الأخبار", fontSize = 10.sp, color = if (isDark) TextSecondaryDark else TextSecondaryLight)
                        Text(text = "${pendingNews.size}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (pendingNews.isNotEmpty()) crimsonAccent else (if (isDark) TextPrimaryDark else TextPrimaryLight))
                    }
                }

                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onSelectSection(2) },
                    color = if (selectedSection == 2) Color(0xFF0D9488).copy(alpha = 0.22f) else (if (isDark) Color.White.copy(alpha = 0.06f) else Color(0xFFF1F5F9)),
                    border = if (selectedSection == 2) BorderStroke(1.dp, Color(0xFF0D9488).copy(alpha = 0.5f)) else null,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(vertical = 6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "الإعلانات", fontSize = 10.sp, color = if (isDark) TextSecondaryDark else TextSecondaryLight)
                        Text(text = "${pendingAds.size}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (pendingAds.isNotEmpty()) Color(0xFF0D9488) else (if (isDark) TextPrimaryDark else TextPrimaryLight))
                    }
                }

                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onSelectSection(3) },
                    color = if (selectedSection == 3) Color(0xFFEA580C).copy(alpha = 0.22f) else (if (isDark) Color.White.copy(alpha = 0.06f) else Color(0xFFF1F5F9)),
                    border = if (selectedSection == 3) BorderStroke(1.dp, Color(0xFFEA580C).copy(alpha = 0.5f)) else null,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(vertical = 6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "العقارات", fontSize = 10.sp, color = if (isDark) TextSecondaryDark else TextSecondaryLight)
                        Text(text = "${pendingProps.size}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (pendingProps.isNotEmpty()) Color(0xFFEA580C) else (if (isDark) TextPrimaryDark else TextPrimaryLight))
                    }
                }

                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onSelectSection(4) },
                    color = if (selectedSection == 4) Color(0xFF2563EB).copy(alpha = 0.22f) else (if (isDark) Color.White.copy(alpha = 0.06f) else Color(0xFFF1F5F9)),
                    border = if (selectedSection == 4) BorderStroke(1.dp, Color(0xFF2563EB).copy(alpha = 0.5f)) else null,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(vertical = 6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "الاشتراكات", fontSize = 10.sp, color = if (isDark) TextSecondaryDark else TextSecondaryLight)
                        Text(text = "${pendingSubs.size}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (pendingSubs.isNotEmpty()) Color(0xFF2563EB) else (if (isDark) TextPrimaryDark else TextPrimaryLight))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Professional Key Statistics Carousel with High Data Visibility
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(vertical = 2.dp)
        ) {
            item {
                ProfessionalStatCard(
                    title = "المستخدمين المسجلين",
                    value = "${allUsers.size}",
                    subtitle = "حسابات موثقة ومواطنين",
                    icon = Icons.Default.People,
                    accentColor = PurpleSecondary,
                    badgeText = "+8% شهري",
                    isPositiveTrend = true,
                    onClick = { onSelectSection(5) },
                    isDark = isDark
                )
            }
            item {
                ProfessionalStatCard(
                    title = "طابور المراجعة والتدقيق",
                    value = "$totalPending",
                    subtitle = "موزعة بكافة الأقسام",
                    icon = Icons.Default.HourglassTop,
                    accentColor = crimsonAccent,
                    badgeText = if (totalPending > 0) "عاجل" else "مكتمل",
                    isPositiveTrend = totalPending == 0,
                    onClick = { onSelectSection(1) },
                    isDark = isDark
                )
            }
            item {
                ProfessionalStatCard(
                    title = "أخبار قيد المراجعة",
                    value = "${pendingNews.size}",
                    subtitle = "تحتاج اعتماد المحتوى",
                    icon = Icons.Default.Newspaper,
                    accentColor = GoldDark,
                    badgeText = if (pendingNews.isNotEmpty()) "فحص" else "مكتمل",
                    isPositiveTrend = pendingNews.isEmpty(),
                    onClick = { onSelectSection(1) },
                    isDark = isDark
                )
            }
            item {
                ProfessionalStatCard(
                    title = "إعلانات معلقة",
                    value = "${pendingAds.size}",
                    subtitle = "إعلانات تجارية ومحلية",
                    icon = Icons.Default.Campaign,
                    accentColor = Color(0xFF0D9488),
                    badgeText = "نشط",
                    isPositiveTrend = true,
                    onClick = { onSelectSection(2) },
                    isDark = isDark
                )
            }
            item {
                ProfessionalStatCard(
                    title = "عقارات معلقة",
                    value = "${pendingProps.size}",
                    subtitle = "عروض بيع وإيجار جديدة",
                    icon = Icons.Default.HomeWork,
                    accentColor = Color(0xFFEA580C),
                    badgeText = "عاجل",
                    isPositiveTrend = true,
                    onClick = { onSelectSection(3) },
                    isDark = isDark
                )
            }
            item {
                ProfessionalStatCard(
                    title = "اشتراكات وتأكيد دفع",
                    value = "${pendingSubs.size}",
                    subtitle = "فواتير بانتظار الاعتماد",
                    icon = Icons.Default.Payments,
                    accentColor = Color(0xFF2563EB),
                    badgeText = "مالي",
                    isPositiveTrend = true,
                    onClick = { onSelectSection(4) },
                    isDark = isDark
                )
            }
            item {
                ProfessionalStatCard(
                    title = "كفاءة المنظومة",
                    value = "99.9%",
                    subtitle = "استجابة متوسط 18 دقيقة",
                    icon = Icons.Default.Security,
                    accentColor = Color(0xFF10B981),
                    badgeText = "آمن",
                    isPositiveTrend = true,
                    isDark = isDark
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Horizontal Section Tabs
        ScrollableTabRow(
            selectedTabIndex = selectedSection,
            containerColor = if (isDark) SurfaceDark else SurfaceLight,
            contentColor = GoldDark,
            edgePadding = 4.dp,
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .border(
                    BorderStroke(1.dp, if (isDark) Color.White.copy(alpha = 0.08f) else Color(0xFFE2E8F0)),
                    RoundedCornerShape(12.dp)
                )
        ) {
            Tab(
                selected = selectedSection == 0,
                onClick = { onSelectSection(0) },
                text = { Text("نظرة عامة", fontSize = 12.sp, fontWeight = if (selectedSection == 0) FontWeight.Bold else FontWeight.Normal) }
            )
            Tab(
                selected = selectedSection == 1,
                onClick = { onSelectSection(1) },
                text = { Text("الأخبار (${pendingNews.size})", fontSize = 12.sp, fontWeight = if (selectedSection == 1) FontWeight.Bold else FontWeight.Normal) }
            )
            Tab(
                selected = selectedSection == 2,
                onClick = { onSelectSection(2) },
                text = { Text("الإعلانات (${pendingAds.size})", fontSize = 12.sp, fontWeight = if (selectedSection == 2) FontWeight.Bold else FontWeight.Normal) }
            )
            Tab(
                selected = selectedSection == 3,
                onClick = { onSelectSection(3) },
                text = { Text("العقارات (${pendingProps.size})", fontSize = 12.sp, fontWeight = if (selectedSection == 3) FontWeight.Bold else FontWeight.Normal) }
            )
            Tab(
                selected = selectedSection == 4,
                onClick = { onSelectSection(4) },
                text = { Text("الاشتراكات (${pendingSubs.size})", fontSize = 12.sp, fontWeight = if (selectedSection == 4) FontWeight.Bold else FontWeight.Normal) }
            )
            Tab(
                selected = selectedSection == 5,
                onClick = { onSelectSection(5) },
                text = { Text("المستخدمين (${allUsers.size})", fontSize = 12.sp, fontWeight = if (selectedSection == 5) FontWeight.Bold else FontWeight.Normal) }
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Content Area
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.weight(1f)
        ) {
            when (selectedSection) {
                0 -> {
                    // Overview Summary & Quick Actions
                    item {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = if (isDark) SurfaceDark else SurfaceLight,
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.3f))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("حالة نظام جرابلس اليوم", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Surface(color = Color(0xFF10B981).copy(alpha = 0.15f), shape = RoundedCornerShape(6.dp)) {
                                        Text("النظام يعمل بكفاءة 100%", color = Color(0xFF047857), fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "إجمالي الطلبات المعلقة في قائمة الانتظار: ${pendingNews.size + pendingAds.size + pendingProps.size + pendingSubs.size} طلبات بحاجة لتدقيق الإدارة",
                                    fontSize = 11.sp,
                                    color = if (isDark) TextSecondaryDark else TextSecondaryLight
                                )
                            }
                        }
                    }

                    item {
                        Text("أحدث الأخبار بانتظار الاعتماد السريع:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    if (pendingNews.isEmpty()) {
                        item { EmptyModerationPlaceholder("لا توجد أخبار قيد المراجعة حالياً") }
                    } else {
                        items(pendingNews.take(2)) { news ->
                            AdminPendingItemCard(
                                title = news.title,
                                subtitle = "الكاتب: ${news.authorName} • التصنيف: ${news.category}",
                                details = news.content,
                                isDark = isDark,
                                onApprove = { viewModel.approveNews(news.id) },
                                onReject = { rejectTarget = Triple("NEWS", news.id, news.title) }
                            )
                        }
                    }
                }
                1 -> {
                    // Pending News
                    if (pendingNews.isEmpty()) {
                        item { EmptyModerationPlaceholder("لا توجد أخبار قيد المراجعة حالياً") }
                    } else {
                        items(pendingNews) { news ->
                            AdminPendingItemCard(
                                title = news.title,
                                subtitle = "الكاتب: ${news.authorName} • التصنيف: ${news.category}",
                                details = news.content,
                                isDark = isDark,
                                onApprove = { viewModel.approveNews(news.id) },
                                onReject = { rejectTarget = Triple("NEWS", news.id, news.title) }
                            )
                        }
                    }
                }
                2 -> {
                    // Pending Ads
                    if (pendingAds.isEmpty()) {
                        item { EmptyModerationPlaceholder("لا توجد إعلانات بانتظار الاعتماد") }
                    } else {
                        items(pendingAds) { ad ->
                            AdminPendingItemCard(
                                title = ad.title,
                                subtitle = "المعلن: ${ad.authorName} • هاتف: ${ad.phone}",
                                details = ad.content,
                                isDark = isDark,
                                onApprove = { viewModel.approveAnnouncement(ad.id) },
                                onReject = { rejectTarget = Triple("ANNOUNCEMENT", ad.id, ad.title) }
                            )
                        }
                    }
                }
                3 -> {
                    // Pending Properties
                    if (pendingProps.isEmpty()) {
                        item { EmptyModerationPlaceholder("لا توجد عقارات بانتظار الموافقة") }
                    } else {
                        items(pendingProps) { prop ->
                            AdminPendingItemCard(
                                title = prop.title,
                                subtitle = "${prop.type.labelAr} • السعر: ${prop.price} ${prop.currency} • ${prop.district}",
                                details = "${prop.rooms} غرف • ${prop.area} م² • المعلن: ${prop.authorName}",
                                isDark = isDark,
                                onApprove = { viewModel.approveProperty(prop.id) },
                                onReject = { rejectTarget = Triple("PROPERTY", prop.id, prop.title) }
                            )
                        }
                    }
                }
                4 -> {
                    // Pending Subscriptions
                    if (pendingSubs.isEmpty()) {
                        item { EmptyModerationPlaceholder("لا توجد طلبات اشتراك معلقة") }
                    } else {
                        items(pendingSubs) { sub ->
                            AdminPendingItemCard(
                                title = "طلب اشتراك: ${sub.planName}",
                                subtitle = "التاجر: ${sub.userName} • القيمة: ${sub.amount} ${sub.currency}",
                                details = "تاريخ الطلب: قيد التدقيق المالي والإداري",
                                isDark = isDark,
                                onApprove = { viewModel.approveSubscription(sub.id) },
                                onReject = { viewModel.rejectSubscription(sub.id) }
                            )
                        }
                    }
                }
                5 -> {
                    // Users Management
                    item {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("بحث عن مستخدم بالاسم أو رقم الهاتف...", fontSize = 12.sp) },
                            leadingIcon = { Icon(Icons.Default.Search, null, modifier = Modifier.size(18.dp)) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true
                        )
                    }

                    val filteredUsers = allUsers.filter {
                        it.name.contains(searchQuery, ignoreCase = true) || it.phone.contains(searchQuery)
                    }

                    items(filteredUsers) { u ->
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = if (isDark) SurfaceDark else SurfaceLight,
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, if (isDark) Color.White.copy(alpha = 0.08f) else Color(0xFFE2E8F0))
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(PurpleSecondary.copy(alpha = 0.15f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(u.name.take(1), fontWeight = FontWeight.Bold, color = PurpleSecondary)
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(u.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text("${u.role.labelAr} • ${u.phone}", fontSize = 11.sp, color = if (isDark) TextSecondaryDark else TextSecondaryLight)
                                    }
                                }

                                Surface(
                                    color = if (u.isActive) Color(0xFFD1FAE5) else Color(0xFFFEE2E2),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = if (u.isActive) "نشط ومفعل" else "معطل",
                                        color = if (u.isActive) Color(0xFF047857) else Color(0xFFB91C1C),
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
        }
    }

    if (rejectTarget != null) {
        RejectionReasonDialog(
            isOpen = true,
            onDismiss = { rejectTarget = null },
            onConfirm = { reason ->
                val target = rejectTarget!!
                when (target.first) {
                    "NEWS" -> viewModel.rejectNews(target.second, reason)
                    "ANNOUNCEMENT" -> viewModel.rejectAnnouncement(target.second, reason)
                    "PROPERTY" -> viewModel.rejectProperty(target.second, reason)
                }
                rejectTarget = null
            }
        )
    }
}

// -------------------------------------------------------------
// 2. MERCHANT DASHBOARD
// -------------------------------------------------------------
@Composable
fun MerchantDashboardView(
    viewModel: JarablusViewModel,
    user: UserEntity,
    selectedSection: Int,
    onSelectSection: (Int) -> Unit,
    isDark: Boolean,
    modifier: Modifier = Modifier
) {
    val myProducts by viewModel.allProducts.collectAsState()
    val myDiscounts by viewModel.activeDiscounts.collectAsState()
    var showDiscountDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Merchant Hero Overview Card
        item {
            val ratio = if (user.productLimit > 0) (myProducts.size.toFloat() / user.productLimit.toFloat()).coerceIn(0f, 1f) else 0.3f
            HeroOverviewCard(
                title = "متجر: ${user.storeName.ifBlank { user.name }}",
                subtitle = "الاشتراك الحالي: ${user.subscriptionTier} • الحد الأقصى: ${user.productLimit} منتج",
                accentGradient = listOf(GoldDark, GoldPrimary),
                statusBadgeText = "حساب معتمد ⭐",
                statusBadgeColor = GoldDark,
                icon = Icons.Default.Storefront,
                isDark = isDark
            ) {
                Spacer(modifier = Modifier.height(10.dp))
                // Progress Quota Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "استهلاك مساحة الكتالوج:",
                        fontSize = 11.sp,
                        color = if (isDark) TextSecondaryDark else TextSecondaryLight
                    )
                    Text(
                        text = "${myProducts.size} من ${user.productLimit} منتجات معروضة",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldDark
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                LinearProgressIndicator(
                    progress = { ratio },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(7.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = GoldPrimary,
                    trackColor = if (isDark) Color.White.copy(alpha = 0.1f) else Color(0xFFE2E8F0)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Quick Action Shortcuts directly on the Hero Card
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { viewModel.navigateTo(AppScreen.MARKET) },
                        colors = ButtonDefaults.buttonColors(containerColor = PurpleSecondary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.AddShoppingCart, null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("+ إضافة منتج", fontSize = 12.sp)
                    }

                    Button(
                        onClick = { showDiscountDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.LocalOffer, null, tint = Color.Black, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("+ إضافة تخفيض", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Professional Key Statistics Carousel
        item {
            val ratio = if (user.productLimit > 0) (myProducts.size.toFloat() / user.productLimit.toFloat()).coerceIn(0f, 1f) else 0.3f
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(vertical = 2.dp)
            ) {
                item {
                    ProfessionalStatCard(
                        title = "المنتجات المعروضة",
                        value = "${myProducts.size} / ${user.productLimit}",
                        subtitle = "من السعة الإجمالية المتاحة",
                        icon = Icons.Default.Inventory2,
                        accentColor = GoldDark,
                        progress = ratio,
                        progressLabel = "${(ratio * 100).toInt()}% مستخدم",
                        badgeText = if (ratio >= 0.85f) "اقترب الامتلاء" else "سعة جيدة",
                        isPositiveTrend = ratio < 0.85f,
                        onClick = { onSelectSection(1) },
                        isDark = isDark
                    )
                }
                item {
                    ProfessionalStatCard(
                        title = "العروض والتخفيضات",
                        value = "${myDiscounts.size}",
                        subtitle = "عروض ترويجية نشطة بالمتجر",
                        icon = Icons.Default.LocalOffer,
                        accentColor = Color(0xFF10B981),
                        badgeText = "نشط الآن",
                        isPositiveTrend = true,
                        onClick = { onSelectSection(2) },
                        isDark = isDark
                    )
                }
                item {
                    ProfessionalStatCard(
                        title = "مشاهدات المتجر",
                        value = "1,420",
                        subtitle = "زيارة لكتالوج المتجر هذا الشهر",
                        icon = Icons.Default.Visibility,
                        accentColor = PurpleSecondary,
                        badgeText = "+18% نمو",
                        isPositiveTrend = true,
                        isDark = isDark
                    )
                }
                item {
                    ProfessionalStatCard(
                        title = "استفسارات الزبائن",
                        value = "28",
                        subtitle = "تواصل مباشر عبر واتساب",
                        icon = Icons.Default.Chat,
                        accentColor = Color(0xFF0284C7),
                        badgeText = "مباشر",
                        isPositiveTrend = true,
                        isDark = isDark
                    )
                }
                item {
                    ProfessionalStatCard(
                        title = "صلاحية الباقة",
                        value = "24 يوماً",
                        subtitle = "باقة ${user.subscriptionTier}",
                        icon = Icons.Default.Star,
                        accentColor = GoldPrimary,
                        badgeText = "موثق ⭐",
                        isPositiveTrend = true,
                        onClick = { onSelectSection(3) },
                        isDark = isDark
                    )
                }
            }
        }

        // Quick Action Buttons
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { viewModel.navigateTo(AppScreen.MARKET) },
                    colors = ButtonDefaults.buttonColors(containerColor = PurpleSecondary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.AddShoppingCart, null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("إضافة منتج", fontSize = 12.sp)
                }

                Button(
                    onClick = { showDiscountDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.LocalOffer, null, tint = Color.Black, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("إضافة تخفيض", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        item {
            Text("العروض والخصومات الفعالة بالمتجر:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }

        if (myDiscounts.isEmpty()) {
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = if (isDark) SurfaceDark else SurfaceLight,
                    border = BorderStroke(1.dp, if (isDark) Color.White.copy(alpha = 0.08f) else Color(0xFFE2E8F0))
                ) {
                    Box(modifier = Modifier.padding(20.dp), contentAlignment = Alignment.Center) {
                        Text("لم تقم بإضافة عروض تخفيض حالياً. أنشئ عرضاً لجذب زوار جدد!", fontSize = 11.sp, color = if (isDark) TextSecondaryDark else TextSecondaryLight)
                    }
                }
            }
        } else {
            items(myDiscounts) { d ->
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = if (isDark) SurfaceDark else SurfaceLight,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(d.productName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                "${d.discountedPrice} $ (خصم ${d.discountPercent}% - السعر الأصلي ${d.originalPrice} $)",
                                color = if (isDark) GoldLight else GoldDark,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                "ينتهي: ${d.validUntil}",
                                fontSize = 10.sp,
                                color = if (isDark) TextSecondaryDark else TextSecondaryLight
                            )
                        }
                        Surface(color = Color(0xFFD1FAE5), shape = RoundedCornerShape(6.dp)) {
                            Text("نشط", color = Color(0xFF065F46), fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                    }
                }
            }
        }
    }

    if (showDiscountDialog) {
        AddDiscountDialog(
            onDismiss = { showDiscountDialog = false },
            onSubmit = { name, orig, disc, desc, until ->
                viewModel.submitDiscount(name, orig, disc, desc, until)
                showDiscountDialog = false
            }
        )
    }
}

// -------------------------------------------------------------
// 3. SERVICE PROVIDER DASHBOARD
// -------------------------------------------------------------
@Composable
fun ServiceProviderDashboardView(
    viewModel: JarablusViewModel,
    user: UserEntity,
    selectedSection: Int,
    onSelectSection: (Int) -> Unit,
    isDark: Boolean,
    modifier: Modifier = Modifier
) {
    val myServices by viewModel.allServices.collectAsState()

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Service Provider Hero Card
        item {
            HeroOverviewCard(
                title = "ملف مقدم الخدمة: ${user.name}",
                subtitle = "التخصص: ${user.serviceCategory.ifBlank { "مهني معتمد في جرابلس" }}",
                accentGradient = listOf(PurpleSecondary, Color(0xFF4338CA)),
                statusBadgeText = "⭐ تقييم 4.9 ممتاز",
                statusBadgeColor = PurpleSecondary,
                icon = Icons.Default.Handyman,
                isDark = isDark
            ) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "جاهزية استقبال طلبات العمل:",
                        fontSize = 11.sp,
                        color = if (isDark) TextSecondaryDark else TextSecondaryLight
                    )
                    Text(
                        text = "متاح للعمل في جرابلس وضواحيها",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF10B981)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = { viewModel.navigateTo(AppScreen.SERVICES) },
                    colors = ButtonDefaults.buttonColors(containerColor = PurpleSecondary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Add, null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("+ نشر خدمة جديدة في دليل المهنيين", fontSize = 12.sp)
                }
            }
        }

        // Stats Row with High Data Visibility
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(vertical = 2.dp)
            ) {
                item {
                    ProfessionalStatCard(
                        title = "الخدمات المنشورة",
                        value = "${myServices.size}",
                        subtitle = "في دليل أصحاب المهن",
                        icon = Icons.Default.Handyman,
                        accentColor = PurpleSecondary,
                        badgeText = "نشط ومتاح",
                        isPositiveTrend = true,
                        isDark = isDark
                    )
                }
                item {
                    ProfessionalStatCard(
                        title = "طلبات التواصل المباشر",
                        value = "14",
                        subtitle = "اتصالات وزبائن هذا الأسبوع",
                        icon = Icons.Default.Call,
                        accentColor = Color(0xFF10B981),
                        badgeText = "+3 اليوم",
                        isPositiveTrend = true,
                        isDark = isDark
                    )
                }
                item {
                    ProfessionalStatCard(
                        title = "مشاهدات الدليل",
                        value = "890",
                        subtitle = "ظهور في نتائج البحث بالمدينة",
                        icon = Icons.Default.Visibility,
                        accentColor = GoldDark,
                        badgeText = "+12%",
                        isPositiveTrend = true,
                        isDark = isDark
                    )
                }
                item {
                    ProfessionalStatCard(
                        title = "مستوى الاعتماد",
                        value = "ذهبي ⭐",
                        subtitle = "توثيق هوية وحرفة",
                        icon = Icons.Default.VerifiedUser,
                        accentColor = Color(0xFF0284C7),
                        badgeText = "موثق 100%",
                        isPositiveTrend = true,
                        isDark = isDark
                    )
                }
            }
        }

        item {
            Button(
                onClick = { viewModel.navigateTo(AppScreen.SERVICES) },
                colors = ButtonDefaults.buttonColors(containerColor = PurpleSecondary),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Add, null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("إضافة خدمة جديدة إلى دليل المهنيين", fontSize = 12.sp)
            }
        }

        item {
            Text("الخدمات المسجلة في المنصة:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }

        items(myServices) { s ->
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = if (isDark) SurfaceDark else SurfaceLight,
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, if (isDark) Color.White.copy(alpha = 0.08f) else Color(0xFFE2E8F0))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(s.title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Surface(color = Color(0xFFD1FAE5), shape = RoundedCornerShape(6.dp)) {
                            Text("معتمد", color = Color(0xFF047857), fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("⭐ تقييم ${s.rating} • خبرة ${s.experienceYears} سنوات • هاتف: ${s.providerPhone}", fontSize = 11.sp, color = PurpleSecondary)
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 4. USER DASHBOARD (My Posts & Moderation Status)
// -------------------------------------------------------------
@Composable
fun UserDashboardView(
    viewModel: JarablusViewModel,
    user: UserEntity,
    selectedSection: Int,
    onSelectSection: (Int) -> Unit,
    isDark: Boolean,
    modifier: Modifier = Modifier
) {
    val allNews by viewModel.approvedNews.collectAsState()
    val allPendingNews by viewModel.pendingNews.collectAsState()
    val allProps by viewModel.approvedProperties.collectAsState()
    val allPendingProps by viewModel.pendingProperties.collectAsState()

    val myApprovedNews = allNews.filter { it.authorId == user.id }
    val myPendingNews = allPendingNews.filter { it.authorId == user.id }
    val myApprovedProps = allProps.filter { it.authorId == user.id }
    val myPendingProps = allPendingProps.filter { it.authorId == user.id }

    val totalPending = myPendingNews.size + myPendingProps.size
    val totalApproved = myApprovedNews.size + myApprovedProps.size

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // User Hero Overview Card
        item {
            val skyGradient = listOf(Color(0xFF0284C7), Color(0xFF4F46E5))
            HeroOverviewCard(
                title = "مرحباً بك، ${user.name}",
                subtitle = "تابع حالة اعتماد مساهماتك وتفاعلك مع أهالي مدينة جرابلس",
                accentGradient = skyGradient,
                statusBadgeText = if (totalPending > 0) "لديك طلبات قيد التدقيق" else "جميع مساهماتك معتمدة ✓",
                statusBadgeColor = if (totalPending > 0) GoldDark else Color(0xFF10B981),
                icon = Icons.Default.Person,
                isDark = isDark
            ) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "موقف المنشورات:",
                        fontSize = 11.sp,
                        color = if (isDark) TextSecondaryDark else TextSecondaryLight
                    )
                    Text(
                        text = "$totalApproved منشور معتمد • $totalPending قيد المراجعة",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (totalPending > 0) GoldDark else Color(0xFF10B981)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { viewModel.navigateTo(AppScreen.NEWS) },
                        colors = ButtonDefaults.buttonColors(containerColor = PurpleSecondary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Add, null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("+ إضافة خبر", fontSize = 12.sp)
                    }

                    Button(
                        onClick = { viewModel.navigateTo(AppScreen.PROPERTIES) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.HomeWork, null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("+ نشر عقار", fontSize = 12.sp)
                    }
                }
            }
        }

        // Professional Stats Carousel with High Data Visibility
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(vertical = 2.dp)
            ) {
                item {
                    ProfessionalStatCard(
                        title = "المنشورات المعتمدة",
                        value = "$totalApproved",
                        subtitle = "${myApprovedNews.size} أخبار • ${myApprovedProps.size} عقارات منشورة",
                        icon = Icons.Default.CheckCircle,
                        accentColor = Color(0xFF10B981),
                        badgeText = "منشور ومتاح",
                        isPositiveTrend = true,
                        isDark = isDark
                    )
                }
                item {
                    ProfessionalStatCard(
                        title = "قيد المراجعة والتدقيق",
                        value = "$totalPending",
                        subtitle = if (totalPending > 0) "بانتظار موافقة الإدارة" else "لا توجد طلبات معلقة",
                        icon = Icons.Default.HourglassTop,
                        accentColor = if (totalPending > 0) Color(0xFFEA580C) else GoldDark,
                        badgeText = if (totalPending > 0) "قيد الفحص" else "مكتمل",
                        isPositiveTrend = totalPending == 0,
                        isDark = isDark
                    )
                }
                item {
                    ProfessionalStatCard(
                        title = "نقاط المشاركة المجتمعية",
                        value = "150 نقطة",
                        subtitle = "متبقي 50 نقطة للمستوى الذهبي",
                        icon = Icons.Default.Stars,
                        accentColor = PurpleSecondary,
                        progress = 0.75f,
                        progressLabel = "75% إنجاز",
                        badgeText = "مستوى فضي ⭐",
                        isPositiveTrend = true,
                        isDark = isDark
                    )
                }
                item {
                    ProfessionalStatCard(
                        title = "مشاهدات إعلاناتي",
                        value = "420",
                        subtitle = "مشاهدة لمشاركاتك بالمدينة",
                        icon = Icons.Default.Visibility,
                        accentColor = Color(0xFF0284C7),
                        badgeText = "+14% تفاعل",
                        isPositiveTrend = true,
                        isDark = isDark
                    )
                }
                item {
                    ProfessionalStatCard(
                        title = "الاتصالات والرسائل",
                        value = "12",
                        subtitle = "تواصل مباشر عبر الهاتف",
                        icon = Icons.Default.PhoneCallback,
                        accentColor = Color(0xFF10B981),
                        badgeText = "مباشر",
                        isPositiveTrend = true,
                        isDark = isDark
                    )
                }
            }
        }

        item {
            Text("منشوراتي وإعلاناتي وموقف الاعتماد:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }

        if (myPendingNews.isEmpty() && myApprovedNews.isEmpty() && myPendingProps.isEmpty() && myApprovedProps.isEmpty()) {
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = if (isDark) SurfaceDark else SurfaceLight,
                    border = BorderStroke(1.dp, if (isDark) Color.White.copy(alpha = 0.08f) else Color(0xFFE2E8F0))
                ) {
                    Box(modifier = Modifier.padding(20.dp), contentAlignment = Alignment.Center) {
                        Text(
                            "لم تقم بإرسال أي منشورات أو عقارات حتى الآن. يمكنك إضافة خبر أو عقار من القوائم الرئيسية.",
                            fontSize = 11.sp,
                            color = if (isDark) TextSecondaryDark else TextSecondaryLight
                        )
                    }
                }
            }
        } else {
            // Pending items first
            items(myPendingNews) { news ->
                UserPostStatusCard(title = news.title, type = "خبر / مقال", status = news.status, reason = news.rejectionReason, isDark = isDark)
            }
            items(myPendingProps) { prop ->
                UserPostStatusCard(title = prop.title, type = "عقار", status = prop.status, reason = prop.rejectionReason, isDark = isDark)
            }
            // Approved items
            items(myApprovedNews) { news ->
                UserPostStatusCard(title = news.title, type = "خبر / مقال", status = news.status, reason = "", isDark = isDark)
            }
            items(myApprovedProps) { prop ->
                UserPostStatusCard(title = prop.title, type = "عقار", status = prop.status, reason = "", isDark = isDark)
            }
        }
    }
}

// -------------------------------------------------------------
// REUSABLE DASHBOARD CARDS & COMPONENTS
// -------------------------------------------------------------
@Composable
fun UserPostStatusCard(
    title: String,
    type: String,
    status: ModerationStatus,
    reason: String,
    isDark: Boolean
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = if (isDark) SurfaceDark else SurfaceLight,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, if (isDark) Color.White.copy(alpha = 0.08f) else Color(0xFFE2E8F0))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(2.dp))
                Text("القسم: $type", fontSize = 11.sp, color = if (isDark) TextSecondaryDark else TextSecondaryLight)
            }
            StatusBadge(status = status, rejectionReason = reason)
        }
    }
}

@Composable
fun AdminPendingItemCard(
    title: String,
    subtitle: String,
    details: String,
    isDark: Boolean,
    onApprove: () -> Unit,
    onReject: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = if (isDark) SurfaceDark else SurfaceLight,
        shape = RoundedCornerShape(14.dp),
        tonalElevation = 2.dp,
        border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.35f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    modifier = Modifier.weight(1f),
                    color = if (isDark) TextPrimaryDark else TextPrimaryLight
                )
                StatusBadge(status = ModerationStatus.PENDING)
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(subtitle, fontSize = 11.sp, color = PurpleSecondary, fontWeight = FontWeight.Medium)

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                details,
                fontSize = 11.sp,
                color = if (isDark) TextSecondaryDark else TextSecondaryLight,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onApprove,
                    colors = ButtonDefaults.buttonColors(containerColor = StatusApproved),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("اعتماد ونشر", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onReject,
                    colors = ButtonDefaults.buttonColors(containerColor = StatusRejected),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Close, null, tint = Color.White, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("رفض مع السبب", color = Color.White, fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
fun EmptyModerationPlaceholder(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 12.sp
        )
    }
}

@Composable
fun AddDiscountDialog(
    onDismiss: () -> Unit,
    onSubmit: (String, Double, Double, String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var origStr by remember { mutableStateOf("") }
    var discStr by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var until by remember { mutableStateOf("حتى نهاية الأسبوع") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("إضافة تخفيض / عرض خاص للمتجر", fontWeight = FontWeight.Bold, fontSize = 15.sp) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("اسم المنتج المشمول بالخصم", fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = origStr,
                        onValueChange = { origStr = it },
                        label = { Text("السعر الأصلي $", fontSize = 11.sp) },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = discStr,
                        onValueChange = { discStr = it },
                        label = { Text("السعر بعد الخصم $", fontSize = 11.sp) },
                        modifier = Modifier.weight(1f)
                    )
                }
                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("تفاصيل العرض والميزات", fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = until,
                    onValueChange = { until = it },
                    label = { Text("تاريخ انتهاء العرض", fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val orig = origStr.toDoubleOrNull() ?: 0.0
                    val disc = discStr.toDoubleOrNull() ?: 0.0
                    if (name.isNotBlank() && orig > disc) {
                        onSubmit(name, orig, disc, desc, until)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary)
            ) {
                Text("نشر العرض", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("إلغاء") }
        }
    )
}
