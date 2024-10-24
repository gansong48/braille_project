package org.techtown.dotanddoc

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.transformed_doc_file.*

class TransformedDocActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.transformed_doc_file)

        var data:MutableList<TransDocListData> = setData()
        var adapter = TransDocAdapter()
        adapter.listData = data
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    fun setData(): MutableList<TransDocListData> {
        var data:MutableList<TransDocListData> = mutableListOf()
        for(num in 1..10) {
            var title = "${num}번 째 타이틀"
            var listdata = TransDocListData(title)
            data.add(listdata)
        }
        return data
    }
}