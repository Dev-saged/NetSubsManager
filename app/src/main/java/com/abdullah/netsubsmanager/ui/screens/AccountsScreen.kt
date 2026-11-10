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

@Composable
fun AccountsScreen(nav: NavHostController, repo: Repository) {
    val accounts by repo.accountsAll().collectAsState(initial = emptyList())
    var filter by remember { mutableStateOf("all") }
    val filtered = when (filter) {
        "available" -> accounts.filter { it.status == "available" }
        "sold" -> accounts.filter { it.status == "sold" }
        else -> accounts
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("مخزون اليوزرات", style = MaterialTheme.typography.headlineSmall)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip("الكل", filter=="all") { filter="all" }
            FilterChip("متاح", filter=="available") { filter="available" }
            FilterChip("مباع", filter=="sold") { filter="sold" }
        }
        Spacer(Modifier.height(12.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(filtered) { a ->
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(12.dp)) {
                        Text("${a.username} / ${a.password}")
                        Text("الحالة: ${a.status}")
                        if (a.status == "available") {
                            TextButton(onClick = { nav.navigate("assign/${a.id}") }) { Text("بيع/إرسال") }
                        }
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
