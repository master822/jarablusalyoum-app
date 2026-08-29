package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.JarablusViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketScreen(
    viewModel: JarablusViewModel,
    modifier: Modifier = Modifier
) {
    val allProducts by viewModel.allProducts.collectAsState()
    val usedProducts by viewModel.usedProducts.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    var selectedTab by remember { mutableStateOf(0) } // 0: الكل, 1: منتجات المتاجر, 2: سوق المستعمل
    var showAddDialog by remember { mutableStateOf(false) }

    val displayedProducts = when (selectedTab) {
        1 -> allProducts.filter { !it.isUsed }
        2 -> usedProducts
        else -> allProducts
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0.dp),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = PurpleSecondary,
                contentColor = Color.White,
                modifier = Modifier.testTag("add_product_fab")
            ) {
                Row(modifier = Modifier.padding(horizontal = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AddShoppingCart, contentDescription = "إضافة منتج")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("إضافة منتج", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("سوق جرابلس", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text("منتجات المتاجر المحلية وسوق المستعمل", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            // Tabs
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = PurpleSecondary
            ) {
                Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("جميع المنتجات") })
                Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("المتاجر المحلية") })
                Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }, text = { Text("سوق المستعمل") })
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Merchant / User Action Banner
            if (currentUser.role == UserRole.MERCHANT) {
                Surface(
                    onClick = { viewModel.navigateTo(AppScreen.MERCHANT_SUBSCRIPTION_PLANS) },
                    shape = RoundedCornerShape(10.dp),
                    color = GoldDark.copy(alpha = 0.12f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GoldDark.copy(alpha = 0.4f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.WorkspacePremium, contentDescription = null, tint = GoldDark)
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "ترقية باقة متجرك: العادية (10$)، المتوسطة (20$)، الذهبية (35$)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldDark
                            )
                            Text(
                                text = "اعرض حتى 30 أو 80 أو منتجات غير محدودة عبر محفظة شام كاش",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Icon(Icons.Default.ChevronLeft, contentDescription = null, tint = GoldDark)
                    }
                }
            } else {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFF0284C7).copy(alpha = 0.08f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 2.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Sell, contentDescription = null, tint = Color(0xFF0284C7), modifier = Modifier.size(18.dp))
                        Text(
                            text = "يمكنك كمواطن نشر أغراضك وقطعك المستعملة للبيع مجاناً في قسم سوق المستعمل.",
                            fontSize = 11.sp,
                            color = Color(0xFF0284C7)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            if (displayedProducts.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("لا توجد منتجات معروضة حالياً", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(displayedProducts) { prod ->
                        MarketProductCard(
                            product = prod,
                            onContact = {
                                viewModel.sendMessage(
                                    prod.merchantId,
                                    prod.merchantName,
                                    "السلام عليكم، أود شراء أو الاستفسار عن: ${prod.name}"
                                )
                                viewModel.navigateTo(AppScreen.MESSAGES)
                            },
                            onLike = { viewModel.toggleLike("PRODUCT", prod.id) }
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddProductDialog(
            isMerchant = currentUser.role == UserRole.MERCHANT,
            onDismiss = { showAddDialog = false },
            onSubmit = { name, desc, price, currency, category, isUsed, condition, qty ->
                viewModel.submitProduct(name, desc, price, currency, category, isUsed, condition, qty)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun MarketProductCard(
    product: ProductEntity,
    onContact: () -> Unit,
    onLike: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = if (product.isUsed) Color(0xFFFEF3C7) else Color(0xFFEFF6FF),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = if (product.isUsed) "مستعمل (${product.condition})" else "جديد في المتجر",
                            color = if (product.isUsed) Color(0xFF92400E) else Color(0xFF1D4ED8),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = product.category,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Text(
                    text = "${product.price} ${product.currency}",
                    color = GoldDark,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = product.name,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = product.description,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🏬 البائع: ${product.merchantName}",
                    fontSize = 11.sp,
                    color = PurpleSecondary,
                    fontWeight = FontWeight.Medium
                )

                Button(
                    onClick = onContact,
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Icon(Icons.Default.Chat, null, tint = Color.Black, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("طلب ومراسلة", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun AddProductDialog(
    isMerchant: Boolean,
    onDismiss: () -> Unit,
    onSubmit: (String, String, Double, String, String, Boolean, String, Int) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var priceStr by remember { mutableStateOf("") }
    var currency by remember { mutableStateOf("USD") }
    var category by remember { mutableStateOf("عام") }
    var isUsed by remember { mutableStateOf(!isMerchant) }
    var condition by remember { mutableStateOf(if (isMerchant) "جديد" else "مستعمل بحالة جيدة") }
    var qtyStr by remember { mutableStateOf("1") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("إضافة منتج للبيع", fontWeight = FontWeight.Bold) },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = !isUsed,
                            onClick = { isUsed = false; condition = "جديد" },
                            label = { Text("منتج جديد") }
                        )
                        FilterChip(
                            selected = isUsed,
                            onClick = { isUsed = true; condition = "مستعمل بحالة جيدة" },
                            label = { Text("مستعمل (سوق المستعمل)") }
                        )
                    }
                }
                item {
                    OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("اسم المنتج / السلعة") }, modifier = Modifier.fillMaxWidth())
                }
                item {
                    OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("وصف السلعة وحالتها") }, modifier = Modifier.fillMaxWidth(), minLines = 2)
                }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = priceStr, onValueChange = { priceStr = it }, label = { Text("السعر") }, modifier = Modifier.weight(1f))
                        OutlinedTextField(value = currency, onValueChange = { currency = it }, label = { Text("العملة") }, modifier = Modifier.weight(1f))
                    }
                }
                item {
                    OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("التصنيف (إلكترونيات، أثاث...)") }, modifier = Modifier.fillMaxWidth())
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val price = priceStr.toDoubleOrNull() ?: 0.0
                    val qty = qtyStr.toIntOrNull() ?: 1
                    if (name.isNotBlank()) {
                        onSubmit(name, desc, price, currency, category, isUsed, condition, qty)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = PurpleSecondary)
            ) {
                Text("نشر في السوق", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("إلغاء") }
        }
    )
}
