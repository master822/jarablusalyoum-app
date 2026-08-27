package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserRole
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.JarablusViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: JarablusViewModel,
    isDark: Boolean,
    onNavigateRegister: () -> Unit,
    onNavigateForgotPassword: () -> Unit,
    onLoginSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    var identifier by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val authError by viewModel.authError.collectAsState()
    val authSuccess by viewModel.authSuccess.collectAsState()
    val context = LocalContext.current

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = if (isDark) BgDark else BgLight,
        topBar = {
            TopAppBar(
                title = { Text("تسجيل الدخول", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateTo(AppScreen.HOME) }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "الرئيسية")
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
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            item {
                Spacer(modifier = Modifier.height(20.dp))

                // App Brand Badge
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(22.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFF0284C7), Color(0xFF0369A1))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationCity,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(44.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "جرابلس اليوم",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isDark) TextPrimaryDark else TextPrimaryLight
                )

                Text(
                    text = "منصة المدينة الرقمية الموحدة للخدمات والتجارة والعقارات",
                    fontSize = 13.sp,
                    color = if (isDark) TextSecondaryDark else TextSecondaryLight,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(28.dp))

                // Error Feedback
                if (authError != null) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFDC2626).copy(alpha = 0.15f),
                        border = BorderStroke(1.dp, Color(0xFFDC2626).copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = authError ?: "",
                            color = Color(0xFFDC2626),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(12.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Input: Phone or Email
                OutlinedTextField(
                    value = identifier,
                    onValueChange = { identifier = it },
                    label = { Text("رقم الهاتف أو البريد الإلكتروني") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Phone, contentDescription = null)
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("login_input_identifier")
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Input: Password
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("كلمة المرور") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Lock, contentDescription = null)
                    },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = null
                            )
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("login_input_password")
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Forgot Password link
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onNavigateForgotPassword) {
                        Text(
                            text = "نسيت كلمة المرور؟",
                            fontSize = 13.sp,
                            color = Color(0xFF0284C7),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Login Button
                Button(
                    onClick = {
                        viewModel.login(identifier, password) {
                            Toast.makeText(context, "أهلاً بك مجدداً!", Toast.LENGTH_SHORT).show()
                            onLoginSuccess()
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("login_button_submit")
                ) {
                    Text(
                        text = "تسجيل الدخول",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Navigate to Register
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "ليس لديك حساب بعد؟",
                        fontSize = 13.sp,
                        color = if (isDark) TextSecondaryDark else TextSecondaryLight
                    )
                    TextButton(onClick = onNavigateRegister) {
                        Text(
                            text = "إنشاء حساب جديد",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0284C7)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Quick Demo Profiles Bar (For reviewer/testing convenience)
                HorizontalDivider(color = if (isDark) BorderDark else BorderLight)
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "أو الدخول السريع بحساب تجريبي:",
                    fontSize = 11.sp,
                    color = if (isDark) TextSecondaryDark else TextSecondaryLight
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    AssistChip(
                        onClick = {
                            viewModel.switchRoleQuickly(UserRole.ADMIN)
                            onLoginSuccess()
                        },
                        label = { Text("المدير", fontSize = 11.sp) },
                        leadingIcon = { Icon(Icons.Default.AdminPanelSettings, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color(0xFFDC2626)) },
                        modifier = Modifier.weight(1f)
                    )

                    AssistChip(
                        onClick = {
                            viewModel.switchRoleQuickly(UserRole.MERCHANT)
                            onLoginSuccess()
                        },
                        label = { Text("تاجر", fontSize = 11.sp) },
                        leadingIcon = { Icon(Icons.Default.Store, contentDescription = null, modifier = Modifier.size(14.dp), tint = GoldDark) },
                        modifier = Modifier.weight(1f)
                    )

                    AssistChip(
                        onClick = {
                            viewModel.switchRoleQuickly(UserRole.SERVICE_PROVIDER)
                            onLoginSuccess()
                        },
                        label = { Text("مقدم خدمة", fontSize = 11.sp) },
                        leadingIcon = { Icon(Icons.Default.Handyman, contentDescription = null, modifier = Modifier.size(14.dp), tint = PurpleSecondary) },
                        modifier = Modifier.weight(1f)
                    )

                    AssistChip(
                        onClick = {
                            viewModel.switchRoleQuickly(UserRole.USER)
                            onLoginSuccess()
                        },
                        label = { Text("مواطن", fontSize = 11.sp) },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color(0xFF0284C7)) },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    viewModel: JarablusViewModel,
    isDark: Boolean,
    onNavigateLogin: () -> Unit,
    onRegisterSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedRole by remember { mutableStateOf(UserRole.USER) }

    // Common fields
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("جرابلس") }

    // Merchant specific fields
    var storeName by remember { mutableStateOf("") }
    var storeDesc by remember { mutableStateOf("") }

    // Service provider specific fields
    var serviceCategory by remember { mutableStateOf("صيانة وخدمات عامة") }
    var serviceDesc by remember { mutableStateOf("") }
    var whatsappNumber by remember { mutableStateOf("") }

    val authError by viewModel.authError.collectAsState()
    val context = LocalContext.current

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = if (isDark) BgDark else BgLight,
        topBar = {
            TopAppBar(
                title = { Text("إنشاء حساب جديد", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateLogin) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "الرجوع")
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
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "اختر نوع الحساب للانضمام إلى منصة جرابلس اليوم:",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) TextPrimaryDark else TextPrimaryLight
                )
            }

            // Role Selector Tabs (User / Merchant / Service Provider)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    RoleSelectionCard(
                        title = "مستخدم عادي",
                        subtitle = "نشر قطع مستعملة وتصفح",
                        icon = Icons.Default.Person,
                        isSelected = selectedRole == UserRole.USER,
                        accentColor = Color(0xFF0284C7),
                        isDark = isDark,
                        modifier = Modifier.weight(1f),
                        onClick = { selectedRole = UserRole.USER }
                    )

                    RoleSelectionCard(
                        title = "تاجر",
                        subtitle = "متجر وباقات اشتراك",
                        icon = Icons.Default.Store,
                        isSelected = selectedRole == UserRole.MERCHANT,
                        accentColor = GoldDark,
                        isDark = isDark,
                        modifier = Modifier.weight(1f),
                        onClick = { selectedRole = UserRole.MERCHANT }
                    )

                    RoleSelectionCard(
                        title = "مقدم خدمات",
                        subtitle = "واتساب ومعرض 50 صورة",
                        icon = Icons.Default.Handyman,
                        isSelected = selectedRole == UserRole.SERVICE_PROVIDER,
                        accentColor = PurpleSecondary,
                        isDark = isDark,
                        modifier = Modifier.weight(1f),
                        onClick = { selectedRole = UserRole.SERVICE_PROVIDER }
                    )
                }
            }

            // Error display
            if (authError != null) {
                item {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFDC2626).copy(alpha = 0.15f),
                        border = BorderStroke(1.dp, Color(0xFFDC2626).copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = authError ?: "",
                            color = Color(0xFFDC2626),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }

            // Role Specific Highlights & Notices
            item {
                when (selectedRole) {
                    UserRole.USER -> {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFF0284C7).copy(alpha = 0.1f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Default.ShoppingBag, contentDescription = null, tint = Color(0xFF0284C7))
                                Text(
                                    text = "كمستخدم عادي، يمكنك تصفح كافة الخدمات ونشر قطع مستعملة للبيع في سوق المستعمل مجاناً.",
                                    fontSize = 12.sp,
                                    color = Color(0xFF0284C7)
                                )
                            }
                        }
                    }
                    UserRole.MERCHANT -> {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = GoldDark.copy(alpha = 0.12f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = "باقات الاشتراك الشهرية للتجار المعتمدين:",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = GoldDark
                                )
                                Text(
                                    text = "• العادية: 10$ شهرياً (30 منتج)\n• المتوسطة: 20$ شهرياً (80 منتج)\n• الذهبية: 35$ شهرياً (عدد مفتوح من المنتجات)\nيتم الدفع وتفعيل الباقة عبر محفظة شام كاش.",
                                    fontSize = 11.sp,
                                    color = if (isDark) TextPrimaryDark else TextPrimaryLight,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                    UserRole.SERVICE_PROVIDER -> {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = PurpleSecondary.copy(alpha = 0.12f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Default.PhotoLibrary, contentDescription = null, tint = PurpleSecondary)
                                Text(
                                    text = "كمقدم خدمات، يمكنك إضافة رقم الواتساب لتواصل الزبائن مباشرة، ومعرض أعمال يتسع حتى 50 صورة لأعمالك وإنجازاتك.",
                                    fontSize = 12.sp,
                                    color = PurpleSecondary
                                )
                            }
                        }
                    }
                    else -> {}
                }
            }

            // Common Information Fields
            item {
                Text(
                    text = "البيانات الشخصية",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) TextPrimaryDark else TextPrimaryLight
                )
            }

            item {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("الاسم الكامل") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("رقم الهاتف (الأساسي)") },
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("البريد الإلكتروني (اختياري)") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("كلمة المرور") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                OutlinedTextField(
                    value = city,
                    onValueChange = { city = it },
                    label = { Text("المدينة / المنطقة") },
                    leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Merchant Specific Fields
            if (selectedRole == UserRole.MERCHANT) {
                item {
                    Text(
                        text = "بيانات المتجر والنشاط التجاري",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldDark
                    )
                }

                item {
                    OutlinedTextField(
                        value = storeName,
                        onValueChange = { storeName = it },
                        label = { Text("اسم المتجر أو المعرض *") },
                        leadingIcon = { Icon(Icons.Default.Storefront, contentDescription = null, tint = GoldDark) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        value = storeDesc,
                        onValueChange = { storeDesc = it },
                        label = { Text("وصف المتجر والمنتجات المتوفرة") },
                        minLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Service Provider Specific Fields
            if (selectedRole == UserRole.SERVICE_PROVIDER) {
                item {
                    Text(
                        text = "بيانات الخدمة والتواصل (واتساب)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = PurpleSecondary
                    )
                }

                item {
                    OutlinedTextField(
                        value = whatsappNumber,
                        onValueChange = { whatsappNumber = it },
                        label = { Text("رقم الواتساب للتواصل المباشر *") },
                        leadingIcon = { Icon(Icons.Default.Chat, contentDescription = null, tint = Color(0xFF25D366)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        value = serviceCategory,
                        onValueChange = { serviceCategory = it },
                        label = { Text("تصنيف أو مجال الخدمة (مثلاً: سباكة، نجارة، صيانة كهرباء...)") },
                        leadingIcon = { Icon(Icons.Default.Category, contentDescription = null) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        value = serviceDesc,
                        onValueChange = { serviceDesc = it },
                        label = { Text("وصف مفصل لخدمتك وخبرتك") },
                        minLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Submit Button
            item {
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = {
                        viewModel.register(
                            name = name,
                            phone = phone,
                            email = email,
                            pass = password,
                            role = selectedRole,
                            city = city,
                            storeName = storeName,
                            storeDesc = storeDesc,
                            serviceCategory = serviceCategory,
                            whatsapp = whatsappNumber
                        ) {
                            Toast.makeText(context, "تم التسجيل بنجاح! مرحباً بك في جرابلس اليوم", Toast.LENGTH_SHORT).show()
                            onRegisterSuccess()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = when (selectedRole) {
                            UserRole.MERCHANT -> GoldDark
                            UserRole.SERVICE_PROVIDER -> PurpleSecondary
                            else -> Color(0xFF0284C7)
                        }
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("register_button_submit")
                ) {
                    Text(
                        text = "إتمام التسجيل والبدء",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "لديك حساب بالفعل؟", fontSize = 13.sp, color = if (isDark) TextSecondaryDark else TextSecondaryLight)
                    TextButton(onClick = onNavigateLogin) {
                        Text(text = "تسجيل الدخول", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0284C7))
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}

@Composable
fun RoleSelectionCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    accentColor: Color,
    isDark: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) accentColor.copy(alpha = 0.15f) else (if (isDark) SurfaceDark else SurfaceLight)
        ),
        border = BorderStroke(
            if (isSelected) 2.dp else 1.dp,
            if (isSelected) accentColor else (if (isDark) BorderDark else BorderLight)
        ),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) accentColor else accentColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isSelected) Color.White else accentColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) accentColor else (if (isDark) TextPrimaryDark else TextPrimaryLight),
                textAlign = TextAlign.Center
            )

            Text(
                text = subtitle,
                fontSize = 9.sp,
                color = if (isDark) TextSecondaryDark else TextSecondaryLight,
                textAlign = TextAlign.Center,
                lineHeight = 12.sp
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(
    viewModel: JarablusViewModel,
    isDark: Boolean,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var identifier by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    val authError by viewModel.authError.collectAsState()
    val authSuccess by viewModel.authSuccess.collectAsState()
    val context = LocalContext.current

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = if (isDark) BgDark else BgLight,
        topBar = {
            TopAppBar(
                title = { Text("استعادة كلمة المرور", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "الرجوع")
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
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.LockReset,
                contentDescription = null,
                tint = Color(0xFF0284C7),
                modifier = Modifier.size(64.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "إعادة تعيين كلمة المرور",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDark) TextPrimaryDark else TextPrimaryLight
            )

            Text(
                text = "أدخل رقم الهاتف أو البريد المسجل مسبقاً ثم اكتب كلمة المرور الجديدة لحسابك",
                fontSize = 13.sp,
                color = if (isDark) TextSecondaryDark else TextSecondaryLight,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            if (authError != null) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFDC2626).copy(alpha = 0.15f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = authError ?: "",
                        color = Color(0xFFDC2626),
                        fontSize = 12.sp,
                        modifier = Modifier.padding(10.dp),
                        textAlign = TextAlign.Center
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            if (authSuccess != null) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFF10B981).copy(alpha = 0.15f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = authSuccess ?: "",
                        color = Color(0xFF10B981),
                        fontSize = 12.sp,
                        modifier = Modifier.padding(10.dp),
                        textAlign = TextAlign.Center
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            OutlinedTextField(
                value = identifier,
                onValueChange = { identifier = it },
                label = { Text("رقم الهاتف أو البريد المسجل") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = newPassword,
                onValueChange = { newPassword = it },
                label = { Text("كلمة المرور الجديدة") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("تأكيد كلمة المرور الجديدة") },
                leadingIcon = { Icon(Icons.Default.Check, contentDescription = null) },
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    if (newPassword != confirmPassword) {
                        Toast.makeText(context, "كلمتا المرور غير متطابقتين", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    viewModel.resetPassword(identifier, newPassword) {
                        Toast.makeText(context, "تمت إعادة التعيين بنجاح!", Toast.LENGTH_SHORT).show()
                        onNavigateBack()
                    }
                },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(text = "حفظ كلمة المرور الجديدة", fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
