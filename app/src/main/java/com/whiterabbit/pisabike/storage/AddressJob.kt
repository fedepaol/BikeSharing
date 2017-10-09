package com.whiterabbit.pisabike.storage

import android.location.Geocoder
import com.evernote.android.job.Job
import com.evernote.android.job.JobCreator
import com.evernote.android.job.JobManager
import com.evernote.android.job.JobRequest
import com.whiterabbit.pisabike.model.Station
import pl.charmas.android.reactivelocation.ReactiveLocationProvider
import java.util.*
import javax.inject.Inject


class AddressJobCreator @Inject constructor() : JobCreator {
    @Inject lateinit var locationProvider : ReactiveLocationProvider
    @Inject lateinit var database: BikesDatabasee
    @Inject lateinit var preferences: PrefsStorage

    companion object {
        val JOB_TAG = "AddressJob"
    }

    override fun create(tag: String?): Job? {
        return tag?.let {
            when (tag) {
                JOB_TAG -> AddressJob(locationProvider, database, preferences)
                else -> null
            }
        }
    }
}

fun setAddress(s : Station, db : BikesDatabasee, geocoder: Geocoder) {
    val address = geocoder.getFromLocation(s.latitude, s.longitude, 1)
    if (address.isEmpty()) return

    val newStation = s.copy(address = address[0].getAddressLine(0), addressLoaded = true)
    when {
        address.size > 1 -> db.bikesDao().updateStation(newStation)
    }
}

class AddressJob(val locationProvider: ReactiveLocationProvider,
                 val db : BikesDatabasee,
                 val prefs : PrefsStorage) : Job() {
    override fun onRunJob(params: Params?): Result {

        val stationsWithNoAddress = db.bikesDao().getNonLoadedAddress(prefs.currentNetwork.network)
        val geocoder = Geocoder (context, Locale.getDefault())
        stationsWithNoAddress.map { s -> setAddress(s, db, geocoder)}
        return Result.SUCCESS
    }
}

fun scheduleAddressJob() {
    if (JobManager.instance().allJobRequests.isEmpty()) {
        JobRequest.Builder(AddressJobCreator.JOB_TAG)
                .setExecutionWindow(500L, 40_000L)
                .setPersisted(true)
                .setUpdateCurrent(true)
                .build()
                .schedule()
    }
}