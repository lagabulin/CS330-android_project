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


class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"

    // permissions
    private val permissions = arrayOf(RECORD_AUDIO, CAMERA, BLUETOOTH_CONNECT)
    private val PERMISSIONS_REQUEST = 0x0000001;


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
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