package org.techtown.dotanddoc

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TransDocAdapter : RecyclerView.Adapter<Holder2>() {
    var listData = mutableListOf<TransDocListData>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder2 {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.transformed_braille_list_item,
            parent, false)
        return Holder2(view)
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    override fun onBindViewHolder(holder: Holder2, position: Int) {
        val data = listData[position]
        holder.setListData(data)
    }
}

class Holder2(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var textTitleTextView = itemView.findViewById<TextView>(R.id.item_text)

    fun setText(listData: TransDocListData) {
        textTitleTextView.text = listData.title
    }

    fun setListData(listData: TransDocListData) {
    }
}
