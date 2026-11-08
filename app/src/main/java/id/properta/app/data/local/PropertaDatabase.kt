package id.properta.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        PropertyEntity::class,
        RentalUnitEntity::class,
        TenantEntity::class,
        RentalContractEntity::class,
        RentalInvoiceEntity::class,
        RentalPaymentEntity::class,
        PropertyExpenseEntity::class,
        DepositTransactionEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class PropertaDatabase : RoomDatabase() {

    abstract fun propertyDao(): PropertyDao
    abstract fun rentalUnitDao(): RentalUnitDao
    abstract fun tenantDao(): TenantDao
    abstract fun financeDao(): FinanceDao

    companion object {
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "ALTER TABLE tenants ADD COLUMN identityType TEXT NOT NULL DEFAULT ''"
                )
                db.execSQL(
                    "ALTER TABLE tenants ADD COLUMN identityNumber TEXT NOT NULL DEFAULT ''"
                )
                db.execSQL(
                    "ALTER TABLE tenants ADD COLUMN identityAddress TEXT NOT NULL DEFAULT ''"
                )
                db.execSQL(
                    "ALTER TABLE tenants ADD COLUMN identityPhotoPath TEXT"
                )
            }
        }


        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""CREATE TABLE IF NOT EXISTS rental_contracts (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    tenantId INTEGER NOT NULL,
                    unitId INTEGER NOT NULL,
                    tenantName TEXT NOT NULL,
                    unitName TEXT NOT NULL,
                    startDate INTEGER NOT NULL,
                    endDate INTEGER NOT NULL,
                    monthlyRent INTEGER NOT NULL,
                    depositAmount INTEGER NOT NULL,
                    status TEXT NOT NULL,
                    createdAt INTEGER NOT NULL
                )""")

                db.execSQL("""CREATE TABLE IF NOT EXISTS rental_invoices (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    contractId INTEGER NOT NULL,
                    period TEXT NOT NULL,
                    description TEXT NOT NULL,
                    amount INTEGER NOT NULL,
                    dueDate INTEGER NOT NULL,
                    createdAt INTEGER NOT NULL
                )""")

                db.execSQL("""CREATE TABLE IF NOT EXISTS rental_payments (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    invoiceId INTEGER NOT NULL,
                    contractId INTEGER NOT NULL,
                    amount INTEGER NOT NULL,
                    paidAt INTEGER NOT NULL,
                    method TEXT NOT NULL,
                    note TEXT NOT NULL
                )""")

                db.execSQL("""CREATE TABLE IF NOT EXISTS property_expenses (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    propertyId INTEGER,
                    category TEXT NOT NULL,
                    description TEXT NOT NULL,
                    amount INTEGER NOT NULL,
                    occurredAt INTEGER NOT NULL
                )""")

                db.execSQL("""CREATE TABLE IF NOT EXISTS deposit_transactions (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    contractId INTEGER NOT NULL,
                    amount INTEGER NOT NULL,
                    type TEXT NOT NULL,
                    occurredAt INTEGER NOT NULL,
                    note TEXT NOT NULL
                )""")
            }
        }

        @Volatile
        private var INSTANCE: PropertaDatabase? = null

        fun getInstance(context: Context): PropertaDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    PropertaDatabase::class.java,
                    "properta.db"
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
