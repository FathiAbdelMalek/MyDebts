package com.fathi.mydebts.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.fathi.mydebts.DBManager
import com.fathi.mydebts.R
import com.fathi.mydebts.models.Customer
import kotlinx.android.synthetic.main.activity_edit_customer.*

class EditCustomerActivity : AppCompatActivity() {

    private val db = DBManager(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_customer)

        val id = intent.getIntExtra("id", 0)
        val cursor = db.getRow("customers", id)
        if (cursor!!.moveToFirst()) {
            edtNameCE.setText(cursor.getString(cursor.getColumnIndexOrThrow("name")))
            edtPhoneCE.setText(cursor.getString(cursor.getColumnIndexOrThrow("phone")))
            btnEditCustomerCE.setOnClickListener {
                if (edtNameCE.text.isNotEmpty()) {
                    db.updateCustomer(Customer(id, edtNameCE.text.toString(), edtPhoneCE.text.toString()))
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "You must enter the name", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}