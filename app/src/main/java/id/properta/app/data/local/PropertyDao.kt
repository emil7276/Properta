package id.properta.app.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PropertyDao {
    @Query("SELECT * FROM properties ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<PropertyEntity>>

    @Query("SELECT * FROM properties WHERE id = :id")
    suspend fun findById(id: Long): PropertyEntity?

    @Insert
    suspend fun insert(property: PropertyEntity): Long

    @Update
    suspend fun update(property: PropertyEntity)

    @Delete
    suspend fun delete(property: PropertyEntity)
}

@Dao
interface RentalUnitDao {
    @Query("SELECT * FROM rental_units WHERE propertyId = :propertyId ORDER BY name")
    fun observeByProperty(propertyId: Long): Flow<List<RentalUnitEntity>>

    @Query("SELECT * FROM rental_units ORDER BY name")
    fun observeAll(): Flow<List<RentalUnitEntity>>

    @Insert
    suspend fun insert(unit: RentalUnitEntity): Long

    @Update
    suspend fun update(unit: RentalUnitEntity)

    @Delete
    suspend fun delete(unit: RentalUnitEntity)
}

@Dao
interface TenantDao {
    @Query("SELECT * FROM tenants ORDER BY fullName")
    fun observeAll(): Flow<List<TenantEntity>>

    @Query("SELECT * FROM tenants WHERE unitId = :unitId")
    fun observeByUnit(unitId: Long): Flow<List<TenantEntity>>

    @Insert
    suspend fun insert(tenant: TenantEntity): Long

    @Update
    suspend fun update(tenant: TenantEntity)

    @Delete
    suspend fun delete(tenant: TenantEntity)
}
