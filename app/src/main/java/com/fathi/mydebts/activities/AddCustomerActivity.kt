package com.fathi.mydebts.activities

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.fathi.mydebts.DBManager
import com.fathi.mydebts.R
import com.fathi.mydebts.models.Customer
import com.fathi.mydebts.models.Debt
import kotlinx.android.synthetic.main.activity_add_customer.*
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.lang.StringBuilder
import java.util.*

class AddCustomerActivity : AppCompatActivity() {

    private val db = DBManager(this)
    private var bitmap: Bitmap? = null

    private val getContent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == RESULT_OK) {
            val imageUri = it.data?.data
            try {
                val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
                    ImageDecoder.decodeBitmap(ImageDecoder.createSource(contentResolver, imageUri!!))
                else
                    MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
                this.bitmap = bitmap
                imageCA.setImageBitmap(bitmap)
            } catch (e: FileNotFoundException) {
                Toast.makeText(this, e.localizedMessage, Toast.LENGTH_LONG).show()
            } catch (e: IOException) {
                Toast.makeText(this, e.localizedMessage, Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_customer)

        imageCA.setOnClickListener {
            Toast.makeText(this, "Image select", Toast.LENGTH_LONG).show()
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            getContent.launch(intent)
        }

        btnAddCustomerCA.setOnClickListener {
            if (edtNameCA.text.isNotEmpty() && edtDebtCA.text.isNotEmpty()) {
                try {
                    val customer = Customer(edtNameCA.text.toString(), edtPhoneCA.text.toString())
                    db.insertCustomer(customer)
                    val cursor = db.getRows("customers", "name", customer.name)
                    cursor!!.moveToFirst()
                    val byteArrayOutputStream = ByteArrayOutputStream()
                    bitmap?.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
                    val image: ByteArray = byteArrayOutputStream.toByteArray()
                    val c = Calendar.getInstance()
                    val debt = Debt(
                        edtDebtCA.text.toString().toInt(),
                        StringBuilder().append(c.get(Calendar.YEAR).toString()).append("-").append(c.get(Calendar.MONTH).toString()).append("-").append(c.get(Calendar.DATE).toString()).toString(),
                        edtDescCA.text.toString(),
                        image,
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")))
                    db.insertDebt(debt)
                    db.close()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } catch (e: Exception) {
                    Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(this, "Name and debt mustn't be empty", Toast.LENGTH_LONG).show()
            }
        }
    }
}