package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface JarablusDao {

    // USERS
    @Query("SELECT * FROM users ORDER BY id ASC")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    suspend fun getUserById(userId: Long): UserEntity?

    @Query("SELECT * FROM users WHERE email = :identifier OR phone = :identifier LIMIT 1")
    suspend fun getUserByPhoneOrEmail(identifier: String): UserEntity?

    @Query("SELECT * FROM users WHERE role = :role")
    fun getUsersByRole(role: UserRole): Flow<List<UserEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity): Long

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("UPDATE users SET password = :password WHERE id = :userId")
    suspend fun updateUserPassword(userId: Long, password: String)

    @Query("UPDATE users SET portfolioImages = :images WHERE id = :userId")
    suspend fun updateUserPortfolio(userId: Long, images: String)

    @Query("UPDATE users SET subscriptionTier = :tier, productLimit = :limit WHERE id = :userId")
    suspend fun updateUserSubscription(userId: Long, tier: String, limit: Int)

    @Query("DELETE FROM users WHERE id = :userId")
    suspend fun deleteUser(userId: Long)

    // NEWS
    @Query("SELECT * FROM news WHERE status = 'APPROVED' ORDER BY createdAt DESC")
    fun getApprovedNews(): Flow<List<NewsEntity>>

    @Query("SELECT * FROM news WHERE status = 'PENDING' ORDER BY createdAt DESC")
    fun getPendingNews(): Flow<List<NewsEntity>>

    @Query("SELECT * FROM news WHERE authorId = :authorId ORDER BY createdAt DESC")
    fun getNewsByAuthor(authorId: Long): Flow<List<NewsEntity>>

    @Query("SELECT * FROM news WHERE id = :id LIMIT 1")
    suspend fun getNewsById(id: Long): NewsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNews(news: NewsEntity): Long

    @Query("UPDATE news SET status = :status, rejectionReason = :reason WHERE id = :id")
    suspend fun updateNewsStatus(id: Long, status: ModerationStatus, reason: String = "")

    @Query("UPDATE news SET likesCount = likesCount + :delta WHERE id = :id")
    suspend fun updateNewsLikes(id: Long, delta: Int)

    @Query("UPDATE news SET commentsCount = commentsCount + :delta WHERE id = :id")
    suspend fun updateNewsCommentsCount(id: Long, delta: Int)

    @Query("DELETE FROM news WHERE id = :id")
    suspend fun deleteNews(id: Long)

    // ANNOUNCEMENTS
    @Query("SELECT * FROM announcements WHERE status = 'APPROVED' ORDER BY createdAt DESC")
    fun getApprovedAnnouncements(): Flow<List<AnnouncementEntity>>

    @Query("SELECT * FROM announcements WHERE status = 'PENDING' ORDER BY createdAt DESC")
    fun getPendingAnnouncements(): Flow<List<AnnouncementEntity>>

    @Query("SELECT * FROM announcements WHERE authorId = :authorId ORDER BY createdAt DESC")
    fun getAnnouncementsByAuthor(authorId: Long): Flow<List<AnnouncementEntity>>

    @Query("SELECT * FROM announcements WHERE id = :id LIMIT 1")
    suspend fun getAnnouncementById(id: Long): AnnouncementEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnnouncement(announcement: AnnouncementEntity): Long

    @Query("UPDATE announcements SET status = :status, rejectionReason = :reason WHERE id = :id")
    suspend fun updateAnnouncementStatus(id: Long, status: ModerationStatus, reason: String = "")

    @Query("UPDATE announcements SET likesCount = likesCount + :delta WHERE id = :id")
    suspend fun updateAnnouncementLikes(id: Long, delta: Int)

    @Query("UPDATE announcements SET commentsCount = commentsCount + :delta WHERE id = :id")
    suspend fun updateAnnouncementCommentsCount(id: Long, delta: Int)

    @Query("DELETE FROM announcements WHERE id = :id")
    suspend fun deleteAnnouncement(id: Long)

    // PROPERTIES
    @Query("SELECT * FROM properties WHERE status = 'APPROVED' ORDER BY createdAt DESC")
    fun getApprovedProperties(): Flow<List<PropertyEntity>>

    @Query("SELECT * FROM properties WHERE status = 'APPROVED' AND (expiresAt = 0 OR expiresAt > :now) ORDER BY createdAt DESC")
    fun getActiveApprovedProperties(now: Long): Flow<List<PropertyEntity>>

    @Query("SELECT * FROM properties WHERE status = 'PENDING' ORDER BY createdAt DESC")
    fun getPendingProperties(): Flow<List<PropertyEntity>>

    @Query("SELECT * FROM properties WHERE status = 'APPROVED' AND type = :type AND (expiresAt = 0 OR expiresAt > :now) ORDER BY createdAt DESC")
    fun getActivePropertiesByType(type: PropertyType, now: Long): Flow<List<PropertyEntity>>

    @Query("SELECT * FROM properties WHERE status = 'APPROVED' AND type = :type ORDER BY createdAt DESC")
    fun getPropertiesByType(type: PropertyType): Flow<List<PropertyEntity>>

    @Query("SELECT * FROM properties WHERE authorId = :authorId ORDER BY createdAt DESC")
    fun getPropertiesByAuthor(authorId: Long): Flow<List<PropertyEntity>>

    @Query("SELECT * FROM properties WHERE id = :id LIMIT 1")
    suspend fun getPropertyById(id: Long): PropertyEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProperty(property: PropertyEntity): Long

    @Query("UPDATE properties SET status = :status, rejectionReason = :reason WHERE id = :id")
    suspend fun updatePropertyStatus(id: Long, status: ModerationStatus, reason: String = "")

    @Query("UPDATE properties SET status = :status, rejectionReason = :reason, activatedAt = :activatedAt, expiresAt = :expiresAt WHERE id = :id")
    suspend fun updatePropertyApproval(id: Long, status: ModerationStatus, reason: String = "", activatedAt: Long = 0L, expiresAt: Long = 0L)

    @Query("UPDATE properties SET likesCount = likesCount + :delta WHERE id = :id")
    suspend fun updatePropertyLikes(id: Long, delta: Int)

    @Query("UPDATE properties SET commentsCount = commentsCount + :delta WHERE id = :id")
    suspend fun updatePropertyCommentsCount(id: Long, delta: Int)

    @Query("DELETE FROM properties WHERE id = :id")
    suspend fun deleteProperty(id: Long)

    // PRODUCTS
    @Query("SELECT * FROM products ORDER BY createdAt DESC")
    fun getAllProducts(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE isUsed = 1 ORDER BY createdAt DESC")
    fun getUsedProducts(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE merchantId = :merchantId ORDER BY createdAt DESC")
    fun getProductsByMerchant(merchantId: Long): Flow<List<ProductEntity>>

    @Query("SELECT COUNT(*) FROM products WHERE merchantId = :merchantId")
    fun getProductCountByMerchant(merchantId: Long): Flow<Int>

    @Query("SELECT * FROM products WHERE id = :id LIMIT 1")
    suspend fun getProductById(id: Long): ProductEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductEntity): Long

    @Query("UPDATE products SET likesCount = likesCount + :delta WHERE id = :id")
    suspend fun updateProductLikes(id: Long, delta: Int)

    @Query("DELETE FROM products WHERE id = :id")
    suspend fun deleteProduct(id: Long)

    // SERVICES
    @Query("SELECT * FROM services ORDER BY rating DESC")
    fun getAllServices(): Flow<List<ServiceEntity>>

    @Query("SELECT * FROM services WHERE category = :category ORDER BY rating DESC")
    fun getServicesByCategory(category: String): Flow<List<ServiceEntity>>

    @Query("SELECT * FROM services WHERE providerId = :providerId")
    fun getServicesByProvider(providerId: Long): Flow<List<ServiceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertService(service: ServiceEntity): Long

    @Query("DELETE FROM services WHERE id = :id")
    suspend fun deleteService(id: Long)

    // JOBS
    @Query("SELECT * FROM jobs ORDER BY createdAt DESC")
    fun getAllJobs(): Flow<List<JobEntity>>

    @Query("SELECT * FROM jobs WHERE authorId = :authorId ORDER BY createdAt DESC")
    fun getJobsByAuthor(authorId: Long): Flow<List<JobEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJob(job: JobEntity): Long

    @Query("DELETE FROM jobs WHERE id = :id")
    suspend fun deleteJob(id: Long)

    // DISCOUNTS
    @Query("SELECT * FROM discounts WHERE isActive = 1 ORDER BY createdAt DESC")
    fun getActiveDiscounts(): Flow<List<DiscountEntity>>

    @Query("SELECT * FROM discounts WHERE merchantId = :merchantId ORDER BY createdAt DESC")
    fun getDiscountsByMerchant(merchantId: Long): Flow<List<DiscountEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDiscount(discount: DiscountEntity): Long

    @Query("DELETE FROM discounts WHERE id = :id")
    suspend fun deleteDiscount(id: Long)

    // COMMENTS
    @Query("SELECT * FROM comments WHERE contentType = :type AND contentId = :id AND status = 'APPROVED' ORDER BY createdAt ASC")
    fun getApprovedComments(type: String, id: Long): Flow<List<CommentEntity>>

    @Query("SELECT * FROM comments WHERE status = 'PENDING' ORDER BY createdAt DESC")
    fun getPendingComments(): Flow<List<CommentEntity>>

    @Query("SELECT * FROM comments ORDER BY createdAt DESC")
    fun getAllComments(): Flow<List<CommentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(comment: CommentEntity): Long

    @Query("UPDATE comments SET status = :status WHERE id = :id")
    suspend fun updateCommentStatus(id: Long, status: ModerationStatus)

    @Query("DELETE FROM comments WHERE id = :id")
    suspend fun deleteComment(id: Long)

    // LIKES
    @Query("SELECT * FROM likes WHERE contentType = :type AND contentId = :id AND userId = :userId LIMIT 1")
    suspend fun getUserLike(type: String, id: Long, userId: Long): LikeEntity?

    @Query("SELECT COUNT(*) FROM likes WHERE contentType = :type AND contentId = :id")
    fun getLikesCount(type: String, id: Long): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLike(like: LikeEntity): Long

    @Query("DELETE FROM likes WHERE contentType = :type AND contentId = :id AND userId = :userId")
    suspend fun deleteLike(type: String, id: Long, userId: Long)

    // NOTIFICATIONS
    @Query("SELECT * FROM notifications WHERE userId = :userId OR (userId = 0 AND :isAdmin = 1) ORDER BY createdAt DESC")
    fun getNotificationsForUser(userId: Long, isAdmin: Boolean): Flow<List<NotificationEntity>>

    @Query("SELECT COUNT(*) FROM notifications WHERE (userId = :userId OR (userId = 0 AND :isAdmin = 1)) AND isRead = 0")
    fun getUnreadNotificationsCount(userId: Long, isAdmin: Boolean): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity): Long

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markNotificationRead(id: Long)

    @Query("UPDATE notifications SET isRead = 1 WHERE userId = :userId OR (userId = 0 AND :isAdmin = 1)")
    suspend fun markAllNotificationsRead(userId: Long, isAdmin: Boolean)

    @Query("DELETE FROM notifications WHERE id = :id")
    suspend fun deleteNotification(id: Long)

    @Query("DELETE FROM notifications WHERE userId = :userId OR (userId = 0 AND :isAdmin = 1)")
    suspend fun deleteAllNotifications(userId: Long, isAdmin: Boolean)

    // MESSAGES
    @Query("SELECT * FROM messages WHERE (senderId = :u1 AND receiverId = :u2) OR (senderId = :u2 AND receiverId = :u1) ORDER BY timestamp ASC")
    fun getConversation(u1: Long, u2: Long): Flow<List<MessageEntity>>

    @Query("SELECT * FROM messages WHERE senderId = :userId OR receiverId = :userId ORDER BY timestamp DESC")
    fun getAllUserMessages(userId: Long): Flow<List<MessageEntity>>

    @Query("SELECT COUNT(*) FROM messages WHERE receiverId = :userId AND isRead = 0")
    fun getUnreadMessagesCount(userId: Long): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity): Long

    @Query("UPDATE messages SET isRead = 1 WHERE receiverId = :userId AND senderId = :otherId")
    suspend fun markMessagesAsRead(userId: Long, otherId: Long)

    // SUBSCRIPTIONS
    @Query("SELECT * FROM subscriptions ORDER BY createdAt DESC")
    fun getAllSubscriptions(): Flow<List<SubscriptionEntity>>

    @Query("SELECT * FROM subscriptions WHERE status = 'PENDING' ORDER BY createdAt DESC")
    fun getPendingSubscriptions(): Flow<List<SubscriptionEntity>>

    @Query("SELECT * FROM subscriptions WHERE id = :id LIMIT 1")
    suspend fun getSubscriptionById(id: Long): SubscriptionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubscription(subscription: SubscriptionEntity): Long

    @Query("UPDATE subscriptions SET status = :status WHERE id = :id")
    suspend fun updateSubscriptionStatus(id: Long, status: ModerationStatus)

    @Query("UPDATE subscriptions SET status = :status, activatedAt = :activatedAt WHERE id = :id")
    suspend fun updateSubscriptionApproval(id: Long, status: ModerationStatus, activatedAt: Long = 0L)
}
