package com.example.aufa.project

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Log.e
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.login.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    val markers: MutableMap<String, Marker> = mutableMapOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.clear()
        mMap.setOnInfoWindowClickListener { m ->
            val key = markers.filterValues { it == m }.keys.first()
            val i = Intent(this, SalesHistoryActivity::class.java)
            i.putExtra("salesid", key)
            startActivity(i)
        }

        loadSalesLocation()

    }

    private fun loadSalesLocation() {
        val database = FirebaseDatabase.getInstance().getReference("sales")
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(child: DataSnapshot?) {
                mMap.clear()
                markers.clear()
                child?.children?.forEach {
                    val status =it.child("login").value.toString()
                    Log.e(it.key,"status = $status")

                    if ( status== "1") {
                        val salesName = it.child("nama").value.toString()
                        val lat = it.child("current_loc").child("latitude").value as? Double ?: 0.0
                        val lng = it.child("current_loc").child("longitude").value as? Double ?: 0.0

                        val mo = MarkerOptions()
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.sales))
                                .position(LatLng(lat, lng))
                                .title(salesName)
                        markers.put(it.key, mMap.addMarker(mo))
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(lat, lng), 15f))
                    }


                }
            }

            override fun onCancelled(p0: DatabaseError?) {

            }


        })
    }


}


