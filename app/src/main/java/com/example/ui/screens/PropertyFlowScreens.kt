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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PropertyType
import com.example.data.model.UserRole
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.JarablusViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePropertyScreen(
    viewModel: JarablusViewModel,
    isDark: Boolean,
    onNavigateBack: () -> Unit,
    onProceedToPayment: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val context = LocalContext.current
    val isAdmin = currentUser.role == UserRole.ADMIN

    var title by remember { mutableStateOf(viewModel.draftPropTitle) }
    var description by remember { mutableStateOf(viewModel.draftPropDesc) }
    var selectedType by remember { mutableStateOf(viewModel.draftPropType) }
    var price by remember { mutableStateOf(viewModel.draftPropPrice) }
    var currency by remember { mutableStateOf(viewModel.draftPropCurrency) }
    var district by remember { mutableStateOf(viewModel.draftPropDistrict) }
    var address by remember { mutableStateOf(viewModel.draftPropAddress) }
    var rooms by remember { mutableStateOf(viewModel.draftPropRooms) }
    var bathrooms by remember { mutableStateOf(viewModel.draftPropBathrooms) }
    var area by remember { mutableStateOf(viewModel.draftPropArea) }
    var buildingAge by remember { mutableStateOf(viewModel.draftPropBuildingAge) }
    var cladding by remember { mutableStateOf(viewModel.draftPropCladding) }

    val sampleImages = listOf(
        "https://images.unsplash.com/photo-1564013799919-ab600027ffc6?w=600",
        "https://images.unsplash.com/photo-1600596542815-ffad4c1539a9?w=600",
        "https://images.unsplash.com/photo-1600585154340-be6161a56a0c?w=600",
        "https://images.unsplash.com/photo-1600607687939-ce8a6c25118c?w=600"
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = if (isDark) BgDark else BgLight,
        topBar = {
            TopAppBar(
                title = { Text("إضافة عقار جديد", fontWeight = FontWeight.Bold) },
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
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(6.dp))

                // Monetization Notice Banner
                if (isAdmin) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF10B981).copy(alpha = 0.15f),
                        border = BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Verified, contentDescription = null, tint = Color(0xFF10B981))
                            Text(
                                text = "حساب الإدارة: يمكنك نشر العقار مباشرة ومجاناً بدون دفع، وسيظهر في قائمة العقارات فورياً.",
                                fontSize = 12.sp,
                                color = Color(0xFF10B981),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                } else {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFEA580C).copy(alpha = 0.12f),
                        border = BorderStroke(1.dp, Color(0xFFEA580C).copy(alpha = 0.35f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(Icons.Default.Payment, contentDescription = null, tint = Color(0xFFEA580C))
                            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Text(
                                    text = "نشر العقار مأجور (50 ليرة تركي لكل إعلان):",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFEA580C)
                                )
                                Text(
                                    text = "يظل الإعلان منشوراً لمدة 3 أيام من وقت موافقة الإدارة ثم يُزال أوتوماتيكياً. بعد تعبئة البيانات سيتم توجيهك لصفحة الدفع عبر شام كاش.",
                                    fontSize = 11.sp,
                                    color = if (isDark) TextSecondaryDark else TextSecondaryLight,
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }
                }
            }

            // Basic Information
            item {
                Text(
                    text = "بيانات العقار",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) TextPrimaryDark else TextPrimaryLight
                )
            }

            item {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("عنوان الإعلان (مثلاً: شقة مفروشة للإيجار)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Property Type selector
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PropertyType.values().forEach { pType ->
                        FilterChip(
                            selected = selectedType == pType,
                            onClick = { selectedType = pType },
                            label = { Text(pType.arabicName) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Price and Currency
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = price,
                        onValueChange = { price = it },
                        label = { Text("السعر") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1.5f)
                    )

                    OutlinedTextField(
                        value = currency,
                        onValueChange = { currency = it },
                        label = { Text("العملة (USD / TRY)") },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // District & Address
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = district,
                        onValueChange = { district = it },
                        label = { Text("الحي / المنطقة") },
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text("العنوان بالتفصيل") },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Area & Rooms & Bathrooms
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = area,
                        onValueChange = { area = it },
                        label = { Text("المساحة م²") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = rooms,
                        onValueChange = { rooms = it },
                        label = { Text("الغرف") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = bathrooms,
                        onValueChange = { bathrooms = it },
                        label = { Text("الحمامات") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Building Age & Cladding
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = buildingAge,
                        onValueChange = { buildingAge = it },
                        label = { Text("عمر البناء (سنة)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = cladding,
                        onValueChange = { cladding = it },
                        label = { Text("نوع الكسوة (ديلوكس، سوبر)") },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Description
            item {
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("الوصف الكامل والمميزات") },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Photos Selector (Multi-photo upload simulation)
            item {
                Text(
                    text = "صور العقار (تم اختيار ${viewModel.draftPropImages.size} صور):",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) TextPrimaryDark else TextPrimaryLight
                )
            }

            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(sampleImages) { url ->
                        val isPicked = viewModel.draftPropImages.contains(url)
                        Surface(
                            onClick = {
                                if (isPicked) {
                                    viewModel.draftPropImages.remove(url)
                                } else {
                                    viewModel.draftPropImages.add(url)
                                }
                            },
                            shape = RoundedCornerShape(10.dp),
                            color = if (isPicked) Color(0xFFEA580C).copy(alpha = 0.2f) else (if (isDark) SurfaceDark else SurfaceLight),
                            border = BorderStroke(1.dp, if (isPicked) Color(0xFFEA580C) else Color.Gray.copy(alpha = 0.4f)),
                            modifier = Modifier.size(70.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = if (isPicked) Icons.Default.CheckCircle else Icons.Default.AddPhotoAlternate,
                                        contentDescription = null,
                                        tint = if (isPicked) Color(0xFFEA580C) else Color.Gray,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Text(
                                        text = if (isPicked) "مختارة ✓" else "إضافة",
                                        fontSize = 10.sp,
                                        color = if (isPicked) Color(0xFFEA580C) else Color.Gray
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Proceed or Submit Button
            item {
                Spacer(modifier = Modifier.height(10.dp))

                // Save draft state
                viewModel.draftPropTitle = title
                viewModel.draftPropDesc = description
                viewModel.draftPropType = selectedType
                viewModel.draftPropPrice = price
                viewModel.draftPropCurrency = currency
                viewModel.draftPropDistrict = district
                viewModel.draftPropAddress = address
                viewModel.draftPropRooms = rooms
                viewModel.draftPropBathrooms = bathrooms
                viewModel.draftPropArea = area
                viewModel.draftPropBuildingAge = buildingAge
                viewModel.draftPropCladding = cladding

                if (isAdmin) {
                    Button(
                        onClick = {
                            if (title.isBlank()) {
                                Toast.makeText(context, "يرجى كتابة عنوان الإعلان", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            viewModel.submitProperty(
                                title = title,
                                description = description,
                                type = selectedType,
                                price = price.toDoubleOrNull() ?: 100.0,
                                currency = currency,
                                district = district,
                                address = address,
                                rooms = rooms.toIntOrNull() ?: 3,
                                bathrooms = bathrooms.toIntOrNull() ?: 1,
                                area = area.toDoubleOrNull() ?: 120.0,
                                buildingAge = buildingAge.toIntOrNull() ?: 1,
                                cladding = cladding
                            )
                            Toast.makeText(context, "تم نشر العقار فورياً كمدير بدون دفع!", Toast.LENGTH_SHORT).show()
                            viewModel.navigateTo(AppScreen.PROPERTIES)
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("admin_free_property_submit")
                    ) {
                        Icon(imageVector = Icons.Default.Publish, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("نشر العقار فورا (مجاني للمدير)", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                } else {
                    Button(
                        onClick = {
                            if (title.isBlank()) {
                                Toast.makeText(context, "يرجى كتابة عنوان الإعلان أولاً", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            onProceedToPayment()
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEA580C)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("user_proceed_to_payment_btn")
                    ) {
                        Icon(imageVector = Icons.Default.Payment, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("المتابعة إلى صفحة الدفع (50 ل.ت)", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PropertyPaymentScreen(
    viewModel: JarablusViewModel,
    isDark: Boolean,
    onNavigateBack: () -> Unit,
    onPaymentCompleted: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shamCashId by viewModel.shamCashCode.collectAsState()
    val context = LocalContext.current
    var receiptNumber by remember { mutableStateOf("SHAM-TRX-${(100000..999999).random()}") }
    var isUploadingReceipt by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = if (isDark) BgDark else BgLight,
        topBar = {
            TopAppBar(
                title = { Text("صفحة الدفع وإشعار شام كاش", fontWeight = FontWeight.Bold) },
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

                // Payment Overview Card
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFEA580C).copy(alpha = 0.12f)),
                    border = BorderStroke(1.dp, Color(0xFFEA580C).copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "رسوم نشر إعلان عقاري",
                            fontSize = 14.sp,
                            color = if (isDark) TextSecondaryDark else TextSecondaryLight
                        )

                        Text(
                            text = "50 ليرة تركي (TRY)",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFFEA580C)
                        )

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF10B981).copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "مدة النشر: 3 أيام بعد موافقة الإدارة",
                                color = Color(0xFF10B981),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            // Steps explanation
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
                            text = "خطوات التحويل والتفعيل:",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) TextPrimaryDark else TextPrimaryLight
                        )

                        Text(
                            text = "1. انسخ كود محفظة شام كاش الخاص بالإدارة أدناه.\n2. افتح تطبيق ShamCash وحوّل مبلغ 50 ليرة تركي.\n3. التقط صورة لإشعار التحويل أو انسخ رقم العملية.\n4. اضغط 'تأكيد وإرسال الإشعار للإدارة' ليتم التدقيق والتفعيل خلال 24 ساعة أقصاها.",
                            fontSize = 12.sp,
                            color = if (isDark) TextSecondaryDark else TextSecondaryLight,
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            // ShamCash Wallet ID Box with One-tap Copy
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
                            text = "كود حساب شام كاش المعتمد لإدارة جرابلس اليوم:",
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
                                Toast.makeText(context, "تم نسخ كود شام كاش!", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = GoldDark),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("نسخ كود شام كاش", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }

            // Receipt Upload Simulator Box
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
                            text = "إشعار تحويل المبلغ (صورة أو رقم المعاملة)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) TextPrimaryDark else TextPrimaryLight
                        )

                        OutlinedTextField(
                            value = receiptNumber,
                            onValueChange = { receiptNumber = it },
                            label = { Text("رقم إشعار أو مرجع التحويل") },
                            leadingIcon = { Icon(Icons.Default.ReceiptLong, contentDescription = null) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Surface(
                            onClick = {
                                isUploadingReceipt = true
                                receiptNumber = "SHAM-RECEIPT-${System.currentTimeMillis() % 1000000}"
                                Toast.makeText(context, "تم إرفاق صورة إشعار الدفع بنجاح ✓", Toast.LENGTH_SHORT).show()
                            },
                            shape = RoundedCornerShape(10.dp),
                            color = if (isDark) Color(0xFF1E293B) else Color(0xFFF8FAFC),
                            border = BorderStroke(1.dp, if (isUploadingReceipt) Color(0xFF10B981) else Color.Gray.copy(alpha = 0.4f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(90.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = if (isUploadingReceipt) Icons.Default.CheckCircle else Icons.Default.CloudUpload,
                                        contentDescription = null,
                                        tint = if (isUploadingReceipt) Color(0xFF10B981) else Color(0xFFEA580C),
                                        modifier = Modifier.size(30.dp)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = if (isUploadingReceipt) "تم التقاط ورفع صورة الإشعار بنجاح ✓" else "اضغط لرفع صورة إشعار التحويل من شام كاش",
                                        fontSize = 11.sp,
                                        color = if (isUploadingReceipt) Color(0xFF10B981) else (if (isDark) TextSecondaryDark else TextSecondaryLight),
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
                        viewModel.submitPropertyWithReceipt(receiptNumber) {
                            Toast.makeText(context, "تم إرسال العقار وإشعار الدفع! ستتم المراجعة والتفعيل خلال 24 ساعة أقصاها.", Toast.LENGTH_LONG).show()
                            onPaymentCompleted()
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("submit_property_receipt_btn")
                ) {
                    Icon(imageVector = Icons.Default.Send, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "إرسال الإشعار للإدارة وتأكيد النشر",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(26.dp))
            }
        }
    }
}
