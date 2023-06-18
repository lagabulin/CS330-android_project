package com.example.pj4test.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.pj4test.ProjectConfiguration
import com.example.pj4test.databinding.FragmentHonkBinding
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
import com.example.pj4test.audioInference.HonkClassifier
import java.util.*
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.concurrent.timer

class  HonkFragment: Fragment(), HonkClassifier.DetectorListener, SensorEventListener {
    private val TAG = "HonkFragment"

    private var _fragmentHonkBinding: FragmentHonkBinding? = null

    private val fragmentHonkBinding
        get() = _fragmentHonkBinding!!

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
    lateinit var honkClassifier: HonkClassifier

    // views
    lateinit var honkView: TextView


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _fragmentHonkBinding = FragmentHonkBinding.inflate(inflater, container, false)

        return fragmentHonkBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        honkView = fragmentHonkBinding.HonkView

        honkClassifier = HonkClassifier()
        honkClassifier.initialize(requireContext())
        honkClassifier.setDetectorListener(this)
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
        honkClassifier.stopInferencing()
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let{
            honkClassifier.fast = false
            honkClassifier.slow = false
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]
            val r = sqrt(x.pow(2) + y.pow(2) + z.pow(2))
            if (r < 1) {
                honkClassifier.slow = true
            }
            else if (r > 5) {
                honkClassifier.fast = true
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
        honkClassifier.startInferencing()
    }

    override fun onResults(score: Float) {
        activity?.runOnUiThread {
            if (score > HonkClassifier.THRESHOLD && !recording){
                recording = true
                honkView.text = "DANGER"
                honkView.setBackgroundColor(ProjectConfiguration.activeBackgroundColor)
                honkView.setTextColor(ProjectConfiguration.activeTextColor)
                honkClassifier.stopInferencing()
                honkClassifier.stopRecording()
                (activity as MainActivity).cameraStart()
            } else {
                honkView.text = "SAFE"
                honkView.setBackgroundColor(ProjectConfiguration.idleBackgroundColor)
                honkView.setTextColor(ProjectConfiguration.idleTextColor)
            }
        }
    }
}