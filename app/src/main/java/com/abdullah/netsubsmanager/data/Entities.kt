package com.abdullah.netsubsmanager.data

import androidx.room.*

@Entity(tableName = "accounts")
data class Account(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val username: String,
    val password: String,
    val price: Double? = null,
    val status: String = "available", // available | sold
    val notes: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "customers",
    indices = [Index(value = ["phone"], unique = false)]
)
data class Customer(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val phone: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "assignments",
    foreignKeys = [
        ForeignKey(entity = Account::class, parentColumns = ["id"], childColumns = ["accountId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = Customer::class, parentColumns = ["id"], childColumns = ["customerId"], onDelete = ForeignKey.CASCADE)
    ],
    indices = [Index("accountId", unique = true), Index("customerId")]
)
data class Assignment(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val accountId: Long,
    val customerId: Long,
    val assignedPrice: Double,
    val paymentStatus: String, // paid | unpaid
    val smsBody: String,
    val sentVia: String,       // sms_intent | copied
    val assignedAt: Long = System.currentTimeMillis(),
    val expiryAt: Long = assignedAt + 30L * 24L * 60L * 60L * 1000L // 30 days by default
)

@Entity(
    tableName = "payments",
    foreignKeys = [
        ForeignKey(entity = Customer::class, parentColumns = ["id"], childColumns = ["customerId"], onDelete = ForeignKey.CASCADE)
    ],
    indices = [Index("customerId")]
)
data class Payment(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val customerId: Long,
    val amount: Double,
    val note: String? = null,
    val paidAt: Long = System.currentTimeMillis()
)

// DTOs
data class CustomerStats(
    val id: Long,
    val name: String,
    val phone: String,
    val totalUsers: Int,
    val paidCount: Int,
    val unpaidCount: Int,
    val balanceDue: Double
)

data class DeliveredUser(
    val assignmentId: Long,
    val username: String,
    val password: String,
    val price: Double,
    val paymentStatus: String,
    val assignedAt: Long,
    val expiryAt: Long
)
