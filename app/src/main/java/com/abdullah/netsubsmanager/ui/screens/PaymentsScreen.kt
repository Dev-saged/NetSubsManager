package com.abdullah.netsubsmanager.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.abdullah.netsubsmanager.repo.Repository
import kotlinx.coroutines.launch

@Composable
fun PaymentsScreen(nav: NavHostController, repo: Repository) {
    val scope = rememberCoroutineScope()
    val stats by repo.customerStats().collectAsState(initial = emptyList())
    var selected by remember { mutableStateOf<Long?>(null) }
    var amount by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }

    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("تسجيل دفعة", style = MaterialTheme.typography.headlineSmall)
        DropdownMenuBox(
            label = "اختر عميل",
            items = stats.map { it.id to (it.name + " — " + it.phone) },
            selectedId = selected,
            onSelect = { selected = it }
        )
        OutlinedTextField(value = amount, onValueChange = { amount = it }, label = { Text("المبلغ") })
        OutlinedTextField(value = note, onValueChange = { note = it }, label = { Text("ملاحظة (اختياري)") })
        Button(onClick = {
            val cid = selected ?: return@Button
            val a = amount.toDoubleOrNull() ?: return@Button
            scope.launch { repo.addPayment(cid, a, note.ifBlank { null }) }
            amount = ""; note = ""
        }) { Text("حفظ الدفعة") }
    }
}

@Composable
fun DropdownMenuBox(label: String, items: List<Pair<Long,String>>, selectedId: Long?, onSelect: (Long)->Unit) {
    var expanded by remember { mutableStateOf(false) }
    val display = items.firstOrNull { it.first==selectedId }?.second ?: ""
    Column {
        OutlinedTextField(value = display, onValueChange = { }, label = { Text(label) }, readOnly = true,
            modifier = Modifier.fillMaxWidth().clickable { expanded = true })
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            items.forEach { (id, title) ->
                DropdownMenuItem(text = { Text(title) }, onClick = { onSelect(id); expanded=false })
            }
        }
    }
}
