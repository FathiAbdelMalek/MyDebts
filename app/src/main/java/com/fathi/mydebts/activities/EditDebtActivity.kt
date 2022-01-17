package com.fathi.mydebts.activities

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
import kotlinx.android.synthetic.main.activity_edit_debt.*
import java.io.FileNotFoundException
import java.io.IOException

class EditDebtActivity : AppCompatActivity() {

    private val db = DBManager(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_debt)
    }
}