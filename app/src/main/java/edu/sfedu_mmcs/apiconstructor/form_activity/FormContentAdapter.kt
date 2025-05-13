package edu.sfedu_mmcs.apiconstructor.form_activity

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import edu.sfedu_mmcs.apiconstructor.R
import edu.sfedu_mmcs.apiconstructor.utils.ContentInfo

class FormContentAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val TEXT = 0
    private val SPINNER = 1
    var mList = mutableListOf<ContentInfo>()

    override fun getItemViewType(position: Int): Int {
        return if (mList[position].enumValues?.isNotEmpty() == true) SPINNER else TEXT
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == SPINNER) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.form_spinner_card, parent, false)
            SpinnerViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.form_card, parent, false)
            TextViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = mList[position]
        if (holder is TextViewHolder) {
            holder.textView.text = item.name
            holder.editText.hint = item.format
            holder.editText.setText(item.example)
        } else if (holder is SpinnerViewHolder) {
            holder.textView.text = item.name
            val adapter = ArrayAdapter(holder.itemView.context, android.R.layout.simple_spinner_item, item.enumValues!!)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            holder.spinner.adapter = adapter
            val defaultPosition = item.enumValues.indexOf(item.example)
            if (defaultPosition >= 0) {
                holder.spinner.setSelection(defaultPosition)
            }
        }
    }

    override fun getItemCount(): Int = mList.size

    @SuppressLint("NotifyDataSetChanged")
    fun setItems(newList: List<ContentInfo>) {
        mList = newList.toMutableList()
        notifyDataSetChanged()
    }

    inner class TextViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val editText: EditText = itemView.findViewById(R.id.formEditText)
        val textView: TextView = itemView.findViewById(R.id.formTextLable)
    }

    inner class SpinnerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.spinnerLabel)
        val spinner: Spinner = itemView.findViewById(R.id.spinner)
    }
}