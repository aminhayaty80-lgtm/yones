package com.example.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.data.model.Customer
import com.example.data.model.CustomerWithPolicies
import com.example.data.model.InsurancePolicy
import com.example.data.model.PaymentVoucher
import com.example.data.model.PolicyInstallment
import kotlinx.coroutines.flow.Flow

@Dao
interface InsuranceDao {
    // --- Customers ---
    @Query("SELECT * FROM customers ORDER BY id DESC")
    fun getAllCustomers(): Flow<List<Customer>>

    @Query("SELECT * FROM customers WHERE id = :id")
    fun getCustomerById(id: Long): Flow<Customer?>

    @Query("SELECT * FROM customers WHERE fullName LIKE '%' || :query || '%' OR nationalCode LIKE '%' || :query || '%' OR phone LIKE '%' || :query || '%'")
    fun searchCustomers(query: String): Flow<List<Customer>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomer(customer: Customer): Long

    @Update
    suspend fun updateCustomer(customer: Customer)

    @Delete
    suspend fun deleteCustomer(customer: Customer)

    @Transaction
    @Query("SELECT * FROM customers WHERE id = :customerId")
    fun getCustomerWithPolicies(customerId: Long): Flow<CustomerWithPolicies?>

    // --- Policies ---
    @Query("SELECT * FROM insurance_policies ORDER BY id DESC")
    fun getAllPolicies(): Flow<List<InsurancePolicy>>

    @Query("SELECT * FROM insurance_policies WHERE customerId = :customerId ORDER BY id DESC")
    fun getPoliciesForCustomer(customerId: Long): Flow<List<InsurancePolicy>>

    @Query("SELECT * FROM insurance_policies WHERE id = :policyId")
    fun getPolicyById(policyId: Long): Flow<InsurancePolicy?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPolicy(policy: InsurancePolicy): Long

    @Update
    suspend fun updatePolicy(policy: InsurancePolicy)

    @Delete
    suspend fun deletePolicy(policy: InsurancePolicy)

    // --- Installments ---
    @Query("SELECT * FROM policy_installments ORDER BY dueDate ASC, id ASC")
    fun getAllInstallments(): Flow<List<PolicyInstallment>>

    @Query("SELECT * FROM policy_installments WHERE policyId = :policyId ORDER BY installmentNumber ASC")
    fun getInstallmentsForPolicy(policyId: Long): Flow<List<PolicyInstallment>>

    @Query("SELECT * FROM policy_installments WHERE customerId = :customerId ORDER BY installmentNumber ASC")
    fun getInstallmentsForCustomer(customerId: Long): Flow<List<PolicyInstallment>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInstallments(installments: List<PolicyInstallment>)

    @Update
    suspend fun updateInstallment(installment: PolicyInstallment)

    @Query("UPDATE policy_installments SET isPaid = :isPaid, paidDate = :paidDate, paymentNote = :paymentNote WHERE id = :id")
    suspend fun setInstallmentPaid(id: Long, isPaid: Boolean, paidDate: String?, paymentNote: String)

    @Delete
    suspend fun deleteInstallment(installment: PolicyInstallment)

    // --- Payment Vouchers (سند پرداخت) ---
    @Query("SELECT * FROM payment_vouchers ORDER BY createdAt DESC")
    fun getAllVouchers(): Flow<List<PaymentVoucher>>

    @Query("SELECT * FROM payment_vouchers WHERE customerId = :customerId ORDER BY createdAt DESC")
    fun getVouchersForCustomer(customerId: Long): Flow<List<PaymentVoucher>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVoucher(voucher: PaymentVoucher): Long

    @Delete
    suspend fun deleteVoucher(voucher: PaymentVoucher)
}
