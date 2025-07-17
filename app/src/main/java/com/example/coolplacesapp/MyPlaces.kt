package com.example.coolplacesapp

import com.google.android.gms.maps.model.LatLng

data class MyPlaces (
    val name: String,
    val latLong: LatLng
)