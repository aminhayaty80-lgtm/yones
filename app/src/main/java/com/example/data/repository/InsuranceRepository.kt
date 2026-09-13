package com.example.data.repository

import com.example.data.db.InsuranceDao
import com.example.data.model.Customer
import com.example.data.model.CustomerWithPolicies
import com.example.data.model.InsurancePolicy
import com.example.data.model.PaymentVoucher
import com.example.data.model.PolicyInstallment
import kotlinx.coroutines.flow.Flow

class InsuranceRepository(private val dao: InsuranceDao) {
    val allCustomers: Flow<List<Customer>> = dao.getAllCustomers()
    val allPolicies: Flow<List<InsurancePolicy>> = dao.getAllPolicies()
    val allInstallments: Flow<List<PolicyInstallment>> = dao.getAllInstallments()
    val allVouchers: Flow<List<PaymentVoucher>> = dao.getAllVouchers()

    fun getCustomerById(id: Long): Flow<Customer?> = dao.getCustomerById(id)
    fun getCustomerWithPolicies(customerId: Long): Flow<CustomerWithPolicies?> = dao.getCustomerWithPolicies(customerId)
    fun getPoliciesForCustomer(customerId: Long): Flow<List<InsurancePolicy>> = dao.getPoliciesForCustomer(customerId)
    fun getInstallmentsForPolicy(policyId: Long): Flow<List<PolicyInstallment>> = dao.getInstallmentsForPolicy(policyId)
    fun getInstallmentsForCustomer(customerId: Long): Flow<List<PolicyInstallment>> = dao.getInstallmentsForCustomer(customerId)
    fun searchCustomers(query: String): Flow<List<Customer>> = dao.searchCustomers(query)

    suspend fun insertCustomer(customer: Customer): Long = dao.insertCustomer(customer)
    suspend fun updateCustomer(customer: Customer) = dao.updateCustomer(customer)
    suspend fun deleteCustomer(customer: Customer) = dao.deleteCustomer(customer)

    suspend fun insertPolicy(policy: InsurancePolicy): Long = dao.insertPolicy(policy)
    suspend fun updatePolicy(policy: InsurancePolicy) = dao.updatePolicy(policy)
    suspend fun deletePolicy(policy: InsurancePolicy) = dao.deletePolicy(policy)

    suspend fun insertInstallments(installments: List<PolicyInstallment>) = dao.insertInstallments(installments)
    suspend fun updateInstallment(installment: PolicyInstallment) = dao.updateInstallment(installment)
    suspend fun setInstallmentPaid(id: Long, isPaid: Boolean, paidDate: String?, note: String) =
        dao.setInstallmentPaid(id, isPaid, paidDate, note)

    suspend fun insertVoucher(voucher: PaymentVoucher): Long = dao.insertVoucher(voucher)
    suspend fun deleteVoucher(voucher: PaymentVoucher) = dao.deleteVoucher(voucher)

    suspend fun createPolicyWithInstallments(
        customer: Customer,
        policyNumber: String,
        insuranceCompany: String,
        insuranceType: String,
        insuredSubject: String,
        totalPremium: Long,
        installmentsCount: Int,
        issueDate: String,
        expiryDate: String,
        notes: String
    ): Long {
        val custId = if (customer.id == 0L) dao.insertCustomer(customer) else customer.id

        val policy = InsurancePolicy(
            customerId = custId,
            policyNumber = policyNumber,
            insuranceCompany = insuranceCompany,
            insuranceType = insuranceType,
            insuredSubject = insuredSubject,
            totalPremium = totalPremium,
            prePayment = 0L,
            installmentsCount = installmentsCount,
            issueDate = issueDate,
            expiryDate = expiryDate,
            notes = notes
        )
        val policyId = dao.insertPolicy(policy)

        val count = if (installmentsCount <= 0) 1 else installmentsCount
        val installmentAmount = totalPremium / count
        val remainder = totalPremium % count

        val installments = (1..count).map { num ->
            val finalAmount = if (num == count) installmentAmount + remainder else installmentAmount
            PolicyInstallment(
                policyId = policyId,
                customerId = custId,
                installmentNumber = num,
                totalInstallments = count,
                amount = finalAmount,
                dueDate = "قسط $num - $expiryDate",
                isPaid = false
            )
        }
        dao.insertInstallments(installments)
        return policyId
    }
}
