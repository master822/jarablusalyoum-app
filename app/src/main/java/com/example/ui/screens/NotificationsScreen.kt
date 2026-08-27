package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.NotificationEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.JarablusViewModel

@Composable
fun NotificationsScreen(
    viewModel: JarablusViewModel,
    modifier: Modifier = Modifier
) {
    val notifications by viewModel.notifications.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("مركز الإشعارات", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text("تنبيهات المراجعة، الموافقات، والرسائل الواردة", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            if (notifications.isNotEmpty()) {
                Row {
                    TextButton(onClick = { viewModel.markAllNotificationsRead() }) {
                        Text("قراءة الكل", fontSize = 11.sp, color = PurpleSecondary)
                    }
                    TextButton(onClick = { viewModel.clearAllNotifications() }) {
                        Text("مسح الكل", fontSize = 11.sp, color = StatusRejected)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (notifications.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Outlined.NotificationsOff,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("لا توجد إشعارات جديدة حالياً", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(notifications) { notif ->
                    NotificationCard(
                        notif = notif,
                        onRead = { viewModel.markNotificationRead(notif.id) },
                        onDelete = { viewModel.deleteNotification(notif.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun NotificationCard(
    notif: NotificationEntity,
    onRead: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = if (notif.isRead) MaterialTheme.colorScheme.surface else GoldContainerLight.copy(alpha = 0.5f),
        tonalElevation = if (notif.isRead) 1.dp else 3.dp,
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (notif.isRead) MaterialTheme.colorScheme.outline.copy(alpha = 0.2f) else GoldPrimary
        )
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        when (notif.type) {
                            "MODERATION" -> GoldPrimary
                            "MESSAGE" -> PurpleSecondary
                            "PAYMENT" -> Color(0xFF10B981)
                            else -> Color(0xFF3B82F6)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (notif.type) {
                        "MODERATION" -> Icons.Default.FactCheck
                        "MESSAGE" -> Icons.Default.Chat
                        "PAYMENT" -> Icons.Default.Payments
                        else -> Icons.Default.Info
                    },
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(notif.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(notif.message, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 16.sp)
            }

            IconButton(onClick = onDelete) {
                Icon(Icons.Default.DeleteOutline, contentDescription = "حذف", tint = Color.Gray, modifier = Modifier.size(18.dp))
            }
        }
    }
}
