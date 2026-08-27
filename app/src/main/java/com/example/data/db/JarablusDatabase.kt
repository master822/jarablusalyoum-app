package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserEntity::class,
        NewsEntity::class,
        AnnouncementEntity::class,
        PropertyEntity::class,
        ProductEntity::class,
        ServiceEntity::class,
        JobEntity::class,
        DiscountEntity::class,
        CommentEntity::class,
        LikeEntity::class,
        NotificationEntity::class,
        MessageEntity::class,
        SubscriptionEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class JarablusDatabase : RoomDatabase() {

    abstract fun jarablusDao(): JarablusDao

    companion object {
        @Volatile
        private var INSTANCE: JarablusDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): JarablusDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    JarablusDatabase::class.java,
                    "jarablus_today_database"
                )
                .fallbackToDestructiveMigration()
                .addCallback(DatabaseCallback(scope))
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        seedDatabase(database.jarablusDao())
                    }
                }
            }
        }

        private suspend fun seedDatabase(dao: JarablusDao) {
            // Seed Users (All Roles)
            val adminUser = UserEntity(
                id = 1,
                name = "إدارة جرابلس اليوم",
                email = "admin@jarablustoday.sy",
                phone = "05310000001",
                city = "جرابلس",
                role = UserRole.ADMIN,
                isActive = true
            )
            val merchantUser = UserEntity(
                id = 2,
                name = "أحمد الحلبي",
                email = "ahmad@store.com",
                phone = "05310000002",
                city = "جرابلس",
                role = UserRole.MERCHANT,
                storeName = "مركز الفرات التجاري",
                storeDescription = "أكبر مركز تجاري للأجهزة الإلكترونية والمنزلية في جرابلس ومحيطها",
                subscriptionTier = "باقة التجار الذهبية",
                productLimit = 50,
                isActive = true
            )
            val serviceUser = UserEntity(
                id = 3,
                name = "المهندس محمود العلي",
                email = "mahmoud@tech.sy",
                phone = "05310000003",
                city = "جرابلس",
                role = UserRole.SERVICE_PROVIDER,
                serviceCategory = "مهندس كهرباء وطاقة شمسية",
                storeDescription = "تنفيذ وتصميم شبكات الكهرباء ومنظومات الطاقة الشمسية المنزلية والصناعية",
                isActive = true
            )
            val normalUser = UserEntity(
                id = 4,
                name = "عمر الجرابلسي",
                email = "omar@gmail.com",
                phone = "05310000004",
                city = "جرابلس",
                role = UserRole.USER,
                isActive = true
            )

            dao.insertUser(adminUser)
            dao.insertUser(merchantUser)
            dao.insertUser(serviceUser)
            dao.insertUser(normalUser)

            // Seed News
            dao.insertNews(
                NewsEntity(
                    id = 1,
                    title = "افتتاح مشروع تعبيد وصيانة الشارع الرئيسي وسوق المدينة في جرابلس",
                    content = "أعلنت بلدية جرابلس اليوم عن اكتمال المرحلة الأولى من مشروع إعادة تأهيل الطرق الرئيسية والإنارة في مركز المدينة لتسهيل حركة المواطنين والتجار وتحسين البنية التحتية.",
                    category = "أخبار المدينة",
                    authorId = 1,
                    authorName = "إدارة جرابلس اليوم",
                    authorRole = UserRole.ADMIN,
                    status = ModerationStatus.APPROVED,
                    viewsCount = 340,
                    likesCount = 28,
                    commentsCount = 3
                )
            )
            dao.insertNews(
                NewsEntity(
                    id = 2,
                    title = "بدء التسجيل على الدورات المهنية والتقنية المجانية لشباب المنطقة",
                    content = "أطلق المركز الثقافي بالتعاون مع المجلس المحلي حزمة تدريبية متقدمة في صيانة الهواتف، التمديدات الكهربائية، وبرمجة الحاسوب لدعم الكوادر المحلية.",
                    category = "خدمات",
                    authorId = 1,
                    authorName = "إدارة جرابلس اليوم",
                    authorRole = UserRole.ADMIN,
                    status = ModerationStatus.APPROVED,
                    viewsCount = 215,
                    likesCount = 19,
                    commentsCount = 2
                )
            )
            dao.insertNews(
                NewsEntity(
                    id = 3,
                    title = "مبادرة شبابية لتنظيف وتشجير كورنيش نهر الفرات بجرابلس",
                    content = "انطلقت صباح اليوم مبادرة تطوعية بمشاركة عشرات الشباب لتجميل ضفاف الفرات وزراعة أكثر من 300 غرسة دائمة الخضرة.",
                    category = "أحداث ومجتمع",
                    authorId = 4,
                    authorName = "عمر الجرابلسي",
                    authorRole = UserRole.USER,
                    status = ModerationStatus.APPROVED,
                    viewsCount = 180,
                    likesCount = 35,
                    commentsCount = 1
                )
            )
            // Pending News to demonstrate Admin Review flow
            dao.insertNews(
                NewsEntity(
                    id = 4,
                    title = "معرض المنتجات اليدوية والمأكولات التراثية نهاية الأسبوع",
                    content = "تنظيم معرض شعبي خاص بالأسر المنتجة في ساحة المدينة القديمة لعرض الحرف التقليدية والمأكولات الشعبية.",
                    category = "فعاليات",
                    authorId = 4,
                    authorName = "عمر الجرابلسي",
                    authorRole = UserRole.USER,
                    status = ModerationStatus.PENDING,
                    viewsCount = 1
                )
            )

            // Seed Announcements
            dao.insertAnnouncement(
                AnnouncementEntity(
                    id = 1,
                    title = "إعلان عن فقدان حقيبة أوراق ثبوتية بالقرب من المشفى العام",
                    content = "فُقدت حقيبة سوداء صغيرة تحتوي على وثائق شخصية باسم عمر الجرابلسي، يرجى ممن يعثر عليها التواصل وله مكافأة مجزية.",
                    category = "مفقودات وموجودات",
                    phone = "05310000004",
                    authorId = 4,
                    authorName = "عمر الجرابلسي",
                    status = ModerationStatus.APPROVED,
                    likesCount = 5
                )
            )
            dao.insertAnnouncement(
                AnnouncementEntity(
                    id = 2,
                    title = "تنويه حول انقطاع مؤقت لضخ المياه للصيانة يوم الخميس",
                    content = "سيتم إجراء صيانة دورية للمضخات الرئيسية من الساعة 8 صباحاً وحتى 2 ظهراً، يرجى من الأهالي أخذ الاحتياطات وتخزين المياه.",
                    category = "تنويهات خدمية",
                    phone = "05310000001",
                    authorId = 1,
                    authorName = "إدارة جرابلس اليوم",
                    status = ModerationStatus.APPROVED,
                    likesCount = 12
                )
            )
            dao.insertAnnouncement(
                AnnouncementEntity(
                    id = 3,
                    title = "افتتاح صيدلية مناوبة جديدة في الحي الشمالي",
                    content = "تم بعون الله افتتاح صيدلية الشفاء لتقديم الخدمات الدوائية على مدار 24 ساعة في شارع المدارس.",
                    category = "إعلانات تجارية",
                    phone = "05319988776",
                    authorId = 2,
                    authorName = "أحمد الحلبي",
                    status = ModerationStatus.PENDING
                )
            )

            // Seed Properties
            dao.insertProperty(
                PropertyEntity(
                    id = 1,
                    title = "شقة سكنية مفروشة للإيجار قرب شارع المصرف",
                    description = "شقة طابق أول بموقع مميز، 3 غرف نوم وصالون واسع، تكييف وإنترنت وطاقة شمسية 24 ساعة، مناسبة للعائلات.",
                    type = PropertyType.RENT,
                    price = 150.0,
                    currency = "USD",
                    city = "جرابلس",
                    district = "الحي الأوسط",
                    address = "شارع المصرف التجاري",
                    rooms = 3,
                    bathrooms = 1,
                    area = 135.0,
                    buildingAge = 3,
                    claddingType = "سوبر ديلوكس",
                    authorId = 2,
                    authorName = "أحمد الحلبي",
                    phone = "05310000002",
                    status = ModerationStatus.APPROVED,
                    likesCount = 14
                )
            )
            dao.insertProperty(
                PropertyEntity(
                    id = 2,
                    title = "منزل مستقل للبيع مع حديقة قريبة من نهر الفرات",
                    description = "بناء حديث طابق أرضي على مساحة 250 متر مربع، إكساء ممتاز، خزان ماء كبير، طابو نظامي، إطلالة هادئة ومفتوحة.",
                    type = PropertyType.SALE,
                    price = 28000.0,
                    currency = "USD",
                    city = "جرابلس",
                    district = "حي الفرات",
                    address = "طريق الكورنيش الشرقي",
                    rooms = 4,
                    bathrooms = 2,
                    area = 250.0,
                    buildingAge = 1,
                    claddingType = "ديلوكس",
                    authorId = 4,
                    authorName = "عمر الجرابلسي",
                    phone = "05310000004",
                    status = ModerationStatus.APPROVED,
                    likesCount = 22
                )
            )
            // Pending property for Admin moderation check
            dao.insertProperty(
                PropertyEntity(
                    id = 3,
                    title = "محل تجاري للإيجار في السوق المسقوف",
                    description = "محل بمساحة 40 متر مجهز برفوف وديكور كامل جاهز للعمل فوراً، واجهة زجاجية، كهرباء 24 ساعة.",
                    type = PropertyType.RENT,
                    price = 100.0,
                    currency = "USD",
                    city = "جرابلس",
                    district = "السوق الرئيسي",
                    address = "مقابل جامع الإيمان",
                    rooms = 1,
                    bathrooms = 1,
                    area = 40.0,
                    buildingAge = 5,
                    claddingType = "ممتاز",
                    authorId = 4,
                    authorName = "عمر الجرابلسي",
                    phone = "05310000004",
                    status = ModerationStatus.PENDING
                )
            )

            // Seed Products
            dao.insertProduct(
                ProductEntity(
                    id = 1,
                    name = "منظومة طاقة شمسية متكاملة 5000 واط",
                    description = "أنفرتر هجين ذكي + بطاريات ليثيوم 48V + 6 ألواح مونو عالية الكفاءة مع التركيب والضمان لمدة عامين.",
                    price = 1450.0,
                    currency = "USD",
                    category = "طاقة وكهرباء",
                    isUsed = false,
                    condition = "جديد",
                    quantity = 8,
                    merchantId = 2,
                    merchantName = "مركز الفرات التجاري",
                    merchantPhone = "05310000002",
                    likesCount = 18
                )
            )
            dao.insertProduct(
                ProductEntity(
                    id = 2,
                    name = "هاتف سامسونج Galaxy S23 Ultra بحالة الوكالة",
                    description = "استخدام شخصي نظيف جداً 256GB رام 12GB مع الكرتونة والشاحن الأصلي، خالي من أي خدوش.",
                    price = 620.0,
                    currency = "USD",
                    category = "إلكترونيات وموبايل",
                    isUsed = true,
                    condition = "مستعمل بحالة ممتازة",
                    quantity = 1,
                    merchantId = 4,
                    merchantName = "عمر الجرابلسي",
                    merchantPhone = "05310000004",
                    likesCount = 9
                )
            )
            dao.insertProduct(
                ProductEntity(
                    id = 3,
                    name = "لابتوب Dell Core i7 الجيل 12 للأعمال والتصميم",
                    description = "معالج i7-12700H، رامات 16GB، هارد 512GB NVMe، شاشة 15.6 بوصة FHD، بطارية ممتازة.",
                    price = 540.0,
                    currency = "USD",
                    category = "حواسيب وإلكترونيات",
                    isUsed = false,
                    condition = "جديد بالكرتونة",
                    quantity = 5,
                    merchantId = 2,
                    merchantName = "مركز الفرات التجاري",
                    merchantPhone = "05310000002",
                    likesCount = 12
                )
            )

            // Seed Services
            dao.insertService(
                ServiceEntity(
                    id = 1,
                    title = "تركيب وصيانة منظومات الطاقة الشمسية والكهرباء العامة",
                    description = "خبرة أكثر من 10 سنوات في صيانة وتركيب شبكات الكهرباء المنزلية والصناعية ومحطات الطاقة الهجينة بأحدث الأجهزة.",
                    category = "كهرباء وطاقة",
                    providerId = 3,
                    providerName = "المهندس محمود العلي",
                    providerPhone = "05310000003",
                    city = "جرابلس",
                    experienceYears = 10,
                    rating = 4.9f
                )
            )
            dao.insertService(
                ServiceEntity(
                    id = 2,
                    title = "أعمال السباكة والتمديدات الصحية الحديثة وكشف التسريبات",
                    description = "تنفيذ جميع أعمال السباكة والصحي للحمامات والمطابخ بأنابيب عالية الجودة وضمان عدم التسريب مع خدمة الطوارئ.",
                    category = "سباكة وصحي",
                    providerId = 3,
                    providerName = "أبو خالد للسباكة العامة",
                    providerPhone = "05315554433",
                    city = "جرابلس",
                    experienceYears = 8,
                    rating = 4.7f
                )
            )
            dao.insertService(
                ServiceEntity(
                    id = 3,
                    title = "برمجة وتصميم المواقع وتطبيقات الهواتف والأنظمة الإدارية",
                    description = "تطوير أنظمة المبيعات والمتاجر الإلكترونية وحلول الأتمتة للمحلات والمؤسسات بأسعار مناسبة ودعم فني مستمر.",
                    category = "برمجة وتصميم",
                    providerId = 3,
                    providerName = "فريق النخبة البرمجية",
                    providerPhone = "05310000003",
                    city = "جرابلس",
                    experienceYears = 6,
                    rating = 5.0f
                )
            )

            // Seed Jobs
            dao.insertJob(
                JobEntity(
                    id = 1,
                    title = "مطلوب محاسب مالي بخبرة لا تقل عن سنتين",
                    companyName = "مركز الفرات التجاري",
                    description = "متابعة الحسابات اليومية والمخزون وإعداد التقارير المالية لفرع جرابلس بدوام كامل.",
                    requirements = "إتقان برامج المحاسبة، خبرة سابقة، الالتزام وتحمل ضغط العمل",
                    location = "جرابلس - السوق الرئيسي",
                    salary = "400 - 600 $ حسب الخبرة",
                    contactPhone = "05310000002",
                    authorId = 2,
                    authorRole = UserRole.MERCHANT
                )
            )
            dao.insertJob(
                JobEntity(
                    id = 2,
                    title = "مطلوب فني صيانة كهربائية وتمديدات طاقة شمسية",
                    companyName = "مكتب العلي للهندسة والطاقة",
                    description = "العمل الميداني في تركيب وضبط المنظومات الشمسية المنزلية والتجارية مع فريق محترف.",
                    requirements = "خبرة عملية في ربط الأنفرترات واللوحات، رخصة قيادة دراجة نارية أو سيارة",
                    location = "جرابلس ومحيطها",
                    salary = "راتب مجزي + نسب مبيعات",
                    contactPhone = "05310000003",
                    authorId = 3,
                    authorRole = UserRole.SERVICE_PROVIDER
                )
            )

            // Seed Discounts
            dao.insertDiscount(
                DiscountEntity(
                    id = 1,
                    productName = "بطارية ليثيوم 100Ah 48V فوسفات الحديد",
                    originalPrice = 950.0,
                    discountedPrice = 820.0,
                    discountPercent = 14,
                    description = "عرض خاص لنهاية الشهر مع كفالة 3 سنوات وضمان 6000 دورة تفريغ",
                    merchantId = 2,
                    merchantName = "مركز الفرات التجاري",
                    merchantPhone = "05310000002",
                    validUntil = "حتى نهاية الشهر"
                )
            )
            dao.insertDiscount(
                DiscountEntity(
                    id = 2,
                    productName = "شاشة سمارت 50 بوصة 4K أندرويد",
                    originalPrice = 290.0,
                    discountedPrice = 245.0,
                    discountPercent = 16,
                    description = "شاشة فائقة الوضوح مع مدخلات HDMI وUSB وتطبيقات البث المباشر",
                    merchantId = 2,
                    merchantName = "مركز الفرات التجاري",
                    merchantPhone = "05310000002",
                    validUntil = "ساري حالياً"
                )
            )

            // Seed Comments
            dao.insertComment(
                CommentEntity(
                    id = 1,
                    contentType = "NEWS",
                    contentId = 1,
                    contentTitle = "مشروع تعبيد الشارع الرئيسي",
                    userId = 4,
                    userName = "عمر الجرابلسي",
                    commentText = "خطوة مباركة وجهد رائع من البلدية والقائمين على المشروع، نتمنى استكمال الشوارع الفرعية أيضاً.",
                    status = ModerationStatus.APPROVED
                )
            )
            dao.insertComment(
                CommentEntity(
                    id = 2,
                    contentType = "NEWS",
                    contentId = 1,
                    contentTitle = "مشروع تعبيد الشارع الرئيسي",
                    userId = 2,
                    userName = "أحمد الحلبي",
                    commentText = "هذا المشروع سهل كثيراً حركة الزبائن والشاحنات إلى مركز السوق.",
                    status = ModerationStatus.APPROVED
                )
            )

            // Seed Notifications
            dao.insertNotification(
                NotificationEntity(
                    id = 1,
                    userId = 0, // Admin broadcast notification
                    title = "طلب مراجعة محتوى جديد",
                    message = "قام المستخدم عمر الجرابلسي بنشر عقار جديد (محل تجاري في السوق المسقوف) وهو بانتظار موافقتك.",
                    type = "MODERATION",
                    relatedContentType = "PROPERTY",
                    relatedContentId = 3,
                    isRead = false
                )
            )
            dao.insertNotification(
                NotificationEntity(
                    id = 2,
                    userId = 4,
                    title = "تمت الموافقة على نشر العقار",
                    message = "تهانينا! تمت الموافقة على عقارك (منزل مستقل للبيع مع حديقة) وأصبح معروضاً للعامة.",
                    type = "MODERATION",
                    relatedContentType = "PROPERTY",
                    relatedContentId = 2,
                    isRead = true
                )
            )

            // Seed Messages
            dao.insertMessage(
                MessageEntity(
                    id = 1,
                    senderId = 4,
                    senderName = "عمر الجرابلسي",
                    receiverId = 2,
                    receiverName = "أحمد الحلبي",
                    messageText = "السلام عليكم أخ أحمد، هل منظومة الطاقة الشمسية 5000 واط متوفرة مع التركيب الفوري؟",
                    isRead = true,
                    timestamp = System.currentTimeMillis() - 3600000
                )
            )
            dao.insertMessage(
                MessageEntity(
                    id = 2,
                    senderId = 2,
                    senderName = "أحمد الحلبي",
                    receiverId = 4,
                    receiverName = "عمر الجرابلسي",
                    messageText = "وعليكم السلام ورحمة الله أخي عمر، نعم جاهزة ولدينا فريق فني للتركيب خلال 24 ساعة.",
                    isRead = false,
                    timestamp = System.currentTimeMillis() - 1800000
                )
            )

            // Seed Subscriptions
            dao.insertSubscription(
                SubscriptionEntity(
                    id = 1,
                    userId = 2,
                    userName = "أحمد الحلبي (مركز الفرات)",
                    planName = "باقة التجار الذهبية - Gold",
                    amount = 45.0,
                    currency = "USD",
                    status = ModerationStatus.APPROVED
                )
            )
            dao.insertSubscription(
                SubscriptionEntity(
                    id = 2,
                    userId = 4,
                    userName = "عمر الجرابلسي",
                    planName = "باقة التجار المتوسطة - Medium",
                    amount = 25.0,
                    currency = "USD",
                    status = ModerationStatus.PENDING
                )
            )
        }
    }
}
