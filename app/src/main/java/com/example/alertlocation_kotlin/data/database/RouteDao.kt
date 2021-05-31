package com.example.tvshows

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.alertlocation_kotlin.data.model.Route


@Dao
interface RouteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(route: Route)

    @Query("SELECT * from Route")
    fun getRoutes(): LiveData<List<Route>>

//    @Query("SELECT * FROM now_playing where page = :page AND currentFragment='nowPlaying'")
//    suspend fun get_now_playing_per_page(page: Int): NowPlaying
//
//
//    @Query("SELECT count(*) from Route")
//    fun isDbEmpty(): Int
//
//    @Query("DELETE FROM now_playing ")
//    suspend fun deleteAllFromNowPlaying()
//
//    @Query("SELECT COUNT(page) from now_playing  ")
//    fun countOfNowPLaying(): LiveData<Int>
//
//
//
////---------------------------------Most Popular queries--------------------------------//
//
//    @Query("SELECT * FROM now_playing where page = :page AND currentFragment='mostPopular' ")
//    suspend fun getMostPopularPerPage(page: Int): NowPlaying
//
//    @Query("DELETE FROM Route Where currentFragment='mostPopular'")
//    suspend fun deleteAllFromPopular()
//
////---------------------------------Top rated queries----------------------------------------//
//
//    @Query("SELECT * FROM now_playing where page = :page AND currentFragment='topRated'")
//    suspend fun getTopRated(page: Int): NowPlaying
//
//    @Query("DELETE FROM Route Where currentFragment='topRated'")
//    suspend fun deleteAllFromTop()
//
////----------------------------------Watchlist queries------------------------------------//
//
//    @Query("SELECT * FROM show_details where currentFragment='watchlist'")
//    fun getWatchlistShows(): LiveData<MutableList<TvShowDetails>>
//
//    @Insert(onConflict = OnConflictStrategy.IGNORE)
//    suspend fun insertToTvShowDetails(show_details: TvShowDetails):Long
//
//    @Query("SELECT * FROM show_details where id=:id")
//    suspend fun getTvShowDetails(id: String): TvShowDetails
//
//    @Query("SELECT * FROM show_details WHERE currentFragment='watchlist' ")
//    fun getTvShowDetailsAll():LiveData<MutableList<TvShowDetails>>
//
//    @Query("DELETE FROM show_details Where id=:id AND currentFragment='watchlist'")
//    suspend fun deleteTvShowFromWatchlist(id: String)
//
//    @Query("UPDATE show_details SET underNotification=:b where id=:id")
//    suspend fun underNotification(id: String,b: Boolean)
//
//    @Query("UPDATE show_details SET exactDateOfNotification=:date_of_notification where id=:id")
//    suspend fun update_exact_time_of_notification(id: String,date_of_notification: String)
//
//    // move from watchlist to favorites
//
//    @Query("select COUNT(id)  from show_details Where currentFragment='watchlist' ")
//    fun countTvShowsFromWatchlist(): LiveData<Int>
//
//    //----------------------favorites------------------------------------//
//
//    @Query("select COUNT(id)  from show_details Where currentFragment='favorites' ")
//    fun countTvShowsFromFavorites(): LiveData<Int>
//
//    @Query("SELECT * FROM show_details where currentFragment='favorites'")
//    fun getFavorites(): LiveData<MutableList<TvShowDetails>>
//
//    @Query("DELETE FROM show_details Where id=:id AND currentFragment='favorites'")
//    suspend fun deleteTvShowFromFavorites(id: String)
//
//
//
//    //--------------------------seen--------------------------------------//
//    @Query("SELECT * FROM show_details where currentFragment='seen'")
//    fun getSeen(): LiveData<MutableList<TvShowDetails>>
//
//    @Query("select COUNT(id)  from show_details Where currentFragment='seen' ")
//    fun countTvShowsFromSeen()  : LiveData<Int>
//
//    @Query("UPDATE show_details SET  currentFragment='favorites' where id=:id")
//    suspend fun moveToFavorites(id: String)
//
//    @Query("UPDATE show_details SET  currentFragment='watchlist' where id=:id")
//    suspend fun moveToWatchlist(id: String)
//
//    @Query("UPDATE show_details SET  currentFragment='seen' where id=:id")
//    suspend fun moveToSeen(id: String)
//
//    @Query("DELETE FROM show_details Where id=:id AND currentFragment='seen'")
//    suspend fun deleteTvShowFromSeen(id: String)
//
//    @Query("SELECT EXISTS(SELECT * FROM show_details WHERE id = :id AND currentFragment=:currentFragment)")
//    suspend fun RowExists(id : String, currentFragment:String) : Boolean



}