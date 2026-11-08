package id.properta.app.data.backup

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.util.Base64
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

object PropertaBackup {

    private const val FORMAT = "PROPERTA_BACKUP"
    private const val FORMAT_VERSION = 1
    private const val DATABASE_VERSION = 3

    private val exportOrder = listOf(
        "properties",
        "rental_units",
        "tenants",
        "rental_contracts",
        "rental_invoices",
        "rental_payments",
        "property_expenses",
        "deposit_transactions"
    )

    private val deleteOrder = listOf(
        "deposit_transactions",
        "rental_payments",
        "rental_invoices",
        "rental_contracts",
        "property_expenses",
        "tenants",
        "rental_units",
        "properties"
    )

    suspend fun export(database: RoomDatabase): String =
        withContext(Dispatchers.IO) {
            val sql = database.openHelper.readableDatabase
            val root = JSONObject()

            root.put("format", FORMAT)
            root.put("formatVersion", FORMAT_VERSION)
            root.put("databaseVersion", DATABASE_VERSION)
            root.put("createdAt", System.currentTimeMillis())

            val tables = JSONObject()

            exportOrder.forEach { table ->
                val rows = JSONArray()
                val cursor = sql.query("SELECT * FROM `$table`")

                cursor.use {
                    while (it.moveToNext()) {
                        val row = JSONObject()

                        for (index in 0 until it.columnCount) {
                            val value: Any? = when (it.getType(index)) {
                                android.database.Cursor.FIELD_TYPE_NULL -> null
                                android.database.Cursor.FIELD_TYPE_INTEGER ->
                                    it.getLong(index)
                                android.database.Cursor.FIELD_TYPE_FLOAT ->
                                    it.getDouble(index)
                                android.database.Cursor.FIELD_TYPE_STRING ->
                                    it.getString(index)
                                android.database.Cursor.FIELD_TYPE_BLOB ->
                                    throw IllegalStateException(
                                        "Kolom BLOB tidak didukung: $table"
                                    )
                                else -> null
                            }

                            row.put(it.getColumnName(index), value ?: JSONObject.NULL)
                        }

                        rows.put(row)
                    }
                }

                tables.put(table, rows)
            }

            root.put("tables", tables)
            root.toString(2)
        }

    fun validate(json: String) {
        val root = JSONObject(json)

        require(root.optString("format") == FORMAT) {
            "File bukan backup PROPERTA."
        }

        require(root.optInt("formatVersion") == FORMAT_VERSION) {
            "Format backup tidak didukung."
        }

        require(root.optInt("databaseVersion") == DATABASE_VERSION) {
            "Versi database backup tidak cocok."
        }

        val tables = root.optJSONObject("tables")
            ?: throw IllegalArgumentException("Data tabel tidak ditemukan.")

        exportOrder.forEach { table ->
            val rows = tables.optJSONArray(table)
                ?: throw IllegalArgumentException(
                    "Data tabel $table tidak lengkap."
                )

            for (index in 0 until rows.length()) {
                require(rows.opt(index) is JSONObject) {
                    "Data pada tabel $table tidak valid."
                }
            }
        }
    }

    suspend fun restore(database: RoomDatabase, json: String) =
        withContext(Dispatchers.IO) {
            validate(json)

            val root = JSONObject(json)
            val tables = root.getJSONObject("tables")
            val sql = database.openHelper.writableDatabase

            sql.beginTransaction()

            try {
                deleteOrder.forEach { table ->
                    sql.delete(table, null, null)
                }

                exportOrder.forEach { table ->
                    val rows = tables.getJSONArray(table)

                    for (index in 0 until rows.length()) {
                        val row = rows.getJSONObject(index)
                        val values = ContentValues(row.length())
                        val keys = row.keys()

                        while (keys.hasNext()) {
                            val key = keys.next()
                            val value = row.get(key)

                            when {
                                value == JSONObject.NULL ->
                                    values.putNull(key)

                                value is String ->
                                    values.put(key, value)

                                value is Int ->
                                    values.put(key, value)

                                value is Long ->
                                    values.put(key, value)

                                value is Double ->
                                    values.put(key, value)

                                value is Boolean ->
                                    values.put(key, if (value) 1 else 0)

                                else -> throw IllegalArgumentException(
                                    "Tipe data tidak didukung pada $table.$key"
                                )
                            }
                        }

                        sql.insert(
                            table,
                            SQLiteDatabase.CONFLICT_ABORT,
                            values
                        )
                    }
                }

                sql.setTransactionSuccessful()
            } finally {
                sql.endTransaction()
            }
        }
}
