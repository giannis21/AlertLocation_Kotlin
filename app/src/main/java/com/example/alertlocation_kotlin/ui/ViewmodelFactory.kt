package com.example.tvshows.ui.nowplaying

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.alertlocation_kotlin.data.repositories.mainRepository
import com.example.alertlocation_kotlin.ui.add_route.DetailsViewModel


@Suppress("UNCHECKED_CAST")
class ViewmodelFactory(private val remoteRepository: mainRepository, var context: Context) : ViewModelProvider.Factory{

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(DetailsViewModel::class.java) -> DetailsViewModel(remoteRepository,context) as T

//            modelClass.isAssignableFrom(FavoritesViewModel::class.java) -> FavoritesViewModel(remoteRepository, context) as T
//            modelClass.isAssignableFrom(SearchViewModel::class.java) -> SearchViewModel(remoteRepository, context) as T
//            modelClass.isAssignableFrom(SeenViewModel::class.java) -> SeenViewModel(remoteRepository, context) as T
//            modelClass.isAssignableFrom(ShowDetailsViewModel::class.java) -> ShowDetailsViewModel(remoteRepository, context) as T
//            modelClass.isAssignableFrom(TopRatedViewModel::class.java) -> TopRatedViewModel(remoteRepository, context) as T
//            modelClass.isAssignableFrom(WatchlistViewModel::class.java) -> WatchlistViewModel(remoteRepository,context) as T

            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }

    }
}

