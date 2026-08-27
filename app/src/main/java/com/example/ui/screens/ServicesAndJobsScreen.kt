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
fun ServicesAndJobsScreen(
    viewModel: JarablusViewModel,
    initialTab: Int = 0, // 0: Services, 1: Jobs
    modifier: Modifier = Modifier
) {
    val allServices by viewModel.allServices.collectAsState()
    val allJobs by viewModel.allJobs.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    var selectedTab by remember { mutableStateOf(initialTab) }
    var showAddServiceDialog by remember { mutableStateOf(false) }
    var showAddJobDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (selectedTab == 0) showAddServiceDialog = true else showAddJobDialog = true
                },
                containerColor = if (selectedTab == 0) PurpleSecondary else GoldPrimary,
                contentColor = if (selectedTab == 0) Color.White else Color.Black,
                modifier = Modifier.testTag("add_service_or_job_fab")
            ) {
                Row(modifier = Modifier.padding(horizontal = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (selectedTab == 0) "إضافة خدمة" else "نشر فرصة عمل", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Header Tabs
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = PurpleSecondary
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("دليل الخدمات والمهن") },
                    icon = { Icon(Icons.Default.Handyman, null) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("فرص العمل والوظائف") },
                    icon = { Icon(Icons.Default.Work, null) }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (selectedTab == 0) {
                // Services List
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (currentUser.role == UserRole.SERVICE_PROVIDER) {
                        item {
                            Surface(
                                onClick = { viewModel.navigateTo(AppScreen.SERVICE_PROVIDER_PORTFOLIO) },
                                shape = RoundedCornerShape(12.dp),
                                color = PurpleSecondary.copy(alpha = 0.12f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, PurpleSecondary.copy(alpha = 0.4f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Icon(Icons.Default.Collections, contentDescription = null, tint = PurpleSecondary)
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "معرض أعمالك وخدماتك (حتى 50 صورة)",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = PurpleSecondary
                                        )
                                        Text(
                                            text = "أضف صور أعمالك المنجزة ورقم الواتساب للتواصل الفوري المباشر مع الزبائن",
                                            fontSize = 10.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Icon(Icons.Default.ChevronLeft, contentDescription = null, tint = PurpleSecondary)
                                }
                            }
                        }
                    }

                    items(allServices) { service ->
                        ServiceFullCard(
                            service = service,
                            onContact = {
                                viewModel.sendMessage(
                                    service.providerId,
                                    service.providerName,
                                    "السلام عليكم، أود طلب خدمة: ${service.title}"
                                )
                                viewModel.navigateTo(AppScreen.MESSAGES)
                            },
                            onViewPortfolio = {
                                viewModel.selectProvider(service.providerId)
                                viewModel.navigateTo(AppScreen.SERVICE_PROVIDER_PORTFOLIO)
                            }
                        )
                    }
                }
            } else {
                // Jobs List
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(allJobs) { job ->
                        JobFullCard(
                            job = job,
                            onApply = {
                                viewModel.sendMessage(
                                    job.authorId,
                                    job.companyName,
                                    "السلام عليكم، أتقدم بطلب للعمل في وظيفة: ${job.title}"
                                )
                                viewModel.navigateTo(AppScreen.MESSAGES)
                            }
                        )
                    }
                }
            }
        }
    }

    if (showAddServiceDialog) {
        AddServiceDialog(
            onDismiss = { showAddServiceDialog = false },
            onSubmit = { title, desc, cat, exp ->
                viewModel.submitService(title, desc, cat, exp)
                showAddServiceDialog = false
            }
        )
    }

    if (showAddJobDialog) {
        AddJobDialog(
            defaultPhone = currentUser.phone,
            onDismiss = { showAddJobDialog = false },
            onSubmit = { title, company, desc, reqs, loc, salary, phone ->
                viewModel.submitJob(title, company, desc, reqs, loc, salary, phone)
                showAddJobDialog = false
            }
        )
    }
}

@Composable
fun ServiceFullCard(
    service: ServiceEntity,
    onContact: () -> Unit,
    onViewPortfolio: () -> Unit,
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
                Surface(color = PurpleContainerLight, shape = RoundedCornerShape(6.dp)) {
                    Text(service.category, color = PurpleDark, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
                }
                Text("⭐ ${service.rating} / 5.0", color = GoldDark, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(service.title, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Spacer(modifier = Modifier.height(6.dp))
            Text(service.description, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 16.sp)

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("👤 ${service.providerName}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Text("خبرة: ${service.experienceYears} سنوات • 📍 ${service.city}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedButton(
                        onClick = onViewPortfolio,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(Icons.Default.Collections, null, modifier = Modifier.size(14.dp), tint = PurpleSecondary)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("المعرض", fontSize = 11.sp, color = PurpleSecondary)
                    }

                    Button(
                        onClick = onContact,
                        colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Icon(Icons.Default.Chat, null, tint = Color.Black, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("طلب الخدمة", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun JobFullCard(
    job: JobEntity,
    onApply: () -> Unit,
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
                Text(job.title, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Surface(color = Color(0xFFD1FAE5), shape = RoundedCornerShape(6.dp)) {
                    Text(job.salary, color = Color(0xFF065F46), fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text("🏢 جهة العمل: ${job.companyName} • 📍 ${job.location}", fontSize = 12.sp, color = PurpleSecondary, fontWeight = FontWeight.Medium)

            Spacer(modifier = Modifier.height(8.dp))
            Text("الوصف الوظيفي: ${job.description}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 16.sp)

            if (job.requirements.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text("المتطلبات والشروط: ${job.requirements}", fontSize = 12.sp, color = Color(0xFF475569))
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("📞 هاتف: ${job.contactPhone}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                Button(
                    onClick = onApply,
                    colors = ButtonDefaults.buttonColors(containerColor = PurpleSecondary),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("تقديم على الوظيفة", color = Color.White, fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
fun AddServiceDialog(
    onDismiss: () -> Unit,
    onSubmit: (String, String, String, Int) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var cat by remember { mutableStateOf("كهرباء وطاقة") }
    var expStr by remember { mutableStateOf("5") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("إضافة خدمة مهنية", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("عنوان الخدمة") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("وصف الخدمة ونطاق العمل") }, modifier = Modifier.fillMaxWidth(), minLines = 2)
                OutlinedTextField(value = cat, onValueChange = { cat = it }, label = { Text("المجال / التخصص") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = expStr, onValueChange = { expStr = it }, label = { Text("سنوات الخبرة") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onSubmit(title, desc, cat, expStr.toIntOrNull() ?: 1)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = PurpleSecondary)
            ) {
                Text("حفظ ونشر الخدمة", color = Color.White)
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("إلغاء") } }
    )
}

@Composable
fun AddJobDialog(
    defaultPhone: String,
    onDismiss: () -> Unit,
    onSubmit: (String, String, String, String, String, String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var company by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var reqs by remember { mutableStateOf("") }
    var loc by remember { mutableStateOf("جرابلس") }
    var salary by remember { mutableStateOf("راتب مجزي") }
    var phone by remember { mutableStateOf(defaultPhone) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("نشر فرصة عمل", fontWeight = FontWeight.Bold) },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                item { OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("المسمى الوظيفي") }, modifier = Modifier.fillMaxWidth()) }
                item { OutlinedTextField(value = company, onValueChange = { company = it }, label = { Text("اسم الشركة / المتجر") }, modifier = Modifier.fillMaxWidth()) }
                item { OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("الوصف والمهام") }, modifier = Modifier.fillMaxWidth(), minLines = 2) }
                item { OutlinedTextField(value = reqs, onValueChange = { reqs = it }, label = { Text("الشروط والمؤهلات") }, modifier = Modifier.fillMaxWidth()) }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = loc, onValueChange = { loc = it }, label = { Text("الموقع") }, modifier = Modifier.weight(1f))
                        OutlinedTextField(value = salary, onValueChange = { salary = it }, label = { Text("الراتب") }, modifier = Modifier.weight(1f))
                    }
                }
                item { OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("رقم للتواصل") }, modifier = Modifier.fillMaxWidth()) }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onSubmit(title, company, desc, reqs, loc, salary, phone)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary)
            ) {
                Text("نشر الوظيفة", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("إلغاء") } }
    )
}
