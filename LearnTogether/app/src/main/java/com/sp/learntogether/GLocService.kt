package com.sp.learntogether

import android.Manifest
import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.getSystemService
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.sp.learntogether.data.AppDatabase
import com.sp.learntogether.data.TrackingDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Date


class GLocService: Service(), LocationListener {

    private val database = Firebase.database(Utils.FIREBASE_RTDB_URL)
    private val myRef = database.getReference("meetups")
    private val scope = CoroutineScope(Dispatchers.IO)
    private lateinit var trackingDao: TrackingDao
//    private val shared       getSharedPreferences("meetups", MODE_PRIVATE)
//
//
//            ;
    private lateinit var sharedPrefs: SharedPreferences

    private val TAG = "GLocService"
    @SuppressLint("MissingPermission")
    override fun onCreate() {
        super.onCreate()
        sharedPrefs = getSharedPreferences("meetups", MODE_PRIVATE)
        trackingDao = AppDatabase.getInstance(applicationContext).trackingDao()
        Log.i(TAG, "onCreate")


        val builder: NotificationCompat.Builder = NotificationCompat.Builder(this, "location")
        builder.setSmallIcon(R.drawable.baseline_location_on_24)
            .setContentTitle("Location tracking enabled")
            .setContentText("Your location will be shared for meetups.")
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        startForeground(NOTIFICATION_ID, builder.build())

        val locationManager: LocationManager = applicationContext.getSystemService(LOCATION_SERVICE) as LocationManager
        if (ActivityCompat.checkSelfPermission(
                this,
                App.BACKGND_LOC_PM
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(applicationContext, "No permissions granted for meetups live location", Toast.LENGTH_LONG).show()
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 1.0f, this)
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 0f, this);
        val loc: Location? = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        
        onLocationChanged(loc!!)
    }
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }


    override fun onLocationChanged(location: Location) {
        Log.i(TAG, "onLocationChanged: " + location.latitude + " " + location.longitude)
//        Toast.makeText(applicationContext, "Location changed", Toast.LENGTH_LONG).show()
        val jsonLoc: com.sp.learntogether.objects.Location = com.sp.learntogether.objects.Location(
            location.latitude,
            location.longitude,
            Date().time
        )

        scope.launch(Dispatchers.IO) {
            trackingDao.getFuture(Date().time).forEach {
                if (it.timestmp - Date().time > 86400000) {
                    // more than 1 day
                    return@forEach
                }
                // TODO use WorkManager https://developer.android.com/topic/libraries/architecture/workmanager#kotlin .
                myRef.child(it.meetupUid).child("initiatorLocation")
                    .setValue(jsonLoc).await()
            }
        }

    }

    companion object {
        public const val NOTIFICATION_ID = 4389
    }
}
