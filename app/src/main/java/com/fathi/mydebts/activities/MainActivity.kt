package com.fathi.mydebts.activities

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.ContextMenu
import android.view.View
import android.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.fathi.mydebts.DBManager
import com.fathi.mydebts.R
import com.fathi.mydebts.adapters.CustomerAdapter
import com.fathi.mydebts.models.Customer
import com.google.android.material.snackbar.Snackbar
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {

//    val db = Firebase.database("https://my-debts-fa-k-a-2021-default-rtdb.europe-west1.firebasedatabase.app/")
//    val ref = db.reference
    private val db = DBManager(this)
    private var customersList = ArrayList<Customer>()
    private var fullList = ArrayList<Customer>()
    private var adapter: CustomerAdapter? = null

    @SuppressLint("Range")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val cursor = db.getAll("customers")
        if (cursor!!.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndex("id"))
                val name = cursor.getString(cursor.getColumnIndex("name"))
                customersList.add(Customer(id, name, "0"))
            } while (cursor.moveToNext())
        }
        fullList.addAll(customersList)
        adapter = CustomerAdapter(this, customersList)
        lstCustomers.layoutManager = LinearLayoutManager(this)
        lstCustomers.adapter = adapter

        btnAddCustomerM.setOnClickListener {
            startActivity(Intent(this, AddCustomerActivity::class.java))
        }
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        val search = menu?.findItem(R.id.search)!!.actionView as SearchView
        search.queryHint = "Type here to search"
        search.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                if (p0!!.isNotEmpty()) {
                    customersList.clear()
                    fullList.forEach {
                        if (it.name.lowercase(Locale.getDefault()).contains(p0.lowercase(Locale.getDefault())))
                            customersList.add(it)
                    }
                } else {
                    customersList.clear()
                    customersList.addAll(fullList)
                }
                lstCustomers.adapter!!.notifyDataSetChanged()
                return true
            }
        })
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
            else -> Toast.makeText(this, item.title, Toast.LENGTH_LONG).show()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        finishAffinity()
    }
}