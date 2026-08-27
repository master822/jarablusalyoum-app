package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class UserRole(val labelAr: String) {
    USER("مستخدم عادي"),
    MERCHANT("تاجر"),
    SERVICE_PROVIDER("مقدم خدمة"),
    ADMIN("مدير النظام")
}

enum class ModerationStatus(val labelAr: String) {
    PENDING("قيد المراجعة"),
    APPROVED("تمت الموافقة"),
    REJECTED("مرفوض")
}

enum class PropertyType(val labelAr: String) {
    SALE("للبيع"),
    RENT("للإيجار")
}

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val email: String,
    val phone: String,
    val city: String = "جرابلس",
    val role: UserRole = UserRole.USER,
    val serviceCategory: String = "", // e.g. "كهربائي", "سباك", "مبرمج", "مدرس"
    val storeName: String = "",
    val storeDescription: String = "",
    val subscriptionTier: String = "مجاني", // "مجاني", "باقة عادية", "باقة متوسطة", "باقة ذهبية"
    val productLimit: Int = 5,
    val whatsapp: String = "", // WhatsApp contact for merchants/service providers
    val portfolioImages: String = "", // Comma/pipe separated image URLs (up to 50 photos)
    val password: String = "123456",
    val isActive: Boolean = true,
    val avatarUrl: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "news")
data class NewsEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val content: String,
    val category: String = "أخبار المدينة",
    val authorId: Long,
    val authorName: String,
    val authorRole: UserRole = UserRole.USER,
    val status: ModerationStatus = ModerationStatus.PENDING,
    val rejectionReason: String = "",
    val viewsCount: Int = 1,
    val likesCount: Int = 0,
    val commentsCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "announcements")
data class AnnouncementEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val content: String,
    val category: String = "إعلانات عامة",
    val phone: String,
    val authorId: Long,
    val authorName: String,
    val status: ModerationStatus = ModerationStatus.PENDING,
    val rejectionReason: String = "",
    val likesCount: Int = 0,
    val commentsCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "properties")
data class PropertyEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String,
    val type: PropertyType = PropertyType.SALE,
    val price: Double,
    val currency: String = "USD", // USD, TRY, SYP
    val city: String = "جرابلس",
    val district: String = "وسط المدينة",
    val address: String = "",
    val rooms: Int = 3,
    val bathrooms: Int = 1,
    val area: Double = 120.0, // m²
    val buildingAge: Int = 2,
    val claddingType: String = "سوبر ديلوكس", // عادي, وسط, ديلوكس, سوبر ديلوكس
    val images: String = "", // Up to 4 image URLs separated by |
    val paymentReceiptUrl: String = "", // Payment proof image URL
    val isPaid: Boolean = false,
    val paidAmount: Double = 50.0,
    val paidCurrency: String = "TRY",
    val activatedAt: Long = 0L, // Activation timestamp
    val expiresAt: Long = 0L, // 3 days after activation
    val authorId: Long,
    val authorName: String,
    val phone: String,
    val status: ModerationStatus = ModerationStatus.PENDING,
    val rejectionReason: String = "",
    val likesCount: Int = 0,
    val commentsCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val description: String,
    val price: Double,
    val currency: String = "USD",
    val category: String = "عام",
    val isUsed: Boolean = false,
    val condition: String = "جديد", // جديد, مستعمل بحالة ممتازة, مستعمل جيد
    val quantity: Int = 1,
    val merchantId: Long,
    val merchantName: String,
    val merchantPhone: String,
    val likesCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "services")
data class ServiceEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String,
    val category: String, // كهرباء, سباكة, نجارة, برمجيات, صيانة, تدريس
    val providerId: Long,
    val providerName: String,
    val providerPhone: String,
    val city: String = "جرابلس",
    val experienceYears: Int = 5,
    val rating: Float = 4.8f,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "jobs")
data class JobEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val companyName: String,
    val description: String,
    val requirements: String,
    val location: String = "جرابلس",
    val salary: String = "يحدد بعد المقابلة",
    val contactPhone: String,
    val authorId: Long,
    val authorRole: UserRole = UserRole.MERCHANT,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "discounts")
data class DiscountEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val productName: String,
    val originalPrice: Double,
    val discountedPrice: Double,
    val discountPercent: Int,
    val description: String,
    val merchantId: Long,
    val merchantName: String,
    val merchantPhone: String,
    val validUntil: String = "حتى نفاد الكمية",
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "comments")
data class CommentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val contentType: String, // "NEWS", "ANNOUNCEMENT", "PROPERTY"
    val contentId: Long,
    val contentTitle: String = "",
    val userId: Long,
    val userName: String,
    val commentText: String,
    val status: ModerationStatus = ModerationStatus.APPROVED,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "likes")
data class LikeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val contentType: String, // "NEWS", "ANNOUNCEMENT", "PROPERTY", "PRODUCT"
    val contentId: Long,
    val userId: Long
)

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long, // 0 for Admin broadcast/admin queue
    val title: String,
    val message: String,
    val type: String = "MODERATION", // "MODERATION", "SYSTEM", "MESSAGE", "PAYMENT"
    val relatedContentType: String = "",
    val relatedContentId: Long = 0,
    val isRead: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val senderId: Long,
    val senderName: String,
    val receiverId: Long,
    val receiverName: String,
    val messageText: String,
    val isRead: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "subscriptions")
data class SubscriptionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val userName: String,
    val storeName: String = "",
    val planName: String, // "باقة التجار العادية - Standard", "باقة التجار المتوسطة - Medium", "باقة التجار الذهبية - Gold"
    val amount: Double,
    val currency: String = "USD",
    val productsLimit: Int = 30,
    val receiptUrl: String = "", // Payment proof image URL
    val activatedAt: Long = 0L,
    val status: ModerationStatus = ModerationStatus.PENDING,
    val createdAt: Long = System.currentTimeMillis()
)
