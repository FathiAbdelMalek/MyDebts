package com.fathi.mydebts.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.fathi.mydebts.DBManager
import com.fathi.mydebts.R
import com.fathi.mydebts.models.Debt
import kotlinx.android.synthetic.main.activity_add_debt.*
import java.io.IOException
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import android.provider.MediaStore
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import androidx.activity.result.contract.ActivityResultContracts
import java.lang.StringBuilder
import java.util.*


class AddDebtActivity : AppCompatActivity() {

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
                imageDA.setImageBitmap(bitmap)
            } catch (e: FileNotFoundException) {
                Toast.makeText(this, e.localizedMessage, Toast.LENGTH_LONG).show()
            } catch (e: IOException) {
                Toast.makeText(this, e.localizedMessage, Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_debt)

        imageDA.setOnClickListener {
            Toast.makeText(this, "Image select", Toast.LENGTH_LONG).show()
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            getContent.launch(intent)
        }

        btnAddDebtDA.setOnClickListener {
            val id = intent.getIntExtra("id", 0)
            if (edtDebtDA.text.isNotEmpty()) {
                val byteArrayOutputStream = ByteArrayOutputStream()
                bitmap?.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
                val image: ByteArray = byteArrayOutputStream.toByteArray()
                val c = Calendar.getInstance()
                val debt = Debt(
                    edtDebtDA.text.toString().toInt(),
                    StringBuilder().append(c.get(Calendar.YEAR).toString()).append("-").append(c.get(Calendar.MONTH).toString()).append("-").append(c.get(Calendar.DATE).toString()).toString(),
                    edtDescDA.text.toString(),
                    if (image.isNotEmpty()) image else null,
                    id)
                db.insertDebt(debt)
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "You must enter the debt", Toast.LENGTH_LONG).show()
            }
        }
    }
}