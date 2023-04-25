package com.thesis.sportologia.utils

import android.content.Context
import android.location.Geocoder
import java.util.*

class YandexMaps {

    companion object {

        fun getPosition(context: Context, address: String): Position? {
            val geocoder = Geocoder(context)
            val geocodeResult =
                geocoder.getFromLocationName(address, 1)?.firstOrNull() ?: return null

            return Position(geocodeResult.latitude, geocodeResult.longitude)
        }

        fun getAddress(context: Context, position: Position?): String? {
            if (position == null) {
                return null
            }

            val geocoder = Geocoder(context)
            val geocodeResult = geocoder.getFromLocation(
                position.latitude,
                position.longitude,
                1
            )?.firstOrNull() ?: return null

            val address = StringBuilder()

            if (geocodeResult.locality != null) {
                address.append(geocodeResult.locality)
                address.append(", ")
            } else if (geocodeResult.adminArea != null) {
                address.append(geocodeResult.adminArea)
                address.append(", ")
            }
            if (geocodeResult.thoroughfare != null) {
                address.append(geocodeResult.thoroughfare)
                address.append(", ")
            }
            if (geocodeResult.featureName != null
                && geocodeResult.postalCode != geocodeResult.featureName
                && geocodeResult.thoroughfare != geocodeResult.featureName
                && geocodeResult.locality != geocodeResult.featureName
                && geocodeResult.premises != geocodeResult.featureName
            ) {
                address.append(geocodeResult.featureName)
                address.append(", ")
            }

            return address.substring(0, address.length - 2).toString()
        }

    }

}