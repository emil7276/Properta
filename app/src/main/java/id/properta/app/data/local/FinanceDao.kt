package id.properta.app.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface FinanceDao {
    @Query("SELECT * FROM rental_contracts ORDER BY createdAt DESC")
    fun observeContracts(): Flow<List<RentalContractEntity>>

    @Insert
    suspend fun insertContract(item: RentalContractEntity): Long

    @Update
    suspend fun updateContract(item: RentalContractEntity)

    @Delete
    suspend fun deleteContract(item: RentalContractEntity)

    @Query("""
        SELECT i.*,
        COALESCE((SELECT SUM(p.amount) FROM rental_payments p
        WHERE p.invoiceId = i.id), 0) AS paidTotal
        FROM rental_invoices i ORDER BY i.dueDate DESC
    """)
    fun observeInvoices(): Flow<List<InvoiceWithPaidTotal>>

    @Insert
    suspend fun insertInvoice(item: RentalInvoiceEntity): Long

    @Update
    suspend fun updateInvoice(item: RentalInvoiceEntity)

    @Delete
    suspend fun deleteInvoice(item: RentalInvoiceEntity)

    @Query("SELECT * FROM rental_payments ORDER BY paidAt DESC")
    fun observePayments(): Flow<List<RentalPaymentEntity>>

    @Insert
    suspend fun insertPayment(item: RentalPaymentEntity): Long

    @Update
    suspend fun updatePayment(item: RentalPaymentEntity)

    @Delete
    suspend fun deletePayment(item: RentalPaymentEntity)

    @Query("SELECT * FROM property_expenses ORDER BY occurredAt DESC")
    fun observeExpenses(): Flow<List<PropertyExpenseEntity>>

    @Insert
    suspend fun insertExpense(item: PropertyExpenseEntity): Long

    @Update
    suspend fun updateExpense(item: PropertyExpenseEntity)

    @Delete
    suspend fun deleteExpense(item: PropertyExpenseEntity)

    @Query("SELECT * FROM deposit_transactions ORDER BY occurredAt DESC")
    fun observeDeposits(): Flow<List<DepositTransactionEntity>>

    @Insert
    suspend fun insertDeposit(item: DepositTransactionEntity): Long

    @Update
    suspend fun updateDeposit(item: DepositTransactionEntity)

    @Delete
    suspend fun deleteDeposit(item: DepositTransactionEntity)
}
