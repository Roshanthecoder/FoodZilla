package com.tech.foodzilla.util.extention

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.tech.foodzilla.util.BASE_LATITUDE
import com.tech.foodzilla.util.BASE_LONGITUDE
import com.tech.foodzilla.util.LOCATION_ZOOM


fun createMarkerOption(
    latLag: LatLng, icon: BitmapDescriptor
): MarkerOptions {
    return MarkerOptions().position(
            latLag
        ).title("My Location")
            .icon(icon)

}

fun GoogleMap.moveCameraToDefault() {
    animateCamera(
        CameraUpdateFactory.newLatLngZoom(
            LatLng(
               BASE_LATITUDE,
                BASE_LONGITUDE
            ), LOCATION_ZOOM
        )
    )
}