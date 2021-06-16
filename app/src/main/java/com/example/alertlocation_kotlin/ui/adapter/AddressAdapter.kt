package com.example.alertlocation_kotlin.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.alertlocation_kotlin.BR
import com.example.alertlocation_kotlin.R
import com.example.alertlocation_kotlin.data.model.Points
import com.example.alertlocation_kotlin.data.model.Route
import com.example.alertlocation_kotlin.ui.add_route.DetailsViewModel
import kotlinx.android.synthetic.main.routes_item.view.*

class AddressAdapter(var list: List<Points>,var  context: Context, var viewModel: DetailsViewModel,var callback:(()-> Unit)?=null) : RecyclerView.Adapter<AddressAdapter.ViewHolder>() {


    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.addressTxt)

    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.address_item, parent, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.textView.text = list[position].address
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = list.size

}