package org.techtown.dotanddoc

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.main_toolbar.*

class WriteActivity : AppCompatActivity() {
    private var content: EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_toolbar)

        setSupportActionBar(main_layout_toolbar) // 툴바를 액티비티의 앱바로 지정
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back) // 홈버튼 이미지 변경
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.elevation = 0f

        val action_bar_title = findViewById<View>(R.id.action_bar_title) as TextView
        action_bar_title.text = "텍스트 수정"
        //content = findViewById<View>(R.id.content) as EditText

        content?.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                Log.d("content", "현재 입력된 값=${s.toString()}")
            }
        })
    }
}
