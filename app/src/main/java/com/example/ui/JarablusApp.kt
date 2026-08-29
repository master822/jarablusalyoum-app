package com.example.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.example.data.model.UserRole
import com.example.ui.components.JarablusTopBar
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.JarablusViewModel

@Composable
fun JarablusApp(
    viewModel: JarablusViewModel,
    modifier: Modifier = Modifier
) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    val isFullScreen = currentScreen in setOf(
        AppScreen.LOGIN,
        AppScreen.REGISTER,
        AppScreen.FORGOT_PASSWORD,
        AppScreen.CREATE_PROPERTY,
        AppScreen.PROPERTY_PAYMENT,
        AppScreen.MERCHANT_SUBSCRIPTION_PLANS,
        AppScreen.SUBSCRIPTION_PAYMENT,
        AppScreen.SERVICE_PROVIDER_PORTFOLIO,
        AppScreen.CREATE_NEWS_OR_AD,
        AppScreen.ADMIN_DASHBOARD
    )

    JarablusTodayTheme(darkTheme = isDarkMode) {
        // Enforce Arabic RTL Layout
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            if (isFullScreen) {
                Box(modifier = Modifier.fillMaxSize()) {
                    when (currentScreen) {
                        AppScreen.LOGIN -> LoginScreen(
                            viewModel = viewModel,
                            isDark = isDarkMode,
                            onNavigateRegister = { viewModel.navigateTo(AppScreen.REGISTER) },
                            onNavigateForgotPassword = { viewModel.navigateTo(AppScreen.FORGOT_PASSWORD) },
                            onLoginSuccess = { viewModel.navigateTo(AppScreen.HOME) }
                        )
                        AppScreen.REGISTER -> RegisterScreen(
                            viewModel = viewModel,
                            isDark = isDarkMode,
                            onNavigateLogin = { viewModel.navigateTo(AppScreen.LOGIN) },
                            onRegisterSuccess = { viewModel.navigateTo(AppScreen.HOME) }
                        )
                        AppScreen.FORGOT_PASSWORD -> ForgotPasswordScreen(
                            viewModel = viewModel,
                            isDark = isDarkMode,
                            onNavigateBack = { viewModel.navigateTo(AppScreen.LOGIN) }
                        )
                        AppScreen.CREATE_PROPERTY -> CreatePropertyScreen(
                            viewModel = viewModel,
                            isDark = isDarkMode,
                            onNavigateBack = { viewModel.navigateTo(AppScreen.PROPERTIES) },
                            onProceedToPayment = { viewModel.navigateTo(AppScreen.PROPERTY_PAYMENT) }
                        )
                        AppScreen.PROPERTY_PAYMENT -> PropertyPaymentScreen(
                            viewModel = viewModel,
                            isDark = isDarkMode,
                            onNavigateBack = { viewModel.navigateTo(AppScreen.CREATE_PROPERTY) },
                            onPaymentCompleted = { viewModel.navigateTo(AppScreen.PROPERTIES) }
                        )
                        AppScreen.MERCHANT_SUBSCRIPTION_PLANS -> MerchantSubscriptionPlansScreen(
                            viewModel = viewModel,
                            isDark = isDarkMode,
                            onNavigateBack = { viewModel.navigateTo(AppScreen.MERCHANT_DASHBOARD) },
                            onSelectPlan = { viewModel.navigateTo(AppScreen.SUBSCRIPTION_PAYMENT) }
                        )
                        AppScreen.SUBSCRIPTION_PAYMENT -> SubscriptionPaymentScreen(
                            viewModel = viewModel,
                            isDark = isDarkMode,
                            onNavigateBack = { viewModel.navigateTo(AppScreen.MERCHANT_SUBSCRIPTION_PLANS) },
                            onPaymentCompleted = { viewModel.navigateTo(AppScreen.MERCHANT_DASHBOARD) }
                        )
                        AppScreen.SERVICE_PROVIDER_PORTFOLIO -> ServiceProviderPortfolioScreen(
                            viewModel = viewModel,
                            isDark = isDarkMode,
                            onNavigateBack = { viewModel.navigateTo(AppScreen.SERVICE_DASHBOARD) }
                        )
                        AppScreen.CREATE_NEWS_OR_AD -> CreateNewsOrAdScreen(
                            viewModel = viewModel,
                            isDark = isDarkMode,
                            onNavigateBack = { viewModel.navigateTo(AppScreen.HOME) },
                            onNavigateProperty = { viewModel.navigateTo(AppScreen.CREATE_PROPERTY) }
                        )
                        AppScreen.ADMIN_DASHBOARD -> AdminDashboardScreen(
                            viewModel = viewModel,
                            isDark = isDarkMode,
                            onNavigateBack = { viewModel.navigateTo(AppScreen.HOME) }
                        )
                        else -> HomeScreen(viewModel = viewModel)
                    }
                }
            } else {
                Scaffold(
                    topBar = {
                        JarablusTopBar(viewModel = viewModel)
                    },
                    bottomBar = {
                        NavigationBar(
                            containerColor = MaterialTheme.colorScheme.surface,
                            tonalElevation = 4.dp
                        ) {
                        NavigationBarItem(
                            selected = currentScreen == AppScreen.HOME,
                            onClick = { viewModel.navigateTo(AppScreen.HOME) },
                            icon = { Icon(Icons.Default.Home, contentDescription = "الرئيسية") },
                            label = { Text("الرئيسية") },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = GoldDark,
                                selectedTextColor = GoldDark,
                                indicatorColor = GoldContainerLight
                            ),
                            modifier = Modifier.testTag("nav_home")
                        )
                        NavigationBarItem(
                            selected = currentScreen == AppScreen.NEWS || currentScreen == AppScreen.NEWS_DETAILS,
                            onClick = { viewModel.navigateTo(AppScreen.NEWS) },
                            icon = { Icon(Icons.Default.Article, contentDescription = "الأخبار") },
                            label = { Text("الأخبار") },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = PurpleSecondary,
                                selectedTextColor = PurpleSecondary,
                                indicatorColor = PurpleContainerLight
                            ),
                            modifier = Modifier.testTag("nav_news")
                        )
                        NavigationBarItem(
                            selected = currentScreen == AppScreen.PROPERTIES || currentScreen == AppScreen.PROPERTY_DETAILS,
                            onClick = { viewModel.navigateTo(AppScreen.PROPERTIES) },
                            icon = { Icon(Icons.Default.Apartment, contentDescription = "العقارات") },
                            label = { Text("العقارات") },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color(0xFF0D9488),
                                selectedTextColor = Color(0xFF0D9488),
                                indicatorColor = Color(0xFFCCFBF1)
                            ),
                            modifier = Modifier.testTag("nav_properties")
                        )
                        NavigationBarItem(
                            selected = currentScreen == AppScreen.MARKET,
                            onClick = { viewModel.navigateTo(AppScreen.MARKET) },
                            icon = { Icon(Icons.Default.Storefront, contentDescription = "السوق") },
                            label = { Text("السوق") },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color(0xFFEA580C),
                                selectedTextColor = Color(0xFFEA580C),
                                indicatorColor = Color(0xFFFFEDD5)
                            ),
                            modifier = Modifier.testTag("nav_market")
                        )
                        NavigationBarItem(
                            selected = currentScreen == AppScreen.ADMIN_DASHBOARD ||
                                    currentScreen == AppScreen.MERCHANT_DASHBOARD ||
                                    currentScreen == AppScreen.SERVICE_DASHBOARD ||
                                    currentScreen == AppScreen.USER_DASHBOARD,
                            onClick = {
                                viewModel.navigateTo(
                                    when (currentUser.role) {
                                        UserRole.ADMIN -> AppScreen.ADMIN_DASHBOARD
                                        UserRole.MERCHANT -> AppScreen.MERCHANT_DASHBOARD
                                        UserRole.SERVICE_PROVIDER -> AppScreen.SERVICE_DASHBOARD
                                        UserRole.USER -> AppScreen.USER_DASHBOARD
                                    }
                                )
                            },
                            icon = {
                                Icon(
                                    imageVector = when (currentUser.role) {
                                        UserRole.ADMIN -> Icons.Default.AdminPanelSettings
                                        UserRole.MERCHANT -> Icons.Default.Dashboard
                                        UserRole.SERVICE_PROVIDER -> Icons.Default.Build
                                        UserRole.USER -> Icons.Default.AccountCircle
                                    },
                                    contentDescription = "لوحتي"
                                )
                            },
                            label = {
                                Text(
                                    when (currentUser.role) {
                                        UserRole.ADMIN -> "لوحة الإدارة"
                                        UserRole.MERCHANT -> "لوحة المتجر"
                                        UserRole.SERVICE_PROVIDER -> "لوحة الخدمة"
                                        UserRole.USER -> "لوحتي"
                                    }
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = GoldDark,
                                selectedTextColor = GoldDark,
                                indicatorColor = GoldContainerLight
                            ),
                            modifier = Modifier.testTag("nav_dashboard")
                        )
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        when (currentScreen) {
                            AppScreen.HOME -> HomeScreen(viewModel = viewModel)
                            AppScreen.NEWS -> NewsScreen(viewModel = viewModel)
                            AppScreen.NEWS_DETAILS -> NewsDetailsScreen(viewModel = viewModel)
                            AppScreen.ANNOUNCEMENTS -> AnnouncementsScreen(viewModel = viewModel)
                            AppScreen.PROPERTIES -> PropertiesScreen(viewModel = viewModel)
                            AppScreen.PROPERTY_DETAILS -> PropertyDetailsScreen(viewModel = viewModel)
                            AppScreen.MARKET -> MarketScreen(viewModel = viewModel)
                            AppScreen.MERCHANTS -> MerchantsScreen(viewModel = viewModel)
                            AppScreen.SERVICES -> ServicesAndJobsScreen(viewModel = viewModel, initialTab = 0)
                            AppScreen.JOBS -> ServicesAndJobsScreen(viewModel = viewModel, initialTab = 1)
                            AppScreen.DISCOUNTS -> MarketScreen(viewModel = viewModel)
                            AppScreen.MESSAGES -> MessagesScreen(viewModel = viewModel)
                            AppScreen.NOTIFICATIONS -> NotificationsScreen(viewModel = viewModel)
                            AppScreen.MERCHANT_DASHBOARD -> DashboardsScreen(viewModel = viewModel, dashboardType = AppScreen.MERCHANT_DASHBOARD)
                            AppScreen.SERVICE_DASHBOARD -> DashboardsScreen(viewModel = viewModel, dashboardType = AppScreen.SERVICE_DASHBOARD)
                            AppScreen.USER_DASHBOARD -> DashboardsScreen(viewModel = viewModel, dashboardType = AppScreen.USER_DASHBOARD)
                            AppScreen.SEARCH -> SearchScreen(viewModel = viewModel)
                            AppScreen.PROFILE -> ProfileScreen(viewModel = viewModel)
                            else -> HomeScreen(viewModel = viewModel)
                        }
                    }
                }
            }
        }
    }
}
