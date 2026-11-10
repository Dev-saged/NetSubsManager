package com.abdullah.netsubsmanager.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountDao {
    @Query("SELECT * FROM accounts ORDER BY id DESC")
    fun all(): Flow<List<Account>>

    @Query("SELECT * FROM accounts WHERE status=:status ORDER BY id DESC")
    fun byStatus(status: String): Flow<List<Account>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(accounts: List<Account>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(account: Account): Long

    @Update
    suspend fun update(account: Account)

    @Query("UPDATE accounts SET status='sold', updatedAt=:ts WHERE id=:id")
    suspend fun markSold(id: Long, ts: Long)

    @Query("SELECT * FROM accounts WHERE id=:id")
    suspend fun getById(id: Long): Account?
}

@Dao
interface CustomerDao {
    @Query("SELECT * FROM customers ORDER BY name ASC")
    fun all(): Flow<List<Customer>>

    @Insert
    suspend fun insert(customer: Customer): Long

    @Update
    suspend fun update(customer: Customer)
}

@Dao
interface AssignmentDao {
    @Insert
    suspend fun insert(a: Assignment): Long

    @Transaction
    @Query("SELECT COUNT(*) FROM assignments")
    fun count(): Flow<Int>

    @Query("""
        SELECT s.id as assignmentId, a.username as username, a.password as password,
               s.assignedPrice as price, s.paymentStatus as paymentStatus,
               s.assignedAt as assignedAt, s.expiryAt as expiryAt
        FROM assignments s
        INNER JOIN accounts a ON a.id = s.accountId
        WHERE s.customerId = :customerId
        ORDER BY s.assignedAt DESC
    """)
    fun deliveredByCustomer(customerId: Long): Flow<List<DeliveredUser>>
}

@Dao
interface PaymentDao {
    @Insert
    suspend fun insert(p: Payment): Long

    @Query("SELECT SUM(amount) FROM payments WHERE customerId=:customerId")
    suspend fun sumByCustomer(customerId: Long): Double?
}

@Dao
interface ReportsDao {
    @Query("""
        SELECT c.id as id, c.name as name, c.phone as phone,
               COUNT(a.id) as totalUsers,
               SUM(CASE WHEN s.paymentStatus='paid' THEN 1 ELSE 0 END) as paidCount,
               SUM(CASE WHEN s.paymentStatus='unpaid' THEN 1 ELSE 0 END) as unpaidCount,
               (
                 COALESCE(SUM(CASE WHEN s.paymentStatus='unpaid' THEN s.assignedPrice ELSE 0 END),0)
                 - COALESCE((SELECT SUM(p.amount) FROM payments p WHERE p.customerId=c.id),0)
               ) as balanceDue
        FROM customers c
        LEFT JOIN assignments s ON s.customerId=c.id
        LEFT JOIN accounts a ON a.id = s.accountId
        GROUP BY c.id
        ORDER BY c.name ASC
    """)
    fun customerStats(): Flow<List<CustomerStats>>
}
