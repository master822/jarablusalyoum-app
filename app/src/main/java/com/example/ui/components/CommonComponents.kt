package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.JarablusViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JarablusTopBar(
    viewModel: JarablusViewModel,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val unreadNotifs by viewModel.unreadNotificationsCount.collectAsState()
    val unreadMsgs by viewModel.unreadMessagesCount.collectAsState()
    val isDark by viewModel.isDarkMode.collectAsState()
    var userMenuOpen by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)),
        color = if (isDark) SurfaceDark else SurfaceLight,
        tonalElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Brand logo & title
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { viewModel.navigateTo(AppScreen.HOME) }
                        .padding(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(listOf(GoldPrimary, PurpleSecondary))
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationCity,
                            contentDescription = "جرابلس اليوم",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "جرابلس اليوم",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = if (isDark) GoldLight else GoldDark
                        )
                        Text(
                            text = "المنصة الشاملة للمدينة",
                            fontSize = 10.sp,
                            color = if (isDark) TextSecondaryDark else TextSecondaryLight
                        )
                    }
                }

                // Action Icons
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Search
                    IconButton(
                        onClick = { viewModel.navigateTo(AppScreen.SEARCH) },
                        modifier = Modifier.testTag("nav_search_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "البحث",
                            tint = if (isDark) TextPrimaryDark else TextPrimaryLight
                        )
                    }

                    // Messages with Badge
                    BadgedBox(
                        badge = {
                            if (unreadMsgs > 0) {
                                Badge(containerColor = PurpleSecondary) {
                                    Text(unreadMsgs.toString(), color = Color.White)
                                }
                            }
                        }
                    ) {
                        IconButton(
                            onClick = { viewModel.navigateTo(AppScreen.MESSAGES) },
                            modifier = Modifier.testTag("nav_messages_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Chat,
                                contentDescription = "الرسائل",
                                tint = if (isDark) TextPrimaryDark else TextPrimaryLight
                            )
                        }
                    }

                    // Notifications with Badge
                    BadgedBox(
                        badge = {
                            if (unreadNotifs > 0) {
                                Badge(containerColor = GoldPrimary) {
                                    Text(unreadNotifs.toString(), color = Color.Black)
                                }
                            }
                        }
                    ) {
                        IconButton(
                            onClick = { viewModel.navigateTo(AppScreen.NOTIFICATIONS) },
                            modifier = Modifier.testTag("nav_notifications_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Notifications,
                                contentDescription = "الإشعارات",
                                tint = if (isDark) TextPrimaryDark else TextPrimaryLight
                            )
                        }
                    }

                    // Dark mode toggle
                    IconButton(onClick = { viewModel.toggleDarkMode() }) {
                        Icon(
                            imageVector = if (isDark) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "الوضع الليلي",
                            tint = if (isDark) GoldLight else PurpleSecondary
                        )
                    }

                    // User Profile / Role Switcher
                    Box {
                        IconButton(
                            onClick = { userMenuOpen = true },
                            modifier = Modifier.testTag("nav_user_menu_btn")
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when (currentUser.role) {
                                            UserRole.ADMIN -> StatusRejected
                                            UserRole.MERCHANT -> GoldPrimary
                                            UserRole.SERVICE_PROVIDER -> PurpleSecondary
                                            UserRole.USER -> Color(0xFF0284C7)
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = when (currentUser.role) {
                                        UserRole.ADMIN -> Icons.Default.AdminPanelSettings
                                        UserRole.MERCHANT -> Icons.Default.Storefront
                                        UserRole.SERVICE_PROVIDER -> Icons.Default.Build
                                        UserRole.USER -> Icons.Default.Person
                                    },
                                    contentDescription = currentUser.name,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = userMenuOpen,
                            onDismissRequest = { userMenuOpen = false }
                        ) {
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(currentUser.name, fontWeight = FontWeight.Bold)
                                        Text(currentUser.role.labelAr, fontSize = 11.sp, color = PurpleSecondary)
                                    }
                                },
                                onClick = {
                                    userMenuOpen = false
                                    viewModel.navigateTo(AppScreen.PROFILE)
                                },
                                leadingIcon = { Icon(Icons.Default.AccountCircle, null) }
                            )
                            HorizontalDivider()
                            // Role Dashboard Link
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        when (currentUser.role) {
                                            UserRole.ADMIN -> "لوحة تحكم المدير"
                                            UserRole.MERCHANT -> "لوحة تحكم التاجر"
                                            UserRole.SERVICE_PROVIDER -> "لوحة مقدم الخدمة"
                                            UserRole.USER -> "لوحة المستخدم"
                                        }
                                    )
                                },
                                onClick = {
                                    userMenuOpen = false
                                    viewModel.navigateTo(
                                        when (currentUser.role) {
                                            UserRole.ADMIN -> AppScreen.ADMIN_DASHBOARD
                                            UserRole.MERCHANT -> AppScreen.MERCHANT_DASHBOARD
                                            UserRole.SERVICE_PROVIDER -> AppScreen.SERVICE_DASHBOARD
                                            UserRole.USER -> AppScreen.USER_DASHBOARD
                                        }
                                    )
                                },
                                leadingIcon = { Icon(Icons.Default.Dashboard, null, tint = GoldDark) }
                            )
                            HorizontalDivider()
                            DropdownMenuItem(
                                text = { Text("تسجيل الدخول") },
                                onClick = {
                                    userMenuOpen = false
                                    viewModel.navigateTo(AppScreen.LOGIN)
                                },
                                leadingIcon = { Icon(Icons.Default.Login, null, tint = Color(0xFF0284C7)) }
                            )
                            DropdownMenuItem(
                                text = { Text("إنشاء حساب جديد") },
                                onClick = {
                                    userMenuOpen = false
                                    viewModel.navigateTo(AppScreen.REGISTER)
                                },
                                leadingIcon = { Icon(Icons.Default.PersonAdd, null, tint = Color(0xFF10B981)) }
                            )
                            if (currentUser.role == UserRole.MERCHANT) {
                                DropdownMenuItem(
                                    text = { Text("باقات اشتراك التاجر") },
                                    onClick = {
                                        userMenuOpen = false
                                        viewModel.navigateTo(AppScreen.MERCHANT_SUBSCRIPTION_PLANS)
                                    },
                                    leadingIcon = { Icon(Icons.Default.CardMembership, null, tint = GoldDark) }
                                )
                            }
                            if (currentUser.role == UserRole.SERVICE_PROVIDER) {
                                DropdownMenuItem(
                                    text = { Text("معرض أعمالي وواتساب") },
                                    onClick = {
                                        userMenuOpen = false
                                        viewModel.navigateTo(AppScreen.SERVICE_PROVIDER_PORTFOLIO)
                                    },
                                    leadingIcon = { Icon(Icons.Default.PhotoLibrary, null, tint = PurpleSecondary) }
                                )
                            }
                            HorizontalDivider()
                            Text(
                                "تبديل الحساب / الدور (للتجربة السريعة):",
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            DropdownMenuItem(
                                text = { Text("حساب المدير (Admin)") },
                                onClick = {
                                    userMenuOpen = false
                                    viewModel.switchRoleQuickly(UserRole.ADMIN)
                                },
                                leadingIcon = { Icon(Icons.Default.Shield, null, tint = StatusRejected) }
                            )
                            DropdownMenuItem(
                                text = { Text("حساب التاجر (Merchant)") },
                                onClick = {
                                    userMenuOpen = false
                                    viewModel.switchRoleQuickly(UserRole.MERCHANT)
                                },
                                leadingIcon = { Icon(Icons.Default.Store, null, tint = GoldDark) }
                            )
                            DropdownMenuItem(
                                text = { Text("مقدم خدمة (Service Provider)") },
                                onClick = {
                                    userMenuOpen = false
                                    viewModel.switchRoleQuickly(UserRole.SERVICE_PROVIDER)
                                },
                                leadingIcon = { Icon(Icons.Default.Handyman, null, tint = PurpleSecondary) }
                            )
                            DropdownMenuItem(
                                text = { Text("مستخدم عادي (User)") },
                                onClick = {
                                    userMenuOpen = false
                                    viewModel.switchRoleQuickly(UserRole.USER)
                                },
                                leadingIcon = { Icon(Icons.Default.PersonOutline, null) }
                            )
                        }
                    }
                }
            }

            // Subtle Gold Border Accent
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(
                        Brush.horizontalGradient(
                            listOf(PurpleSecondary, GoldPrimary, PurpleSecondary)
                        )
                    )
            )
        }
    }
}

@Composable
fun StatusBadge(status: ModerationStatus, rejectionReason: String = "") {
    val (bgColor, textColor, text, icon) = when (status) {
        ModerationStatus.PENDING -> Quadruple(
            Color(0xFFFEF3C7),
            Color(0xFF92400E),
            "قيد المراجعة",
            Icons.Default.Schedule
        )
        ModerationStatus.APPROVED -> Quadruple(
            Color(0xFFD1FAE5),
            Color(0xFF065F46),
            "تمت الموافقة",
            Icons.Default.CheckCircle
        )
        ModerationStatus.REJECTED -> Quadruple(
            Color(0xFFFEE2E2),
            Color(0xFF991B1B),
            "مرفوض",
            Icons.Default.Cancel
        )
    }

    Column {
        Surface(
            color = bgColor,
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = textColor,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = text,
                    color = textColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        if (status == ModerationStatus.REJECTED && rejectionReason.isNotBlank()) {
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "السبب: $rejectionReason",
                color = StatusRejected,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

@Composable
fun SectionHeader(
    title: String,
    icon: ImageVector,
    onViewMore: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(GoldContainerLight),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = GoldDark,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        if (onViewMore != null) {
            TextButton(
                onClick = onViewMore,
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "عرض الكل",
                    fontSize = 12.sp,
                    color = PurpleSecondary,
                    fontWeight = FontWeight.SemiBold
                )
                Icon(
                    imageVector = Icons.Default.ChevronLeft,
                    contentDescription = null,
                    tint = PurpleSecondary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun LikeCommentBar(
    likesCount: Int,
    commentsCount: Int,
    onLikeClick: () -> Unit,
    onCommentClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onLikeClick, modifier = Modifier.size(36.dp)) {
                Icon(
                    imageVector = Icons.Outlined.FavoriteBorder,
                    contentDescription = "إعجاب",
                    tint = Color(0xFFE11D48),
                    modifier = Modifier.size(18.dp)
                )
            }
            Text(
                text = "$likesCount إعجاب",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .clickable { onCommentClick() }
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.ChatBubbleOutline,
                contentDescription = "تعليق",
                tint = PurpleSecondary,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "$commentsCount تعليق",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun RejectionReasonDialog(
    isOpen: Boolean = true,
    title: String = "سبب رفض المحتوى",
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    if (!isOpen) return
    var reason by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(title, fontWeight = FontWeight.Bold)
        },
        text = {
            Column {
                Text(
                    "يرجى تحديد سبب الرفض ليتم إرساله في إشعار واضح لصاحب المنشور:",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = reason,
                    onValueChange = { reason = it },
                    label = { Text("سبب الرفض") },
                    placeholder = { Text("مثال: مخالف للشروط، معلومات ناقصة...") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(reason)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = StatusRejected)
            ) {
                Text("تأكيد الرفض والإشعار", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء")
            }
        }
    )
}

@Composable
fun FooterView(
    onNavigate: (AppScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = FooterDark
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(GoldPrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.LocationCity,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "منصة جرابلس اليوم",
                    color = GoldLight,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "المنصة الرقمية الموحدة للأخبار، الإعلانات، العقارات، السوق المحلي، الخدمات وفرص العمل في مدينة جرابلس وريفها.",
                color = Color(0xFF94A3B8),
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Quick navigation links
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                TextButton(onClick = { onNavigate(AppScreen.NEWS) }) {
                    Text("الأخبار", color = Color.White, fontSize = 12.sp)
                }
                TextButton(onClick = { onNavigate(AppScreen.PROPERTIES) }) {
                    Text("العقارات", color = Color.White, fontSize = 12.sp)
                }
                TextButton(onClick = { onNavigate(AppScreen.MARKET) }) {
                    Text("السوق", color = Color.White, fontSize = 12.sp)
                }
                TextButton(onClick = { onNavigate(AppScreen.SERVICES) }) {
                    Text("الخدمات", color = Color.White, fontSize = 12.sp)
                }
                TextButton(onClick = { onNavigate(AppScreen.JOBS) }) {
                    Text("الوظائف", color = Color.White, fontSize = 12.sp)
                }
            }

            HorizontalDivider(
                color = Color(0xFF1E293B),
                modifier = Modifier.padding(vertical = 12.dp)
            )

            Text(
                text = "© 2026 جرابلس اليوم - جميع الحقوق محفوظة",
                color = Color(0xFF64748B),
                fontSize = 11.sp
            )
        }
    }
}
