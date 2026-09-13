package com.example.data.model

import androidx.room.Embedded
import androidx.room.Relation

data class CustomerWithPolicies(
    @Embedded val customer: Customer,
    @Relation(
        parentColumn = "id",
        entityColumn = "customerId"
    )
    val policies: List<InsurancePolicy>
)

data class PolicyWithInstallments(
    @Embedded val policy: InsurancePolicy,
    @Relation(
        parentColumn = "id",
        entityColumn = "policyId"
    )
    val installments: List<PolicyInstallment>
)

// Summary for customer financial status (بدهکار / بستانکار / مانده)
data class CustomerFinancialSummary(
    val customer: Customer,
    val policy: InsurancePolicy?,
    val totalPoliciesCount: Int,
    val totalPremium: Long,
    val totalPaid: Long,
    val remainingBalance: Long, // مانده بدهکاری
    val totalInstallments: Int,
    val paidInstallments: Int,  // مثلا ۳ از ۷
    val isSettled: Boolean
)
