package com.whiterabbit.pisabike.storage

import android.util.Log
import com.evernote.android.job.Job
import com.evernote.android.job.JobCreator
import com.evernote.android.job.JobManager
import com.evernote.android.job.JobRequest
import com.squareup.sqlbrite.BriteDatabase
import com.whiterabbit.pisabike.model.Station
import pl.charmas.android.reactivelocation.ReactiveLocationProvider
import rx.Observable
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class AddressJobCreator @Inject constructor() : JobCreator {
    @Inject lateinit var locationProvider : ReactiveLocationProvider
    @Inject lateinit var sqlBrite : BriteDatabase

    companion object {
        val JOB_TAG = "AddressJob"
    }

    override fun create(tag: String?): Job? {
        return tag?.let {
            when (tag) {
                JOB_TAG -> AddressJob(locationProvider, sqlBrite)
                else -> null
            }
        }
    }
}

class AddressJob(val locationProvider: ReactiveLocationProvider,
                 val sqlBrite : BriteDatabase) : Job() {
    override fun onRunJob(params: Params?): Result {

        val filteredStations = sqlBrite.createQuery(PisaBikeDbHelper.STATION_TABLE,
                "SELECT * FROM station where loaded = 0")
        var error = false

        filteredStations.first().flatMap { query ->
            val cursor = query.run()
            val s = ArrayList<Station>(cursor!!.count)
            while (cursor.moveToNext()) {
                s.add(Station(cursor))
            }
            cursor.close()
            Observable.from(s) }
                .flatMap {
                        station -> locationProvider.getReverseGeocodeObservable(station.latitude, station.longitude, 1)
                        .map {address -> station.address = address[0].getAddressLine(0)
                                         station.isAddressLoaded = true
                                         station}}
                .subscribe({station -> sqlBrite.update(PisaBikeDbHelper.STATION_TABLE, station.updateValuesWithAddr,
                        PisaBikeDbHelper.STATION_NAME_COLUMN + " = ?", station.name)},
                        {e -> error = true})


        val res = when {
                        !error -> Result.SUCCESS
                        else -> Result.RESCHEDULE
                        }
        return res
    }
}

fun scheduleAddressJob() {
    if (JobManager.instance().allJobRequests.isEmpty())
        JobRequest.Builder(AddressJobCreator.JOB_TAG)
                .setExecutionWindow(500L, 40_000L)
                .setPersisted(true)
                .build()
                .schedule()
}