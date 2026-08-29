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
fun NewsScreen(
    viewModel: JarablusViewModel,
    modifier: Modifier = Modifier
) {
    val approvedNews by viewModel.approvedNews.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    var selectedCategory by remember { mutableStateOf("الكل") }
    var showAddDialog by remember { mutableStateOf(false) }

    val categories = listOf("الكل", "أخبار المدينة", "خدمات", "أحداث ومجتمع", "فعاليات")

    val filteredNews = if (selectedCategory == "الكل") {
        approvedNews
    } else {
        approvedNews.filter { it.category == selectedCategory }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0.dp),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.navigateTo(AppScreen.CREATE_NEWS_OR_AD) },
                containerColor = GoldPrimary,
                contentColor = Color.Black,
                modifier = Modifier.testTag("add_news_fab")
            ) {
                Row(modifier = Modifier.padding(horizontal = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Add, contentDescription = "إضافة خبر")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("إضافة خبر", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Screen Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("أخبار جرابلس اليوم", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text("التغطية الإخبارية والخدمية الشاملة لمدينة جرابلس", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
                            selectedContainerColor = PurpleSecondary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (filteredNews.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("لا توجد أخبار في هذا التصنيف حالياً", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredNews) { news ->
                        NewsCard(
                            news = news,
                            onClick = { viewModel.viewNewsDetails(news) },
                            onLike = { viewModel.toggleLike("NEWS", news.id) }
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddNewsDialog(
            isAdmin = currentUser.role == UserRole.ADMIN,
            onDismiss = { showAddDialog = false },
            onSubmit = { title, content, category ->
                viewModel.submitNews(title, content, category)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun NewsCard(
    news: NewsEntity,
    onClick: () -> Unit,
    onLike: () -> Unit,
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
                    color = PurpleContainerLight,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = news.category,
                        color = PurpleDark,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                Text(
                    text = "👁️ ${news.viewsCount} مشاهدة",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = news.title,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = news.content,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 3,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "✍️ بواسطة: ${news.authorName}",
                    fontSize = 11.sp,
                    color = GoldDark,
                    fontWeight = FontWeight.Medium
                )

                LikeCommentBar(
                    likesCount = news.likesCount,
                    commentsCount = news.commentsCount,
                    onLikeClick = onLike,
                    onCommentClick = onClick,
                    modifier = Modifier.width(180.dp)
                )
            }
        }
    }
}

@Composable
fun NewsDetailsScreen(
    viewModel: JarablusViewModel,
    modifier: Modifier = Modifier
) {
    val news by viewModel.selectedNews.collectAsState()
    if (news == null) return

    val currentNews = news!!
    val comments by viewModel.getCommentsFor("NEWS", currentNews.id).collectAsState(initial = emptyList())
    var commentText by remember { mutableStateOf("") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Button(
                onClick = { viewModel.navigateTo(AppScreen.NEWS) },
                colors = ButtonDefaults.outlinedButtonColors()
            ) {
                Icon(Icons.Default.ArrowForward, contentDescription = "رجوع")
                Spacer(modifier = Modifier.width(4.dp))
                Text("العودة للأخبار")
            }
        }

        item {
            Surface(
                color = PurpleContainerLight,
                shape = RoundedCornerShape(6.dp)
            ) {
                Text(
                    text = currentNews.category,
                    color = PurpleDark,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }

        item {
            Text(
                text = currentNews.title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 26.sp
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("بواسطة: ${currentNews.authorName}", color = GoldDark, fontSize = 12.sp)
                Text("👁️ ${currentNews.viewsCount} مشاهدة", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
            }
        }

        item {
            HorizontalDivider()
        }

        item {
            Text(
                text = currentNews.content,
                fontSize = 14.sp,
                lineHeight = 22.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        item {
            LikeCommentBar(
                likesCount = currentNews.likesCount,
                commentsCount = currentNews.commentsCount,
                onLikeClick = { viewModel.toggleLike("NEWS", currentNews.id) },
                onCommentClick = {}
            )
        }

        item {
            HorizontalDivider()
        }

        // Add Comment Box
        item {
            Text("التعليقات والمناقشات (${comments.size})", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = commentText,
                    onValueChange = { commentText = it },
                    placeholder = { Text("أضف تعليقك هنا...") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        viewModel.addComment("NEWS", currentNews.id, currentNews.title, commentText)
                        commentText = ""
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("إرسال", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Comments List
        items(comments) { c ->
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(c.userName, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = PurpleSecondary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(c.commentText, fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
fun AddNewsDialog(
    isAdmin: Boolean,
    onDismiss: () -> Unit,
    onSubmit: (String, String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("أخبار المدينة") }

    val categories = listOf("أخبار المدينة", "خدمات", "أحداث ومجتمع", "فعاليات")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("إضافة خبر جديد", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                if (!isAdmin) {
                    Surface(
                        color = Color(0xFFFEF3C7),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "ملاحظة: سيتم نشر الخبر بعد مراجعته واعتماده من قبل إدارة جرابلس اليوم.",
                            color = Color(0xFF92400E),
                            fontSize = 11.sp,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("عنوان الخبر") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("تفاصيل الخبر") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 4
                )

                Text("التصنيف:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    categories.take(3).forEach { cat ->
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
                        onSubmit(title, content, category)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary)
            ) {
                Text("نشر الخبر", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء")
            }
        }
    )
}
