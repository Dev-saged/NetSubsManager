package com.abdullah.netsubsmanager.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [Account::class, Customer::class, Assignment::class, Payment::class],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun accountDao(): AccountDao
    abstract fun customerDao(): CustomerDao
    abstract fun assignmentDao(): AssignmentDao
    abstract fun paymentDao(): PaymentDao
    abstract fun reportsDao(): ReportsDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null
        fun get(context: Context): AppDatabase = INSTANCE ?: synchronized(this){
            INSTANCE ?: Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "netsubs.db"
            ).fallbackToDestructiveMigration()
             .build().also { INSTANCE = it }
        }
    }
}
