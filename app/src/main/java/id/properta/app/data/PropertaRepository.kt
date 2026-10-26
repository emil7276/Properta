package id.properta.app.data

import id.properta.app.data.local.PropertyDao
import id.properta.app.data.local.PropertyEntity
import id.properta.app.data.local.RentalUnitDao
import id.properta.app.data.local.RentalUnitEntity
import id.properta.app.data.local.TenantDao
import id.properta.app.data.local.TenantEntity
import kotlinx.coroutines.flow.Flow

class PropertaRepository(
    private val propertyDao: PropertyDao,
    private val rentalUnitDao: RentalUnitDao,
    private val tenantDao: TenantDao
) {
    fun observeProperties(): Flow<List<PropertyEntity>> =
        propertyDao.observeAll()

    suspend fun addProperty(
        name: String,
        address: String = "",
        notes: String = ""
    ): Long {
        require(name.isNotBlank()) { "Nama properti wajib diisi." }
        return propertyDao.insert(
            PropertyEntity(
                name = name.trim(),
                address = address.trim(),
                notes = notes.trim()
            )
        )
    }

    suspend fun updateProperty(property: PropertyEntity) {
        require(property.name.isNotBlank()) { "Nama properti wajib diisi." }
        propertyDao.update(property.copy(name = property.name.trim()))
    }

    suspend fun deleteProperty(property: PropertyEntity) {
        propertyDao.delete(property)
    }

    fun observeUnits(propertyId: Long): Flow<List<RentalUnitEntity>> =
        rentalUnitDao.observeByProperty(propertyId)

    fun observeAllUnits(): Flow<List<RentalUnitEntity>> =
        rentalUnitDao.observeAll()

    suspend fun addUnit(
        propertyId: Long,
        name: String,
        monthlyRent: Long,
        notes: String = ""
    ): Long {
        require(name.isNotBlank()) { "Nama unit wajib diisi." }
        require(monthlyRent >= 0) { "Harga sewa tidak boleh negatif." }
        return rentalUnitDao.insert(
            RentalUnitEntity(
                propertyId = propertyId,
                name = name.trim(),
                monthlyRent = monthlyRent,
                notes = notes.trim()
            )
        )
    }

    suspend fun updateUnit(unit: RentalUnitEntity) {
        require(unit.name.isNotBlank()) { "Nama unit wajib diisi." }
        require(unit.monthlyRent >= 0) { "Harga sewa tidak boleh negatif." }
        rentalUnitDao.update(unit.copy(name = unit.name.trim()))
    }

    suspend fun deleteUnit(unit: RentalUnitEntity) {
        rentalUnitDao.delete(unit)
    }

    fun observeTenants(): Flow<List<TenantEntity>> =
        tenantDao.observeAll()

    fun observeTenantsByUnit(unitId: Long): Flow<List<TenantEntity>> =
        tenantDao.observeByUnit(unitId)

    suspend fun addTenant(
        fullName: String,
        phone: String = "",
        unitId: Long? = null,
        contractStart: Long? = null,
        contractEnd: Long? = null,
        deposit: Long = 0,
        notes: String = "",
        identityType: String = "",
        identityNumber: String = "",
        identityAddress: String = "",
        identityPhotoPath: String? = null
    ): Long {
        require(fullName.isNotBlank()) { "Nama penyewa wajib diisi." }
        require(deposit >= 0) { "Deposit tidak boleh negatif." }
        if (contractStart != null && contractEnd != null) {
            require(contractEnd >= contractStart) {
                "Tanggal akhir kontrak tidak boleh sebelum tanggal mulai."
            }
        }
        return tenantDao.insert(
            TenantEntity(
                fullName = fullName.trim(),
                phone = phone.trim(),
                unitId = unitId,
                contractStart = contractStart,
                contractEnd = contractEnd,
                deposit = deposit,
                notes = notes.trim(),
                identityType = identityType.trim(),
                identityNumber = identityNumber.trim(),
                identityAddress = identityAddress.trim(),
                identityPhotoPath = identityPhotoPath
            )
        )
    }

    suspend fun updateTenant(tenant: TenantEntity) {
        require(tenant.fullName.isNotBlank()) { "Nama penyewa wajib diisi." }
        require(tenant.deposit >= 0) { "Deposit tidak boleh negatif." }
        if (tenant.contractStart != null && tenant.contractEnd != null) {
            require(tenant.contractEnd >= tenant.contractStart) {
                "Tanggal akhir kontrak tidak boleh sebelum tanggal mulai."
            }
        }
        tenantDao.update(tenant.copy(fullName = tenant.fullName.trim()))
    }

    suspend fun deleteTenant(tenant: TenantEntity) {
        tenantDao.delete(tenant)
    }
}
