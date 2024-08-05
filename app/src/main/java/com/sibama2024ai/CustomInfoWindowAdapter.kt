package com.sibama2024ai
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker

class CustomInfoWindowAdapter(private val inflater: LayoutInflater) : GoogleMap.InfoWindowAdapter {

    override fun getInfoWindow(marker: Marker): View? {
        // Use default InfoWindow frame if you don't want a custom frame
        return null
    }

    override fun getInfoContents(marker: Marker): View {
        // Inflate the custom layout
        val view = inflater.inflate(R.layout.custom_info_window, null)

        // Set the title and snippet
        val title: TextView = view.findViewById(R.id.title)
        val snippet: TextView = view.findViewById(R.id.snippet)

        title.text = marker.title
        snippet.text = marker.snippet

        return view
    }
}

