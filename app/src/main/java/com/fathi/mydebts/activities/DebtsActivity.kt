package com.fathi.mydebts.activities

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.fathi.mydebts.DBManager
import com.fathi.mydebts.R
import com.fathi.mydebts.adapters.DebtAdapter
import com.fathi.mydebts.models.Debt
import kotlinx.android.synthetic.main.activity_debts.*
import kotlin.collections.ArrayList

class DebtsActivity : AppCompatActivity() {

    private val db = DBManager(this)
    private val debtsList = ArrayList<Debt>()
    private var adapter: DebtAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_debts)

        val customerId = intent.getIntExtra("id", 0)
        var cursor = db.getRow("customers", customerId)
        if (cursor!!.moveToFirst()) {
            name.text = cursor.getString(cursor.getColumnIndexOrThrow("name"))
            phone.text = cursor.getString(cursor.getColumnIndexOrThrow("phone"))
        }
        cursor = db.getAllWhere("debts", "customer_id", customerId.toString())
        if (cursor!!.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val value = cursor.getInt(cursor.getColumnIndexOrThrow("value"))
                val date = cursor.getString(cursor.getColumnIndexOrThrow("date"))
                debtsList.add(Debt(id, value, date, customerId))
            } while (cursor.moveToNext())
        }
        cursor.close()
        adapter = DebtAdapter(this, debtsList)
        lstDebts.layoutManager = LinearLayoutManager(this)
        lstDebts.adapter = adapter
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_debts, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.addCustomerM -> {
                startActivity(Intent(this, AddCustomerActivity::class.java))
            }
            R.id.about -> {
                AlertDialog.Builder(this).apply {
                    setTitle("About")
                    setMessage("This app was created by ABDELMALEK FATHI")
                    setPositiveButton("OK") { dialog, _ ->
                        dialog.cancel()
                        dialog.dismiss()
                    }
                    show()
                }
            }
            R.id.exit -> {
                AlertDialog.Builder(this).apply {
                    setTitle("Confirm exit")
                    setMessage("Are you sure you want to exit ?")
                    setPositiveButton("Yes") { _, _ ->
                        finish()
                    }
                    setNegativeButton("No") { dialog, _ ->
                        dialog.cancel()
                        dialog.dismiss()
                    }
                    show()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }
}