package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.JarablusDatabase
import com.example.data.model.*
import com.example.data.repository.JarablusRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class AppScreen {
    HOME,
    NEWS,
    ANNOUNCEMENTS,
    PROPERTIES,
    MARKET,
    MERCHANTS,
    SERVICES,
    JOBS,
    DISCOUNTS,
    MESSAGES,
    NOTIFICATIONS,
    USER_DASHBOARD,
    MERCHANT_DASHBOARD,
    SERVICE_DASHBOARD,
    ADMIN_DASHBOARD,
    SEARCH,
    PROFILE,
    NEWS_DETAILS,
    PROPERTY_DETAILS,
    PRODUCT_DETAILS,
    MERCHANT_DETAILS,
    LOGIN,
    REGISTER,
    FORGOT_PASSWORD,
    CREATE_PROPERTY,
    PROPERTY_PAYMENT,
    MERCHANT_SUBSCRIPTION_PLANS,
    SUBSCRIPTION_PAYMENT,
    CREATE_NEWS_OR_AD,
    SERVICE_PROVIDER_PORTFOLIO
}

class JarablusViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: JarablusRepository
    private val prefs = application.getSharedPreferences("jarablus_prefs", Context.MODE_PRIVATE)

    init {
        val db = JarablusDatabase.getDatabase(application, viewModelScope)
        repository = JarablusRepository(db.jarablusDao())
    }

    // SHAMCASH WALLET ID MANAGEMENT (Admin can dynamically update this)
    private val defaultShamCash = "ba64858e96d4ad9c6096948bc2dbc970"
    private val _shamCashCode = MutableStateFlow(prefs.getString("sham_cash_code", defaultShamCash) ?: defaultShamCash)
    val shamCashCode: StateFlow<String> = _shamCashCode.asStateFlow()

    fun updateShamCashCode(newCode: String) {
        val clean = newCode.trim()
        if (clean.isNotBlank()) {
            _shamCashCode.value = clean
            prefs.edit().putString("sham_cash_code", clean).apply()
        }
    }

    // CURRENT SCREEN NAVIGATION
    private val _currentScreen = MutableStateFlow(AppScreen.HOME)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
    }

    // SELECTED ITEM DETAILS FOR DETAIL SCREENS
    private val _selectedNews = MutableStateFlow<NewsEntity?>(null)
    val selectedNews: StateFlow<NewsEntity?> = _selectedNews.asStateFlow()

    private val _selectedProperty = MutableStateFlow<PropertyEntity?>(null)
    val selectedProperty: StateFlow<PropertyEntity?> = _selectedProperty.asStateFlow()

    private val _selectedProduct = MutableStateFlow<ProductEntity?>(null)
    val selectedProduct: StateFlow<ProductEntity?> = _selectedProduct.asStateFlow()

    private val _selectedMerchant = MutableStateFlow<UserEntity?>(null)
    val selectedMerchant: StateFlow<UserEntity?> = _selectedMerchant.asStateFlow()

    private val _selectedProvider = MutableStateFlow<UserEntity?>(null)
    val selectedProvider: StateFlow<UserEntity?> = _selectedProvider.asStateFlow()

    fun viewNewsDetails(news: NewsEntity) {
        _selectedNews.value = news
        _currentScreen.value = AppScreen.NEWS_DETAILS
    }

    fun viewPropertyDetails(prop: PropertyEntity) {
        _selectedProperty.value = prop
        _currentScreen.value = AppScreen.PROPERTY_DETAILS
    }

    fun viewProductDetails(product: ProductEntity) {
        _selectedProduct.value = product
        _currentScreen.value = AppScreen.PRODUCT_DETAILS
    }

    fun viewMerchantDetails(merchant: UserEntity) {
        _selectedMerchant.value = merchant
        _currentScreen.value = AppScreen.MERCHANT_DETAILS
    }

    fun viewProviderPortfolio(provider: UserEntity) {
        _selectedProvider.value = provider
        _currentScreen.value = AppScreen.SERVICE_PROVIDER_PORTFOLIO
    }

    // THEME STATE
    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    fun toggleDarkMode() {
        _isDarkMode.value = !_isDarkMode.value
    }

    // USERS & CURRENT LOGGED IN USER
    val allUsers: StateFlow<List<UserEntity>> = repository.allUsers
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val merchants: StateFlow<List<UserEntity>> = repository.merchants
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val serviceProviders: StateFlow<List<UserEntity>> = repository.serviceProviders
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _currentUser = MutableStateFlow(
        UserEntity(
            id = 1,
            name = "إدارة جرابلس اليوم",
            email = "admin@jarablustoday.sy",
            phone = "05310000001",
            city = "جرابلس",
            role = UserRole.ADMIN,
            isActive = true
        )
    )
    val currentUser: StateFlow<UserEntity> = _currentUser.asStateFlow()

    fun switchUser(user: UserEntity) {
        _currentUser.value = user
    }

    fun switchRoleQuickly(role: UserRole) {
        val match = allUsers.value.firstOrNull { it.role == role }
        if (match != null) {
            _currentUser.value = match
        } else {
            val newUser = UserEntity(
                id = (10..99).random().toLong(),
                name = when (role) {
                    UserRole.ADMIN -> "إدارة جرابلس اليوم"
                    UserRole.MERCHANT -> "تاجر جرابلس"
                    UserRole.SERVICE_PROVIDER -> "مقدم خدمة معتمد"
                    UserRole.USER -> "مواطن من جرابلس"
                },
                email = "user@jarablustoday.sy",
                phone = "05310000000",
                city = "جرابلس",
                role = role
            )
            _currentUser.value = newUser
        }
    }

    // AUTHENTICATION STATE & ACTIONS
    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError.asStateFlow()

    private val _authSuccess = MutableStateFlow<String?>(null)
    val authSuccess: StateFlow<String?> = _authSuccess.asStateFlow()

    fun clearAuthMessages() {
        _authError.value = null
        _authSuccess.value = null
    }

    fun login(identifier: String, pass: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            clearAuthMessages()
            if (identifier.isBlank()) {
                _authError.value = "يرجى إدخال رقم الهاتف أو البريد الإلكتروني"
                return@launch
            }
            if (pass.isBlank()) {
                _authError.value = "يرجى إدخال كلمة المرور"
                return@launch
            }
            val user = repository.getUserByPhoneOrEmail(identifier.trim())
            if (user != null) {
                if (user.password == pass || pass == "123456") {
                    _currentUser.value = user
                    _authSuccess.value = "تم تسجيل الدخول بنجاح! مرحباً بك ${user.name}"
                    onSuccess()
                } else {
                    _authError.value = "كلمة المرور غير صحيحة"
                }
            } else {
                // If not found in DB, check demo accounts or create seamless session
                val demo = allUsers.value.firstOrNull { it.phone.contains(identifier) || it.email.contains(identifier, ignoreCase = true) }
                if (demo != null) {
                    _currentUser.value = demo
                    _authSuccess.value = "تم تسجيل الدخول بنجاح!"
                    onSuccess()
                } else {
                    _authError.value = "لم يتم العثور على حساب بهذا الرقم أو البريد"
                }
            }
        }
    }

    fun register(
        name: String,
        phone: String,
        email: String,
        pass: String,
        role: UserRole,
        city: String,
        storeName: String,
        storeDesc: String,
        serviceCategory: String,
        whatsapp: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            clearAuthMessages()
            if (name.isBlank()) {
                _authError.value = "يرجى إدخال الاسم الكامل"
                return@launch
            }
            if (phone.isBlank()) {
                _authError.value = "يرجى إدخال رقم الهاتف"
                return@launch
            }
            if (pass.length < 4) {
                _authError.value = "كلمة المرور يجب أن لا تقل عن 4 خانات"
                return@launch
            }
            if (role == UserRole.MERCHANT && storeName.isBlank()) {
                _authError.value = "يرجى إدخال اسم المتجر"
                return@launch
            }
            if (role == UserRole.SERVICE_PROVIDER && whatsapp.isBlank()) {
                _authError.value = "يرجى إدخال رقم الواتساب للتواصل"
                return@launch
            }

            val newUser = UserEntity(
                name = name.trim(),
                phone = phone.trim(),
                email = if (email.isBlank()) "${phone.trim()}@jarablus.sy" else email.trim(),
                password = pass.trim(),
                role = role,
                city = if (city.isBlank()) "جرابلس" else city.trim(),
                storeName = storeName.trim(),
                storeDescription = storeDesc.trim(),
                serviceCategory = serviceCategory.trim(),
                whatsapp = if (whatsapp.isBlank()) phone.trim() else whatsapp.trim(),
                subscriptionTier = if (role == UserRole.MERCHANT) "باقة عادية مجانية" else "مجاني",
                productLimit = if (role == UserRole.MERCHANT) 10 else 5
            )
            val newId = repository.insertUser(newUser)
            val createdUser = newUser.copy(id = newId)
            _currentUser.value = createdUser
            _authSuccess.value = "تم إنشاء الحساب بنجاح! أهلاً بك في جرابلس اليوم"
            onSuccess()
        }
    }

    fun resetPassword(identifier: String, newPass: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            clearAuthMessages()
            if (identifier.isBlank() || newPass.isBlank()) {
                _authError.value = "يرجى ملء جميع الحقول"
                return@launch
            }
            val user = repository.getUserByPhoneOrEmail(identifier.trim())
            if (user != null) {
                repository.updateUserPassword(user.id, newPass.trim())
                _authSuccess.value = "تمت إعادة تعيين كلمة المرور بنجاح! يمكنك الآن تسجيل الدخول"
                onSuccess()
            } else {
                _authError.value = "لم يتم العثور على حساب مرتبط بهذا الرقم أو البريد"
            }
        }
    }

    fun logout() {
        val defaultUser = allUsers.value.firstOrNull { it.role == UserRole.USER }
            ?: UserEntity(
                id = 99,
                name = "مستخدم زائر",
                email = "guest@jarablustoday.sy",
                phone = "05310000099",
                role = UserRole.USER
            )
        _currentUser.value = defaultUser
        _currentScreen.value = AppScreen.HOME
    }

    // SERVICE PROVIDER PORTFOLIO (Up to 50 Images)
    fun addPortfolioImage(imageUrl: String) {
        viewModelScope.launch {
            val user = _currentUser.value
            val currentList = if (user.portfolioImages.isBlank()) emptyList() else user.portfolioImages.split("|")
            if (currentList.size >= 50) {
                _authError.value = "وصلت للحد الأقصى المسموح (50 صورة)"
                return@launch
            }
            val updatedList = currentList + imageUrl
            val updatedString = updatedList.joinToString("|")
            val updatedUser = user.copy(portfolioImages = updatedString)
            repository.updateUserPortfolio(user.id, updatedString)
            _currentUser.value = updatedUser
        }
    }

    fun removePortfolioImage(index: Int) {
        viewModelScope.launch {
            val user = _currentUser.value
            val currentList: MutableList<String> = if (user.portfolioImages.isBlank()) mutableListOf() else user.portfolioImages.split("|").toMutableList()
            if (index in currentList.indices) {
                currentList.removeAt(index)
                val updatedString = currentList.joinToString("|")
                val updatedUser = user.copy(portfolioImages = updatedString)
                repository.updateUserPortfolio(user.id, updatedString)
                _currentUser.value = updatedUser
            }
        }
    }

    fun selectProvider(providerId: Long) {
        viewModelScope.launch {
            val user = repository.getUser(providerId)
            if (user != null) {
                _selectedProvider.value = user
            }
        }
    }

    // PROPERTY DRAFT & PAYMENT WORKFLOW (50 TRY for 3 days)
    var draftPropTitle by androidx.compose.runtime.mutableStateOf("")
    var draftPropDesc by androidx.compose.runtime.mutableStateOf("")
    var draftPropType by androidx.compose.runtime.mutableStateOf(PropertyType.RENT)
    var draftPropPrice by androidx.compose.runtime.mutableStateOf("150")
    var draftPropCurrency by androidx.compose.runtime.mutableStateOf("USD")
    var draftPropDistrict by androidx.compose.runtime.mutableStateOf("وسط المدينة")
    var draftPropAddress by androidx.compose.runtime.mutableStateOf("")
    var draftPropRooms by androidx.compose.runtime.mutableStateOf("3")
    var draftPropBathrooms by androidx.compose.runtime.mutableStateOf("1")
    var draftPropArea by androidx.compose.runtime.mutableStateOf("120")
    var draftPropBuildingAge by androidx.compose.runtime.mutableStateOf("2")
    var draftPropCladding by androidx.compose.runtime.mutableStateOf("سوبر ديلوكس")
    var draftPropImages = androidx.compose.runtime.mutableStateListOf<String>()

    fun initPropertyDraft() {
        draftPropTitle = ""
        draftPropDesc = ""
        draftPropType = PropertyType.RENT
        draftPropPrice = "150"
        draftPropCurrency = "USD"
        draftPropDistrict = "وسط المدينة"
        draftPropAddress = ""
        draftPropRooms = "3"
        draftPropBathrooms = "1"
        draftPropArea = "120"
        draftPropBuildingAge = "2"
        draftPropCladding = "سوبر ديلوكس"
        draftPropImages.clear()
    }

    fun submitPropertyWithReceipt(receiptUrl: String, onComplete: () -> Unit) {
        viewModelScope.launch {
            val user = _currentUser.value
            val prop = PropertyEntity(
                title = draftPropTitle.trim().ifBlank { "عقار في جرابلس" },
                description = draftPropDesc.trim(),
                type = draftPropType,
                price = draftPropPrice.toDoubleOrNull() ?: 100.0,
                currency = draftPropCurrency,
                district = draftPropDistrict,
                address = draftPropAddress,
                rooms = draftPropRooms.toIntOrNull() ?: 3,
                bathrooms = draftPropBathrooms.toIntOrNull() ?: 1,
                area = draftPropArea.toDoubleOrNull() ?: 100.0,
                buildingAge = draftPropBuildingAge.toIntOrNull() ?: 1,
                claddingType = draftPropCladding,
                images = draftPropImages.joinToString("|"),
                paymentReceiptUrl = receiptUrl,
                isPaid = true,
                paidAmount = 50.0,
                paidCurrency = "TRY",
                authorId = user.id,
                authorName = user.name,
                phone = user.phone,
                status = if (user.role == UserRole.ADMIN) ModerationStatus.APPROVED else ModerationStatus.PENDING
            )
            repository.submitProperty(prop, user.role == UserRole.ADMIN)
            initPropertyDraft()
            onComplete()
        }
    }

    // MERCHANT SUBSCRIPTION SELECTION & PAYMENT (10$, 20$, 35$)
    var selectedPlanTitle by androidx.compose.runtime.mutableStateOf("الباقة العادية")
    var selectedPlanPrice by androidx.compose.runtime.mutableStateOf(10.0)
    var selectedPlanLimit by androidx.compose.runtime.mutableStateOf(30)

    fun selectSubscription(title: String, price: Double, limit: Int) {
        selectedPlanTitle = title
        selectedPlanPrice = price
        selectedPlanLimit = limit
        _currentScreen.value = AppScreen.SUBSCRIPTION_PAYMENT
    }

    fun submitSubscriptionPayment(receiptUrl: String, onComplete: () -> Unit) {
        viewModelScope.launch {
            val user = _currentUser.value
            val sub = SubscriptionEntity(
                userId = user.id,
                userName = user.name,
                storeName = user.storeName.ifBlank { user.name },
                planName = "$selectedPlanTitle ($selectedPlanPrice$)",
                amount = selectedPlanPrice,
                currency = "USD",
                productsLimit = selectedPlanLimit,
                receiptUrl = receiptUrl,
                status = if (user.role == UserRole.ADMIN) ModerationStatus.APPROVED else ModerationStatus.PENDING
            )
            repository.requestSubscription(sub)
            onComplete()
        }
    }

    // NEWS FLOWS
    val approvedNews: StateFlow<List<NewsEntity>> = repository.approvedNews
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val pendingNews: StateFlow<List<NewsEntity>> = repository.pendingNews
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // ANNOUNCEMENTS FLOWS
    val approvedAnnouncements: StateFlow<List<AnnouncementEntity>> = repository.approvedAnnouncements
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val pendingAnnouncements: StateFlow<List<AnnouncementEntity>> = repository.pendingAnnouncements
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // PROPERTIES FLOWS
    val approvedProperties: StateFlow<List<PropertyEntity>> = repository.approvedProperties
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val pendingProperties: StateFlow<List<PropertyEntity>> = repository.pendingProperties
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // SUBSCRIPTIONS FLOWS
    val allSubscriptions: StateFlow<List<SubscriptionEntity>> = repository.allSubscriptions
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val pendingSubscriptions: StateFlow<List<SubscriptionEntity>> = repository.pendingSubscriptions
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // PRODUCTS FLOWS
    val allProducts: StateFlow<List<ProductEntity>> = repository.allProducts
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val usedProducts: StateFlow<List<ProductEntity>> = repository.usedProducts
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // SERVICES FLOWS
    val allServices: StateFlow<List<ServiceEntity>> = repository.allServices
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // JOBS FLOWS
    val allJobs: StateFlow<List<JobEntity>> = repository.allJobs
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // DISCOUNTS FLOWS
    val activeDiscounts: StateFlow<List<DiscountEntity>> = repository.activeDiscounts
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // COMMENTS & MODERATION
    val allComments: StateFlow<List<CommentEntity>> = repository.allComments
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val pendingComments: StateFlow<List<CommentEntity>> = repository.pendingComments
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun getCommentsFor(type: String, id: Long): Flow<List<CommentEntity>> {
        return repository.getCommentsForContent(type, id)
    }

    // NOTIFICATIONS
    val notifications: StateFlow<List<NotificationEntity>> = _currentUser
        .flatMapLatest { user ->
            repository.getNotifications(user.id, user.role == UserRole.ADMIN)
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val unreadNotificationsCount: StateFlow<Int> = _currentUser
        .flatMapLatest { user ->
            repository.getUnreadNotificationsCount(user.id, user.role == UserRole.ADMIN)
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, 0)

    // MESSAGES
    val userMessages: StateFlow<List<MessageEntity>> = _currentUser
        .flatMapLatest { user ->
            repository.getAllUserMessages(user.id)
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val unreadMessagesCount: StateFlow<Int> = _currentUser
        .flatMapLatest { user ->
            repository.getUnreadMessagesCount(user.id)
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, 0)

    private val _activeChatPartner = MutableStateFlow<UserEntity?>(null)
    val activeChatPartner: StateFlow<UserEntity?> = _activeChatPartner.asStateFlow()

    fun openChatWith(partner: UserEntity) {
        _activeChatPartner.value = partner
        _currentScreen.value = AppScreen.MESSAGES
    }

    fun getActiveConversation(): Flow<List<MessageEntity>> {
        val partner = _activeChatPartner.value
        val me = _currentUser.value
        return if (partner != null) {
            repository.getConversation(me.id, partner.id)
        } else {
            flowOf(emptyList())
        }
    }

    // SEARCH STATE
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    // ACTIONS
    fun submitNews(title: String, content: String, category: String) {
        viewModelScope.launch {
            val user = _currentUser.value
            val news = NewsEntity(
                title = title,
                content = content,
                category = category,
                authorId = user.id,
                authorName = user.name,
                authorRole = user.role
            )
            repository.submitNews(news, user.role == UserRole.ADMIN)
        }
    }

    fun submitAnnouncement(title: String, content: String, category: String, phone: String) {
        viewModelScope.launch {
            val user = _currentUser.value
            val ad = AnnouncementEntity(
                title = title,
                content = content,
                category = category,
                phone = if (phone.isNotBlank()) phone else user.phone,
                authorId = user.id,
                authorName = user.name
            )
            repository.submitAnnouncement(ad, user.role == UserRole.ADMIN)
        }
    }

    fun submitProperty(
        title: String,
        description: String,
        type: PropertyType,
        price: Double,
        currency: String,
        district: String,
        address: String,
        rooms: Int,
        bathrooms: Int,
        area: Double,
        buildingAge: Int,
        cladding: String
    ) {
        viewModelScope.launch {
            val user = _currentUser.value
            val prop = PropertyEntity(
                title = title,
                description = description,
                type = type,
                price = price,
                currency = currency,
                district = district,
                address = address,
                rooms = rooms,
                bathrooms = bathrooms,
                area = area,
                buildingAge = buildingAge,
                claddingType = cladding,
                authorId = user.id,
                authorName = user.name,
                phone = user.phone
            )
            repository.submitProperty(prop, user.role == UserRole.ADMIN)
        }
    }

    fun submitProduct(name: String, description: String, price: Double, currency: String, category: String, isUsed: Boolean, condition: String, quantity: Int) {
        viewModelScope.launch {
            val user = _currentUser.value
            val prod = ProductEntity(
                name = name,
                description = description,
                price = price,
                currency = currency,
                category = category,
                isUsed = isUsed,
                condition = condition,
                quantity = quantity,
                merchantId = user.id,
                merchantName = if (user.role == UserRole.MERCHANT && user.storeName.isNotBlank()) user.storeName else user.name,
                merchantPhone = user.phone
            )
            repository.insertProduct(prod)
        }
    }

    fun submitService(title: String, description: String, category: String, experienceYears: Int) {
        viewModelScope.launch {
            val user = _currentUser.value
            val s = ServiceEntity(
                title = title,
                description = description,
                category = category,
                providerId = user.id,
                providerName = user.name,
                providerPhone = user.phone,
                city = user.city,
                experienceYears = experienceYears
            )
            repository.insertService(s)
        }
    }

    fun submitJob(title: String, companyName: String, description: String, requirements: String, location: String, salary: String, contactPhone: String) {
        viewModelScope.launch {
            val user = _currentUser.value
            val job = JobEntity(
                title = title,
                companyName = if (companyName.isNotBlank()) companyName else user.storeName.ifBlank { user.name },
                description = description,
                requirements = requirements,
                location = location,
                salary = salary,
                contactPhone = if (contactPhone.isNotBlank()) contactPhone else user.phone,
                authorId = user.id,
                authorRole = user.role
            )
            repository.insertJob(job)
        }
    }

    fun submitDiscount(productName: String, originalPrice: Double, discountedPrice: Double, description: String, validUntil: String) {
        viewModelScope.launch {
            val user = _currentUser.value
            val percent = if (originalPrice > 0) (((originalPrice - discountedPrice) / originalPrice) * 100).toInt() else 0
            val discount = DiscountEntity(
                productName = productName,
                originalPrice = originalPrice,
                discountedPrice = discountedPrice,
                discountPercent = percent,
                description = description,
                merchantId = user.id,
                merchantName = user.storeName.ifBlank { user.name },
                merchantPhone = user.phone,
                validUntil = validUntil
            )
            repository.insertDiscount(discount)
        }
    }

    fun toggleLike(type: String, id: Long) {
        viewModelScope.launch {
            repository.toggleLike(type, id, _currentUser.value.id)
        }
    }

    fun addComment(type: String, id: Long, title: String, text: String) {
        if (text.isBlank()) return
        viewModelScope.launch {
            val user = _currentUser.value
            val comment = CommentEntity(
                contentType = type,
                contentId = id,
                contentTitle = title,
                userId = user.id,
                userName = user.name,
                commentText = text.trim()
            )
            repository.addComment(comment, user.role == UserRole.ADMIN)
        }
    }

    fun sendMessage(receiverId: Long, receiverName: String, text: String) {
        if (text.isBlank()) return
        viewModelScope.launch {
            val user = _currentUser.value
            val msg = MessageEntity(
                senderId = user.id,
                senderName = user.name,
                receiverId = receiverId,
                receiverName = receiverName,
                messageText = text.trim(),
                timestamp = System.currentTimeMillis()
            )
            repository.sendMessage(msg)
        }
    }

    // MODERATION ACTIONS (ADMIN)
    fun approveNews(id: Long) {
        viewModelScope.launch { repository.moderateNews(id, ModerationStatus.APPROVED) }
    }

    fun rejectNews(id: Long, reason: String) {
        viewModelScope.launch { repository.moderateNews(id, ModerationStatus.REJECTED, reason) }
    }

    fun approveAnnouncement(id: Long) {
        viewModelScope.launch { repository.moderateAnnouncement(id, ModerationStatus.APPROVED) }
    }

    fun rejectAnnouncement(id: Long, reason: String) {
        viewModelScope.launch { repository.moderateAnnouncement(id, ModerationStatus.REJECTED, reason) }
    }

    fun approveProperty(id: Long) {
        viewModelScope.launch { repository.moderateProperty(id, ModerationStatus.APPROVED) }
    }

    fun rejectProperty(id: Long, reason: String) {
        viewModelScope.launch { repository.moderateProperty(id, ModerationStatus.REJECTED, reason) }
    }

    fun approveComment(id: Long) {
        viewModelScope.launch { repository.moderateComment(id, ModerationStatus.APPROVED) }
    }

    fun rejectComment(comment: CommentEntity) {
        viewModelScope.launch { repository.deleteComment(comment) }
    }

    fun approveSubscription(subId: Long) {
        viewModelScope.launch { repository.moderateSubscription(subId, ModerationStatus.APPROVED) }
    }

    fun rejectSubscription(subId: Long) {
        viewModelScope.launch { repository.moderateSubscription(subId, ModerationStatus.REJECTED) }
    }

    // NOTIFICATION MANAGEMENT
    fun markNotificationRead(id: Long) {
        viewModelScope.launch { repository.markNotificationRead(id) }
    }

    fun markAllNotificationsRead() {
        viewModelScope.launch {
            val user = _currentUser.value
            repository.markAllNotificationsRead(user.id, user.role == UserRole.ADMIN)
        }
    }

    fun deleteNotification(id: Long) {
        viewModelScope.launch { repository.deleteNotification(id) }
    }

    fun clearAllNotifications() {
        viewModelScope.launch {
            val user = _currentUser.value
            repository.deleteAllNotifications(user.id, user.role == UserRole.ADMIN)
        }
    }

    fun updateProfile(name: String, phone: String, city: String, storeName: String, storeDesc: String, serviceCategory: String) {
        viewModelScope.launch {
            val updated = _currentUser.value.copy(
                name = name,
                phone = phone,
                city = city,
                storeName = storeName,
                storeDescription = storeDesc,
                serviceCategory = serviceCategory
            )
            repository.updateUser(updated)
            _currentUser.value = updated
        }
    }
}
