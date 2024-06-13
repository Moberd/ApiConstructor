package edu.sfedu_mmcs.apiconstructor.list_activity

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import edu.sfedu_mmcs.apiconstructor.R

class ListAdapter: RecyclerView.Adapter<ListAdapter.ViewHolder>() {

    private var mList = mutableListOf<String>()
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val text: TextView = itemView.findViewById(R.id.listText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_card, parent, false)
        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.text.setText(mList[position])
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setItems(newList: List<String>) {
        this.mList = newList.toMutableList()
        notifyDataSetChanged()
    }


}