package com.example.alertlocation_kotlin.ui.adapter

import android.content.Context
import android.location.Location
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.*
import com.example.alertlocation_kotlin.BR
import com.example.alertlocation_kotlin.R
import com.example.alertlocation_kotlin.data.model.Route
import com.example.alertlocation_kotlin.databinding.RoutesItemBinding
import com.example.alertlocation_kotlin.ui.add_route.DetailsViewModel
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.chip.Chip
import kotlinx.android.synthetic.main.routes_item.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class RoutesAdapter(var context: Context, var viewModel: DetailsViewModel) :  ListAdapter<Route, RoutesAdapter.ViewHolder>(
    RoutesDiffCallback()
){


    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
  var editNameListener : ((Long) -> Unit)?=null
  var startLocationUpdatedListener : ((Boolean, Route) -> Unit)?=null
  var mapCurrent: GoogleMap? = null
  inner  class ViewHolder(val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root),OnMapReadyCallback  {


      var map: MapView? = null
      init {
          map = binding.root.mapView.findViewById(R.id.mapView)
          if (map != null) {
              map!!.onCreate(null)
              map!!.onResume()
              map!!.getMapAsync(this)
          }
      }

        fun bind(route: Route, context: Context) {
            binding.setVariable(BR.route, route)

            binding.root.expand_icon.setOnClickListener {

                val isExpanded = route.isExpanded
                route.isExpanded = !isExpanded
                notifyItemChanged(adapterPosition)

//                if (route.isExpanded) {
//                    dataSet.forEach { each_route ->
//                        if (each_route != route && each_route.isExpanded) {
//                            each_route.isExpanded = false
//                            notifyItemChanged(dataSet.indexOf(each_route))
//                        }
//                    }
//                }
            }

            viewModel.viewModelScope.launch(Dispatchers.Main) {
                binding.root.chip_group_users.removeAllViews()

                route.users.forEach { user ->
                    val chip = Chip(context)
                    chip.text = user.username
                    chip.tag = user.token
                    chip.textSize=17f
                    //chip.chipIcon = ContextCompat.getDrawable(context,R.drawable.ic_save)

                    chip.setChipBackgroundColorResource(R.color.teal_200)
                    chip.setTextColor(ContextCompat.getColor(context, R.color.white))
                    binding.root.chip_group_users.addView(chip)
                }

                binding.root.name_route.setOnClickListener {
                    editNameListener?.invoke(route.id)
                }
                binding.root.switch1.setOnClickListener {

                    if(route.isEnabled){
                        startLocationUpdatedListener?.invoke(false, route)
                        viewModel.enableRoute(route.id, false)

                    }else{
                        val switchEnabled= viewModel.allRoutes.value?.find { it.isEnabled }
                        if(switchEnabled == null){
                            startLocationUpdatedListener?.invoke(true, route)
                            viewModel.enableRoute(route.id, true)
                        }else{
                            binding.root.switch1.isChecked=false
                        }


                    }

                }

                binding.root.addressRecyclerview.let { recycler ->
                    val list= currentList.map { it.points }[0] ?: arrayListOf()
                    val adapter = AddressAdapter(list, context, viewModel){

                    }
                    recycler.layoutManager = LinearLayoutManager(
                        context,
                        LinearLayoutManager.HORIZONTAL,
                        false
                    )
                    recycler.adapter = adapter
                    val snapHelper: SnapHelper = LinearSnapHelper()
                    snapHelper.attachToRecyclerView(recycler)
                    recycler.addOnScrollListener(object: RecyclerView.OnScrollListener() {
                        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                            super.onScrolled(recyclerView, dx, dy)

                        }

                        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                            super.onScrollStateChanged(recyclerView, newState)
                            val visibleChild: View = recyclerView.getChildAt(0)
                            val firstChild: Int = recyclerView.getChildAdapterPosition(visibleChild)
                             addMarker(LatLng(list[firstChild].latitude,list[firstChild].latitude))
                        }
                    })
                }


            }

        }

      override fun onMapReady(googleMap: GoogleMap) {
          MapsInitializer.initialize(context)
          mapCurrent = googleMap;


      }
  }
  fun addMarker(location:LatLng){
      mapCurrent!!.addMarker(
          MarkerOptions()
              .position(location)
              //.icon(BitmapDescriptorFactory.fromResource(R.drawable.location_marker))
              .icon(
                  BitmapDescriptorFactory
                      .defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
              )
      )
  }
    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate<RoutesItemBinding>(
            LayoutInflater.from(viewGroup.context),
            R.layout.routes_item,
            viewGroup,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val route= getItem(position)
        route.let {
            viewHolder.bind(it, context = context)
        }

    }

    override fun getItemCount() = currentList.size

    fun getItem1(position: Int): Route {
         return getItem(position)
    }

    private class RoutesDiffCallback : DiffUtil.ItemCallback<Route>() {
        override fun areItemsTheSame(
            oldItem: Route,
            newItem: Route
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: Route,
            newItem: Route
        ): Boolean {
            return oldItem == newItem
        }
    }


}