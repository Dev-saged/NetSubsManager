package com.abdullah.netsubsmanager.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.abdullah.netsubsmanager.repo.Repository
import com.abdullah.netsubsmanager.util.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

@Composable
fun AssignScreen(nav: NavHostController, repo: Repository, accountId: Long) {
    val ctx = LocalContext.current
    val accounts by repo.accountsAll().collectAsState(initial = emptyList())
    val account = accounts.firstOrNull { it.id == accountId }
    val customers by repo.customersAll().collectAsState(initial = emptyList())
    val settings = remember { Settings(ctx) }
    val template by settings.templateFlow.collectAsState(initial = defaultMessageTemplate())

    var selectedCustomer by remember { mutableStateOf<Long?>(null) }
    var priceText by remember { mutableStateOf(account?.price?.toString() ?: "0") }
    var paid by remember { mutableStateOf(false) }

    val sdf = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }

    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("توزيع/إرسال يوزر", style = MaterialTheme.typography.headlineSmall)
        account?.let { a ->
            Text("يوزر: ${a.username} / ${a.password}")
            OutlinedTextField(value = priceText, onValueChange = { priceText = it }, label = { Text("السعر") })
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip("غير مدفوع", !paid) { paid = false }
                FilterChip("مدفوع", paid) { paid = true }
            }
            Text("اختر عميل")
            LazyColumn(Modifier.weight(1f)) {
                items(customers) { c ->
                    ListItem(
                        headlineContent = { Text("${c.name}") },
                        supportingContent = { Text(c.phone) },
                        trailingContent = {
                            RadioButton(selected = (selectedCustomer==c.id), onClick = { selectedCustomer = c.id })
                        }
                    )
                    Divider()
                }
            }

            val assignTime = System.currentTimeMillis()
            val expiryAt = assignTime + TimeUnit.DAYS.toMillis(30)
            val msg = renderTemplate(
                template = template,
                username = a.username,
                password = a.password,
                paid = paid,
                expiryDate = sdf.format(Date(expiryAt))
            )

            Text(msg)

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                val scope = rememberCoroutineScope()
                Button(onClick = {
                    val cid = selectedCustomer ?: return@Button
                    val price = priceText.toDoubleOrNull() ?: 0.0
                    val phone = customers.first { it.id==cid }.phone
                    sendSms(ctx, phone, msg)
                    scope.launch {
                        repo.assign(a, cid, price, paid, msg, "sms_intent", expiryAt)
                        scheduleExpiryReminder(ctx, assignmentId = a.id, customerName = customers.first { it.id==cid }.name, whenAt = expiryAt)
                    }
                    nav.popBackStack()
                }) { Text("إرسال SMS") }

                OutlinedButton(onClick = {
                    val cid = selectedCustomer ?: return@OutlinedButton
                    val price = priceText.toDoubleOrNull() ?: 0.0
                    copyToClipboard(ctx, msg)
                    val scope2 = rememberCoroutineScope()
                    scope2.launch {
                        repo.assign(a, cid, price, paid, msg, "copied", expiryAt)
                        scheduleExpiryReminder(ctx, assignmentId = a.id, customerName = customers.first { it.id==cid }.name, whenAt = expiryAt)
                    }
                    nav.popBackStack()
                }) { Text("نسخ فقط") }
            }
        } ?: Text("اليوزر غير موجود\nعد إلى المخزون")
    }
}

@Composable
private fun FilterChip(text: String, selected: Boolean, onClick: ()->Unit) {
    AssistChip(onClick = onClick, label = { Text(text) }, leadingIcon = null)
}
