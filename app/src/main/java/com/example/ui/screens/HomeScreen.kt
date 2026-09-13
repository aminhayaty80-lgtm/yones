package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AddPolicyDialog
import com.example.ui.theme.Gold500
import com.example.ui.theme.Green600
import com.example.ui.theme.Navy800
import com.example.ui.theme.Navy900
import com.example.ui.theme.Red600
import com.example.ui.theme.Teal500
import com.example.ui.viewmodel.AppNavDestination
import com.example.ui.viewmodel.InsuranceViewModel
import com.example.ui.viewmodel.PolicyInstallmentSummary

@Composable
fun HomeScreen(
    viewModel: InsuranceViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val customers by viewModel.customers.collectAsState()
    val ledgerSummaries by viewModel.ledgerSummaries.collectAsState()
    val installments by viewModel.installments.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }

    val totalPaid = ledgerSummaries.sumOf { it.paidAmount }
    val totalRemaining = ledgerSummaries.sumOf { it.remainingAmount }
    val unpaidInstallments = installments.filter { !it.isPaid }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Dashboard Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp, bottom = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(Navy900, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = Teal500,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "مدیریت مشتریان و اقساط بیمه",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = Navy900
                            )
                            Text(
                                text = "سامانه مالی، صدور فاکتور و پیگیری اقساط",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Button(
                        onClick = { showAddDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Navy800),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("btn_new_policy")
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("بیمه‌نامه جدید", fontSize = 12.sp)
                    }
                }
            }

            // Financial Summary Banner
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Navy900)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "وضعیت مالی کل پرتفوی بیمه",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "مجموع وصولی (بستانکار):",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                                Text(
                                    text = InsuranceViewModel.formatToman(totalPaid),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Green600
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "کل مانده بدهکاری اقساط:",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                                Text(
                                    text = InsuranceViewModel.formatToman(totalRemaining),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (totalRemaining > 0) Red600 else Green600
                                )
                            }
                        }

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 10.dp),
                            color = Color.White.copy(alpha = 0.15f)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "تعداد بیمه‌گذاران: ${customers.size} نفر",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                            Text(
                                text = "اقساط مانده تسویه: ${unpaidInstallments.size} قسط",
                                style = MaterialTheme.typography.labelSmall,
                                color = Gold500
                            )
                        }
                    }
                }
            }

            // Quick Direct Action Hub for user keys 2, 6, 7
            item {
                Text(
                    text = "دسترسی مستقیم با کلیدهای اعلامی:",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = Navy800
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    HomeKeyCard(
                        digit = "۲",
                        title = "اطلاعات مشتری",
                        description = "کد بیمه‌گذار، تلفن، تولد و ش بیمه",
                        color = Navy800,
                        onClick = {
                            viewModel.handleShortcutKey("2")
                        },
                        modifier = Modifier.weight(1f)
                    )

                    HomeKeyCard(
                        digit = "۶",
                        title = "ثبت سند پرداخت",
                        description = "سند زدن پرداخت و صدور رسید",
                        color = Teal500,
                        onClick = {
                            viewModel.handleShortcutKey("6")
                        },
                        modifier = Modifier.weight(1f)
                    )

                    HomeKeyCard(
                        digit = "۷",
                        title = "اقساط و مانده",
                        description = "بدهکار، بستانکار و تیک پرداخت",
                        color = Gold500,
                        onClick = {
                            viewModel.handleShortcutKey("7")
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Reminder section: سررسید اقساط و یادآوری مشتریان
            if (unpaidInstallments.isNotEmpty()) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.NotificationsActive,
                                contentDescription = null,
                                tint = Gold500,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "یادآوری سررسید اقساط معوقه و نزدیک",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = Navy900
                            )
                        }

                        Text(
                            text = "${unpaidInstallments.size} مورد",
                            style = MaterialTheme.typography.labelSmall,
                            color = Gold500,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                items(ledgerSummaries.filter { it.remainingAmount > 0 }.take(4)) { item ->
                    val cust = item.customer
                    val firstUnpaid = item.installments.firstOrNull { !it.isPaid }
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "${cust.fullName} (${item.policy.insuranceType})",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "قسط ${firstUnpaid?.installmentNumber ?: 1} از ${item.totalCount} • سررسید: ${firstUnpaid?.dueDate ?: "-"}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "مانده: ${InsuranceViewModel.formatToman(item.remainingAmount)}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Red600,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                // Call
                                IconButton(
                                    onClick = {
                                        val intent = Intent(Intent.ACTION_DIAL).apply {
                                            data = Uri.parse("tel:${cust.phone}")
                                        }
                                        context.startActivity(intent)
                                    },
                                    modifier = Modifier
                                        .background(Green600.copy(alpha = 0.12f), CircleShape)
                                        .size(36.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Phone, contentDescription = "تماس", tint = Green600, modifier = Modifier.size(18.dp))
                                }

                                // Direct to Key 7
                                OutlinedButton(
                                    onClick = {
                                        viewModel.selectCustomer(cust.id)
                                        viewModel.navigateTo(AppNavDestination.INSTALLMENTS_LEDGER)
                                    },
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("اقساط (۷)", fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }

            // Recent Customer Dossiers list
            item {
                Text(
                    text = "فهرست پرونده‌های فعال مشتریان (${ledgerSummaries.size} پرونده)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Navy800,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            items(ledgerSummaries) { summary ->
                HomeCustomerPolicyCard(
                    summary = summary,
                    onOpenDossier = {
                        viewModel.selectCustomer(summary.customer.id)
                        viewModel.navigateTo(AppNavDestination.CUSTOMER_INFO)
                    },
                    onOpenInvoice = {
                        viewModel.openInvoice(summary.policy)
                    },
                    onRecordPayment = {
                        viewModel.selectCustomer(summary.customer.id)
                        viewModel.navigateTo(AppNavDestination.PAYMENT_VOUCHER)
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(70.dp))
            }
        }
    }

    if (showAddDialog) {
        AddPolicyDialog(
            onDismiss = { showAddDialog = false },
            onSubmit = { customerName, nationalCode, phone, birthDate, postalCode, address, policyNumber, company, type, subject, premium, count, notes ->
                viewModel.createPolicyAndInstallments(
                    customerName, nationalCode, phone, birthDate, postalCode, address,
                    policyNumber, company, type, subject, premium, count, notes
                ) {
                    showAddDialog = false
                }
            }
        )
    }
}

@Composable
fun HomeKeyCard(
    digit: String,
    title: String,
    description: String,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .testTag("home_key_card_$digit"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(color, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = digit,
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = Navy900
            )

            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2
            )
        }
    }
}

@Composable
fun HomeCustomerPolicyCard(
    summary: PolicyInstallmentSummary,
    onOpenDossier: () -> Unit,
    onOpenInvoice: () -> Unit,
    onRecordPayment: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(Navy800.copy(alpha = 0.1f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = Navy800, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = summary.customer.fullName,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Navy900
                        )
                        Text(
                            text = "${summary.policy.insuranceCompany} • ${summary.policy.insuranceType}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (summary.remainingAmount == 0L) Green600.copy(alpha = 0.15f) else Gold500.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "${summary.paidCount} از ${summary.totalCount} قسط",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (summary.remainingAmount == 0L) Green600 else Navy800,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "مورد: ${summary.policy.insuredSubject}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = if (summary.remainingAmount == 0L) "تسویه شده" else "مانده: ${InsuranceViewModel.formatToman(summary.remainingAmount)}",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = if (summary.remainingAmount == 0L) Green600 else Red600
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Button(
                    onClick = onOpenDossier,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Navy800)
                ) {
                    Text("اطلاعات کامل (۲)", fontSize = 11.sp)
                }

                OutlinedButton(
                    onClick = onRecordPayment,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("سند پرداخت (۶)", fontSize = 11.sp)
                }

                OutlinedButton(
                    onClick = onOpenInvoice,
                    modifier = Modifier.weight(0.8f)
                ) {
                    Text("فاکتور", fontSize = 11.sp)
                }
            }
        }
    }
}
