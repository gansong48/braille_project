package org.techtown.dotanddoc

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity

class BeforeTxtBrailleActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.KITKAT_WATCH)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.after_ocr)

        val resultTxt: String? = intent.getStringExtra("resultTxt")

        val BrailleIntent = Intent(this, TxtBrailleActivity::class.java)
        BrailleIntent.putExtra("resultTxt", resultTxt)

        val trans_braille_btn = findViewById<Button>(R.id.trans_braille_btn)

        trans_braille_btn.setOnClickListener {
            Log.d("TxtBrailleActivity","액티비티 넘어감")
            startActivity(BrailleIntent)
        }
    }
}
