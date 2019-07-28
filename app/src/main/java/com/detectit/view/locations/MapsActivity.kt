package com.detectit.view.locations

import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.detectit.detection.R

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.detectit.detection.general.MasterApplication
import com.detectit.model.location.PointOfInterestEntity
import com.google.android.gms.maps.model.LatLngBounds
import timber.log.Timber


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private val bounds = LatLngBounds.Builder()

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
                            addPoint(LatLng(it.latitude, it.longitude))
                            Timber.i("Point of interest fetched ${it}")
                        },
                        {
                            Timber.e("Failed to fetch point of interest, ${it.localizedMessage}")
                        },
                        {
                            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 50))
                        })

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

}
