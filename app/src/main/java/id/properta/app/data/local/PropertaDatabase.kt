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
        TenantEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class PropertaDatabase : RoomDatabase() {

    abstract fun propertyDao(): PropertyDao
    abstract fun rentalUnitDao(): RentalUnitDao
    abstract fun tenantDao(): TenantDao

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

        @Volatile
        private var INSTANCE: PropertaDatabase? = null

        fun getInstance(context: Context): PropertaDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    PropertaDatabase::class.java,
                    "properta.db"
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
