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
import com.example.ui.components.LikeCommentBar
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.JarablusViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnnouncementsScreen(
    viewModel: JarablusViewModel,
    modifier: Modifier = Modifier
) {
    val approvedAds by viewModel.approvedAnnouncements.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    var selectedCategory by remember { mutableStateOf("الكل") }
    var showAddDialog by remember { mutableStateOf(false) }

    val categories = listOf("الكل", "مفقودات وموجودات", "تنويهات خدمية", "إعلانات تجارية", "إعلانات عامة")

    val filteredAds = if (selectedCategory == "الكل") {
        approvedAds
    } else {
        approvedAds.filter { it.category == selectedCategory }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.navigateTo(AppScreen.CREATE_NEWS_OR_AD) },
                containerColor = PurpleSecondary,
                contentColor = Color.White,
                modifier = Modifier.testTag("add_announcement_fab")
            ) {
                Row(modifier = Modifier.padding(horizontal = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Campaign, contentDescription = "نشر إعلان")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("نشر إعلان", fontWeight = FontWeight.Bold, fontSize = 12.sp)
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
                    Text("لوحة إعلانات جرابلس", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text("المفقودات، التنويهات، والمناقصات والإعلانات العامة", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            // Categories Filter
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { cat ->
                    FilterChip(
                        selected = selectedCategory == cat,
                        onClick = { selectedCategory = cat },
                        label = { Text(cat, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = GoldDark,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (filteredAds.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("لا توجد إعلانات في هذا القسم حالياً", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredAds) { ad ->
                        AnnouncementCard(
                            announcement = ad,
                            onContact = {
                                viewModel.sendMessage(
                                    ad.authorId,
                                    ad.authorName,
                                    "السلام عليكم، بخصوص إعلانك: ${ad.title}"
                                )
                                viewModel.navigateTo(AppScreen.MESSAGES)
                            },
                            onLike = { viewModel.toggleLike("ANNOUNCEMENT", ad.id) }
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddAnnouncementDialog(
            isAdmin = currentUser.role == UserRole.ADMIN,
            defaultPhone = currentUser.phone,
            onDismiss = { showAddDialog = false },
            onSubmit = { title, content, category, phone ->
                viewModel.submitAnnouncement(title, content, category, phone)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun AnnouncementCard(
    announcement: AnnouncementEntity,
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
                Surface(
                    color = GoldContainerLight,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = announcement.category,
                        color = GoldDark,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Phone, contentDescription = null, tint = PurpleSecondary, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = announcement.phone,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = PurpleSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = announcement.title,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = announcement.content,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "👤 المعلن: ${announcement.authorName}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Button(
                    onClick = onContact,
                    colors = ButtonDefaults.buttonColors(containerColor = PurpleSecondary),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Icon(Icons.Default.Chat, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("مراسلة المعلن", fontSize = 11.sp, color = Color.White)
                }
            }
        }
    }
}

@Composable
fun AddAnnouncementDialog(
    isAdmin: Boolean,
    defaultPhone: String,
    onDismiss: () -> Unit,
    onSubmit: (String, String, String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("إعلانات عامة") }
    var phone by remember { mutableStateOf(defaultPhone) }

    val categories = listOf("مفقودات وموجودات", "تنويهات خدمية", "إعلانات تجارية", "إعلانات عامة")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("نشر إعلان جديد", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                if (!isAdmin) {
                    Surface(
                        color = Color(0xFFFEF3C7),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "ملاحظة: سيتم عرض الإعلان بعد مراجعته من قبل المشرفين.",
                            color = Color(0xFF92400E),
                            fontSize = 11.sp,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("عنوان الإعلان") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("تفاصيل الإعلان") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("رقم هاتف للتواصل") },
                    modifier = Modifier.fillMaxWidth()
                )

                Text("التصنيف:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    categories.take(2).forEach { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = { Text(cat, fontSize = 10.sp) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank() && content.isNotBlank()) {
                        onSubmit(title, content, category, phone)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = PurpleSecondary)
            ) {
                Text("نشر الإعلان", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء")
            }
        }
    )
}
