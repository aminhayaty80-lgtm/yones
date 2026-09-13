package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Dialpad
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.Gold500
import com.example.ui.theme.Navy800
import com.example.ui.theme.Navy900
import com.example.ui.theme.Teal500
import com.example.ui.viewmodel.AppNavDestination

@Composable
fun QuickKeypadBar(
    currentScreen: AppNavDestination,
    onNavigate: (AppNavDestination) -> Unit,
    onKeyPress: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showPad by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .shadow(4.dp, RoundedCornerShape(18.dp)),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            // Header Row: Shortcuts title and dialpad toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Calculate,
                            contentDescription = "کلیدهای سریع",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "دسترسی سریع با کلیدهای میانبر (۲ / ۶ / ۷)",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                IconButton(
                    onClick = { showPad = !showPad },
                    modifier = Modifier
                        .size(34.dp)
                        .testTag("toggle_dialpad_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Dialpad,
                        contentDescription = "صفحه کلید سریع",
                        tint = if (showPad) Teal500 else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Main 3 Shortcut Action Buttons (2, 6, 7) + Home
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                KeyShortcutBadgeButton(
                    keyDigit = "۲",
                    title = "اطلاعات مشتری",
                    sub = "کد، تلفن، بیمه‌نامه",
                    icon = Icons.Default.Person,
                    accentColor = Navy800,
                    isSelected = currentScreen == AppNavDestination.CUSTOMER_INFO,
                    onClick = {
                        onKeyPress("2")
                        onNavigate(AppNavDestination.CUSTOMER_INFO)
                    },
                    modifier = Modifier.weight(1f)
                )

                KeyShortcutBadgeButton(
                    keyDigit = "۶",
                    title = "سند پرداخت",
                    sub = "ثبت سند و رسید",
                    icon = Icons.Default.Paid,
                    accentColor = Teal500,
                    isSelected = currentScreen == AppNavDestination.PAYMENT_VOUCHER,
                    onClick = {
                        onKeyPress("6")
                        onNavigate(AppNavDestination.PAYMENT_VOUCHER)
                    },
                    modifier = Modifier.weight(1f)
                )

                KeyShortcutBadgeButton(
                    keyDigit = "۷",
                    title = "اقساط و مانده",
                    sub = "تیک پرداخت و مانده",
                    icon = Icons.Default.ReceiptLong,
                    accentColor = Gold500,
                    isSelected = currentScreen == AppNavDestination.INSTALLMENTS_LEDGER,
                    onClick = {
                        onKeyPress("7")
                        onNavigate(AppNavDestination.INSTALLMENTS_LEDGER)
                    },
                    modifier = Modifier.weight(1f)
                )
            }

            // Interactive Dropdown Mini-Pad
            AnimatedVisibility(visible = showPad) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "برای پرش مستقیم به هر بخش، روی عدد مورد نظر بزنید:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        listOf("1", "2", "3", "4", "5", "6", "7", "8", "9").chunked(3).forEach { rowKeys ->
                            // mini pad columns
                        }
                    }

                    // Direct row of primary digits
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        DialpadKey(digit = "۲", label = "اطلاعات مشتری", isTarget = true) {
                            onKeyPress("2")
                            showPad = false
                        }
                        DialpadKey(digit = "۶", label = "ثبت سند", isTarget = true) {
                            onKeyPress("6")
                            showPad = false
                        }
                        DialpadKey(digit = "۷", label = "جدول اقساط", isTarget = true) {
                            onKeyPress("7")
                            showPad = false
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun KeyShortcutBadgeButton(
    keyDigit: String,
    title: String,
    sub: String,
    icon: ImageVector,
    accentColor: Color,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) accentColor.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        animationSpec = tween(200),
        label = "bg"
    )

    val borderColor = if (isSelected) accentColor else Color.Transparent

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(backgroundColor)
            .border(1.5.dp, borderColor, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp, horizontal = 6.dp)
            .testTag("shortcut_key_$keyDigit"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Digit badge in circle
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .background(if (isSelected) accentColor else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = keyDigit,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) accentColor else MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                maxLines = 1
            )

            Text(
                text = sub,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 9.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}

@Composable
fun DialpadKey(
    digit: String,
    label: String,
    isTarget: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        color = if (isTarget) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isTarget) MaterialTheme.colorScheme.primary else Color.LightGray),
        modifier = Modifier
            .padding(horizontal = 4.dp)
            .size(width = 90.dp, height = 46.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = digit,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = if (isTarget) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
