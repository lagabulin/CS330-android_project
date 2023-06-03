package com.example.pj4test

import android.Manifest.permission.BLUETOOTH_CONNECT
import android.Manifest.permission.CAMERA
import android.Manifest.permission.RECORD_AUDIO
import android.R
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.pj4test.fragment.CarFragment
import java.util.*


class MainActivity : AppCompatActivity(), SensorEventListener {
    private val TAG = "MainActivity"

    // permissions
    private val permissions = arrayOf(RECORD_AUDIO, CAMERA, BLUETOOTH_CONNECT)
    private val PERMISSIONS_REQUEST = 0x0000001;

    private val sensorManager by lazy {
        getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    // mp3 alert
    lateinit var mMediaPlayer: MediaPlayer


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkPermissions() // check permissions
        mMediaPlayer = MediaPlayer.create(this, R.raw.alert)
//        alert()

    }

    private fun checkPermissions() {
        if (permissions.all{ActivityCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED}){
            Log.d(TAG, "All Permission Granted")
        }
        else{
            requestPermissions(permissions, PERMISSIONS_REQUEST)
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let{
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]
//            val r = sqrt(x.pow(2) + y.pow(2) + z.pow(2))

//            Log.d("TAG", "onSensorChanged: x: $x, y: $y, z: $z, R: $r")
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
//        TODO("Not yet implemented")
    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(this,
            sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_FASTEST
        )

    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
        mMediaPlayer.stop()
        mMediaPlayer.reset()
    }

    fun alert(){
        Log.d("ALERT START", "ALERT START")
        mMediaPlayer.start()
        Log.d("ALERT FIN", "ALERT FIN")
    }

//    fun CarInvoke(){
//        val cf: CarFragment? =
//            supportFragmentManager.findFragmentById(R.id.) as CarFragment?
//        tf.testFunction()
//    }

}