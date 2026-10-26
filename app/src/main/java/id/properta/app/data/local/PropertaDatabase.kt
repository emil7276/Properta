package id.properta.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        PropertyEntity::class,
        RentalUnitEntity::class,
        TenantEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class PropertaDatabase : RoomDatabase() {
    abstract fun propertyDao(): PropertyDao
    abstract fun rentalUnitDao(): RentalUnitDao
    abstract fun tenantDao(): TenantDao

    companion object {
        @Volatile
        private var INSTANCE: PropertaDatabase? = null

        fun getInstance(context: Context): PropertaDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    PropertaDatabase::class.java,
                    "properta.db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
