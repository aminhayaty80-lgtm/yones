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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Pending
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Customer
import com.example.data.model.InsurancePolicy
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

@Composable
fun CustomerDetailScreen(
    viewModel: InsuranceViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val customers by viewModel.filteredCustomers.collectAsState()
    val policies by viewModel.policies.collectAsState()
    val installments by viewModel.installments.collectAsState()
    val selectedCustId by viewModel.selectedCustomerId.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    // Determine current customer
    val activeCustomer = customers.find { it.id == selectedCustId } ?: customers.firstOrNull()
    val customerPolicies = policies.filter { it.customerId == activeCustomer?.id }
    val activePolicy = customerPolicies.firstOrNull()
    val customerInstallments = installments.filter { it.customerId == activeCustomer?.id }

    val totalPaid = customerInstallments.filter { it.isPaid }.sumOf { it.amount }
    val totalAmount = activePolicy?.totalPremium ?: 0L
    val remaining = (totalAmount - totalPaid).coerceAtLeast(0L)

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("customer_detail_screen")
    ) {
        // Title Bar with "کلید ۲" Tag
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
                        .background(Navy800, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "۲",
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "پرونده و اطلاعات مشتریان (کلید ۲)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Navy800
                    )
                    Text(
                        text = "مشخصات هویتی، بیمه‌نامه و پیگیری اقساط",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            IconButton(onClick = { viewModel.navigateTo(AppNavDestination.HOME) }) {
                Icon(imageVector = Icons.Default.Home, contentDescription = "داشبورد")
            }
        }

        // Search Field
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.setSearchQuery(it) },
            placeholder = { Text("جستجوی نام، کد ملی، شماره تماس...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
                .testTag("search_customer_field")
        )

        // Customer Quick Switcher Chips
        if (customers.isNotEmpty()) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(customers) { cust ->
                    val isSelected = cust.id == activeCustomer?.id
                    Surface(
                        onClick = { viewModel.selectCustomer(cust.id) },
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) Navy800 else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) Navy800 else Color.LightGray)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = cust.fullName,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }

        if (activeCustomer == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("مشتری مورد نظر یافت نشد.")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 1. Detailed Customer Profile Card (نام و نام خانوادگی / تلفن / تاریخ تولد / کد پستی / کد بیمه‌گذار)
                item {
                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .background(Teal500.copy(alpha = 0.15f), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Person,
                                            contentDescription = null,
                                            tint = Teal500
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = activeCustomer.fullName,
                                            style = MaterialTheme.typography.titleLarge,
                                            fontWeight = FontWeight.Bold,
                                            color = Navy900
                                        )
                                        Text(
                                            text = "کد بیمه‌گذار / ملی: ${activeCustomer.nationalCode}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                // Quick Call Button
                                IconButton(
                                    onClick = {
                                        val intent = Intent(Intent.ACTION_DIAL).apply {
                                            data = Uri.parse("tel:${activeCustomer.phone}")
                                        }
                                        context.startActivity(intent)
                                    },
                                    modifier = Modifier
                                        .background(Green600.copy(alpha = 0.12f), CircleShape)
                                        .size(40.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Phone, contentDescription = "تماس", tint = Green600)
                                }
                            }

                            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                            // Grid of customer info as requested
                            InfoDetailItem(icon = Icons.Default.Phone, label = "شماره تماس و همراه:", value = activeCustomer.phone)
                            InfoDetailItem(icon = Icons.Default.Cake, label = "تاریخ تولد:", value = activeCustomer.birthDate)
                            InfoDetailItem(icon = Icons.Default.Email, label = "کد پستی:", value = activeCustomer.postalCode)
                            if (activeCustomer.address.isNotBlank()) {
                                InfoDetailItem(icon = Icons.Default.LocationOn, label = "نشانی محل سکونت:", value = activeCustomer.address)
                            }
                            if (activeCustomer.notes.isNotBlank()) {
                                InfoDetailItem(icon = Icons.Default.Description, label = "توضیحات پرونده:", value = activeCustomer.notes)
                            }
                        }
                    }
                }

                // 2. Policy Dossier Card (نوع بیمه / مورد بیمه / شرکت صادر کننده / ش بیمه / مبلغ)
                if (activePolicy != null) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Navy800)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = activePolicy.insuranceCompany,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = Teal500
                                    ) {
                                        Text(
                                            text = activePolicy.insuranceType,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Color.White,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                PolicyInfoRowLight(label = "مورد بیمه:", value = activePolicy.insuredSubject)
                                PolicyInfoRowLight(label = "شماره بیمه‌نامه (ش بیمه):", value = activePolicy.policyNumber)
                                PolicyInfoRowLight(label = "مبلغ کل حق بیمه:", value = InsuranceViewModel.formatToman(activePolicy.totalPremium))
                                PolicyInfoRowLight(label = "طرح تقسیط:", value = "${activePolicy.installmentsCount} قسط ماهانه")
                                PolicyInfoRowLight(label = "اعتبار بیمه‌نامه:", value = "${activePolicy.issueDate} الی ${activePolicy.expiryDate}")

                                Spacer(modifier = Modifier.height(12.dp))

                                // Invoice and Payment Action buttons
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = { viewModel.openInvoice(activePolicy) },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.buttonColors(containerColor = Teal500)
                                    ) {
                                        Icon(imageVector = Icons.Default.Receipt, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("صدور فاکتور رسمی")
                                    }

                                    Button(
                                        onClick = {
                                            viewModel.navigateTo(AppNavDestination.PAYMENT_VOUCHER)
                                        },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.buttonColors(containerColor = Gold500)
                                    ) {
                                        Icon(imageVector = Icons.Default.Paid, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("ثبت سند پرداخت (۶)")
                                    }
                                }
                            }
                        }
                    }
                }

                // 3. Installments Tracking Section (پیگیری اقساط، تیک پرداخت و مانده)
                item {
                    Text(
                        text = "پیگیری اقساط بیمه‌نامه (${customerInstallments.count { it.isPaid }} از ${customerInstallments.size} پرداخت شده)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Navy800,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }

                // Balance summary card
                item {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.4f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "مبلغ پرداختی (بستانکار)", style = MaterialTheme.typography.labelSmall, color = Green600)
                                Text(text = InsuranceViewModel.formatToman(totalPaid), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = Green600)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "مانده بدهی (بدهکار)", style = MaterialTheme.typography.labelSmall, color = if (remaining > 0) Red600 else Green600)
                                Text(text = InsuranceViewModel.formatToman(remaining), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = if (remaining > 0) Red600 else Green600)
                            }
                        }
                    }
                }

                // List of Installments with Interactive Checkboxes
                items(customerInstallments) { inst ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.toggleInstallment(inst) },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (inst.isPaid) Green600.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (inst.isPaid) Green600.copy(alpha = 0.4f) else Color.LightGray.copy(alpha = 0.4f)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(
                                    checked = inst.isPaid,
                                    onCheckedChange = { viewModel.toggleInstallment(inst) },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = Green600,
                                        uncheckedColor = Slate500
                                    ),
                                    modifier = Modifier.testTag("checkbox_installment_${inst.id}")
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Column {
                                    Text(
                                        text = "قسط ${inst.installmentNumber} از ${inst.totalInstallments}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "سررسید: ${inst.dueDate}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = InsuranceViewModel.formatToman(inst.amount),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (inst.isPaid) Green600 else Navy800
                                )
                                Text(
                                    text = if (inst.isPaid) "پرداخت شد (${inst.paidDate ?: "ثبت‌شده"})" else "پرداخت نشده (تیک بزنید)",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (inst.isPaid) Green600 else Red600,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
fun InfoDetailItem(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = Navy800, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(130.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun PolicyInfoRowLight(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = Color.White.copy(alpha = 0.8f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}
