package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.JarablusViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MerchantSubscriptionPlansScreen(
    viewModel: JarablusViewModel,
    isDark: Boolean,
    onNavigateBack: () -> Unit,
    onSelectPlan: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = if (isDark) BgDark else BgLight,
        topBar = {
            TopAppBar(
                title = { Text("باقات اشتراك التجار المعتمدة", fontWeight = FontWeight.Bold) },
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Spacer(modifier = Modifier.height(10.dp))
                // Hero Banner
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(GoldDark, Color(0xFFB45309), Color(0xFF78350F))
                            )
                        )
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "رقّ متجرك وانطلق في سوق جرابلس",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "اختر الباقة المناسبة لحجم أعمالك لعرض المزيد من المنتجات والوصول إلى آلاف المشترين يومياً. الدفع سهل وفوري عبر شام كاش.",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            // Plan 1: Regular ($10/mo, 30 prods)
            item {
                SubscriptionTierCard(
                    title = "الباقة العادية",
                    price = "10$ / شهرياً",
                    productsCount = "30 منتج",
                    features = listOf(
                        "عرض حتى 30 منتج في المتجر",
                        "شارة تاجر موثق في جرابلس",
                        "تنبيهات فورية عند وصول طلبات جديدة",
                        "دعم فني عبر الواتساب"
                    ),
                    isFeatured = false,
                    accentColor = Color(0xFF0284C7),
                    isDark = isDark,
                    onChoose = {
                        viewModel.selectSubscription("الباقة العادية", 10.0, 30)
                        onSelectPlan()
                    }
                )
            }

            // Plan 2: Medium ($20/mo, 80 prods)
            item {
                SubscriptionTierCard(
                    title = "الباقة المتوسطة (الأكثر طلباً)",
                    price = "20$ / شهرياً",
                    productsCount = "80 منتج",
                    features = listOf(
                        "عرض حتى 80 منتج في المتجر",
                        "شارة تاجر مميز بلون ذهبي",
                        "أولوية الظهور في نتائج البحث وصفحة السوق",
                        "إمكانية نشر عروض وتخفيضات أسبوعية",
                        "دعم فني مخصص"
                    ),
                    isFeatured = true,
                    accentColor = GoldDark,
                    isDark = isDark,
                    onChoose = {
                        viewModel.selectSubscription("الباقة المتوسطة", 20.0, 80)
                        onSelectPlan()
                    }
                )
            }

            // Plan 3: Gold VIP ($35/mo, Unlimited)
            item {
                SubscriptionTierCard(
                    title = "الباقة الذهبية (VIP)",
                    price = "35$ / شهرياً",
                    productsCount = "منتجات غير محدودة (مفتوح)",
                    features = listOf(
                        "عرض عدد مفتوح وغير محدود من المنتجات",
                        "أعلى أولوية ظهور في المنصة بالكامل",
                        "تثبيت متجرك في مقدمة دليل التجار",
                        "نشر غير محدود للتخفيضات والخصومات",
                        "دعم فني على مدار الساعة وخدمة VIP"
                    ),
                    isFeatured = false,
                    accentColor = Color(0xFF7C3AED),
                    isDark = isDark,
                    onChoose = {
                        viewModel.selectSubscription("الباقة الذهبية", 35.0, 99999)
                        onSelectPlan()
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}

@Composable
fun SubscriptionTierCard(
    title: String,
    price: String,
    productsCount: String,
    features: List<String>,
    isFeatured: Boolean,
    accentColor: Color,
    isDark: Boolean,
    onChoose: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = if (isDark) SurfaceDark else SurfaceLight),
        border = BorderStroke(
            if (isFeatured) 2.dp else 1.dp,
            if (isFeatured) accentColor else (if (isDark) BorderDark else BorderLight)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) TextPrimaryDark else TextPrimaryLight
                )

                if (isFeatured) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = accentColor
                    ) {
                        Text(
                            text = "الأفضل قيمة",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = price,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = accentColor
                )
                Text(
                    text = "• $productsCount",
                    fontSize = 13.sp,
                    color = if (isDark) TextSecondaryDark else TextSecondaryLight,
                    modifier = Modifier.padding(bottom = 3.dp)
                )
            }

            HorizontalDivider(color = if (isDark) BorderDark else BorderLight)

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                features.forEach { feat ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color(0xFF10B981),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = feat,
                            fontSize = 12.sp,
                            color = if (isDark) TextSecondaryDark else TextSecondaryLight
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Button(
                onClick = onChoose,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text(
                    text = "اختيار هذه الباقة والمتابعة للدفع",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionPaymentScreen(
    viewModel: JarablusViewModel,
    isDark: Boolean,
    onNavigateBack: () -> Unit,
    onPaymentCompleted: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shamCashId by viewModel.shamCashCode.collectAsState()
    val context = LocalContext.current
    var receiptNumber by remember { mutableStateOf("SHAM-SUB-${(100000..999999).random()}") }
    var isUploaded by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = if (isDark) BgDark else BgLight,
        topBar = {
            TopAppBar(
                title = { Text("دفع اشتراك التاجر عبر شام كاش", fontWeight = FontWeight.Bold) },
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
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Spacer(modifier = Modifier.height(10.dp))

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = GoldDark.copy(alpha = 0.12f)),
                    border = BorderStroke(1.dp, GoldDark.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "الباقة المختارة: ${viewModel.selectedPlanTitle}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) TextPrimaryDark else TextPrimaryLight
                        )

                        Text(
                            text = "${viewModel.selectedPlanPrice.toInt()}$ دولار أمريكي",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = GoldDark
                        )

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF10B981).copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "السعة: ${if (viewModel.selectedPlanLimit > 1000) "عدد مفتوح من المنتجات" else "${viewModel.selectedPlanLimit} منتج"}",
                                color = Color(0xFF10B981),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            // ShamCash Account Card
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = if (isDark) SurfaceDark else SurfaceLight),
                    border = BorderStroke(1.dp, GoldDark.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "معرف حساب شام كاش المعتمد لإدارة جرابلس اليوم:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldDark
                        )

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isDark) Color(0xFF1E293B) else Color(0xFFF1F5F9),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = shamCashId,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) TextPrimaryDark else TextPrimaryLight,
                                modifier = Modifier.padding(10.dp),
                                textAlign = TextAlign.Center
                            )
                        }

                        Button(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                clipboard.setPrimaryClip(ClipData.newPlainText("ShamCash Wallet", shamCashId))
                                Toast.makeText(context, "تم نسخ معرف شام كاش!", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = GoldDark),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("نسخ كود شام كاش للتحويل", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }

            // Upload Receipt Card
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = if (isDark) SurfaceDark else SurfaceLight),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "إشعار تحويل قيمة الاشتراك",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) TextPrimaryDark else TextPrimaryLight
                        )

                        OutlinedTextField(
                            value = receiptNumber,
                            onValueChange = { receiptNumber = it },
                            label = { Text("رقم العملية أو مرجع التحويل") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Surface(
                            onClick = {
                                isUploaded = true
                                receiptNumber = "SUB-SHAM-${System.currentTimeMillis() % 1000000}"
                                Toast.makeText(context, "تم إرفاق صورة الإشعار بنجاح ✓", Toast.LENGTH_SHORT).show()
                            },
                            shape = RoundedCornerShape(10.dp),
                            color = if (isDark) Color(0xFF1E293B) else Color(0xFFF8FAFC),
                            border = BorderStroke(1.dp, if (isUploaded) Color(0xFF10B981) else Color.Gray.copy(alpha = 0.4f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(90.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = if (isUploaded) Icons.Default.CheckCircle else Icons.Default.CloudUpload,
                                        contentDescription = null,
                                        tint = if (isUploaded) Color(0xFF10B981) else GoldDark,
                                        modifier = Modifier.size(30.dp)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = if (isUploaded) "تم إرفاق صورة إشعار التحويل بنجاح ✓" else "اضغط لرفع صورة إشعار التحويل من شام كاش",
                                        fontSize = 11.sp,
                                        color = if (isUploaded) Color(0xFF10B981) else (if (isDark) TextSecondaryDark else TextSecondaryLight),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Submit Button
            item {
                Spacer(modifier = Modifier.height(6.dp))

                Button(
                    onClick = {
                        viewModel.submitSubscriptionPayment(receiptNumber) {
                            Toast.makeText(context, "تم إرسال طلب الاشتراك للإدارة! سيتم تفعيل باقتك خلال مدة أقصاها 24 ساعة.", Toast.LENGTH_LONG).show()
                            onPaymentCompleted()
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    Icon(imageVector = Icons.Default.Send, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("إرسال إشعار الدفع وتأكيد الاشتراك", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(26.dp))
            }
        }
    }
}
