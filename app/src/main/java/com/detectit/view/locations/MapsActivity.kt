package com.detectit.view.locations

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.detectit.detection.general.MasterApplication
import com.detectit.model.location.DetectionLocation
import com.detectit.model.location.Mapper
import com.detectit.model.location.PointOfInterestEntity
import com.google.android.gms.maps.model.LatLngBounds
import com.google.gson.Gson
import org.json.JSONArray
import timber.log.Timber
import java.io.File
import android.content.Intent
import android.net.Uri
import com.detectit.detection.R
import com.detectit.utils.toPrettyJson
import java.util.*


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private val bounds = LatLngBounds.Builder()
    private var points = emptyList<DetectionLocation>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        showPointsOnMap()
    }

    private fun addPoint(point: LatLng) {
        mMap.addMarker(MarkerOptions().position(point).title("Marker in Sydney"))
        bounds.include(point)
    }

    private fun showPointsOnMap() {
        (application as? MasterApplication)
                ?.data
                ?.select(PointOfInterestEntity::class)
                ?.where(PointOfInterestEntity.ID.notNull())
                ?.get()
                ?.observable()
                ?.subscribe(
                        {
                            points += (Mapper.toDetectionLocation(it))
                            addPoint(LatLng(it.latitude, it.longitude))
                            Timber.i("Point of interest fetched ${it}")
                        },
                        {
                            Timber.e("Failed to fetch point of interest, ${it.localizedMessage}")
                        },
                        {
                            if (points.isNotEmpty())
                                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 50))
                        })

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.locations_actionbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.clear -> (application as? MasterApplication)
                    ?.data
                    ?.delete(PointOfInterestEntity::class)
                    ?.where(PointOfInterestEntity.ID.notNull())
                    ?.get()?.single()?.subscribe()
            R.id.export -> {
                var jsArray = JSONArray(Gson().toJson(points))
                val file = File(applicationContext.externalCacheDir, "detection_points.json")
                file.writeText(jsArray.toPrettyJson())
                Timber.d("Map -> ${jsArray.toPrettyJson()}")
                shareFile(file)
            }
            R.id.home -> this.finish()
            else -> return false
        }
        return true
    }


    private fun shareFile(file: File) {
        val emailIntent = Intent(Intent.ACTION_SENDTO)
        intent.type = "text/plain";
        emailIntent.data = Uri.parse("mailto:")
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "detection_points.json ${Date()}")
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Body")
        emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://$file"))
        startActivity(Intent.createChooser(emailIntent, "Please choose how do you want to share it"))
    }

}
