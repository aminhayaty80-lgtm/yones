package com.example.ui.components

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pending
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.Customer
import com.example.data.model.InsurancePolicy
import com.example.data.model.PolicyInstallment
import com.example.ui.theme.Green600
import com.example.ui.theme.Navy800
import com.example.ui.theme.Navy900
import com.example.ui.theme.Red600
import com.example.ui.theme.Teal500
import com.example.ui.viewmodel.InsuranceViewModel

@Composable
fun InvoiceDialog(
    policy: InsurancePolicy,
    customer: Customer,
    installments: List<PolicyInstallment>,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val totalPaid = installments.filter { it.isPaid }.sumOf { it.amount }
    val remaining = (policy.totalPremium - totalPaid).coerceAtLeast(0L)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(vertical = 24.dp)
                .testTag("invoice_dialog"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Top Bar with Close
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "بستن")
                    }
                    Text(
                        text = "صورتحساب و فاکتور رسمی بیمه‌نامه",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Navy800
                    )
                    Box(modifier = Modifier.size(40.dp))
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                // Official Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Navy900, RoundedCornerShape(12.dp))
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = policy.insuranceCompany,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        Text(
                            text = "نمایندگی رسمی خدمات بیمه‌ای و صدور",
                            style = MaterialTheme.typography.bodySmall,
                            color = Teal500
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "شماره فاکتور: ${policy.id + 1040}",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                        Text(
                            text = "تاریخ: ${policy.issueDate}",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Customer Info Box
                InvoiceSectionHeader(title = "مشخصات بیمه‌گذار و طرف حساب")
                InvoiceInfoRow(label = "نام و نام خانوادگی:", value = customer.fullName)
                InvoiceInfoRow(label = "کد ملی / کد بیمه‌گذار:", value = customer.nationalCode)
                InvoiceInfoRow(label = "شماره تماس:", value = customer.phone)
                InvoiceInfoRow(label = "تاریخ تولد:", value = customer.birthDate)
                InvoiceInfoRow(label = "کد پستی:", value = customer.postalCode)
                if (customer.address.isNotBlank()) {
                    InvoiceInfoRow(label = "نشانی:", value = customer.address)
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Policy Info Box
                InvoiceSectionHeader(title = "مشخصات بیمه‌نامه")
                InvoiceInfoRow(label = "شماره بیمه‌نامه:", value = policy.policyNumber)
                InvoiceInfoRow(label = "نوع بیمه:", value = policy.insuranceType)
                InvoiceInfoRow(label = "مورد بیمه:", value = policy.insuredSubject)
                InvoiceInfoRow(label = "تاریخ شروع / اعتبار:", value = "${policy.issueDate} تا ${policy.expiryDate}")

                Spacer(modifier = Modifier.height(10.dp))

                // Installments Breakdown
                InvoiceSectionHeader(title = "جدول اقساط و وضعیت تسویه (${installments.size} قسط)")
                installments.forEach { inst ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .background(
                                if (inst.isPaid) Green600.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (inst.isPaid) Icons.Default.CheckCircle else Icons.Default.Pending,
                                contentDescription = null,
                                tint = if (inst.isPaid) Green600 else Red600,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "قسط ${inst.installmentNumber} از ${inst.totalInstallments} (${inst.dueDate})",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = InsuranceViewModel.formatToman(inst.amount),
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (inst.isPaid) "تسویه شده" else "مانده",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (inst.isPaid) Green600 else Red600,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Financial Summary Box
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "مبلغ کل حق بیمه:", style = MaterialTheme.typography.bodyMedium)
                            Text(text = InsuranceViewModel.formatToman(policy.totalPremium), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "مجموع مبالغ پرداخت شده (بستانکار):", style = MaterialTheme.typography.bodyMedium, color = Green600)
                            Text(text = InsuranceViewModel.formatToman(totalPaid), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = Green600)
                        }
                        HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "مانده بدهکاری قابل پرداخت:", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = if (remaining > 0) Red600 else Green600)
                            Text(text = InsuranceViewModel.formatToman(remaining), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.ExtraBold, color = if (remaining > 0) Red600 else Green600)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Official Seal Stamp
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        modifier = Modifier
                            .border(1.5.dp, Teal500, RoundedCornerShape(24.dp))
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Security, contentDescription = null, tint = Teal500, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "ممهور به امضای الکترونیک نمایندگی ${policy.insuranceCompany}",
                            style = MaterialTheme.typography.labelSmall,
                            color = Teal500,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Share & Print Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            shareInvoiceText(context, policy, customer, totalPaid, remaining, installments)
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Navy800)
                    ) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("ارسال و اشتراک‌گذاری")
                    }

                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(0.5f)
                    ) {
                        Text("بستن")
                    }
                }
            }
        }
    }
}

@Composable
fun InvoiceSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Bold,
        color = Navy800,
        modifier = Modifier.padding(vertical = 4.dp)
    )
}

@Composable
fun InvoiceInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

private fun shareInvoiceText(
    context: Context,
    policy: InsurancePolicy,
    customer: Customer,
    totalPaid: Long,
    remaining: Long,
    installments: List<PolicyInstallment>
) {
    val text = buildString {
        appendLine("📄 فاکتور رسمی بیمه‌نامه ${policy.insuranceCompany}")
        appendLine("----------------------------------")
        appendLine("بیمه‌گذار: ${customer.fullName}")
        appendLine("کد ملی / کد بیمه‌گذار: ${customer.nationalCode}")
        appendLine("شماره تماس: ${customer.phone}")
        appendLine("نوع بیمه: ${policy.insuranceType}")
        appendLine("مورد بیمه: ${policy.insuredSubject}")
        appendLine("شماره بیمه‌نامه: ${policy.policyNumber}")
        appendLine("تاریخ صدور: ${policy.issueDate}")
        appendLine("----------------------------------")
        appendLine("مبلغ کل حق بیمه: ${InsuranceViewModel.formatToman(policy.totalPremium)}")
        appendLine("مبلغ پرداخت شده: ${InsuranceViewModel.formatToman(totalPaid)}")
        appendLine("مانده بدهکاری: ${InsuranceViewModel.formatToman(remaining)}")
        appendLine("تعداد اقساط: ${installments.size} قسط (${installments.count { it.isPaid }} قسط تسویه شده)")
        appendLine("----------------------------------")
        appendLine("نمایندگی رسمی ${policy.insuranceCompany} - سامانه مدیریت اقساط بیمه")
    }

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, "فاکتور بیمه ${customer.fullName}")
        putExtra(Intent.EXTRA_TEXT, text)
    }
    context.startActivity(Intent.createChooser(intent, "اشتراک‌گذاری فاکتور بیمه"))
}
