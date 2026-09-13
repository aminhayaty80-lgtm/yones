package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.InvoiceDialog
import com.example.ui.components.QuickKeypadBar
import com.example.ui.screens.CustomerDetailScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.InstallmentsLedgerScreen
import com.example.ui.screens.PaymentVoucherScreen
import com.example.ui.theme.Gold500
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.Navy800
import com.example.ui.theme.Teal500
import com.example.ui.viewmodel.AppNavDestination
import com.example.ui.viewmodel.InsuranceViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    InsuranceAppRoot()
                }
            }
        }
    }
}

@Composable
fun InsuranceAppRoot(
    viewModel: InsuranceViewModel = viewModel()
) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val invoicePolicy by viewModel.invoicePolicy.collectAsState()
    val customers by viewModel.customers.collectAsState()
    val installments by viewModel.installments.collectAsState()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("app_root"),
        topBar = {
            QuickKeypadBar(
                currentScreen = currentScreen,
                onNavigate = { viewModel.navigateTo(it) },
                onKeyPress = { viewModel.handleShortcutKey(it) }
            )
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier.testTag("bottom_nav_bar"),
                containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp
            ) {
                // 1. Dashboard / Home
                NavigationBarItem(
                    selected = currentScreen == AppNavDestination.HOME,
                    onClick = { viewModel.navigateTo(AppNavDestination.HOME) },
                    icon = { Icon(Icons.Default.Home, contentDescription = "داشبورد") },
                    label = { Text("داشبورد", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = Navy800)
                )

                // 2. Customer Dossier (کلید ۲)
                NavigationBarItem(
                    selected = currentScreen == AppNavDestination.CUSTOMER_INFO,
                    onClick = { viewModel.navigateTo(AppNavDestination.CUSTOMER_INFO) },
                    icon = {
                        BadgedBox(badge = { Badge { Text("۲") } }) {
                            Icon(Icons.Default.Person, contentDescription = "اطلاعات مشتری (۲)")
                        }
                    },
                    label = { Text("اطلاعات (۲)", fontSize = 11.sp, fontWeight = if (currentScreen == AppNavDestination.CUSTOMER_INFO) FontWeight.Bold else FontWeight.Normal) },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = Navy800)
                )

                // 3. Payment Voucher (کلید ۶)
                NavigationBarItem(
                    selected = currentScreen == AppNavDestination.PAYMENT_VOUCHER,
                    onClick = { viewModel.navigateTo(AppNavDestination.PAYMENT_VOUCHER) },
                    icon = {
                        BadgedBox(badge = { Badge { Text("۶") } }) {
                            Icon(Icons.Default.Paid, contentDescription = "سند پرداخت (۶)")
                        }
                    },
                    label = { Text("سند پرداخت (۶)", fontSize = 11.sp, fontWeight = if (currentScreen == AppNavDestination.PAYMENT_VOUCHER) FontWeight.Bold else FontWeight.Normal) },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = Teal500)
                )

                // 4. Installments Ledger (کلید ۷)
                NavigationBarItem(
                    selected = currentScreen == AppNavDestination.INSTALLMENTS_LEDGER,
                    onClick = { viewModel.navigateTo(AppNavDestination.INSTALLMENTS_LEDGER) },
                    icon = {
                        BadgedBox(badge = { Badge { Text("۷") } }) {
                            Icon(Icons.Default.ReceiptLong, contentDescription = "اقساط و مانده (۷)")
                        }
                    },
                    label = { Text("اقساط و مانده (۷)", fontSize = 11.sp, fontWeight = if (currentScreen == AppNavDestination.INSTALLMENTS_LEDGER) FontWeight.Bold else FontWeight.Normal) },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = Gold500)
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = currentScreen,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "ScreenTransition"
            ) { screen ->
                when (screen) {
                    AppNavDestination.HOME -> HomeScreen(viewModel = viewModel)
                    AppNavDestination.CUSTOMER_INFO -> CustomerDetailScreen(viewModel = viewModel)
                    AppNavDestination.PAYMENT_VOUCHER -> PaymentVoucherScreen(viewModel = viewModel)
                    AppNavDestination.INSTALLMENTS_LEDGER -> InstallmentsLedgerScreen(viewModel = viewModel)
                }
            }
        }
    }

    // Official Invoice Dialog
    invoicePolicy?.let { policy ->
        val customer = customers.find { it.id == policy.customerId } ?: com.example.data.model.Customer(
            id = policy.customerId,
            fullName = "مشتری بیمه",
            nationalCode = "-",
            phone = "-",
            birthDate = "-",
            postalCode = "-"
        )
        val policyInstallments = installments.filter { it.policyId == policy.id }

        InvoiceDialog(
            policy = policy,
            customer = customer,
            installments = policyInstallments,
            onDismiss = { viewModel.closeInvoice() }
        )
    }
}
