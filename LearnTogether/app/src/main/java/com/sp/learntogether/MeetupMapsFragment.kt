package com.sp.learntogether

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.sp.learntogether.objects.Location
import com.sp.learntogether.R


private const val TAG = "MeetupMapsFragment"

class MeetupMapsFragment : Fragment() {
    private val database = Firebase.database(Utils.FIREBASE_RTDB_URL)
    private val ref by lazy { database.getReference("meetups").child(args.meetup.id) }
    private val initiatorMarker by lazy { MarkerOptions()
        .title(getString(R.string.position_of_initiator)) }


    private var initMarker: Marker? = null
    @SuppressLint("MissingPermission")
    private val callback = OnMapReadyCallback { googleMap ->
        val meetupLocation = LatLng(args.meetup.latitude, args.meetup.longitide)
        ref.child("initiatorLocation").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val current = snapshot.getValue<Location>()
                current?.let {
                    if (initiatorMarker.position == null) {
                        initiatorMarker.position(LatLng(it.lat, it.lng))
                        initMarker = googleMap.addMarker(initiatorMarker)
                    } else {
                        initMarker?.position = LatLng(it.lat, it.lng)
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })

        googleMap.addMarker(MarkerOptions().position(meetupLocation).title(args.meetup.name))
        val locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (isLocationEnabled(requireContext()) && ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, LocationListener {
                googleMap.addMarker(MarkerOptions().position(LatLng(it.latitude, it.longitude)).title(
                    App.appCtx.getString(
                        R.string.your_location
                    )))
            }, null)
        }
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(meetupLocation))
        googleMap.uiSettings.isCompassEnabled = true
        initCompass(googleMap)

    }

    val args: MeetupMapsFragmentArgs by navArgs()

    @Suppress("deprecation")
    fun isLocationEnabled(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            // This is a new method provided in API 28
            val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            lm.isLocationEnabled
        } else {
            // This was deprecated in API 28
            val mode: Int = Settings.Secure.getInt(
                context.contentResolver, Settings.Secure.LOCATION_MODE,
                Settings.Secure.LOCATION_MODE_OFF
            )
            mode != Settings.Secure.LOCATION_MODE_OFF
        }
    }

    lateinit var sensorManager: SensorManager
    lateinit var accelerometer: Sensor
    lateinit var magnetField: Sensor


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_meetup_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        //https://stackoverflow.com/questions/3740228/first-android-app-how-to-access-the-compass

        mapFragment?.getMapAsync(callback)

        sensorManager = requireContext().getSystemService(Context.SENSOR_SERVICE) as SensorManager

        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)!!
        magnetField = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)!!



        ref.child("nearby").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val current = snapshot.getValue<Boolean>()
                view.findViewById<TextView>(R.id.desc_geofence)
                    .setText(if (current == true) "Initiator has entered location" else "Initiator has left location")
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })


    }



    private fun initCompass(googleMap: GoogleMap) {
        val listener: SensorEventListener = object : SensorEventListener {
            private var magnetometerUsed: Boolean = false
            private var accelerometerUsed: Boolean = false
            private var magnetometerValue: FloatArray = FloatArray(3)
            private var accelerometerValue: FloatArray = FloatArray(3)
            private var orientation: FloatArray = FloatArray(3)
            override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) { }
            override fun onSensorChanged(event: SensorEvent) {
                if (event.sensor == magnetField) {
                    System.arraycopy(event.values, 0, magnetometerValue, 0, event.values.size)
                    magnetometerUsed = true
                } else if (event.sensor == accelerometer) {
                    System.arraycopy(event.values, 0, accelerometerValue, 0, event.values.size)
                    accelerometerUsed = true
                }
                if (magnetometerUsed && accelerometerUsed) {
                    var rotationMatrix: FloatArray = FloatArray(9)
                    SensorManager.getRotationMatrix(
                        rotationMatrix,
                        null,
                        accelerometerValue,
                        magnetometerValue
                    )
                    SensorManager.getOrientation(rotationMatrix, orientation)
                    val azimuthInRadians: Float = orientation[0]
                    val azimuthInDegrees =
                        (Math.toDegrees(azimuthInRadians.toDouble()) + 360).toFloat() % 360
                    val oldPos = googleMap.cameraPosition

                    val pos = CameraPosition.builder(oldPos).bearing(azimuthInDegrees)
                        .build()

                    googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(pos))

                }
            }

        }

        sensorManager.registerListener(listener, magnetField, SensorManager.SENSOR_DELAY_NORMAL)
        sensorManager.registerListener(listener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)

    }
}