package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
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
import com.example.data.model.UserRole
import com.example.ui.theme.*
import com.example.ui.viewmodel.JarablusViewModel

@Composable
fun ProfileScreen(
    viewModel: JarablusViewModel,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()

    var name by remember(currentUser) { mutableStateOf(currentUser.name) }
    var phone by remember(currentUser) { mutableStateOf(currentUser.phone) }
    var city by remember(currentUser) { mutableStateOf(currentUser.city) }
    var storeName by remember(currentUser) { mutableStateOf(currentUser.storeName) }
    var storeDesc by remember(currentUser) { mutableStateOf(currentUser.storeDescription) }
    var serviceCat by remember(currentUser) { mutableStateOf(currentUser.serviceCategory) }

    var saveSuccess by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(
                                when (currentUser.role) {
                                    UserRole.ADMIN -> StatusRejected
                                    UserRole.MERCHANT -> GoldPrimary
                                    UserRole.SERVICE_PROVIDER -> PurpleSecondary
                                    UserRole.USER -> Color(0xFF0284C7)
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Person, null, tint = Color.White, modifier = Modifier.size(30.dp))
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Text(currentUser.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text(currentUser.email, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(4.dp))
                        Surface(
                            color = GoldContainerLight,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "${currentUser.role.labelAr} • ${currentUser.subscriptionTier}",
                                color = GoldDark,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }

        item {
            Text("تعديل البيانات الشخصية", fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }

        item {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("الاسم الكامل") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("رقم الهاتف للتواصل") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            OutlinedTextField(
                value = city,
                onValueChange = { city = it },
                label = { Text("المدينة / المنطقة") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (currentUser.role == UserRole.MERCHANT) {
            item {
                OutlinedTextField(
                    value = storeName,
                    onValueChange = { storeName = it },
                    label = { Text("اسم المتجر أو النشاط التجاري") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item {
                OutlinedTextField(
                    value = storeDesc,
                    onValueChange = { storeDesc = it },
                    label = { Text("نبذة عن المتجر والمنتجات") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )
            }
        }

        if (currentUser.role == UserRole.SERVICE_PROVIDER) {
            item {
                OutlinedTextField(
                    value = serviceCat,
                    onValueChange = { serviceCat = it },
                    label = { Text("التخصص المهني (مثال: كهربائي، سباك، مبرمج...)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        item {
            Button(
                onClick = {
                    viewModel.updateProfile(name, phone, city, storeName, storeDesc, serviceCat)
                    saveSuccess = true
                },
                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth().testTag("save_profile_btn")
            ) {
                Icon(Icons.Default.Save, null, tint = Color.Black)
                Spacer(modifier = Modifier.width(6.dp))
                Text("حفظ التعديلات", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }

        if (currentUser.role == UserRole.MERCHANT) {
            item {
                OutlinedButton(
                    onClick = { viewModel.navigateTo(AppScreen.MERCHANT_SUBSCRIPTION_PLANS) },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = GoldDark),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.WorkspacePremium, null, tint = GoldDark)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("ترقية أو تجديد باقة المتجر (10$, 20$, 35$)", fontWeight = FontWeight.Bold)
                }
            }
        }

        if (currentUser.role == UserRole.SERVICE_PROVIDER) {
            item {
                OutlinedButton(
                    onClick = { viewModel.navigateTo(AppScreen.SERVICE_PROVIDER_PORTFOLIO) },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = PurpleSecondary),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.PhotoLibrary, null, tint = PurpleSecondary)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("إدارة معرض الأعمال (50 صورة) ورقم الواتساب", fontWeight = FontWeight.Bold)
                }
            }
        }

        item {
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { viewModel.navigateTo(AppScreen.LOGIN) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Login, null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("تسجيل دخول", fontSize = 12.sp)
                }

                OutlinedButton(
                    onClick = { viewModel.navigateTo(AppScreen.REGISTER) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.PersonAdd, null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("حساب جديد", fontSize = 12.sp)
                }
            }
        }

        item {
            Button(
                onClick = {
                    viewModel.logout()
                    viewModel.navigateTo(AppScreen.LOGIN)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626).copy(alpha = 0.15f)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.ExitToApp, null, tint = Color(0xFFDC2626))
                Spacer(modifier = Modifier.width(6.dp))
                Text("تسجيل الخروج من الحساب", color = Color(0xFFDC2626), fontWeight = FontWeight.Bold)
            }
        }

        if (saveSuccess) {
            item {
                Surface(
                    color = Color(0xFFD1FAE5),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "✅ تم حفظ التعديلات بنجاح!",
                        color = Color(0xFF065F46),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }
    }
}
