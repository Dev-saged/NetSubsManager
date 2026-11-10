package com.abdullah.netsubsmanager.util

import android.content.Context
import android.net.Uri
import com.abdullah.netsubsmanager.data.Account
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

suspend fun parseBdfOrCsv(context: Context, uri: Uri): List<Account> {
    val result = mutableListOf<Account>()
    context.contentResolver.openInputStream(uri)?.bufferedReader()?.use { br ->
        br.forEachLine { raw ->
            val line = raw.trim()
            if (line.isEmpty()) return@forEachLine
            val parts = when {
                line.contains(';') -> line.split(';')
                line.contains(',') -> line.split(',')
                line.contains('\t') -> line.split('\t')
                else -> line.split(' ')
            }.map { it.trim() }.filter { it.isNotEmpty() }
            if (parts.size >= 2) {
                val user = parts[0]
                val pass = parts[1]
                result += Account(username = user, password = pass)
            }
        }
    }
    return result
}

@Serializable
data class Backup(
    val accounts: List<SimpleAccount>,
    val customers: List<SimpleCustomer>,
    val assignments: List<SimpleAssignment>,
    val payments: List<SimplePayment>
)

@Serializable data class SimpleAccount(val id: Long, val username: String, val password: String, val price: Double?, val status: String, val notes: String?, val createdAt: Long, val updatedAt: Long)
@Serializable data class SimpleCustomer(val id: Long, val name: String, val phone: String, val createdAt: Long)
@Serializable data class SimpleAssignment(val id: Long, val accountId: Long, val customerId: Long, val assignedPrice: Double, val paymentStatus: String, val smsBody: String, val sentVia: String, val assignedAt: Long, val expiryAt: Long)
@Serializable data class SimplePayment(val id: Long, val customerId: Long, val amount: Double, val note: String?, val paidAt: Long)

val jsonPretty = Json { prettyPrint = true; ignoreUnknownKeys = true }
