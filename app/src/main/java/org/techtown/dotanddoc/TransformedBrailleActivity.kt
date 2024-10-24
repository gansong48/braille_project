package org.techtown.dotanddoc

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.transformed_braille_file.*

class TransformedBrailleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.transformed_braille_file)

        var data:MutableList<TransBrailleListData> = setData()
        var adapter = TransBrailleAdapter()
        adapter.listData = data
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    fun setData(): MutableList<TransBrailleListData> {
        var data:MutableList<TransBrailleListData> = mutableListOf()
        for(num in 1..10) {
            var title = "${num}번 째 타이틀"
            var listdata = TransBrailleListData(title)
            data.add(listdata)
        }
        return data
    }
}