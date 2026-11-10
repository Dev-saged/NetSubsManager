package com.abdullah.netsubsmanager.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.abdullah.netsubsmanager.repo.Repository
import com.abdullah.netsubsmanager.util.daysRemaining
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CustomerDetailsScreen(nav: NavHostController, repo: Repository, id: Long) {
    val stats = repo.customerStats().collectAsState(initial = emptyList()).value.firstOrNull { it.id == id }
    val delivered by repo.deliveredByCustomer(id).collectAsState(initial = emptyList())
    var filter by remember { mutableStateOf("all") }

    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("تفاصيل العميل", style = MaterialTheme.typography.headlineSmall)
        stats?.let { s ->
            Text("${s.name} — ${s.phone}")
            Text("إجمالي يوزرات: ${s.totalUsers}")
            Text("مدفوع/غير: ${s.paidCount}/${s.unpaidCount}")
            Text("الرصيد: ${String.format("%.2f", s.balanceDue)}")
        } ?: Text("غير موجود")

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip("الكل", filter=="all") { filter="all" }
            FilterChip("سارية", filter=="active") { filter="active" }
            FilterChip("منتهية", filter=="expired") { filter="expired" }
        }

        val now = System.currentTimeMillis()
        val sdf = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }
        val list = delivered.filter { d ->
            when(filter) {
                "active" -> d.expiryAt > now
                "expired" -> d.expiryAt <= now
                else -> true
            }
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.weight(1f)) {
            items(list) { d ->
                val remain = daysRemaining(now, d.expiryAt)
                Card {
                    Column(Modifier.padding(12.dp)) {
                        Text("${d.username} / ${d.password}")
                        Text("السعر: ${d.price} — الحالة: ${if (d.paymentStatus == "paid") "مدفوع" else "غير مدفوع"}")
                        Text("تاريخ التسليم: ${sdf.format(Date(d.assignedAt))}")
                        Text("تاريخ الانتهاء: ${sdf.format(Date(d.expiryAt))}")
                        AssistChip(onClick = {}, label = { Text("متبقي: ${remain} يوم") })
                    }
                }
            }
        }
    }
}

@Composable
private fun FilterChip(text: String, selected: Boolean, onClick: ()->Unit) {
    AssistChip(onClick = onClick, label = { Text(text) }, leadingIcon = null)
}
