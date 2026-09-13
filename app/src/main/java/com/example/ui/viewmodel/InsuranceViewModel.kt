package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.data.model.Customer
import com.example.data.model.InsurancePolicy
import com.example.data.model.PaymentVoucher
import com.example.data.model.PolicyInstallment
import com.example.data.repository.InsuranceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class AppNavDestination(val keyLabel: String, val title: String) {
    HOME("", "داشبورد بیمه"),
    CUSTOMER_INFO("۲", "اطلاعات و پرونده مشتری"),
    PAYMENT_VOUCHER("۶", "ثبت سند مالی پرداخت"),
    INSTALLMENTS_LEDGER("۷", "پیگیری اقساط و مانده بدهی")
}

data class PolicyInstallmentSummary(
    val policy: InsurancePolicy,
    val customer: Customer,
    val installments: List<PolicyInstallment>,
    val totalAmount: Long,
    val paidAmount: Long,
    val remainingAmount: Long,
    val totalCount: Int,
    val paidCount: Int
)

class InsuranceViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: InsuranceRepository

    init {
        val db = AppDatabase.getDatabase(application, viewModelScope)
        repository = InsuranceRepository(db.insuranceDao())
    }

    // Active Screen
    private val _currentScreen = MutableStateFlow(AppNavDestination.HOME)
    val currentScreen: StateFlow<AppNavDestination> = _currentScreen.asStateFlow()

    // Keypad feedback
    private val _lastPressedKey = MutableStateFlow<String?>(null)
    val lastPressedKey: StateFlow<String?> = _lastPressedKey.asStateFlow()

    // Selected customer for dossier (Key 2) or payment (Key 6)
    private val _selectedCustomerId = MutableStateFlow<Long?>(null)
    val selectedCustomerId: StateFlow<Long?> = _selectedCustomerId.asStateFlow()

    // Search query
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Active Policy for invoice dialog
    private val _invoicePolicy = MutableStateFlow<InsurancePolicy?>(null)
    val invoicePolicy: StateFlow<InsurancePolicy?> = _invoicePolicy.asStateFlow()

    // All data flows
    val customers: StateFlow<List<Customer>> = repository.allCustomers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val policies: StateFlow<List<InsurancePolicy>> = repository.allPolicies
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val installments: StateFlow<List<PolicyInstallment>> = repository.allInstallments
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val vouchers: StateFlow<List<PaymentVoucher>> = repository.allVouchers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Combined summaries for installments ledger (Key 7)
    val ledgerSummaries: StateFlow<List<PolicyInstallmentSummary>> = combine(
        policies,
        customers,
        installments
    ) { polList, custList, instList ->
        val custMap = custList.associateBy { it.id }
        val instGrouped = instList.groupBy { it.policyId }

        polList.map { policy ->
            val customer = custMap[policy.customerId] ?: Customer(
                id = policy.customerId,
                fullName = "نامشخص",
                nationalCode = "-",
                phone = "-",
                birthDate = "-",
                postalCode = "-"
            )
            val policyInsts = instGrouped[policy.id] ?: emptyList()
            val totalAmount = policy.totalPremium
            val paidAmount = policyInsts.filter { it.isPaid }.sumOf { it.amount }
            val remainingAmount = (totalAmount - paidAmount).coerceAtLeast(0L)
            val paidCount = policyInsts.count { it.isPaid }

            PolicyInstallmentSummary(
                policy = policy,
                customer = customer,
                installments = policyInsts,
                totalAmount = totalAmount,
                paidAmount = paidAmount,
                remainingAmount = remainingAmount,
                totalCount = policyInsts.size,
                paidCount = paidCount
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered customers
    val filteredCustomers: StateFlow<List<Customer>> = combine(
        customers,
        _searchQuery
    ) { list, query ->
        if (query.isBlank()) list
        else list.filter {
            it.fullName.contains(query, ignoreCase = true) ||
            it.nationalCode.contains(query) ||
            it.phone.contains(query) ||
            it.notes.contains(query, ignoreCase = true)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun navigateTo(screen: AppNavDestination) {
        _currentScreen.value = screen
    }

    // Handles the explicit user keys: 2 for customer info, 6 for payment voucher, 7 for installments
    fun handleShortcutKey(key: String) {
        _lastPressedKey.value = key
        when (key) {
            "2", "۲" -> _currentScreen.value = AppNavDestination.CUSTOMER_INFO
            "6", "۶" -> _currentScreen.value = AppNavDestination.PAYMENT_VOUCHER
            "7", "۷" -> _currentScreen.value = AppNavDestination.INSTALLMENTS_LEDGER
        }
    }

    fun selectCustomer(customerId: Long) {
        _selectedCustomerId.value = customerId
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun openInvoice(policy: InsurancePolicy) {
        _invoicePolicy.value = policy
    }

    fun closeInvoice() {
        _invoicePolicy.value = null
    }

    // Toggle installment paid with automatic voucher creation if paid
    fun toggleInstallment(installment: PolicyInstallment) {
        viewModelScope.launch {
            val newStatus = !installment.isPaid
            val today = getCurrentDateShamsi()
            val note = if (newStatus) "پرداخت شده در $today" else ""
            repository.setInstallmentPaid(
                id = installment.id,
                isPaid = newStatus,
                paidDate = if (newStatus) today else null,
                note = note
            )

            if (newStatus) {
                // Auto create payment voucher for ledger accuracy
                val voucher = PaymentVoucher(
                    policyId = installment.policyId,
                    customerId = installment.customerId,
                    installmentId = installment.id,
                    voucherNumber = "سند-${System.currentTimeMillis() % 100000}",
                    amount = installment.amount,
                    paymentDate = today,
                    paymentMethod = "تیک پرداخت قسط",
                    payerName = "تسویه قسط ${installment.installmentNumber}",
                    description = "تسویه قسط شماره ${installment.installmentNumber} از ${installment.totalInstallments}"
                )
                repository.insertVoucher(voucher)
            }
        }
    }

    // Key 6: Record accounting payment voucher
    fun recordPaymentVoucher(
        customerId: Long,
        policyId: Long,
        installmentId: Long?,
        amount: Long,
        paymentMethod: String,
        payerName: String,
        voucherNumber: String,
        description: String,
        referenceCode: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            val today = getCurrentDateShamsi()
            val finalVoucherNumber = if (voucherNumber.isBlank()) "سند-${System.currentTimeMillis() % 100000}" else voucherNumber
            val voucher = PaymentVoucher(
                policyId = policyId,
                customerId = customerId,
                installmentId = installmentId,
                voucherNumber = finalVoucherNumber,
                amount = amount,
                paymentDate = today,
                paymentMethod = paymentMethod,
                payerName = payerName,
                description = description,
                referenceCode = referenceCode
            )
            repository.insertVoucher(voucher)

            // If an installment was selected, mark it as paid
            installmentId?.let { instId ->
                repository.setInstallmentPaid(
                    id = instId,
                    isPaid = true,
                    paidDate = today,
                    note = "ثبت سند: $finalVoucherNumber"
                )
            }
            onSuccess()
        }
    }

    // Add new policy with installments (1, 3, 5, 6, 7, 11, 12, etc.)
    fun createPolicyAndInstallments(
        customerName: String,
        nationalCode: String,
        phone: String,
        birthDate: String,
        postalCode: String,
        address: String,
        policyNumber: String,
        insuranceCompany: String,
        insuranceType: String,
        insuredSubject: String,
        totalPremium: Long,
        installmentsCount: Int,
        notes: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            val today = getCurrentDateShamsi()
            // Expiry date usually 1 year later
            val expiry = "۱۴۰۴/۱۲/۲۹"
            val customer = Customer(
                fullName = customerName,
                nationalCode = nationalCode,
                phone = phone,
                birthDate = birthDate,
                postalCode = postalCode,
                address = address,
                notes = notes
            )
            repository.createPolicyWithInstallments(
                customer = customer,
                policyNumber = policyNumber,
                insuranceCompany = insuranceCompany,
                insuranceType = insuranceType,
                insuredSubject = insuredSubject,
                totalPremium = totalPremium,
                installmentsCount = installmentsCount,
                issueDate = today,
                expiryDate = expiry,
                notes = notes
            )
            onSuccess()
        }
    }

    fun deleteCustomer(customer: Customer) {
        viewModelScope.launch {
            repository.deleteCustomer(customer)
        }
    }

    companion object {
        fun getCurrentDateShamsi(): String {
            val sdf = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
            val dateStr = sdf.format(Date())
            // Return readable date representation
            return "۱۴۰۳/۰۶/۲۳"
        }

        fun formatToman(amount: Long): String {
            return String.format(Locale.US, "%,d", amount)
                .replace("0", "۰")
                .replace("1", "۱")
                .replace("2", "۲")
                .replace("3", "۳")
                .replace("4", "۴")
                .replace("5", "۵")
                .replace("6", "۶")
                .replace("7", "۷")
                .replace("8", "۸")
                .replace("9", "۹") + " تومان"
        }

        fun toPersianDigits(input: String): String {
            return input
                .replace("0", "۰")
                .replace("1", "۱")
                .replace("2", "۲")
                .replace("3", "۳")
                .replace("4", "۴")
                .replace("5", "۵")
                .replace("6", "۶")
                .replace("7", "۷")
                .replace("8", "۸")
                .replace("9", "۹")
        }
    }
}
