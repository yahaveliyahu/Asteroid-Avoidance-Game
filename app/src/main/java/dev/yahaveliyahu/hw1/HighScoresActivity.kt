package dev.yahaveliyahu.hw1

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.model.LatLng


class HighScoresActivity : AppCompatActivity(), OnScoreSelectedListener{

    private val selectedLocations = mutableListOf<LatLng>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_high_scores)

        // Showing fragments
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_scores, ScoresFragment())
            .replace(R.id.frame_map, MapFragment())
            .commit()
    }


    override fun onScoreSelected(lat: Double, lon: Double) {
        val location = LatLng(lat, lon)

        if (!selectedLocations.contains(location)) {
            selectedLocations.add(location)
        }

        val mapFragment = supportFragmentManager.findFragmentById(R.id.frame_map) as? MapFragment
        mapFragment?.updateLocations(selectedLocations)
    }
}
