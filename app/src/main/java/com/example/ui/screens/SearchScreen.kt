package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.JarablusViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: JarablusViewModel,
    modifier: Modifier = Modifier
) {
    val query by viewModel.searchQuery.collectAsState()
    val newsList by viewModel.approvedNews.collectAsState()
    val adsList by viewModel.approvedAnnouncements.collectAsState()
    val propsList by viewModel.approvedProperties.collectAsState()
    val productsList by viewModel.allProducts.collectAsState()
    val servicesList by viewModel.allServices.collectAsState()
    val jobsList by viewModel.allJobs.collectAsState()

    val cleanQuery = query.trim()

    val filteredNews = if (cleanQuery.isBlank()) emptyList() else newsList.filter { it.title.contains(cleanQuery, true) || it.content.contains(cleanQuery, true) }
    val filteredAds = if (cleanQuery.isBlank()) emptyList() else adsList.filter { it.title.contains(cleanQuery, true) || it.content.contains(cleanQuery, true) }
    val filteredProps = if (cleanQuery.isBlank()) emptyList() else propsList.filter { it.title.contains(cleanQuery, true) || it.district.contains(cleanQuery, true) }
    val filteredProducts = if (cleanQuery.isBlank()) emptyList() else productsList.filter { it.name.contains(cleanQuery, true) || it.description.contains(cleanQuery, true) }
    val filteredServices = if (cleanQuery.isBlank()) emptyList() else servicesList.filter { it.title.contains(cleanQuery, true) || it.category.contains(cleanQuery, true) }
    val filteredJobs = if (cleanQuery.isBlank()) emptyList() else jobsList.filter { it.title.contains(cleanQuery, true) || it.companyName.contains(cleanQuery, true) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = { viewModel.updateSearchQuery(it) },
            placeholder = { Text("ابحث في الأخبار، العقارات، السوق، الخدمات، الوظائف...") },
            leadingIcon = { Icon(Icons.Default.Search, null, tint = GoldDark) },
            trailingIcon = {
                if (query.isNotBlank()) {
                    IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                        Icon(Icons.Default.Close, null)
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().testTag("search_text_field"),
            shape = RoundedCornerShape(16.dp)
        )

        Spacer(modifier = Modifier.height(14.dp))

        if (cleanQuery.isBlank()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Search, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("اكتب كلمة البحث لاستكشاف جميع أقسام منصة جرابلس اليوم", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
                }
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // News results
                if (filteredNews.isNotEmpty()) {
                    item { Text("نتائج الأخبار (${filteredNews.size})", fontWeight = FontWeight.Bold, color = GoldDark) }
                    items(filteredNews) { n ->
                        SearchResultCard(
                            title = n.title,
                            subtitle = "الأخبار • ${n.category}",
                            onClick = { viewModel.viewNewsDetails(n) }
                        )
                    }
                }

                // Properties results
                if (filteredProps.isNotEmpty()) {
                    item { Text("نتائج العقارات (${filteredProps.size})", fontWeight = FontWeight.Bold, color = GoldDark) }
                    items(filteredProps) { p ->
                        SearchResultCard(
                            title = p.title,
                            subtitle = "العقارات (${p.type.labelAr}) • ${p.price} ${p.currency} • ${p.district}",
                            onClick = { viewModel.viewPropertyDetails(p) }
                        )
                    }
                }

                // Products results
                if (filteredProducts.isNotEmpty()) {
                    item { Text("نتائج السوق والمنتجات (${filteredProducts.size})", fontWeight = FontWeight.Bold, color = PurpleSecondary) }
                    items(filteredProducts) { pr ->
                        SearchResultCard(
                            title = pr.name,
                            subtitle = "السوق • ${pr.price} ${pr.currency} • ${pr.merchantName}",
                            onClick = { viewModel.navigateTo(AppScreen.MARKET) }
                        )
                    }
                }

                // Services results
                if (filteredServices.isNotEmpty()) {
                    item { Text("نتائج الخدمات (${filteredServices.size})", fontWeight = FontWeight.Bold, color = PurpleSecondary) }
                    items(filteredServices) { s ->
                        SearchResultCard(
                            title = s.title,
                            subtitle = "الخدمات • ${s.providerName} • ${s.category}",
                            onClick = { viewModel.navigateTo(AppScreen.SERVICES) }
                        )
                    }
                }

                // Jobs results
                if (filteredJobs.isNotEmpty()) {
                    item { Text("نتائج الوظائف (${filteredJobs.size})", fontWeight = FontWeight.Bold, color = Color(0xFF0D9488)) }
                    items(filteredJobs) { j ->
                        SearchResultCard(
                            title = j.title,
                            subtitle = "الوظائف • ${j.companyName} • ${j.salary}",
                            onClick = { viewModel.navigateTo(AppScreen.JOBS) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SearchResultCard(title: String, subtitle: String, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() },
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(12.dp),
        tonalElevation = 2.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(2.dp))
            Text(subtitle, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
