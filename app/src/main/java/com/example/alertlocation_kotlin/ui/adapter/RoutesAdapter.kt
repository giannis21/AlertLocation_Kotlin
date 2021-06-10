package com.example.alertlocation_kotlin.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.example.alertlocation_kotlin.BR
import com.example.alertlocation_kotlin.R
import com.example.alertlocation_kotlin.data.model.Route
import com.example.alertlocation_kotlin.databinding.RoutesItemBinding

class RoutesAdapter(private val dataSet: MutableList<Route>,context: Context) : RecyclerView.Adapter<RoutesAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(route: Route) {
            binding.setVariable(BR.route,route)
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate<RoutesItemBinding>(LayoutInflater.from(viewGroup.context), R.layout.routes_item, viewGroup, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        dataSet[position].let {
            viewHolder.bind(it)
        }

    }

    override fun getItemCount() = dataSet.size

    fun updateData(routes: MutableList<Route>) {
        dataSet.clear()
        dataSet.addAll(routes)
        notifyDataSetChanged()
    }

}