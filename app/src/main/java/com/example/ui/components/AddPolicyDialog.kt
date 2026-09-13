package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PostAdd
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.Navy800
import com.example.ui.theme.Teal500
import com.example.ui.viewmodel.InsuranceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPolicyDialog(
    onDismiss: () -> Unit,
    onSubmit: (
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
        notes: String
    ) -> Unit
) {
    var fullName by remember { mutableStateOf("") }
    var nationalCode by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf("۱۳۷۰/۰۱/۰۱") }
    var postalCode by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }

    val insuranceCompanies = listOf("بیمه ایران", "بیمه دانا", "بیمه آسیا", "بیمه سامان", "بیمه البرز", "بیمه پاسارگاد", "بیمه کوثر", "بیمه معلم", "بیمه رازی")
    var selectedCompany by remember { mutableStateOf(insuranceCompanies[0]) }
    var companyExpanded by remember { mutableStateOf(false) }

    val insuranceTypes = listOf("شخص ثالث خودرو", "بدنه اتومبیل", "عمر و سرمایه‌گذاری", "درمان تکمیلی", "آتش‌سوزی و زلزله", "مسئولیت مدنی", "حوادث انفرادی")
    var selectedType by remember { mutableStateOf(insuranceTypes[0]) }
    var typeExpanded by remember { mutableStateOf(false) }

    var insuredSubject by remember { mutableStateOf("") }
    var policyNumber by remember { mutableStateOf("۱۴۰۳/${(10..99).random()}/${(1000..9999).random()}") }
    var totalPremiumText by remember { mutableStateOf("7000000") }
    var selectedInstallmentsCount by remember { mutableIntStateOf(7) } // e.g. 7 or 5 or 11 or 6
    var notes by remember { mutableStateOf("") }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(vertical = 20.dp)
                .testTag("add_policy_dialog"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "انصراف")
                    }
                    Text(
                        text = "صدور بیمه‌نامه و پرونده جدید مشتری",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Navy800
                    )
                    Icon(
                        imageVector = Icons.Default.PostAdd,
                        contentDescription = null,
                        tint = Teal500
                    )
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                // Section 1: Customer Details
                Text(
                    text = "۱. مشخصات بیمه‌گذار (مشتری)",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = Navy800
                )

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("نام و نام خانوادگی *") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_customer_name")
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = nationalCode,
                        onValueChange = { nationalCode = it },
                        label = { Text("کد ملی / کد بیمه‌گذار *") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("شماره تماس *") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = birthDate,
                        onValueChange = { birthDate = it },
                        label = { Text("تاریخ تولد (مثال: ۱۳۶۵/۰۴/۱۵)") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = postalCode,
                        onValueChange = { postalCode = it },
                        label = { Text("کد پستی ده رقمی") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("نشانی محل سکونت / کار") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Section 2: Policy Details
                Text(
                    text = "۲. مشخصات بیمه‌نامه و شرکت صادرکننده",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = Navy800
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Company Dropdown
                ExposedDropdownMenuBox(
                    expanded = companyExpanded,
                    onExpandedChange = { companyExpanded = !companyExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = selectedCompany,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("شرکت صادر کننده *") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = companyExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = companyExpanded,
                        onDismissRequest = { companyExpanded = false }
                    ) {
                        insuranceCompanies.forEach { company ->
                            DropdownMenuItem(
                                text = { Text(company) },
                                onClick = {
                                    selectedCompany = company
                                    companyExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Type Dropdown
                ExposedDropdownMenuBox(
                    expanded = typeExpanded,
                    onExpandedChange = { typeExpanded = !typeExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = selectedType,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("نوع بیمه *") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = typeExpanded,
                        onDismissRequest = { typeExpanded = false }
                    ) {
                        insuranceTypes.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type) },
                                onClick = {
                                    selectedType = type
                                    typeExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = insuredSubject,
                    onValueChange = { insuredSubject = it },
                    label = { Text("مورد بیمه (مثال: پژو ۲۰۶ سفید / آپارتمان مسکونی) *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = policyNumber,
                        onValueChange = { policyNumber = it },
                        label = { Text("شماره بیمه‌نامه") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = totalPremiumText,
                        onValueChange = { totalPremiumText = it.filter { char -> char.isDigit() } },
                        label = { Text("مبلغ کل حق‌بیمه (تومان) *") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Section 3: Installment Plan (تعداد اقساط: ۷ قسط، ۵ قسط، ۱۱ قسط، ۶ قسط، ...)
                Text(
                    text = "۳. طرح تقسیط بیمه‌نامه (انتخاب تعداد اقساط)",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = Navy800
                )

                Text(
                    text = "تعداد اقساط مورد نظر را انتخاب کنید (به همراه محاسبه خودکار هر قسط):",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                val installmentOptions = listOf(1, 3, 5, 6, 7, 10, 11, 12)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    installmentOptions.take(4).forEach { count ->
                        InstallmentCountChip(
                            count = count,
                            isSelected = selectedInstallmentsCount == count,
                            onClick = { selectedInstallmentsCount = count },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    installmentOptions.drop(4).forEach { count ->
                        InstallmentCountChip(
                            count = count,
                            isSelected = selectedInstallmentsCount == count,
                            onClick = { selectedInstallmentsCount = count },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                val totalParsed = totalPremiumText.toLongOrNull() ?: 0L
                if (totalParsed > 0) {
                    val perInstallment = totalParsed / selectedInstallmentsCount
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "مبلغ هر قسط ($selectedInstallmentsCount قسط):",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = InsuranceViewModel.formatToman(perInstallment),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = Navy800
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("توضیحات و یادداشت‌ها") },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )

                if (errorMessage != null) {
                    Text(
                        text = errorMessage ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(vertical = 6.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Submit & Cancel
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            if (fullName.isBlank()) {
                                errorMessage = "لطفاً نام و نام خانوادگی را وارد نمایید."
                                return@Button
                            }
                            if (phone.isBlank()) {
                                errorMessage = "لطفاً شماره تماس را وارد نمایید."
                                return@Button
                            }
                            if (totalParsed <= 0) {
                                errorMessage = "مبلغ حق‌بیمه معتبر نیست."
                                return@Button
                            }
                            if (insuredSubject.isBlank()) {
                                errorMessage = "لطفاً مورد بیمه را وارد نمایید."
                                return@Button
                            }
                            onSubmit(
                                fullName,
                                nationalCode.ifBlank { "ثبت‌نشده" },
                                phone,
                                birthDate,
                                postalCode.ifBlank { "ثبت‌نشده" },
                                address,
                                policyNumber,
                                selectedCompany,
                                selectedType,
                                insuredSubject,
                                totalParsed,
                                selectedInstallmentsCount,
                                notes
                            )
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Navy800)
                    ) {
                        Text("ثبت و ایجاد اقساط")
                    }

                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(0.5f)
                    ) {
                        Text("انصراف")
                    }
                }
            }
        }
    }
}

@Composable
fun InstallmentCountChip(
    count: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bg = if (isSelected) Navy800 else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
    val textColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
    val label = if (count == 1) "یک قسط (نقد)" else "$count قسط"

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bg)
            .clickable(onClick = onClick)
            .padding(vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = textColor,
            maxLines = 1
        )
    }
}
