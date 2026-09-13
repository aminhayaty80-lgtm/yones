package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PolicyInstallment
import com.example.ui.theme.Gold500
import com.example.ui.theme.Green600
import com.example.ui.theme.Navy800
import com.example.ui.theme.Navy900
import com.example.ui.theme.Red600
import com.example.ui.theme.Slate500
import com.example.ui.theme.Teal500
import com.example.ui.viewmodel.AppNavDestination
import com.example.ui.viewmodel.InsuranceViewModel
import com.example.ui.viewmodel.PolicyInstallmentSummary

@Composable
fun InstallmentsLedgerScreen(
    viewModel: InsuranceViewModel,
    modifier: Modifier = Modifier
) {
    val ledgerSummaries by viewModel.ledgerSummaries.collectAsState()

    var filterStatus by remember { mutableStateOf("ALL") } // ALL, UNPAID, SETTLED

    val filteredList = when (filterStatus) {
        "UNPAID" -> ledgerSummaries.filter { it.remainingAmount > 0 }
        "SETTLED" -> ledgerSummaries.filter { it.remainingAmount == 0L }
        else -> ledgerSummaries
    }

    val totalPortfolio = ledgerSummaries.sumOf { it.totalAmount }
    val totalCreditor = ledgerSummaries.sumOf { it.paidAmount } // بستانکار (وصول شده)
    val totalDebtor = ledgerSummaries.sumOf { it.remainingAmount } // بدهکار (مانده بدهی)

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("installments_ledger_screen")
    ) {
        // Title Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(Gold500, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "۷",
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "جدول جامع اقساط و مانده بدهکاری (کلید ۷)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Navy800
                    )
                    Text(
                        text = "بدهکار، بستانکار، مانده بدهی و ثبت تیک پرداخت",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            IconButton(onClick = { viewModel.navigateTo(AppNavDestination.HOME) }) {
                Icon(imageVector = Icons.Default.Home, contentDescription = "داشبورد")
            }
        }

        // Ledger Financial Header (بدهکار / بستانکار / مانده)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Navy900)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "تراز مالی کلی اقساط (بدهکار و بستانکار)",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White.copy(alpha = 0.8f)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    LedgerStatBox(
                        title = "بستانکار (وصولی)",
                        amount = totalCreditor,
                        color = Green600,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    LedgerStatBox(
                        title = "بدهکار (مانده)",
                        amount = totalDebtor,
                        color = Red600,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Filter Chips
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FilterChip(
                selected = filterStatus == "ALL",
                onClick = { filterStatus = "ALL" },
                label = { Text("همه پرونده‌ها (${ledgerSummaries.size})") }
            )
            FilterChip(
                selected = filterStatus == "UNPAID",
                onClick = { filterStatus = "UNPAID" },
                label = { Text("دارای مانده بدهی (${ledgerSummaries.count { it.remainingAmount > 0 }})") }
            )
            FilterChip(
                selected = filterStatus == "SETTLED",
                onClick = { filterStatus = "SETTLED" },
                label = { Text("تسویه کامل (${ledgerSummaries.count { it.remainingAmount == 0L }})") }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // List of Policies with Installments and Checkboxes
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filteredList) { item ->
                PolicyLedgerCard(
                    item = item,
                    onToggleInstallment = { inst -> viewModel.toggleInstallment(inst) },
                    onViewCustomer = {
                        viewModel.selectCustomer(item.customer.id)
                        viewModel.navigateTo(AppNavDestination.CUSTOMER_INFO)
                    },
                    onOpenInvoice = { viewModel.openInvoice(item.policy) },
                    onRecordPayment = {
                        viewModel.selectCustomer(item.customer.id)
                        viewModel.navigateTo(AppNavDestination.PAYMENT_VOUCHER)
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun LedgerStatBox(
    title: String,
    amount: Long,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        color = Color.White.copy(alpha = 0.08f)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.9f)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = InsuranceViewModel.formatToman(amount),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}

@Composable
fun PolicyLedgerCard(
    item: PolicyInstallmentSummary,
    onToggleInstallment: (PolicyInstallment) -> Unit,
    onViewCustomer: () -> Unit,
    onOpenInvoice: () -> Unit,
    onRecordPayment: () -> Unit
) {
    var isExpanded by remember { mutableStateOf(true) }
    val progress = if (item.totalCount > 0) item.paidCount.toFloat() / item.totalCount.toFloat() else 0f

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: Customer Name & Policy Type
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "${item.customer.fullName} • ${item.policy.insuredSubject}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Navy900
                    )
                    Text(
                        text = "${item.policy.insuranceCompany} • ${item.policy.insuranceType} (${item.policy.policyNumber})",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Expand/Collapse Arrow
                IconButton(onClick = { isExpanded = !isExpanded }) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = "باز/بسته"
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Installments Status Badge: "۳ از ۷ قسط پرداخت شده"
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (item.remainingAmount == 0L) Green600.copy(alpha = 0.15f) else Gold500.copy(alpha = 0.15f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${item.paidCount} از ${item.totalCount} قسط پرداخت شده",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (item.remainingAmount == 0L) Green600 else Navy800
                    )

                    Text(
                        text = if (item.remainingAmount == 0L) "تسویه کامل" else "مانده بدهکاری: ${InsuranceViewModel.formatToman(item.remainingAmount)}",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (item.remainingAmount == 0L) Green600 else Red600
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Progress bar
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = Green600,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Financial Balance Row (بدهکار / بستانکار)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "بستانکار: ${InsuranceViewModel.formatToman(item.paidAmount)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Green600,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "بدهکار (مانده): ${InsuranceViewModel.formatToman(item.remainingAmount)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (item.remainingAmount > 0) Red600 else Green600,
                    fontWeight = FontWeight.Bold
                )
            }

            // Installments Checklist (جای داشته باشه تیک بزنی)
            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "اقساط بیمه‌نامه (با تیک زدن وضعیت پرداخت تغییر می‌یابد):",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Navy800
                    )

                    item.installments.forEach { inst ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (inst.isPaid) Green600.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface)
                                .border(1.dp, if (inst.isPaid) Green600.copy(alpha = 0.4f) else Color.LightGray.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                .clickable { onToggleInstallment(inst) }
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(
                                    checked = inst.isPaid,
                                    onCheckedChange = { onToggleInstallment(inst) },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = Green600,
                                        uncheckedColor = Slate500
                                    )
                                )
                                Column {
                                    Text(
                                        text = "قسط ${inst.installmentNumber} از ${inst.totalInstallments}",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "سررسید: ${inst.dueDate}",
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = InsuranceViewModel.formatToman(inst.amount),
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (inst.isPaid) Green600 else Red600
                                )
                                Text(
                                    text = if (inst.isPaid) "پرداخت شده" else "پرداخت نشده",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                    color = if (inst.isPaid) Green600 else Red600,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Action Buttons Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        OutlinedButton(
                            onClick = onOpenInvoice,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(imageVector = Icons.Default.Receipt, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("فاکتور", fontSize = 12.sp)
                        }

                        Button(
                            onClick = onRecordPayment,
                            modifier = Modifier.weight(1.2f),
                            colors = ButtonDefaults.buttonColors(containerColor = Teal500)
                        ) {
                            Icon(imageVector = Icons.Default.Paid, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("سند پرداخت (۶)", fontSize = 12.sp)
                        }

                        OutlinedButton(
                            onClick = onViewCustomer,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(imageVector = Icons.Default.Person, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("پرونده (۲)", fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}
