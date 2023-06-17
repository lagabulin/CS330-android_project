package com.example.pj4test.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.pj4test.ProjectConfiguration
import com.example.pj4test.audioInference.WalkClassifier
import com.example.pj4test.databinding.FragmentWalkBinding
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.CountDownTimer
import android.util.Log
import com.example.pj4test.MainActivity
import java.util.*
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.concurrent.timer

class  WalkFragment: Fragment(), WalkClassifier.DetectorListener, SensorEventListener {
    private val TAG = "WalkFragment"

    private var _fragmentWalkBinding: FragmentWalkBinding? = null

    private val fragmentWalkBinding
        get() = _fragmentWalkBinding!!

    private val sensorManager by lazy {
        context?.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

//    private val acc_move: CountDownTimer = object : CountDownTimer(3000, 3000) {
//        override fun onTick(millisUntilFinished: Long) {
//
//        }
//
//        override fun onFinish() {
//            move = false
//            walkClassifier.stopInferencing()
//            walkClassifier.stopRecording()
//        }
//    }
    private var acc: TimerTask? = null

    var recording = false

    // classifiers
    lateinit var walkClassifier: WalkClassifier

    // views
    lateinit var walkView: TextView


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _fragmentWalkBinding = FragmentWalkBinding.inflate(inflater, container, false)

        return fragmentWalkBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        walkView = fragmentWalkBinding.WalkView

        walkClassifier = WalkClassifier()
        walkClassifier.initialize(requireContext())
        walkClassifier.setDetectorListener(this)
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
//        move = false
        walkClassifier.stopInferencing()
//        walkClassifier.stopRecording()
//        acc_move?.cancel()
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let{
            walkClassifier.fast = false
            walkClassifier.slow = false
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]
            val r = sqrt(x.pow(2) + y.pow(2) + z.pow(2))
            if (r < 1) {
                walkClassifier.slow = true
            }
            else if (r > 5) {
                walkClassifier.fast = true
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
//        TODO("Not yet implemented")
    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(this,
            sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION),
            SensorManager.SENSOR_DELAY_NORMAL
        )
        walkClassifier.startInferencing()
    }

    override fun onResults(score: Float) {
        activity?.runOnUiThread {
            if (score > WalkClassifier.THRESHOLD && !recording){
                recording = true
                Log.d("tag","yupyup")
                walkView.text = "WALK"
                walkView.setBackgroundColor(ProjectConfiguration.activeBackgroundColor)
                walkView.setTextColor(ProjectConfiguration.activeTextColor)
                walkClassifier.stopRecording()
                walkClassifier.stopInferencing()
                (activity as MainActivity).cameraStart()
            } else {
                walkView.text = "NO WALK"
                walkView.setBackgroundColor(ProjectConfiguration.idleBackgroundColor)
                walkView.setTextColor(ProjectConfiguration.idleTextColor)
            }
        }
    }
}