package com.abdullah.netsubsmanager.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.abdullah.netsubsmanager.repo.Repository
import com.abdullah.netsubsmanager.util.Settings
import com.abdullah.netsubsmanager.util.defaultMessageTemplate
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(nav: NavHostController, repo: Repository) {
    val ctx = androidx.compose.ui.platform.LocalContext.current
    val settings = remember { Settings(ctx) }
    val scope = rememberCoroutineScope()
    val current by settings.templateFlow.collectAsState(initial = defaultMessageTemplate())
    var editor by remember { mutableStateOf(current) }

    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("قالب الرسائل", style = MaterialTheme.typography.headlineSmall)
        Text("المتغيرات المتاحة: {USERNAME} {PASSWORD} {PAID_STATUS} {EXPIRY_DATE}")
        OutlinedTextField(value = editor, onValueChange = { editor = it }, modifier = Modifier.fillMaxWidth().weight(1f), minLines = 6)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { scope.launch { settings.saveTemplate(editor) } }) { Text("حفظ") }
            OutlinedButton(onClick = { editor = defaultMessageTemplate() }) { Text("إعادة الافتراضي") }
        }
    }
}
