package dev.yahaveliyahu.hw1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions

class MapFragment : Fragment(), OnMapReadyCallback {

    private var map: GoogleMap? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_map, container, false)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
            ?: SupportMapFragment.newInstance().also {
                childFragmentManager.beginTransaction().replace(R.id.map, it).commit()}
        mapFragment.getMapAsync(this)
        return view
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map?.mapType = GoogleMap.MAP_TYPE_NORMAL
        map?.uiSettings?.isZoomControlsEnabled = true
    }

    fun updateLocations(locations: List<LatLng>) {
        map?.clear()
        locations.forEach { location ->
            map?.addMarker(
                MarkerOptions()
                    .position(location)
                    .title("High Score Location")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
            )
        }
        // Single marker: zoom in close for street-level detail
        if (locations.size == 1) {
            map?.moveCamera(CameraUpdateFactory.newLatLngZoom(locations[0], 15f))

            // If we want to focus on everyone:
            // Multiple markers: fit all in view
        } else if (locations.isNotEmpty()) {
            val builder = LatLngBounds.Builder().apply {locations.forEach { include(it) }}
            val bounds = builder.build()
            val padding = 100
            map?.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding))
        }
    }
}

