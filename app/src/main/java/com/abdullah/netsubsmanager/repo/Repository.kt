package com.abdullah.netsubsmanager.repo

import android.content.Context
import com.abdullah.netsubsmanager.data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class Repository(context: Context) {
    private val db = AppDatabase.get(context)
    private val accounts = db.accountDao()
    private val customers = db.customerDao()
    private val assignments = db.assignmentDao()
    private val payments = db.paymentDao()
    private val reports = db.reportsDao()

    fun accountsAll() = accounts.all()
    fun accountsByStatus(status: String) = accounts.byStatus(status)
    suspend fun addAccounts(list: List<Account>) = accounts.insertAll(list)
    suspend fun updateAccount(a: Account) = accounts.update(a)
    suspend fun accountById(id: Long) = accounts.getById(id)

    fun customersAll() = customers.all()
    suspend fun addCustomer(name: String, phone: String): Long = customers.insert(Customer(name=name, phone=phone))

    fun assignmentsCount() = assignments.count()
    fun deliveredByCustomer(customerId: Long) = assignments.deliveredByCustomer(customerId)
    suspend fun assign(account: Account, customerId: Long, price: Double, paid: Boolean, sms: String, sentVia: String, expiryAt: Long) {
        withContext(Dispatchers.IO) {
            assignments.insert(
                Assignment(
                    accountId = account.id,
                    customerId = customerId,
                    assignedPrice = price,
                    paymentStatus = if (paid) "paid" else "unpaid",
                    smsBody = sms,
                    sentVia = sentVia,
                    assignedAt = System.currentTimeMillis(),
                    expiryAt = expiryAt
                )
            )
            accounts.markSold(account.id, System.currentTimeMillis())
        }
    }

    suspend fun addPayment(customerId: Long, amount: Double, note: String?) = payments.insert(Payment(customerId, amount, note))

    fun customerStats(): Flow<List<CustomerStats>> = reports.customerStats()
}
