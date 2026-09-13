package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Customer
import com.example.data.model.InsurancePolicy
import com.example.data.model.PaymentVoucher
import com.example.data.model.PolicyInstallment
import com.example.ui.theme.Green600
import com.example.ui.theme.Navy800
import com.example.ui.theme.Teal500
import com.example.ui.viewmodel.AppNavDestination
import com.example.ui.viewmodel.InsuranceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentVoucherScreen(
    viewModel: InsuranceViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val customers by viewModel.customers.collectAsState()
    val policies by viewModel.policies.collectAsState()
    val installments by viewModel.installments.collectAsState()
    val vouchers by viewModel.vouchers.collectAsState()

    var selectedCustomer by remember(customers) { mutableStateOf(customers.firstOrNull()) }
    var customerExpanded by remember { mutableStateOf(false) }

    val customerPolicies = policies.filter { it.customerId == selectedCustomer?.id }
    var selectedPolicy by remember(customerPolicies) { mutableStateOf(customerPolicies.firstOrNull()) }
    var policyExpanded by remember { mutableStateOf(false) }

    val customerUnpaidInstallments = installments.filter { it.policyId == selectedPolicy?.id && !it.isPaid }
    var selectedInstallment by remember(customerUnpaidInstallments) { mutableStateOf(customerUnpaidInstallments.firstOrNull()) }
    var installmentExpanded by remember { mutableStateOf(false) }

    var amountText by remember { mutableStateOf(selectedInstallment?.amount?.toString() ?: "1000000") }
    var voucherNumber by remember { mutableStateOf("سند-${(1000..9999).random()}") }

    val paymentMethods = listOf("کارت‌خوان شعبه (POS)", "کارت به کارت شتابی", "حواله پایا / ساتنا", "چک صیادی بانکی", "واریز نقدی")
    var selectedMethod by remember { mutableStateOf(paymentMethods[0]) }
    var methodExpanded by remember { mutableStateOf(false) }

    var referenceCode by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("تسویه قسط بیمه‌نامه") }
    var showSuccessBanner by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("payment_voucher_screen")
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
                        .background(Teal500, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "۶",
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "ثبت سند مالی و پرداخت (کلید ۶)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Navy800
                    )
                    Text(
                        text = "سند زدن پرداخت حق بیمه و تسویه اقساط",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            IconButton(onClick = { viewModel.navigateTo(AppNavDestination.HOME) }) {
                Icon(imageVector = Icons.Default.Home, contentDescription = "داشبورد")
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Form Card
            item {
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "مشخصات سند پرداخت جدید",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Navy800
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Customer Picker
                        ExposedDropdownMenuBox(
                            expanded = customerExpanded,
                            onExpandedChange = { customerExpanded = !customerExpanded },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = selectedCustomer?.fullName ?: "انتخاب مشتری",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("طرف حساب / بیمه‌گذار *") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = customerExpanded) },
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth()
                            )
                            ExposedDropdownMenu(
                                expanded = customerExpanded,
                                onDismissRequest = { customerExpanded = false }
                            ) {
                                customers.forEach { cust ->
                                    DropdownMenuItem(
                                        text = { Text("${cust.fullName} (${cust.phone})") },
                                        onClick = {
                                            selectedCustomer = cust
                                            val pols = policies.filter { it.customerId == cust.id }
                                            selectedPolicy = pols.firstOrNull()
                                            val unpaids = installments.filter { it.policyId == selectedPolicy?.id && !it.isPaid }
                                            selectedInstallment = unpaids.firstOrNull()
                                            selectedInstallment?.let { amountText = it.amount.toString() }
                                            customerExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Policy Picker
                        if (customerPolicies.isNotEmpty()) {
                            ExposedDropdownMenuBox(
                                expanded = policyExpanded,
                                onExpandedChange = { policyExpanded = !policyExpanded },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                OutlinedTextField(
                                    value = selectedPolicy?.let { "${it.insuranceCompany} - ${it.insuranceType} (${it.insuredSubject})" } ?: "انتخاب بیمه‌نامه",
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("بیمه‌نامه مورد نظر *") },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = policyExpanded) },
                                    modifier = Modifier
                                        .menuAnchor()
                                        .fillMaxWidth()
                                )
                                ExposedDropdownMenu(
                                    expanded = policyExpanded,
                                    onDismissRequest = { policyExpanded = false }
                                ) {
                                    customerPolicies.forEach { pol ->
                                        DropdownMenuItem(
                                            text = { Text("${pol.insuranceCompany} - ${pol.insuranceType} (${pol.policyNumber})") },
                                            onClick = {
                                                selectedPolicy = pol
                                                val unpaids = installments.filter { it.policyId == pol.id && !it.isPaid }
                                                selectedInstallment = unpaids.firstOrNull()
                                                selectedInstallment?.let { amountText = it.amount.toString() }
                                                policyExpanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Installment Picker (Optional تسویه قسط مشخص)
                        if (customerUnpaidInstallments.isNotEmpty()) {
                            ExposedDropdownMenuBox(
                                expanded = installmentExpanded,
                                onExpandedChange = { installmentExpanded = !installmentExpanded },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                OutlinedTextField(
                                    value = selectedInstallment?.let { "قسط ${it.installmentNumber} از ${it.totalInstallments} (${InsuranceViewModel.formatToman(it.amount)})" } ?: "بدون اتصال به قسط خاص",
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("بابت تسویه قسط (اختیاری)") },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = installmentExpanded) },
                                    modifier = Modifier
                                        .menuAnchor()
                                        .fillMaxWidth()
                                )
                                ExposedDropdownMenu(
                                    expanded = installmentExpanded,
                                    onDismissRequest = { installmentExpanded = false }
                                ) {
                                    customerUnpaidInstallments.forEach { inst ->
                                        DropdownMenuItem(
                                            text = { Text("قسط شماره ${inst.installmentNumber} - سررسید: ${inst.dueDate} (${InsuranceViewModel.formatToman(inst.amount)})") },
                                            onClick = {
                                                selectedInstallment = inst
                                                amountText = inst.amount.toString()
                                                description = "تسویه قسط ${inst.installmentNumber} بیمه‌نامه ${selectedPolicy?.insuranceType}"
                                                installmentExpanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Amount & Voucher Number
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = amountText,
                                onValueChange = { amountText = it.filter { char -> char.isDigit() } },
                                label = { Text("مبلغ پرداختی (تومان) *") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("input_voucher_amount")
                            )
                            OutlinedTextField(
                                value = voucherNumber,
                                onValueChange = { voucherNumber = it },
                                label = { Text("شماره سند") },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Payment Method Dropdown
                        ExposedDropdownMenuBox(
                            expanded = methodExpanded,
                            onExpandedChange = { methodExpanded = !methodExpanded },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = selectedMethod,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("روش پرداخت / واریز") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = methodExpanded) },
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth()
                            )
                            ExposedDropdownMenu(
                                expanded = methodExpanded,
                                onDismissRequest = { methodExpanded = false }
                            ) {
                                paymentMethods.forEach { m ->
                                    DropdownMenuItem(
                                        text = { Text(m) },
                                        onClick = {
                                            selectedMethod = m
                                            methodExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = referenceCode,
                            onValueChange = { referenceCode = it },
                            label = { Text("کد پیگیری / شماره ارجاع بانکی") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("شرح سند مالی") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Submit Button
                        Button(
                            onClick = {
                                val amountVal = amountText.toLongOrNull() ?: 0L
                                val cust = selectedCustomer ?: return@Button
                                val pol = selectedPolicy ?: return@Button
                                if (amountVal <= 0) return@Button

                                viewModel.recordPaymentVoucher(
                                    customerId = cust.id,
                                    policyId = pol.id,
                                    installmentId = selectedInstallment?.id,
                                    amount = amountVal,
                                    paymentMethod = selectedMethod,
                                    payerName = cust.fullName,
                                    voucherNumber = voucherNumber,
                                    description = description,
                                    referenceCode = referenceCode
                                ) {
                                    showSuccessBanner = true
                                    voucherNumber = "سند-${(1000..9999).random()}"
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("btn_submit_voucher"),
                            colors = ButtonDefaults.buttonColors(containerColor = Teal500)
                        ) {
                            Icon(imageVector = Icons.Default.Check, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("ثبت سند و صدور رسید مالی")
                        }

                        if (showSuccessBanner) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Green600.copy(alpha = 0.15f),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = Green600)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "سند مالی با موفقیت صادر و قسط مربوطه تسویه گردید.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Green600,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Recent Vouchers List
            item {
                Text(
                    text = "دفتر روزنامه اسناد پرداختی اخیر (${vouchers.size} سند)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Navy800,
                    modifier = Modifier.padding(top = 6.dp)
                )
            }

            items(vouchers) { voucher ->
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .background(Teal500.copy(alpha = 0.12f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(imageVector = Icons.Default.Receipt, contentDescription = null, tint = Teal500, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "${voucher.voucherNumber} - ${voucher.payerName}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${voucher.paymentMethod} • ${voucher.paymentDate}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                if (voucher.description.isNotBlank()) {
                                    Text(
                                        text = voucher.description,
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = InsuranceViewModel.formatToman(voucher.amount),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = Green600
                            )
                            IconButton(
                                onClick = {
                                    val shareText = "رسید سند مالی بیمه\nشماره سند: ${voucher.voucherNumber}\nپرداخت کننده: ${voucher.payerName}\nمبلغ: ${InsuranceViewModel.formatToman(voucher.amount)}\nروش پرداخت: ${voucher.paymentMethod}\nتاریخ: ${voucher.paymentDate}\nکد رهگیری: ${voucher.referenceCode}"
                                    val intent = Intent(Intent.ACTION_SEND).apply {
                                        type = "text/plain"
                                        putExtra(Intent.EXTRA_TEXT, shareText)
                                    }
                                    context.startActivity(Intent.createChooser(intent, "ارسال رسید پرداخت"))
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Share, contentDescription = "اشتراک رسید", tint = Navy800, modifier = Modifier.size(18.dp))
                            }
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
