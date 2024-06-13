package edu.sfedu_mmcs.apiconstructor.form_activity

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import edu.sfedu_mmcs.apiconstructor.R
import java.lang.reflect.Type

class FormAdapter: RecyclerView.Adapter<FormAdapter.ViewHolder>() {


    private var mList = mutableListOf<String>()
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val editText : TextView = itemView.findViewById(R.id.formEditText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.form_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: FormAdapter.ViewHolder, position: Int) {
        holder.editText.setHint(mList[position])
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