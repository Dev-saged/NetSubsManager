package com.abdullah.netsubsmanager.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.abdullah.netsubsmanager.repo.Repository
import com.abdullah.netsubsmanager.Routes

@Composable
fun DashboardScreen(nav: NavHostController, repo: Repository) {
    val accounts by repo.accountsAll().collectAsState(initial = emptyList())
    val sold = accounts.count { it.status == "sold" }
    val available = accounts.size - sold
    val customers by repo.customersAll().collectAsState(initial = emptyList())

    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("لوحة التحكم", style = MaterialTheme.typography.headlineSmall)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard("المتاحة", available)
            StatCard("المباعة", sold)
            StatCard("العملاء", customers.size)
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { nav.navigate(Routes.Accounts) }) { Text("مخزون اليوزرات") }
            Button(onClick = { nav.navigate(Routes.Customers) }) { Text("العملاء") }
            Button(onClick = { nav.navigate(Routes.ImportExport) }) { Text("استيراد/نسخ احتياطي") }
            OutlinedButton(onClick = { nav.navigate(Routes.Settings) }) { Text("قالب الرسائل") }
        }
    }
}

@Composable
private fun StatCard(label: String, value: Int) {
    Card(Modifier.weight(1f)) {
        Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label, style = MaterialTheme.typography.bodyMedium)
            Text(value.toString(), style = MaterialTheme.typography.headlineMedium)
        }
    }
}
