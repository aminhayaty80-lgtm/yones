package com.example.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "insurance_policies",
    foreignKeys = [
        ForeignKey(
            entity = Customer::class,
            parentColumns = ["id"],
            childColumns = ["customerId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["customerId"])]
)
data class InsurancePolicy(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val customerId: Long,
    val policyNumber: String,          // شماره بیمه‌نامه
    val insuranceCompany: String,      // شرکت صادرکننده (بیمه ایران، دانا، آسیا، البرز، سامان و ...)
    val insuranceType: String,         // نوع بیمه (شخص ثالث، بدنه، عمر و پس‌انداز، تکمیلی درمان و ...)
    val insuredSubject: String,        // مورد بیمه (پژو پارس، آپارتمان مسکونی، عمر انفرادی و ...)
    val totalPremium: Long,            // مبلغ کل حق‌بیمه (تومان)
    val prePayment: Long = 0L,         // پیش‌پرداخت نقد
    val installmentsCount: Int = 1,    // تعداد اقساط (مثلا ۱، ۳، ۵، ۶، ۷، ۱۱، ۱۲)
    val issueDate: String,             // تاریخ صدور
    val expiryDate: String,            // تاریخ انقضا / یادآوری
    val notes: String = "",            // توضیحات
    val createdAt: Long = System.currentTimeMillis()
)
