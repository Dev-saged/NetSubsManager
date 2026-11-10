package com.abdullah.netsubsmanager.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.abdullah.netsubsmanager.repo.Repository
import com.abdullah.netsubsmanager.util.parseBdfOrCsv
import kotlinx.coroutines.launch

@Composable
fun ImportExportScreen(nav: NavHostController, repo: Repository) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    var lastImported by remember { mutableStateOf(0) }

    val pickDoc = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
        uri?.let {
            scope.launch {
                val list = parseBdfOrCsv(ctx, it)
                repo.addAccounts(list)
                lastImported = list.size
            }
        }
    }

    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("استيراد / نسخ احتياطي", style = MaterialTheme.typography.headlineSmall)
        Button(onClick = { pickDoc.launch(arrayOf("text/*","application/octet-stream")) }) { Text("استيراد ملف BDF/CSV") }
        if (lastImported>0) Text("تم استيراد: " + lastImported + " يوزر")
    }
}
