package org.techtown.dotanddoc

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class TransBrailleAdapter : RecyclerView.Adapter<Holder1>() {

    var listData = mutableListOf<TransBrailleListData>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder1 {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.transformed_braille_list_item,
            parent, false)
        return Holder1(view)

    }

    override fun getItemCount(): Int {
        return listData.size
    }

    override fun onBindViewHolder(holder: Holder1, position: Int) {
        val data = listData[position]
        holder.setListData(data)
    }
}

class Holder1(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var textTitleTextView = itemView.findViewById<TextView>(R.id.item_text)

    fun setText(listData: TransBrailleListData) {
        textTitleTextView.text = listData.title
    }

    fun setListData(listData: TransBrailleListData) {
    }
}
