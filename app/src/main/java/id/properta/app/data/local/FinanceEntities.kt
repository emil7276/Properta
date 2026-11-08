package id.properta.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Embedded
import androidx.room.ColumnInfo

@Entity(tableName = "rental_contracts")
data class RentalContractEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val tenantId: Long,
    val unitId: Long,
    val tenantName: String,
    val unitName: String,
    val startDate: Long,
    val endDate: Long,
    val monthlyRent: Long,
    val depositAmount: Long,
    val status: String = "AKTIF",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "rental_invoices")
data class RentalInvoiceEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val contractId: Long,
    val period: String,
    val description: String,
    val amount: Long,
    val dueDate: Long,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "rental_payments")
data class RentalPaymentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val invoiceId: Long,
    val contractId: Long,
    val amount: Long,
    val paidAt: Long = System.currentTimeMillis(),
    val method: String = "TUNAI",
    val note: String = ""
)

@Entity(tableName = "property_expenses")
data class PropertyExpenseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val propertyId: Long?,
    val category: String,
    val description: String,
    val amount: Long,
    val occurredAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "deposit_transactions")
data class DepositTransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val contractId: Long,
    val amount: Long,
    val type: String,
    val occurredAt: Long = System.currentTimeMillis(),
    val note: String = ""
)

data class InvoiceWithPaidTotal(
    @Embedded val invoice: RentalInvoiceEntity,
    @ColumnInfo(name = "paidTotal") val paidTotal: Long
)
