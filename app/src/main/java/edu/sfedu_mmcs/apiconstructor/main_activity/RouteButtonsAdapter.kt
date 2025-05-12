package edu.sfedu_mmcs.apiconstructor.main_activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import edu.sfedu_mmcs.apiconstructor.R
import edu.sfedu_mmcs.apiconstructor.utils.Endpoint
import edu.sfedu_mmcs.apiconstructor.utils.MethodItem
import edu.sfedu_mmcs.apiconstructor.utils.RouteGroup
import edu.sfedu_mmcs.apiconstructor.utils.RouteInfo

class RouteButtonsAdapter(
    private val onClick: (route: RouteInfo) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items = mutableListOf<Any>() //RouteGroup, Endpoint, MethodItem

    companion object {
        private const val TYPE_GROUP = 0
        private const val TYPE_ENDPOINT_SINGLE = 1
        private const val TYPE_ENDPOINT_MULTI = 2
        private const val TYPE_METHOD = 3
    }

    override fun getItemViewType(position: Int): Int {
        return when (val item = items[position]) {
            is RouteGroup -> TYPE_GROUP
            is Endpoint -> if (item.isSingleMethod) TYPE_ENDPOINT_SINGLE else TYPE_ENDPOINT_MULTI
            is MethodItem -> TYPE_METHOD
            else -> throw IllegalArgumentException("Unknown item type")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_GROUP -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_group, parent, false)
                GroupViewHolder(view)
            }
            TYPE_ENDPOINT_SINGLE -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_method, parent, false)
                SingleMethodViewHolder(view)
            }
            TYPE_ENDPOINT_MULTI -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_endpoint, parent, false)
                MultiMethodViewHolder(view)
            }
            TYPE_METHOD -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_multi_method_button, parent, false)
                MethodViewHolder(view)
            }
            else -> throw IllegalArgumentException("Unknown view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is GroupViewHolder -> {
                val group = items[position] as RouteGroup
                holder.bind(group)
            }
            is SingleMethodViewHolder -> {
                val endpoint = items[position] as Endpoint
                holder.bind(endpoint)
            }
            is MultiMethodViewHolder -> {
                val endpoint = items[position] as Endpoint
                holder.bind(endpoint)
            }
            is MethodViewHolder -> {
                val method = items[position] as MethodItem
                holder.bind(method)
            }
        }
    }

    override fun getItemCount(): Int = items.size

    inner class GroupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textView: TextView = itemView.findViewById(R.id.group_name)
        private val arrow: ImageView = itemView.findViewById(R.id.group_arrow)

        fun bind(group: RouteGroup) {
            textView.text = group.groupName
            arrow.setImageResource(if (group.isExpanded) R.drawable.ic_arrow_up else R.drawable.ic_arrow_down)
            itemView.setOnClickListener {
                toggleGroup(group)
            }
        }
    }

    inner class SingleMethodViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val button: Button = itemView.findViewById(R.id.method_button)
        private val lockImage: ImageView = itemView.findViewById(R.id.auth_lock)

        fun bind(endpoint: Endpoint) {
            val methodItem = endpoint.methods.first()
            button.text = "${methodItem.method} ${endpoint.path}"
            lockImage.visibility = if (methodItem.routeInfo.security.isNotEmpty()) View.VISIBLE else View.GONE
            button.setBackgroundTintList(getColorForMethod(methodItem.method))
            button.setOnClickListener { onClick(methodItem.routeInfo) }
            lockImage.setOnClickListener {
                if (methodItem.routeInfo.security.isNotEmpty()) {
                    showSecurityDialog(methodItem.routeInfo)
                }
            }
        }

        private fun getColorForMethod(method: String): ColorStateList {
            val colorRes = when (method.uppercase()) {
                "GET" -> R.color.method_get
                "POST" -> R.color.method_post
                "DELETE" -> R.color.method_delete
                "PUT" -> R.color.method_put
                else -> android.R.color.darker_gray // Fallback color
            }
            return ColorStateList.valueOf(ContextCompat.getColor(button.context, colorRes))
        }

        private fun showSecurityDialog(routeInfo: RouteInfo) {
            val securityText = if (routeInfo.security.isEmpty()) {
                "No security requirements"
            } else {
                "Security Requirements:\n${routeInfo.security.joinToString("\n") { "- $it" }}"
            }
            val dialog = AlertDialog.Builder(button.context)
                .setTitle("${routeInfo.method} ${routeInfo.route}")
                .setMessage(securityText)
                .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                .create()
            dialog.show()
        }
    }

    inner class MultiMethodViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textView: TextView = itemView.findViewById(R.id.endpoint_name)
        private val arrow: ImageView = itemView.findViewById(R.id.endpoint_arrow)

        fun bind(endpoint: Endpoint) {
            textView.text = endpoint.path
            arrow.setImageResource(if (endpoint.isExpanded) R.drawable.ic_arrow_up else R.drawable.ic_arrow_down)
            itemView.setOnClickListener {
                toggleEndpoint(endpoint)
            }
        }
    }

    inner class MethodViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val button: Button = itemView.findViewById(R.id.method_button)
        private val lockImage: ImageView = itemView.findViewById(R.id.auth_lock)

        fun bind(method: MethodItem) {
            button.text = "${method.method} ${method.routeInfo.route}"
            lockImage.visibility = if (method.routeInfo.security.isNotEmpty()) View.VISIBLE else View.GONE
            button.setBackgroundTintList(getColorForMethod(method.method))
            button.setOnClickListener { onClick(method.routeInfo) }
            lockImage.setOnClickListener {
                if (method.routeInfo.security.isNotEmpty()) {
                    showSecurityDialog(method.routeInfo)
                }
            }
        }

        private fun getColorForMethod(method: String): ColorStateList {
            val colorRes = when (method.uppercase()) {
                "GET" -> R.color.method_get
                "POST" -> R.color.method_post
                "DELETE" -> R.color.method_delete
                "PUT" -> R.color.method_put
                else -> android.R.color.darker_gray // Fallback color
            }
            return ColorStateList.valueOf(ContextCompat.getColor(button.context, colorRes))
        }

        private fun showSecurityDialog(routeInfo: RouteInfo) {
            val securityText = if (routeInfo.security.isEmpty()) {
                "No security requirements"
            } else {
                "Security Requirements:\n${routeInfo.security.joinToString("\n") { "- $it" }}"
            }
            AlertDialog.Builder(button.context)
                .setTitle("${routeInfo.method} ${routeInfo.route}")
                .setMessage(securityText)
                .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                .create()
                .show()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setGroups(groups: List<RouteGroup>) {
        items.clear()
        groups.forEach { group ->
            items.add(group)
            if (group.isExpanded) {
                group.endpoints.forEach { endpoint ->
                    items.add(endpoint)
                    if (endpoint.isExpanded && !endpoint.isSingleMethod) {
                        items.addAll(endpoint.methods)
                    }
                }
            }
        }
        notifyDataSetChanged()
    }

    private fun toggleGroup(group: RouteGroup) {
        group.isExpanded = !group.isExpanded
        setGroups(items.filterIsInstance<RouteGroup>())
    }

    private fun toggleEndpoint(endpoint: Endpoint) {
        endpoint.isExpanded = !endpoint.isExpanded
        setGroups(items.filterIsInstance<RouteGroup>())
    }
}