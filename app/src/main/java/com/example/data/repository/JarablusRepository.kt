package com.example.data.repository

import com.example.data.db.JarablusDao
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

class JarablusRepository(private val dao: JarablusDao) {

    // USERS
    val allUsers: Flow<List<UserEntity>> = dao.getAllUsers()
    val merchants: Flow<List<UserEntity>> = dao.getUsersByRole(UserRole.MERCHANT)
    val serviceProviders: Flow<List<UserEntity>> = dao.getUsersByRole(UserRole.SERVICE_PROVIDER)

    suspend fun getUser(id: Long): UserEntity? = dao.getUserById(id)
    suspend fun insertUser(user: UserEntity): Long = dao.insertUser(user)
    suspend fun updateUser(user: UserEntity) = dao.updateUser(user)
    suspend fun deleteUser(userId: Long) = dao.deleteUser(userId)

    // NEWS
    val approvedNews: Flow<List<NewsEntity>> = dao.getApprovedNews()
    val pendingNews: Flow<List<NewsEntity>> = dao.getPendingNews()
    fun getNewsByAuthor(authorId: Long): Flow<List<NewsEntity>> = dao.getNewsByAuthor(authorId)
    suspend fun getNewsById(id: Long): NewsEntity? = dao.getNewsById(id)

    suspend fun submitNews(news: NewsEntity, isAdmin: Boolean): Long {
        val finalStatus = if (isAdmin) ModerationStatus.APPROVED else ModerationStatus.PENDING
        val id = dao.insertNews(news.copy(status = finalStatus))
        if (!isAdmin) {
            // Notify admin about pending review
            dao.insertNotification(
                NotificationEntity(
                    userId = 0, // Broadcast to admin
                    title = "خبر جديد بانتظار المراجعة",
                    message = "قام ${news.authorName} بإرسال خبر: '${news.title}' وهو بانتظار اعتمادك.",
                    type = "MODERATION",
                    relatedContentType = "NEWS",
                    relatedContentId = id
                )
            )
        }
        return id
    }

    suspend fun moderateNews(newsId: Long, status: ModerationStatus, reason: String = "") {
        dao.updateNewsStatus(newsId, status, reason)
        val news = dao.getNewsById(newsId) ?: return
        val titleMsg = if (status == ModerationStatus.APPROVED) "تمت الموافقة على نشر الخبر" else "تم رفض نشر الخبر"
        val descMsg = if (status == ModerationStatus.APPROVED) {
            "تمت الموافقة على خبرك '${news.title}' وأصبح متاحاً للجمهور."
        } else {
            "تم رفض خبرك '${news.title}'. سبب الرفض: ${if (reason.isNotBlank()) reason else "غير مطابق للمعايير"}"
        }
        dao.insertNotification(
            NotificationEntity(
                userId = news.authorId,
                title = titleMsg,
                message = descMsg,
                type = "MODERATION",
                relatedContentType = "NEWS",
                relatedContentId = newsId
            )
        )
    }

    suspend fun deleteNews(id: Long) = dao.deleteNews(id)

    // ANNOUNCEMENTS
    val approvedAnnouncements: Flow<List<AnnouncementEntity>> = dao.getApprovedAnnouncements()
    val pendingAnnouncements: Flow<List<AnnouncementEntity>> = dao.getPendingAnnouncements()
    fun getAnnouncementsByAuthor(authorId: Long): Flow<List<AnnouncementEntity>> = dao.getAnnouncementsByAuthor(authorId)
    suspend fun getAnnouncementById(id: Long): AnnouncementEntity? = dao.getAnnouncementById(id)

    suspend fun submitAnnouncement(announcement: AnnouncementEntity, isAdmin: Boolean): Long {
        val finalStatus = if (isAdmin) ModerationStatus.APPROVED else ModerationStatus.PENDING
        val id = dao.insertAnnouncement(announcement.copy(status = finalStatus))
        if (!isAdmin) {
            dao.insertNotification(
                NotificationEntity(
                    userId = 0,
                    title = "إعلان جديد بانتظار المراجعة",
                    message = "قام ${announcement.authorName} بنشر إعلان: '${announcement.title}'.",
                    type = "MODERATION",
                    relatedContentType = "ANNOUNCEMENT",
                    relatedContentId = id
                )
            )
        }
        return id
    }

    suspend fun moderateAnnouncement(announcementId: Long, status: ModerationStatus, reason: String = "") {
        dao.updateAnnouncementStatus(announcementId, status, reason)
        val ad = dao.getAnnouncementById(announcementId) ?: return
        val titleMsg = if (status == ModerationStatus.APPROVED) "تمت الموافقة على الإعلان" else "تم رفض نشر الإعلان"
        val descMsg = if (status == ModerationStatus.APPROVED) {
            "تمت الموافقة على إعلانك '${ad.title}' ونشره في المنصة."
        } else {
            "تم رفض إعلانك '${ad.title}'. سبب الرفض: ${if (reason.isNotBlank()) reason else "غير مطابق للشروط"}"
        }
        dao.insertNotification(
            NotificationEntity(
                userId = ad.authorId,
                title = titleMsg,
                message = descMsg,
                type = "MODERATION",
                relatedContentType = "ANNOUNCEMENT",
                relatedContentId = announcementId
            )
        )
    }

    suspend fun deleteAnnouncement(id: Long) = dao.deleteAnnouncement(id)

    // PROPERTIES
    val approvedProperties: Flow<List<PropertyEntity>> = dao.getApprovedProperties()
    val pendingProperties: Flow<List<PropertyEntity>> = dao.getPendingProperties()
    fun getActiveApprovedProperties(): Flow<List<PropertyEntity>> = dao.getActiveApprovedProperties(System.currentTimeMillis())
    fun getPropertiesByType(type: PropertyType): Flow<List<PropertyEntity>> = dao.getPropertiesByType(type)
    fun getPropertiesByAuthor(authorId: Long): Flow<List<PropertyEntity>> = dao.getPropertiesByAuthor(authorId)
    suspend fun getPropertyById(id: Long): PropertyEntity? = dao.getPropertyById(id)

    suspend fun submitProperty(property: PropertyEntity, isAdmin: Boolean): Long {
        val now = System.currentTimeMillis()
        val finalStatus = if (isAdmin) ModerationStatus.APPROVED else ModerationStatus.PENDING
        val activatedAt = if (isAdmin) now else 0L
        val expiresAt = if (isAdmin) now + 3 * 24 * 3600 * 1000L else 0L
        val id = dao.insertProperty(
            property.copy(
                status = finalStatus,
                activatedAt = activatedAt,
                expiresAt = expiresAt
            )
        )
        if (!isAdmin) {
            dao.insertNotification(
                NotificationEntity(
                    userId = 0,
                    title = "عقار جديد بانتظار تدقيق شام كاش (50 ل.ت)",
                    message = "قام ${property.authorName} بنشر عقار (${property.type.labelAr}): '${property.title}' مع إشعار دفع.",
                    type = "MODERATION",
                    relatedContentType = "PROPERTY",
                    relatedContentId = id
                )
            )
            // Notification to user
            dao.insertNotification(
                NotificationEntity(
                    userId = property.authorId,
                    title = "تم استلام طلب نشر عقارك",
                    message = "طلبك قيد المراجعة والتدقيق، وسيكون متاحاً للعامة خلال 24 ساعة كحد أقصى. مدة الإعلان 3 أيام تبدأ فور التفعيل من الإدارة.",
                    type = "MODERATION",
                    relatedContentType = "PROPERTY",
                    relatedContentId = id
                )
            )
        }
        return id
    }

    suspend fun moderateProperty(propertyId: Long, status: ModerationStatus, reason: String = "") {
        val now = System.currentTimeMillis()
        val activatedAt = if (status == ModerationStatus.APPROVED) now else 0L
        val expiresAt = if (status == ModerationStatus.APPROVED) now + 3 * 24 * 3600 * 1000L else 0L
        dao.updatePropertyApproval(propertyId, status, reason, activatedAt, expiresAt)
        val prop = dao.getPropertyById(propertyId) ?: return
        val titleMsg = if (status == ModerationStatus.APPROVED) "تمت الموافقة وتفعيل إعلان العقار" else "تم رفض نشر العقار"
        val descMsg = if (status == ModerationStatus.APPROVED) {
            "تهانينا! تمت الموافقة على عقارك '${prop.title}' وهو معروض للعامة الآن لمدة 3 أيام من لحظة التفعيل."
        } else {
            "تم رفض إعلان عقارك '${prop.title}'. سبب الرفض: ${if (reason.isNotBlank()) reason else "إشعار الدفع غير مكتمل أو غير مطابق"}"
        }
        dao.insertNotification(
            NotificationEntity(
                userId = prop.authorId,
                title = titleMsg,
                message = descMsg,
                type = "MODERATION",
                relatedContentType = "PROPERTY",
                relatedContentId = propertyId
            )
        )
    }

    suspend fun deleteProperty(id: Long) = dao.deleteProperty(id)

    // PRODUCTS
    val allProducts: Flow<List<ProductEntity>> = dao.getAllProducts()
    val usedProducts: Flow<List<ProductEntity>> = dao.getUsedProducts()
    fun getProductsByMerchant(merchantId: Long): Flow<List<ProductEntity>> = dao.getProductsByMerchant(merchantId)
    fun getMerchantProductCount(merchantId: Long): Flow<Int> = dao.getProductCountByMerchant(merchantId)
    suspend fun insertProduct(product: ProductEntity): Long = dao.insertProduct(product)
    suspend fun deleteProduct(id: Long) = dao.deleteProduct(id)

    // SERVICES
    val allServices: Flow<List<ServiceEntity>> = dao.getAllServices()
    fun getServicesByCategory(category: String): Flow<List<ServiceEntity>> = dao.getServicesByCategory(category)
    fun getServicesByProvider(providerId: Long): Flow<List<ServiceEntity>> = dao.getServicesByProvider(providerId)
    suspend fun insertService(service: ServiceEntity): Long = dao.insertService(service)
    suspend fun deleteService(id: Long) = dao.deleteService(id)

    // JOBS
    val allJobs: Flow<List<JobEntity>> = dao.getAllJobs()
    fun getJobsByAuthor(authorId: Long): Flow<List<JobEntity>> = dao.getJobsByAuthor(authorId)
    suspend fun insertJob(job: JobEntity): Long = dao.insertJob(job)
    suspend fun deleteJob(id: Long) = dao.deleteJob(id)

    // DISCOUNTS
    val activeDiscounts: Flow<List<DiscountEntity>> = dao.getActiveDiscounts()
    fun getDiscountsByMerchant(merchantId: Long): Flow<List<DiscountEntity>> = dao.getDiscountsByMerchant(merchantId)
    suspend fun insertDiscount(discount: DiscountEntity): Long = dao.insertDiscount(discount)
    suspend fun deleteDiscount(id: Long) = dao.deleteDiscount(id)

    // COMMENTS & INTERACTIONS
    fun getCommentsForContent(type: String, id: Long): Flow<List<CommentEntity>> = dao.getApprovedComments(type, id)
    val pendingComments: Flow<List<CommentEntity>> = dao.getPendingComments()
    val allComments: Flow<List<CommentEntity>> = dao.getAllComments()

    suspend fun addComment(comment: CommentEntity, isAdmin: Boolean) {
        val finalStatus = if (isAdmin) ModerationStatus.APPROVED else ModerationStatus.APPROVED // comments approved immediately or queued
        dao.insertComment(comment.copy(status = finalStatus))
        // Increment comment count on parent
        when (comment.contentType) {
            "NEWS" -> dao.updateNewsCommentsCount(comment.contentId, 1)
            "ANNOUNCEMENT" -> dao.updateAnnouncementCommentsCount(comment.contentId, 1)
            "PROPERTY" -> dao.updatePropertyCommentsCount(comment.contentId, 1)
        }
    }

    suspend fun moderateComment(commentId: Long, status: ModerationStatus) {
        dao.updateCommentStatus(commentId, status)
    }

    suspend fun deleteComment(comment: CommentEntity) {
        dao.deleteComment(comment.id)
        when (comment.contentType) {
            "NEWS" -> dao.updateNewsCommentsCount(comment.contentId, -1)
            "ANNOUNCEMENT" -> dao.updateAnnouncementCommentsCount(comment.contentId, -1)
            "PROPERTY" -> dao.updatePropertyCommentsCount(comment.contentId, -1)
        }
    }

    // LIKES (Polymorphic Unified Like System)
    suspend fun isLiked(type: String, id: Long, userId: Long): Boolean {
        return dao.getUserLike(type, id, userId) != null
    }

    suspend fun toggleLike(type: String, id: Long, userId: Long): Boolean {
        val existing = dao.getUserLike(type, id, userId)
        return if (existing != null) {
            dao.deleteLike(type, id, userId)
            when (type) {
                "NEWS" -> dao.updateNewsLikes(id, -1)
                "ANNOUNCEMENT" -> dao.updateAnnouncementLikes(id, -1)
                "PROPERTY" -> dao.updatePropertyLikes(id, -1)
                "PRODUCT" -> dao.updateProductLikes(id, -1)
            }
            false
        } else {
            dao.insertLike(LikeEntity(contentType = type, contentId = id, userId = userId))
            when (type) {
                "NEWS" -> dao.updateNewsLikes(id, 1)
                "ANNOUNCEMENT" -> dao.updateAnnouncementLikes(id, 1)
                "PROPERTY" -> dao.updatePropertyLikes(id, 1)
                "PRODUCT" -> dao.updateProductLikes(id, 1)
            }
            true
        }
    }

    // NOTIFICATIONS
    fun getNotifications(userId: Long, isAdmin: Boolean): Flow<List<NotificationEntity>> =
        dao.getNotificationsForUser(userId, isAdmin)

    fun getUnreadNotificationsCount(userId: Long, isAdmin: Boolean): Flow<Int> =
        dao.getUnreadNotificationsCount(userId, isAdmin)

    suspend fun markNotificationRead(id: Long) = dao.markNotificationRead(id)
    suspend fun markAllNotificationsRead(userId: Long, isAdmin: Boolean) = dao.markAllNotificationsRead(userId, isAdmin)
    suspend fun deleteNotification(id: Long) = dao.deleteNotification(id)
    suspend fun deleteAllNotifications(userId: Long, isAdmin: Boolean) = dao.deleteAllNotifications(userId, isAdmin)

    // MESSAGES
    fun getConversation(u1: Long, u2: Long): Flow<List<MessageEntity>> = dao.getConversation(u1, u2)
    fun getAllUserMessages(userId: Long): Flow<List<MessageEntity>> = dao.getAllUserMessages(userId)
    fun getUnreadMessagesCount(userId: Long): Flow<Int> = dao.getUnreadMessagesCount(userId)

    suspend fun sendMessage(message: MessageEntity): Long {
        val id = dao.insertMessage(message)
        // Notify recipient
        dao.insertNotification(
            NotificationEntity(
                userId = message.receiverId,
                title = "رسالة جديدة من ${message.senderName}",
                message = message.messageText.take(60),
                type = "MESSAGE"
            )
        )
        return id
    }

    suspend fun markMessagesRead(userId: Long, senderId: Long) = dao.markMessagesAsRead(userId, senderId)

    // SUBSCRIPTIONS
    val allSubscriptions: Flow<List<SubscriptionEntity>> = dao.getAllSubscriptions()
    val pendingSubscriptions: Flow<List<SubscriptionEntity>> = dao.getPendingSubscriptions()

    suspend fun requestSubscription(sub: SubscriptionEntity): Long {
        val id = dao.insertSubscription(sub)
        dao.insertNotification(
            NotificationEntity(
                userId = 0, // Admin
                title = "طلب اشتراك باقة تاجر (شام كاش)",
                message = "طلب التاجر ${sub.userName} ترقية إلى ${sub.planName} بمبلغ ${sub.amount} ${sub.currency} مع إشعار التحويل.",
                type = "PAYMENT",
                relatedContentId = id
            )
        )
        dao.insertNotification(
            NotificationEntity(
                userId = sub.userId,
                title = "تم استلام طلب الاشتراك في الباقة",
                message = "طلبك للاشتراك في ${sub.planName} قيد التدقيق وسيتم تفعيل الباقة خلال 24 ساعة كحد أقصى فور تأكيد الإشعار.",
                type = "PAYMENT",
                relatedContentId = id
            )
        )
        return id
    }

    suspend fun moderateSubscription(subId: Long, status: ModerationStatus) {
        val sub = dao.getSubscriptionById(subId) ?: return
        val now = System.currentTimeMillis()
        if (status == ModerationStatus.APPROVED) {
            dao.updateSubscriptionApproval(subId, status, now)
            val (tierName, limit) = when {
                sub.amount >= 35.0 || sub.planName.contains("الذهبي") -> "باقة التجار الذهبية" to 99999
                sub.amount >= 20.0 || sub.planName.contains("المتوسطة") -> "باقة التجار المتوسطة" to 80
                else -> "باقة التجار العادية" to 30
            }
            dao.updateUserSubscription(sub.userId, tierName, limit)
            dao.insertNotification(
                NotificationEntity(
                    userId = sub.userId,
                    title = "تهانينا! تم تفعيل باقة التاجر",
                    message = "تم تفعيل $tierName لحسابك بنجاح! يمكنك الآن نشر حتى ${if (limit >= 99999) "عدد غير محدود من" else limit} المنتجات في سوق جرابلس.",
                    type = "PAYMENT",
                    relatedContentId = subId
                )
            )
        } else {
            dao.updateSubscriptionStatus(subId, status)
            dao.insertNotification(
                NotificationEntity(
                    userId = sub.userId,
                    title = "تم رفض طلب ترقية الباقة",
                    message = "نعتذر، تم رفض طلبك للاشتراك في ${sub.planName}. يرجى التحقق من صحة إشعار شام كاش والتواصل مع الإدارة.",
                    type = "PAYMENT",
                    relatedContentId = subId
                )
            )
        }
    }

    // USER AUTH & PORTFOLIO
    suspend fun getUserByPhoneOrEmail(identifier: String): UserEntity? = dao.getUserByPhoneOrEmail(identifier)
    suspend fun updateUserPassword(userId: Long, password: String) = dao.updateUserPassword(userId, password)
    suspend fun updateUserPortfolio(userId: Long, images: String) = dao.updateUserPortfolio(userId, images)
}
