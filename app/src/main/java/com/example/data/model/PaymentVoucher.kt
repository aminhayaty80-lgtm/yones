package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "payment_vouchers")
data class PaymentVoucher(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val policyId: Long,
    val customerId: Long,
    val installmentId: Long? = null,
    val voucherNumber: String,         // شماره سند پرداخت
    val amount: Long,                  // مبلغ پرداختی (تومان)
    val paymentDate: String,           // تاریخ سند
    val paymentMethod: String,         // روش پرداخت (کارتخوان، کارت به کارت، حواله بانکی، نقد، چک)
    val payerName: String,             // پرداخت کننده
    val description: String,           // شرح سند
    val referenceCode: String = "",    // شماره ارجاع / پیگیری بانکی
    val createdAt: Long = System.currentTimeMillis()
)
