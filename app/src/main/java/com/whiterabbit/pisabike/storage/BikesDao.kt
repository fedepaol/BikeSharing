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

    @Query("update station set favourite = :preferred where name = :name and network = :network")
    fun setStationPreferred(name: String, network: String, preferred: Boolean)

    @Query("SELECT * FROM station where network = :network")
    fun loadAllStations(network: String): Flowable<List<Station>>

    @Query("SELECT * from station where name = :name and network = :network")
    fun getStation(name: String, network : String) : Station?

    @Query("Select * from station where addressLoaded = 0 and network = :network")
    fun getNonLoadedAddress(network: String) : List<Station>

}