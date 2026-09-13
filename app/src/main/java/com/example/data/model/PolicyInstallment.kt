package com.example.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "policy_installments",
    foreignKeys = [
        ForeignKey(
            entity = InsurancePolicy::class,
            parentColumns = ["id"],
            childColumns = ["policyId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["policyId"]), Index(value = ["customerId"])]
)
data class PolicyInstallment(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val policyId: Long,
    val customerId: Long,
    val installmentNumber: Int,        // شماره قسط (مثلا ۱)
    val totalInstallments: Int,        // تعداد کل اقساط (مثلا ۷)
    val amount: Long,                  // مبلغ قسط (تومان)
    val dueDate: String,               // تاریخ سررسید قسط
    val isPaid: Boolean = false,       // وضعیت پرداخت (تیک خورده یا نه)
    val paidDate: String? = null,      // تاریخ پرداخت
    val paymentNote: String = ""       // یادداشت یا کد پیگیری
)
