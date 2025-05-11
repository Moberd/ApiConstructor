package edu.sfedu_mmcs.apiconstructor.settings_activity

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import edu.sfedu_mmcs.apiconstructor.R
import edu.sfedu_mmcs.apiconstructor.utils.AuthInfo

class AuthTypeAdapter():  RecyclerView.Adapter<AuthTypeAdapter.ViewHolder>() {

    private var mList = mutableListOf<AuthInfo>()

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val textView: TextView = itemView.findViewById(R.id.authName)
        val editText: EditText = itemView.findViewById(R.id.edit_text_api_key)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.auth_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ItemsViewModel = mList[position]
        holder.textView.text = ItemsViewModel.name
        holder.editText.hint = ItemsViewModel.type
        holder.editText.setText(ItemsViewModel.value)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setAuths(newList: List<AuthInfo>) {
        this.mList = newList.toMutableList()
        notifyDataSetChanged()
    }
}