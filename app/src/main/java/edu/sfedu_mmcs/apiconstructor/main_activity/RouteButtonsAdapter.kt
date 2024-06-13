package edu.sfedu_mmcs.apiconstructor.main_activity

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import edu.sfedu_mmcs.apiconstructor.R
import edu.sfedu_mmcs.apiconstructor.utils.RouteInfo

class RouteButtonsAdapter(
    private val onClick: (route: RouteInfo) -> Unit,
): RecyclerView.Adapter<RouteButtonsAdapter.ViewHolder>() {

    private var mList = mutableListOf<RouteInfo>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.main_buttons, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ItemsViewModel = mList[position]
        holder.button.text = ItemsViewModel.route
        holder.button.setOnClickListener { this.onClick(ItemsViewModel) }
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val button: Button = itemView.findViewById(R.id.routeBtn)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setRoutes(newList: List<RouteInfo>) {
        this.mList = newList.toMutableList()
        notifyDataSetChanged()
    }
}