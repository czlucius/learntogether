package com.sp.learntogether.objects

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Location(
    val lat: Double,
    val lng: Double,
    val time: Long
) : Parcelable {
    constructor() : this(0.0, 0.0, 0)



}