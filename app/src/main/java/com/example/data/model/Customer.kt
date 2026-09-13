package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "customers")
data class Customer(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val fullName: String,              // نام و نام خانوادگی
    val nationalCode: String,          // کد ملی / کد بیمه‌گذار
    val phone: String,                 // شماره تماس / موبایل
    val birthDate: String,             // تاریخ تولد
    val postalCode: String,            // کد پستی
    val address: String = "",          // آدرس
    val notes: String = "",            // توضیحات
    val createdAt: Long = System.currentTimeMillis()
)
