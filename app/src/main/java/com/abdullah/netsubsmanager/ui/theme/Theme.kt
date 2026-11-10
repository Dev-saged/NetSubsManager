package com.abdullah.netsubsmanager.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = androidx.compose.material3.lightColorScheme(),
        typography = AppTypography,
        content = content
    )
}
