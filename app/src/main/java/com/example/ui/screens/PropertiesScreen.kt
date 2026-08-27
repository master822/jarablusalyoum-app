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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.JarablusViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PropertiesScreen(
    viewModel: JarablusViewModel,
    modifier: Modifier = Modifier
) {
    val approvedProperties by viewModel.approvedProperties.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    var selectedTypeFilter by remember { mutableStateOf<PropertyType?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }

    val filteredProps = if (selectedTypeFilter == null) {
        approvedProperties
    } else {
        approvedProperties.filter { it.type == selectedTypeFilter }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.navigateTo(AppScreen.CREATE_PROPERTY) },
                containerColor = GoldPrimary,
                contentColor = Color.Black,
                modifier = Modifier.testTag("add_property_fab")
            ) {
                Row(modifier = Modifier.padding(horizontal = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AddHomeWork, contentDescription = "إضافة عقار")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("إضافة عقار (50 ل.ت)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
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
                    Text("عقارات جرابلس", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text("شقق، منازل، محلات وأراضي للبيع والإيجار بجرابلس", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            // Monetization Info Banner
            Surface(
                onClick = { viewModel.navigateTo(AppScreen.CREATE_PROPERTY) },
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFEA580C).copy(alpha = 0.1f),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEA580C).copy(alpha = 0.3f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.Campaign, contentDescription = null, tint = Color(0xFFEA580C))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "نشر العقارات في المنصة مأجور (50 ليرة تركي / 3 أيام)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = Color(0xFFEA580C)
                        )
                        Text(
                            text = "الدفع عبر محفظة شام كاش والتفعيل خلال 24 ساعة (مجاناً لحساب الإدارة). اضغط هنا لنشر عقار جديد.",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Icon(Icons.Default.ChevronLeft, contentDescription = null, tint = Color(0xFFEA580C))
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Filter Tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedTypeFilter == null,
                    onClick = { selectedTypeFilter = null },
                    label = { Text("جميع العقارات") },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = GoldDark, selectedLabelColor = Color.White)
                )
                FilterChip(
                    selected = selectedTypeFilter == PropertyType.SALE,
                    onClick = { selectedTypeFilter = PropertyType.SALE },
                    label = { Text("عقارات للبيع") },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color(0xFF059669), selectedLabelColor = Color.White)
                )
                FilterChip(
                    selected = selectedTypeFilter == PropertyType.RENT,
                    onClick = { selectedTypeFilter = PropertyType.RENT },
                    label = { Text("عقارات للإيجار") },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = PurpleSecondary, selectedLabelColor = Color.White)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (filteredProps.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("لا توجد عقارات مطابقة حالياً", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(filteredProps) { prop ->
                        PropertyCard(
                            property = prop,
                            onClick = { viewModel.viewPropertyDetails(prop) },
                            onContact = {
                                viewModel.sendMessage(
                                    prop.authorId,
                                    prop.authorName,
                                    "السلام عليكم، أستفسر بخصوص العقار: ${prop.title}"
                                )
                                viewModel.navigateTo(AppScreen.MESSAGES)
                            }
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddPropertyDialog(
            isAdmin = currentUser.role == UserRole.ADMIN,
            onDismiss = { showAddDialog = false },
            onSubmit = { title, desc, type, price, currency, district, address, rooms, baths, area, age, cladding ->
                viewModel.submitProperty(title, desc, type, price, currency, district, address, rooms, baths, area, age, cladding)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun PropertyCard(
    property: PropertyEntity,
    onClick: () -> Unit,
    onContact: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
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
                Surface(
                    color = if (property.type == PropertyType.SALE) Color(0xFFD1FAE5) else Color(0xFFEDE9FE),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = property.type.labelAr,
                        color = if (property.type == PropertyType.SALE) Color(0xFF047857) else PurpleDark,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                Text(
                    text = "${property.price.toInt()} ${property.currency}",
                    color = GoldDark,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = property.title,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = property.description,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Specs Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SpecBadge(icon = Icons.Default.Bed, label = "${property.rooms} غرف")
                SpecBadge(icon = Icons.Default.Bathtub, label = "${property.bathrooms} حمام")
                SpecBadge(icon = Icons.Default.SquareFoot, label = "${property.area.toInt()} م²")
                SpecBadge(icon = Icons.Default.Place, label = property.district)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "👤 المعلن: ${property.authorName}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Button(
                    onClick = onContact,
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Icon(Icons.Default.Chat, null, tint = Color.Black, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("تواصل مع المالك", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun SpecBadge(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        shape = RoundedCornerShape(6.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, null, tint = PurpleSecondary, modifier = Modifier.size(13.dp))
            Spacer(modifier = Modifier.width(3.dp))
            Text(label, fontSize = 10.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun PropertyDetailsScreen(
    viewModel: JarablusViewModel,
    modifier: Modifier = Modifier
) {
    val prop by viewModel.selectedProperty.collectAsState()
    if (prop == null) return
    val p = prop!!

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Button(
                onClick = { viewModel.navigateTo(AppScreen.PROPERTIES) },
                colors = ButtonDefaults.outlinedButtonColors()
            ) {
                Icon(Icons.Default.ArrowForward, contentDescription = "رجوع")
                Spacer(modifier = Modifier.width(4.dp))
                Text("العودة للعقارات")
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = if (p.type == PropertyType.SALE) Color(0xFFD1FAE5) else Color(0xFFEDE9FE),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = p.type.labelAr,
                        color = if (p.type == PropertyType.SALE) Color(0xFF047857) else PurpleDark,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }

                Text(
                    text = "${p.price.toInt()} ${p.currency}",
                    color = GoldDark,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }

        item {
            Text(p.title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("المواصفات الفنية للعقار:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("• المنطقة: ${p.district}")
                        Text("• العنوان: ${p.address.ifBlank { "وسط جرابلس" }}")
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("• عدد الغرف: ${p.rooms}")
                        Text("• عدد الحمامات: ${p.bathrooms}")
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("• المساحة: ${p.area.toInt()} متر مربع")
                        Text("• عمر البناء: ${p.buildingAge} سنوات")
                    }
                    Text("• نوع الإكساء: ${p.claddingType}")
                }
            }
        }

        item {
            Text("الوصف الكامل:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(p.description, fontSize = 13.sp, lineHeight = 20.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = GoldContainerLight)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("المعلن: ${p.authorName}", fontWeight = FontWeight.Bold, color = Color.Black)
                        Text("رقم الهاتف: ${p.phone}", fontSize = 12.sp, color = Color(0xFF475569))
                    }
                    Button(
                        onClick = {
                            viewModel.sendMessage(p.authorId, p.authorName, "السلام عليكم، أستفسر عن العقار: ${p.title}")
                            viewModel.navigateTo(AppScreen.MESSAGES)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PurpleSecondary)
                    ) {
                        Text("محادثة فورية", color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun AddPropertyDialog(
    isAdmin: Boolean,
    onDismiss: () -> Unit,
    onSubmit: (String, String, PropertyType, Double, String, String, String, Int, Int, Double, Int, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var type by remember { mutableStateOf(PropertyType.SALE) }
    var priceStr by remember { mutableStateOf("") }
    var currency by remember { mutableStateOf("USD") }
    var district by remember { mutableStateOf("وسط المدينة") }
    var address by remember { mutableStateOf("") }
    var roomsStr by remember { mutableStateOf("3") }
    var bathsStr by remember { mutableStateOf("1") }
    var areaStr by remember { mutableStateOf("120") }
    var ageStr by remember { mutableStateOf("2") }
    var cladding by remember { mutableStateOf("سوبر ديلوكس") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("إضافة عقار جديد", fontWeight = FontWeight.Bold) },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = type == PropertyType.SALE,
                            onClick = { type = PropertyType.SALE },
                            label = { Text("للبيع") }
                        )
                        FilterChip(
                            selected = type == PropertyType.RENT,
                            onClick = { type = PropertyType.RENT },
                            label = { Text("للإيجار") }
                        )
                    }
                }
                item {
                    OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("عنوان العقار") }, modifier = Modifier.fillMaxWidth())
                }
                item {
                    OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("وصف العقار ومميزاته") }, modifier = Modifier.fillMaxWidth(), minLines = 2)
                }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = priceStr, onValueChange = { priceStr = it }, label = { Text("السعر") }, modifier = Modifier.weight(1f))
                        OutlinedTextField(value = currency, onValueChange = { currency = it }, label = { Text("العملة") }, modifier = Modifier.weight(1f))
                    }
                }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = district, onValueChange = { district = it }, label = { Text("الحي / المنطقة") }, modifier = Modifier.weight(1f))
                        OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("العنوان بالتفصيل") }, modifier = Modifier.weight(1f))
                    }
                }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = roomsStr, onValueChange = { roomsStr = it }, label = { Text("الغرف") }, modifier = Modifier.weight(1f))
                        OutlinedTextField(value = bathsStr, onValueChange = { bathsStr = it }, label = { Text("الحمامات") }, modifier = Modifier.weight(1f))
                        OutlinedTextField(value = areaStr, onValueChange = { areaStr = it }, label = { Text("المساحة م²") }, modifier = Modifier.weight(1f))
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val price = priceStr.toDoubleOrNull() ?: 0.0
                    val rooms = roomsStr.toIntOrNull() ?: 3
                    val baths = bathsStr.toIntOrNull() ?: 1
                    val area = areaStr.toDoubleOrNull() ?: 100.0
                    val age = ageStr.toIntOrNull() ?: 1
                    if (title.isNotBlank()) {
                        onSubmit(title, desc, type, price, currency, district, address, rooms, baths, area, age, cladding)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary)
            ) {
                Text("حفظ ونشر العقار", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("إلغاء") }
        }
    )
}
