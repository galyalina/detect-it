package com.detectit.detection.general

import io.requery.reactivex.KotlinReactiveEntityStore
import io.requery.Persistable
import io.requery.sql.TableCreationMode
import io.requery.android.sqlite.DatabaseSource
import android.os.StrictMode
import android.app.Application
import android.location.Location
import android.util.Log
import com.patloew.rxlocation.RxLocation
import com.detectit.detection.BuildConfig
import com.detectit.model.location.Models
import com.google.android.gms.location.LocationRequest
import io.reactivex.Observable
import io.requery.sql.KotlinEntityDataStore
import timber.log.Timber
import java.util.*


class MasterApplication : Application() {

    private var dataStore: KotlinReactiveEntityStore<Persistable>? = null

    // Create one instance and share it
    var rxLocation: RxLocation? = null


    public fun getLocation(): Observable<Location>? {
        if (rxLocation == null) {
            rxLocation = RxLocation(this)
        }
        val locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(5000)

        return rxLocation?.location()?.updates(locationRequest)
    }


    public fun getAddress(location: Location) = rxLocation?.geocoding()?.fromLocation(location)?.toObservable()

    val data: KotlinReactiveEntityStore<Persistable>?
        get() {
            if (dataStore == null) {
                val source = DatabaseSource(this, Models.DEFAULT, 1)
                if (BuildConfig.DEBUG) {
                    source.setTableCreationMode(TableCreationMode.CREATE_NOT_EXISTS)
                }
                dataStore = KotlinReactiveEntityStore(KotlinEntityDataStore(source.configuration))
            }
            return dataStore
        }

    override fun onCreate() {
        super.onCreate()
        StrictMode.enableDefaults()
        Timber.plant(Timber.DebugTree())
    }


    private val timberDebugTree = object : Timber.DebugTree() {

        override fun createStackElementTag(element: StackTraceElement): String {
            // adding file name and line number link to logs
            return String.format(
                    Locale.US,
                    "%s(%s:%d)",
                    super.createStackElementTag(element),
                    element.fileName,
                    element.lineNumber
            )
        }

        override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
            if (priority == Log.ERROR || priority == Log.WARN) {
                val formattedMessage = format(priority, tag, message)
            }
        }

        private fun format(priority: Int, tag: String?, message: String): String {
            val messageWithTag = if (tag != null) "[$tag] $message" else message
            return prefixForPriority(priority) + messageWithTag
        }

        private fun prefixForPriority(priority: Int): String {
            return when (priority) {
                Log.VERBOSE -> "[VERBOSE] "
                Log.DEBUG -> "[DEBUG] "
                Log.INFO -> "[INFO] "
                Log.WARN -> "[WARN] "
                Log.ERROR -> "[ERROR] "
                Log.ASSERT -> "[ASSERT] "
                else -> "[UNKNOWN($priority)] "
            }
        }
    }
}