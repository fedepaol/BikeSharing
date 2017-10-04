package com.whiterabbit.pisabike.storage

import android.arch.persistence.room.*
import com.whiterabbit.pisabike.model.Station
import io.reactivex.Flowable


/**
 * Created by fedepaol on 22/09/17.
 */
@Dao
interface BikesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertStation(station: Station)

    @Update
    fun updateStation(station: Station)

    @Query("update station set favourite = :preferred where name = :name and city = :city")
    fun setStationPreferred(name: String, city: String, preferred: Boolean)

    @Query("SELECT * FROM station")
    fun loadAllStations(): Flowable<List<Station>>

    @Query("SELECT * from station where name = :name and city = :city")
    fun getStation(name: String, city : String) : Station?

}