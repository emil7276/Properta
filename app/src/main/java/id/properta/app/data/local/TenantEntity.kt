package id.properta.app.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "tenants",
    foreignKeys = [
        ForeignKey(
            entity = RentalUnitEntity::class,
            parentColumns = ["id"],
            childColumns = ["unitId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("unitId")]
)
data class TenantEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val unitId: Long? = null,
    val fullName: String,
    val phone: String = "",
    val contractStart: Long? = null,
    val contractEnd: Long? = null,
    val deposit: Long = 0,
    val notes: String = "",
    val identityType: String = "",
    val identityNumber: String = "",
    val identityAddress: String = "",
    val identityPhotoPath: String? = null
)
