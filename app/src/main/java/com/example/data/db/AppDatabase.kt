package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.Customer
import com.example.data.model.InsurancePolicy
import com.example.data.model.PaymentVoucher
import com.example.data.model.PolicyInstallment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        Customer::class,
        InsurancePolicy::class,
        PolicyInstallment::class,
        PaymentVoucher::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun insuranceDao(): InsuranceDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "insurance_management_db"
                )
                    .addCallback(DatabaseCallback(scope))
                    .fallbackToDestructiveMigration()
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
                        populateInitialData(database.insuranceDao())
                    }
                }
            }
        }

        suspend fun populateInitialData(dao: InsuranceDao) {
            // مشتری ۱: علی رضایی - بیمه شخص ثالث با ۷ قسط که ۳ قسط پرداخت شده و ۴ قسط (۴۰۰ هزار تومان یا ۴ میلیون) مانده
            val c1Id = dao.insertCustomer(
                Customer(
                    fullName = "علی رضایی",
                    nationalCode = "۰۰۱۸۲۳۴۵۶۱",
                    phone = "۰۹۱۲۳۴۵۶۷۸۹",
                    birthDate = "۱۳۶۵/۰۴/۱۵",
                    postalCode = "۱۴۵۶۷۸۹۱۱۲",
                    address = "تهران، خیابان آزادی، کوچه مریم، پلاک ۱۲",
                    notes = "مشتری خوش‌حساب، خودرو پژو پارس سفید، یادآوری تمدید سالانه"
                )
            )

            val p1Id = dao.insertPolicy(
                InsurancePolicy(
                    customerId = c1Id,
                    policyNumber = "۱۴۰۳/۹۸/۲/۱۲۳۴۵",
                    insuranceCompany = "بیمه ایران",
                    insuranceType = "شخص ثالث خودرو",
                    insuredSubject = "سواری پژو پارس مدل ۱۳۹۹ - پلاک ایران ۶۶",
                    totalPremium = 7000000L, // ۷ میلیون تومان
                    prePayment = 0L,
                    installmentsCount = 7, // ۷ قسط
                    issueDate = "۱۴۰۳/۰۱/۱۵",
                    expiryDate = "۱۴۰۴/۰۱/۱۵",
                    notes = "اقساط ماهانه یک میلیون تومان. ۳ قسط پرداخت شده، ۴ قسط (۴۰۰ تومان واحد یا ۴ میلیون) مانده."
                )
            )

            // ۷ قسط برای مشتری ۱: ۳ قسط پرداخت شده
            val p1Installments = listOf(
                PolicyInstallment(policyId = p1Id, customerId = c1Id, installmentNumber = 1, totalInstallments = 7, amount = 1000000L, dueDate = "۱۴۰۳/۰۲/۱۵", isPaid = true, paidDate = "۱۴۰۳/۰۲/۱۴", paymentNote = "سند صادر شده"),
                PolicyInstallment(policyId = p1Id, customerId = c1Id, installmentNumber = 2, totalInstallments = 7, amount = 1000000L, dueDate = "۱۴۰۳/۰۳/۱۵", isPaid = true, paidDate = "۱۴۰۳/۰۳/۱۳", paymentNote = "واریز پایا"),
                PolicyInstallment(policyId = p1Id, customerId = c1Id, installmentNumber = 3, totalInstallments = 7, amount = 1000000L, dueDate = "۱۴۰۳/۰۴/۱۵", isPaid = true, paidDate = "۱۴۰۳/۰۴/۱۵", paymentNote = "کارتخوان شعبه"),
                PolicyInstallment(policyId = p1Id, customerId = c1Id, installmentNumber = 4, totalInstallments = 7, amount = 1000000L, dueDate = "۱۴۰۳/۰۵/۱۵", isPaid = false),
                PolicyInstallment(policyId = p1Id, customerId = c1Id, installmentNumber = 5, totalInstallments = 7, amount = 1000000L, dueDate = "۱۴۰۳/۰۶/۱۵", isPaid = false),
                PolicyInstallment(policyId = p1Id, customerId = c1Id, installmentNumber = 6, totalInstallments = 7, amount = 1000000L, dueDate = "۱۴۰۳/۰۷/۱۵", isPaid = false),
                PolicyInstallment(policyId = p1Id, customerId = c1Id, installmentNumber = 7, totalInstallments = 7, amount = 1000000L, dueDate = "۱۴۰۳/۰۸/۱۵", isPaid = false)
            )
            dao.insertInstallments(p1Installments)

            // اسناد مالی مشتری ۱
            dao.insertVoucher(PaymentVoucher(policyId = p1Id, customerId = c1Id, installmentId = 1L, voucherNumber = "سند-۰۱", amount = 1000000L, paymentDate = "۱۴۰۳/۰۲/۱۴", paymentMethod = "کارت‌خوان شعبه", payerName = "علی رضایی", description = "تسویه قسط اول بیمه شخص ثالث"))
            dao.insertVoucher(PaymentVoucher(policyId = p1Id, customerId = c1Id, installmentId = 2L, voucherNumber = "سند-۰۲", amount = 1000000L, paymentDate = "۱۴۰۳/۰۳/۱۳", paymentMethod = "کارت به کارت", payerName = "علی رضایی", description = "تسویه قسط دوم بیمه شخص ثالث"))
            dao.insertVoucher(PaymentVoucher(policyId = p1Id, customerId = c1Id, installmentId = 3L, voucherNumber = "سند-۰۳", amount = 1000000L, paymentDate = "۱۴۰۳/۰۴/۱۵", paymentMethod = "حواله بانکی", payerName = "علی رضایی", description = "تسویه قسط سوم بیمه شخص ثالث"))

            // مشتری ۲: مریم محمدی - بیمه درمان تکمیلی با ۵ قسط
            val c2Id = dao.insertCustomer(
                Customer(
                    fullName = "مریم محمدی",
                    nationalCode = "۰۴۵۰۹۸۲۳۱۱",
                    phone = "۰۹۱۸۷۶۵۴۳۲۱",
                    birthDate = "۱۳۷۲/۱۰/۲۲",
                    postalCode = "۱۹۸۲۳۷۴۵۶۱",
                    address = "اصفهان، چهارباغ عباسی، مجتمع پارسیان، طبقه ۳",
                    notes = "بیمه درمان تکمیلی خانواده طرح طلایی"
                )
            )
            val p2Id = dao.insertPolicy(
                InsurancePolicy(
                    customerId = c2Id,
                    policyNumber = "۱۴۰۳/۵۵/۱/۸۸۹۹۲",
                    insuranceCompany = "بیمه دانا",
                    insuranceType = "درمان تکمیلی",
                    insuredSubject = "طرح طلایی درمان ۴ نفره",
                    totalPremium = 5000000L,
                    prePayment = 0L,
                    installmentsCount = 5,
                    issueDate = "۱۴۰۳/۰۲/۰۱",
                    expiryDate = "۱۴۰۴/۰۲/۰۱",
                    notes = "۵ قسط یک میلیون تومانی، ۲ قسط تسویه شده"
                )
            )
            val p2Installments = listOf(
                PolicyInstallment(policyId = p2Id, customerId = c2Id, installmentNumber = 1, totalInstallments = 5, amount = 1000000L, dueDate = "۱۴۰۳/۰۳/۰۱", isPaid = true, paidDate = "۱۴۰۳/۰۳/۰۱", paymentNote = "درگاه اینترنتی"),
                PolicyInstallment(policyId = p2Id, customerId = c2Id, installmentNumber = 2, totalInstallments = 5, amount = 1000000L, dueDate = "۱۴۰۳/۰۴/۰۱", isPaid = true, paidDate = "۱۴۰۳/۰۴/۰۲", paymentNote = "پوز سیار"),
                PolicyInstallment(policyId = p2Id, customerId = c2Id, installmentNumber = 3, totalInstallments = 5, amount = 1000000L, dueDate = "۱۴۰۳/۰۵/۰۱", isPaid = false),
                PolicyInstallment(policyId = p2Id, customerId = c2Id, installmentNumber = 4, totalInstallments = 5, amount = 1000000L, dueDate = "۱۴۰۳/۰۶/۰۱", isPaid = false),
                PolicyInstallment(policyId = p2Id, customerId = c2Id, installmentNumber = 5, totalInstallments = 5, amount = 1000000L, dueDate = "۱۴۰۳/۰۷/۰۱", isPaid = false)
            )
            dao.insertInstallments(p2Installments)

            // مشتری ۳: حسین باقری - بیمه عمر با ۱۱ قسط
            val c3Id = dao.insertCustomer(
                Customer(
                    fullName = "حسین باقری",
                    nationalCode = "۱۲۹۳۸۴۷۵۶۰",
                    phone = "۰۹۳۵۱۱۲۲۳۳۴",
                    birthDate = "۱۳۶۰/۰۲/۰۸",
                    postalCode = "۱۳۸۴۹۵۰۰۱۱",
                    address = "مشهد، بلوار سجاد، خیابان بهار، پلاک ۴۵",
                    notes = "بیمه جامع عمر و مستمری"
                )
            )
            val p3Id = dao.insertPolicy(
                InsurancePolicy(
                    customerId = c3Id,
                    policyNumber = "۱۴۰۳/۷۰/۴/۱۱۴۴۰",
                    insuranceCompany = "بیمه سامان",
                    insuranceType = "عمر و سرمایه‌گذاری",
                    insuredSubject = "بیمه عمر انفرادی با پوشش امراض",
                    totalPremium = 11000000L,
                    prePayment = 0L,
                    installmentsCount = 11,
                    issueDate = "۱۴۰۳/۰۱/۱۰",
                    expiryDate = "۱۴۰۴/۰۱/۱۰",
                    notes = "۱۱ قسط یک میلیون تومانی ماهانه"
                )
            )
            val p3Installments = (1..11).map { i ->
                PolicyInstallment(
                    policyId = p3Id,
                    customerId = c3Id,
                    installmentNumber = i,
                    totalInstallments = 11,
                    amount = 1000000L,
                    dueDate = "۱۴۰۳/${(i + 1).toString().padStart(2, '0')}/۱۰",
                    isPaid = i <= 4,
                    paidDate = if (i <= 4) "۱۴۰۳/${(i + 1).toString().padStart(2, '0')}/۰۸" else null,
                    paymentNote = if (i <= 4) "واریز مستقیم" else ""
                )
            }
            dao.insertInstallments(p3Installments)

            // مشتری ۴: فاطمه رادمهر - بیمه آتش‌سوزی و بدنه با ۶ قسط
            val c4Id = dao.insertCustomer(
                Customer(
                    fullName = "فاطمه رادمهر",
                    nationalCode = "۲۲۸۹۴۷۵۶۱۰",
                    phone = "۰۹۱۲۹۹۸۸۷۷۶",
                    birthDate = "۱۳۷۰/۰۸/۳۰",
                    postalCode = "۱۷۷۳۸۲۹۱۰۰",
                    address = "شیراز، خیابان زند، کوچه نیلوفر، پلاک ۸",
                    notes = "بیمه بدنه هایما S7"
                )
            )
            val p4Id = dao.insertPolicy(
                InsurancePolicy(
                    customerId = c4Id,
                    policyNumber = "۱۴۰۳/۴۰/۲/۳۳۲۱۱",
                    insuranceCompany = "بیمه آسیا",
                    insuranceType = "بدنه اتومبیل",
                    insuredSubject = "خودرو هایما S7 توربو مشکی",
                    totalPremium = 6000000L,
                    prePayment = 0L,
                    installmentsCount = 6,
                    issueDate = "۱۴۰۳/۰۳/۰۱",
                    expiryDate = "۱۴۰۴/۰۳/۰۱",
                    notes = "۶ قسط یک میلیون تومانی"
                )
            )
            val p4Installments = (1..6).map { i ->
                PolicyInstallment(
                    policyId = p4Id,
                    customerId = c4Id,
                    installmentNumber = i,
                    totalInstallments = 6,
                    amount = 1000000L,
                    dueDate = "۱۴۰۳/${(i + 3).toString().padStart(2, '0')}/۰۱",
                    isPaid = i <= 2,
                    paidDate = if (i <= 2) "۱۴۰۳/${(i + 3).toString().padStart(2, '0')}/۰۱" else null
                )
            }
            dao.insertInstallments(p4Installments)
        }
    }
}
