package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.components.FooterView
import com.example.ui.components.SectionHeader
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.JarablusViewModel

@Composable
fun HomeScreen(
    viewModel: JarablusViewModel,
    modifier: Modifier = Modifier
) {
    val approvedNews by viewModel.approvedNews.collectAsState()
    val approvedProperties by viewModel.approvedProperties.collectAsState()
    val allProducts by viewModel.allProducts.collectAsState()
    val activeDiscounts by viewModel.activeDiscounts.collectAsState()
    val allServices by viewModel.allServices.collectAsState()
    val allJobs by viewModel.allJobs.collectAsState()
    val isDark by viewModel.isDarkMode.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen_lazy_column")
    ) {
        // Hero Section
        item {
            HeroBanner(onActionClick = { screen -> viewModel.navigateTo(screen) })
        }

        // Post News/Ad/Property Quick Action Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = if (isDark) SurfaceDark else SurfaceLight),
                border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(GoldPrimary.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Campaign, null, tint = if (isDark) GoldLight else GoldDark, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("انشر في جرابلس اليوم", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("أخبار، إعلانات مبوبة، أو عقارات للبيع والإيجار", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { viewModel.navigateTo(AppScreen.CREATE_NEWS_OR_AD) },
                            colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.PostAdd, null, tint = Color.Black, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("نشر خبر / إعلان", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = { viewModel.navigateTo(AppScreen.CREATE_PROPERTY) },
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF0D9488)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.HomeWork, null, tint = Color(0xFF0D9488), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("نشر عقار (50 ل.ت)", color = Color(0xFF0D9488), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Quick Categories Bar
        item {
            QuickCategoriesRow(onCategoryClick = { screen -> viewModel.navigateTo(screen) })
        }

        // Active Discounts / Deals
        if (activeDiscounts.isNotEmpty()) {
            item {
                SectionHeader(
                    title = "أحدث العروض والتخفيضات في جرابلس",
                    icon = Icons.Default.LocalOffer,
                    onViewMore = { viewModel.navigateTo(AppScreen.DISCOUNTS) }
                )
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(activeDiscounts.take(5)) { discount ->
                        DiscountCard(discount = discount, onClick = { viewModel.navigateTo(AppScreen.DISCOUNTS) })
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // Latest City News
        item {
            SectionHeader(
                title = "أخبار جرابلس اليومية",
                icon = Icons.Default.Newspaper,
                onViewMore = { viewModel.navigateTo(AppScreen.NEWS) }
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                approvedNews.take(3).forEach { news ->
                    NewsItemCard(
                        news = news,
                        onClick = { viewModel.viewNewsDetails(news) },
                        onLike = { viewModel.toggleLike("NEWS", news.id) }
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Featured Real Estate
        item {
            SectionHeader(
                title = "العقارات المميزة (بيع وإيجار)",
                icon = Icons.Default.HomeWork,
                onViewMore = { viewModel.navigateTo(AppScreen.PROPERTIES) }
            )
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(approvedProperties.take(4)) { prop ->
                    PropertyCompactCard(
                        property = prop,
                        onClick = { viewModel.viewPropertyDetails(prop) }
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Local Marketplace
        item {
            SectionHeader(
                title = "سوق جرابلس والمنتجات",
                icon = Icons.Default.ShoppingCart,
                onViewMore = { viewModel.navigateTo(AppScreen.MARKET) }
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                allProducts.take(3).forEach { prod ->
                    ProductItemCard(
                        product = prod,
                        onClick = { viewModel.viewProductDetails(prod) },
                        onContact = {
                            viewModel.sendMessage(
                                prod.merchantId,
                                prod.merchantName,
                                "مرحباً، أستفسر عن منتج: ${prod.name}"
                            )
                            viewModel.navigateTo(AppScreen.MESSAGES)
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Services & Professionals
        item {
            SectionHeader(
                title = "دليل الخدمات والمهنيين",
                icon = Icons.Default.Handyman,
                onViewMore = { viewModel.navigateTo(AppScreen.SERVICES) }
            )
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(allServices.take(4)) { service ->
                    ServiceCompactCard(
                        service = service,
                        onContact = {
                            viewModel.sendMessage(
                                service.providerId,
                                service.providerName,
                                "السلام عليكم، أود الاستفسار عن خدمة: ${service.title}"
                            )
                            viewModel.navigateTo(AppScreen.MESSAGES)
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Job Opportunities
        item {
            SectionHeader(
                title = "فرص العمل المتاحة",
                icon = Icons.Default.Work,
                onViewMore = { viewModel.navigateTo(AppScreen.JOBS) }
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                allJobs.take(2).forEach { job ->
                    JobItemCard(
                        job = job,
                        onApply = {
                            viewModel.sendMessage(
                                job.authorId,
                                job.companyName,
                                "السلام عليكم، أرغب بالتقديم على وظيفة: ${job.title}"
                            )
                            viewModel.navigateTo(AppScreen.MESSAGES)
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        // Footer
        item {
            FooterView(onNavigate = { screen -> viewModel.navigateTo(screen) })
        }
    }
}

@Composable
fun HeroBanner(
    onActionClick: (AppScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.linearGradient(
                    listOf(
                        Color(0xFF1E1B4B),
                        Color(0xFF311042),
                        Color(0xFF0F172A)
                    )
                )
            )
            .border(
                1.dp,
                Brush.horizontalGradient(listOf(GoldPrimary, PurpleSecondary)),
                RoundedCornerShape(20.dp)
            )
            .padding(20.dp)
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(
                    color = GoldPrimary.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary)
                ) {
                    Text(
                        text = "المنصة الرسمية لمدينة جرابلس",
                        color = GoldLight,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Icon(
                    imageVector = Icons.Default.Verified,
                    contentDescription = null,
                    tint = GoldPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "جرابلس اليوم في متناول يدك",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "تابع كل الأخبار، اعثر على عقارات للبيع والإيجار، تصفح منتجات السوق، واطلب خدمات أمهر الحرفيين في مدينتك بكل سهولة وأمان.",
                color = Color(0xFFCBD5E1),
                fontSize = 12.sp,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { onActionClick(AppScreen.PROPERTIES) },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.AddHome, null, tint = Color.Black, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("العقارات", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = { onActionClick(AppScreen.MARKET) },
                    colors = ButtonDefaults.buttonColors(containerColor = PurpleSecondary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Storefront, null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("السوق", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun QuickCategoriesRow(
    onCategoryClick: (AppScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        CategoryItem("الأخبار", Icons.Default.Article, AppScreen.NEWS, GoldDark),
        CategoryItem("الإعلانات", Icons.Default.Campaign, AppScreen.ANNOUNCEMENTS, PurpleSecondary),
        CategoryItem("العقارات", Icons.Default.Apartment, AppScreen.PROPERTIES, Color(0xFF0D9488)),
        CategoryItem("السوق", Icons.Default.ShoppingBag, AppScreen.MARKET, Color(0xFFEA580C)),
        CategoryItem("التجار", Icons.Default.Store, AppScreen.MERCHANTS, Color(0xFF2563EB)),
        CategoryItem("الخدمات", Icons.Default.Engineering, AppScreen.SERVICES, Color(0xFF7C3AED)),
        CategoryItem("الوظائف", Icons.Default.WorkOutline, AppScreen.JOBS, Color(0xFF059669)),
        CategoryItem("التخفيضات", Icons.Default.Percent, AppScreen.DISCOUNTS, Color(0xFFDC2626))
    )

    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(items) { item ->
            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .clickable { onCategoryClick(item.screen) },
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 2.dp,
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(item.color.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.title,
                            tint = item.color,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = item.title,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

data class CategoryItem(val title: String, val icon: ImageVector, val screen: AppScreen, val color: Color)

@Composable
fun DiscountCard(
    discount: DiscountEntity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .width(220.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 3.dp,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = Color(0xFFDC2626),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "خصم ${discount.discountPercent}%",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Text(
                    text = discount.validUntil,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = discount.productName,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "${discount.discountedPrice} $",
                    color = GoldDark,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "${discount.originalPrice} $",
                    color = Color.Gray,
                    fontSize = 11.sp,
                    style = MaterialTheme.typography.bodySmall.copy(textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = discount.merchantName,
                fontSize = 11.sp,
                color = PurpleSecondary
            )
        }
    }
}

@Composable
fun NewsItemCard(
    news: NewsEntity,
    onClick: () -> Unit,
    onLike: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() },
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
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
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }

                Text(
                    text = "${news.viewsCount} مشاهدة",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = news.title,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = news.content,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "بواسطة: ${news.authorName}",
                    fontSize = 11.sp,
                    color = GoldDark,
                    fontWeight = FontWeight.Medium
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.FavoriteBorder, null, tint = Color(0xFFE11D48), modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(3.dp))
                    Text("${news.likesCount}", fontSize = 11.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Icon(Icons.Outlined.ChatBubbleOutline, null, tint = PurpleSecondary, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(3.dp))
                    Text("${news.commentsCount}", fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
fun PropertyCompactCard(
    property: PropertyEntity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .width(240.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = if (property.type == PropertyType.SALE) Color(0xFFECFDF5) else Color(0xFFEFF6FF),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = property.type.labelAr,
                        color = if (property.type == PropertyType.SALE) Color(0xFF047857) else Color(0xFF1D4ED8),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Text(
                    text = "${property.price.toInt()} ${property.currency}",
                    color = GoldDark,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = property.title,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("🏠 ${property.rooms} غرف", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("📐 ${property.area.toInt()} م²", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("📍 ${property.district}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
fun ProductItemCard(
    product: ProductEntity,
    onClick: () -> Unit,
    onContact: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() },
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (product.isUsed) {
                        Surface(color = Color(0xFFFEF3C7), shape = RoundedCornerShape(4.dp)) {
                            Text("مستعمل", color = Color(0xFF92400E), fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                    }
                    Text(
                        text = product.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "${product.price} ${product.currency} • ${product.merchantName}",
                    fontSize = 11.sp,
                    color = GoldDark,
                    fontWeight = FontWeight.SemiBold
                )
            }

            IconButton(onClick = onContact) {
                Icon(
                    imageVector = Icons.Default.Chat,
                    contentDescription = "مراسلة",
                    tint = PurpleSecondary
                )
            }
        }
    }
}

@Composable
fun ServiceCompactCard(
    service: ServiceEntity,
    onContact: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .width(220.dp)
            .clip(RoundedCornerShape(14.dp)),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = service.title,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "👤 ${service.providerName}",
                fontSize = 11.sp,
                color = PurpleSecondary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "⭐ تقييم ${service.rating} • خبرة ${service.experienceYears} سنوات",
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onContact,
                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                Text("تواصل الآن", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun JobItemCard(
    job: JobEntity,
    onApply: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(job.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Surface(color = Color(0xFFECFDF5), shape = RoundedCornerShape(6.dp)) {
                    Text(job.salary, color = Color(0xFF065F46), fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text("${job.companyName} • 📍 ${job.location}", fontSize = 11.sp, color = PurpleSecondary)
            Spacer(modifier = Modifier.height(6.dp))
            Text(job.description, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onApply,
                colors = ButtonDefaults.buttonColors(containerColor = PurpleSecondary),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.align(Alignment.End),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text("تقديم وتواصل", color = Color.White, fontSize = 11.sp)
            }
        }
    }
}
