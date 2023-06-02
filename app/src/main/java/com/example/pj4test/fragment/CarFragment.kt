package com.example.pj4test.fragment

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.camera.core.impl.utils.ContextUtil.getApplicationContext
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.example.pj4test.ProjectConfiguration
import com.example.pj4test.audioInference.CarClassifier
import com.example.pj4test.databinding.FragmentCarBinding


class CarFragment: Fragment(), CarClassifier.DetectorListener {
    private val TAG = "CarFragment"

    private var _fragmentCarBinding: FragmentCarBinding? = null

    private val fragmentCarBinding
        get() = _fragmentCarBinding!!

    // Bluetooth adapter
    var mBluetoothAdapter: BluetoothAdapter? = null

    // classifiers
    lateinit var carClassifier: CarClassifier

    // views
    lateinit var carView: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _fragmentCarBinding = FragmentCarBinding.inflate(inflater, container, false)
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return fragmentCarBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        carView = fragmentCarBinding.CarView

        carClassifier = CarClassifier()
        carClassifier.initialize(requireContext())
        carClassifier.setDetectorListener(this)
    }

    override fun onPause() {
        super.onPause()
        carClassifier.stopInferencing()
    }

    override fun onResume() {
        super.onResume()
        carClassifier.startInferencing()
    }

    override fun onResults(score: Float) {
        activity?.runOnUiThread {
            if (score > CarClassifier.THRESHOLD) {
                carView.text = "CAR"
                carView.setBackgroundColor(ProjectConfiguration.activeBackgroundColor)
                carView.setTextColor(ProjectConfiguration.activeTextColor)

                // BLUETOOTH OFF or Warning Alert
                bluetoothOff()
            } else {
                carView.text = "NO CAR"
                carView.setBackgroundColor(ProjectConfiguration.idleBackgroundColor)
                carView.setTextColor(ProjectConfiguration.idleTextColor)
            }
        }
    }

    /* Reference: https://stickode.tistory.com/219 */
    private fun bluetoothOff(){
        if (mBluetoothAdapter == null) {
            // Device doesn't support Bluetooth
//            Log.d("bluetoothAdapter","Device doesn't support Bluetooth")
        }else{
            if (mBluetoothAdapter?.isEnabled == true) {
                mBluetoothAdapter?.disable()
            }
        }
    }
}