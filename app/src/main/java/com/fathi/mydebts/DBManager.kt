package com.fathi.mydebts

import android.annotation.SuppressLint
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.content.ContentValues
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.fathi.mydebts.models.Customer
import com.fathi.mydebts.models.Debt
import java.lang.StringBuilder
import java.util.*

class DBManager(private val context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "db"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE customers(" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "name TEXT NOT NULL UNIQUE," +
                    "phone TEXT" +
                    ")"
        )
        db.execSQL(
            "CREATE TABLE debts(" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "value INTEGER NOT NULL," +
                    "date TEXT NOT NULL," +
                    "description TEXT," +
                    "image BLOB," +
                    "customer_id INTEGER," +
                    "CONSTRAINT fk_debt_customer FOREIGN KEY(customer_id) REFERENCES customers(id)" +
                    ")"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, paramInt1: Int, paramInt2: Int) {
        db.execSQL("DROP TABLE IF EXISTS customers")
        db.execSQL("DROP TABLE IF EXISTS debts")
        onCreate(db)
    }

    fun getAll(table: String): Cursor {
        val db = readableDatabase
        return db.rawQuery("SELECT * FROM $table", null)
    }

    fun getAllWhere(table: String, condition: String, value: String): Cursor? {
        val db = readableDatabase
        return db.rawQuery("SELECT * FROM $table WHERE $condition='$value'", null)
    }

    fun getRow(table: String, value: Int): Cursor? {
        val db = readableDatabase
        return db.rawQuery("SELECT * FROM $table WHERE id='$value'", null)
    }

    fun getRows(table: String, column: String, value: String): Cursor? {
        val db = readableDatabase
        return db.rawQuery("SELECT * FROM $table WHERE $column='$value'", null)
    }

    @SuppressLint("Range")
    fun getImage(id: Int): Bitmap? {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT image FROM debts WHERE id='$id'", null)
        cursor.moveToFirst()
        val arrayOfByte = cursor.getBlob(cursor.getColumnIndex("image"))
        cursor.close()
        db.close()
        return if (arrayOfByte != null) BitmapFactory.decodeByteArray(
            arrayOfByte,
            0,
            arrayOfByte.size
        ) else null
    }

    @Throws(Exception::class)
    fun insertCustomer(customer: Customer) {
        val db = writableDatabase
        if (!checkName(customer.name)) {
            val cv = ContentValues()
            cv.put("name", customer.name)
            cv.put("phone", customer.phone)
            db.insert("customers", null, cv)
            db.close()
        } else
            throw Exception("Customer already exists")
    }

    fun updateCustomer(customer: Customer) {
        val db = writableDatabase
        val cv = ContentValues()
        cv.put("name", customer.name)
        cv.put("phone", customer.phone)
        db.update("customers", cv, "id=?", arrayOf(customer.id.toString()))
        db.close()
    }

    fun deleteCustomer(id: Int) {
        val db = writableDatabase
        db.delete("customers", "id=?", arrayOf(id.toString()))
        db.close()
    }

    fun insertDebt(debt: Debt) {
        val db = writableDatabase
        val cv = ContentValues()
        val c = Calendar.getInstance()
        cv.put("value", debt.value)
        cv.put("date", StringBuilder().append(c.get(Calendar.YEAR).toString()).append("-").append(c.get(Calendar.MONTH).toString()).append("-").append(c.get(Calendar.DATE).toString()).toString())
        cv.put("description", debt.description)
        cv.put("image", debt.image)
        cv.put("customer_id", debt.customerId)
        db.insert("debts", null, cv)
        db.close()
    }

    fun updateDebt(debt: Debt) {
        val db = writableDatabase
        val cv = ContentValues()
        cv.put("value", debt.value)
        cv.put("description", debt.description)
        cv.put("image", debt.image)
        db.update("debts", cv, "id=?", arrayOf(debt.id.toString()))
        db.close()
    }

    fun deleteDebt(id: Int) {
        val db = writableDatabase
        db.delete("debts", "id=?", arrayOf(id.toString()))
        db.close()
    }

    private fun checkName(name: String): Boolean {
        val db = readableDatabase
        return db.rawQuery("SELECT * FROM customers WHERE name='$name'", null).count > 0
    }
}
