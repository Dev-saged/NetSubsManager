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
import kotlinx.coroutines.launch

@Composable
fun CustomersScreen(nav: NavHostController, repo: Repository) {
    val scope = rememberCoroutineScope()
    val stats by repo.customerStats().collectAsState(initial = emptyList())

    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("العملاء", style = MaterialTheme.typography.headlineSmall)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("الاسم") }, modifier = Modifier.weight(1f))
            OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("الجوال") }, modifier = Modifier.weight(1f))
            Button(onClick = {
                if (name.isNotBlank() && phone.isNotBlank()) {
                    scope.launch { repo.addCustomer(name.trim(), phone.trim()); name=""; phone="" }
                }
            }) { Text("إضافة") }
        }
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(stats) { s ->
                Card(Modifier.fillMaxWidth(), onClick = { nav.navigate("customerDetails/${s.id}") }) {
                    Column(Modifier.padding(12.dp)) {
                        Text("${s.name} — ${s.phone}")
                        Text("يوزرات: ${s.totalUsers} / مدفوع: ${s.paidCount} / غير مدفوع: ${s.unpaidCount}")
                        Text("رصيد مستحق: ${String.format("%.2f", s.balanceDue)}")
                    }
                }
            }
        }
    }
}
