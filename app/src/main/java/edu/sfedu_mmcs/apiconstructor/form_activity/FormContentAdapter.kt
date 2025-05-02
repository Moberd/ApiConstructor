package edu.sfedu_mmcs.apiconstructor.form_activity
import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import edu.sfedu_mmcs.apiconstructor.R
import edu.sfedu_mmcs.apiconstructor.utils.ContentInfo
import java.util.ArrayList

class FormContentAdapter: RecyclerView.Adapter<FormContentAdapter.ViewHolder>() {


    private var mList = mutableListOf<ContentInfo>()
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val editText : TextView = itemView.findViewById(R.id.formEditText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.form_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: FormContentAdapter.ViewHolder, position: Int) {
        holder.editText.setHint(mList[position].name)
        holder.editText.setText(mList[position].example)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setItems(newList: ArrayList<ContentInfo>) {
        this.mList = newList.toMutableList()
        notifyDataSetChanged()
    }
}