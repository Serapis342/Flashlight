package com.serapis.flashlight

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.camera2.CameraManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity


@Suppress("DEPRECATION", "UNUSED_EXPRESSION")
class MainActivity : AppCompatActivity() {
    private var dpHeight:Int = 0
    private var dpWidth:Int = 0
    private var hasFlashlight: Boolean = false
    private var enabled: Boolean = true
    private var morseCode: String = ""
    private var camera: Boolean = true
    private var canChangeBrightness: Boolean = false
    private var brightness: Int = 125
    private var whichLayout: String = "Front_Camera"
    private val cameraManager by lazy { getSystemService(Context.CAMERA_SERVICE) as CameraManager }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_main)

        val displayMetrics = resources.displayMetrics
        dpHeight = displayMetrics.heightPixels
        dpWidth = displayMetrics.widthPixels
        resizeLayout()

        if (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
            if (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
                hasFlashlight = (true)
            }
        }
        newOnClickListener()
    }

    override fun onPause() {
        super.onPause()
        cameraManager.setTorchMode("0", false)
    }

    override fun onStart() {
        super.onStart()
        val powerButton = findViewById<ImageButton>(R.id.flashlightButton)
        cameraManager.setTorchMode("0", false)
        powerButton.setImageResource(R.drawable.ic_power_off)
    }

    private fun resizeLayout() {
        if(whichLayout.equals("Front_Camera")) {
            val linearLayout= findViewById<LinearLayout>(R.id.linearLayout)
            val space1 = findViewById<Space>(R.id.space1)
            val space2 = findViewById<Space>(R.id.space2)
            val space4 = findViewById<Space>(R.id.space4)
            val powerButton = findViewById<ImageButton>(R.id.flashlightButton)
            val userInput = findViewById<EditText>(R.id.userInput)

            val linearLayoutParams = linearLayout.layoutParams
            val space1Params = space1.layoutParams
            val space2Params = space2.layoutParams
            val space4Params = space4.layoutParams
            val powerButtonParams = powerButton.layoutParams
            val userInputParams = userInput.layoutParams
            
            linearLayoutParams.height = calcHeight(360f)
            space1Params.height = calcHeight(185f)
            space2Params.width = calcWidth(105f)
            space4Params.width = calcHeight(10f)
            powerButtonParams.height = calcHeight(150f)
            powerButtonParams.width = calcWidth(150f)
            userInputParams.height = calcHeight(30f)
            userInputParams.width = calcWidth(340f)
        } else {
            val space3 = findViewById<Space>(R.id.space3)
            val space3Params = space3.layoutParams
            space3Params.height = calcHeight(270f)
        }
        val switchCamera = findViewById<ImageButton>(R.id.switchCamera)
        val switchCameraParams = switchCamera.layoutParams
        switchCameraParams.height = calcHeight(50f)
        switchCameraParams.width = calcHeight(50f)
        newOnClickListener()
    }

    private fun calcHeight(value: Float): Int {
        return (dpHeight * (value / 640)).toInt()
    }

    private fun calcWidth(value: Float): Int {
        return (dpWidth * (value / 360)).toInt()
    }

    private fun flash() {
        val powerButton = findViewById<ImageButton>(R.id.flashlightButton)
        if(hasFlashlight) {
            enabled = if(enabled) {
                powerButton.setImageResource(R.drawable.ic_power_on)
                cameraManager.setTorchMode("0", true)
                false
            } else {
                powerButton.setImageResource(R.drawable.ic_power_off)
                cameraManager.setTorchMode("0", false)
                true
            }
        }
    }

    private fun getMorseCode() {
        val userInput = findViewById<EditText>(R.id.userInput)
        val morseFeature = MorsecodeFeature()
        enabled = false
        flash()
        morseCode = morseFeature.translateToMorse(userInput.text.toString().uppercase())
        toggleEditText()
        flashMorseCode()
    }

    private fun flashMorseCode() {
        val userInput = findViewById<EditText>(R.id.userInput)
        val backgroundThread = Thread {
            for (i in morseCode.indices) {
                if (morseCode[i] == '.') {
                    Thread.sleep(500)
                    flash()
                    Thread.sleep(240)
                    flash()
                } else if (morseCode[i] == '-') {
                    Thread.sleep(500)
                    flash()
                    Thread.sleep(720)
                    flash()
                } else if (morseCode[i] == ' ') {
                    Thread.sleep(1680)
                }
            }
            runOnUiThread {
                userInput.setEnabled(true)
                userInput.setText("")
                userInput.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_person, 0, 0, 0)
            }
            newOnClickListener()
        }
        backgroundThread.start()
    }

    private fun newOnClickListener() {
        val switchCamera = findViewById<ImageButton>(R.id.switchCamera)
        if(whichLayout == "Front_Camera") {
            val changeCamera = findViewById<ImageButton>(R.id.switchCamera)
            val flashlightButton = findViewById<ImageButton>(R.id.flashlightButton)
            val userInput = findViewById<EditText>(R.id.userInput)

            flashlightButton.setOnClickListener{
                if(userInput.text.toString() == "") {
                    flash()
                } else {
                    flashlightButton.setOnClickListener{null}
                    changeCamera.setOnClickListener{null}
                    getMorseCode()
                }
            }
        } else {
            val brightnessBar = findViewById<SeekBar>(R.id.brightnessBar)
            val layoutParams = window.attributes

            brightnessBar.progress = brightness
            layoutParams.screenBrightness = brightness / 255f
            window.attributes = layoutParams

            brightnessBar?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                    try {
                        brightness = brightnessBar.progress
                        layoutParams.screenBrightness = brightness / 255f
                        window.attributes = layoutParams
                    } catch (e: Exception) {
                        Toast.makeText(this@MainActivity,
                            "An Error has occured", Toast.LENGTH_SHORT).show()
                        brightnessBar.setOnSeekBarChangeListener(null)
                    }
                }

                override fun onStartTrackingTouch(p0: SeekBar?) {
                    //Not used
                }

                override fun onStopTrackingTouch(p0: SeekBar?) {
                    //Not used
                }
            })
        }

        switchCamera.setOnClickListener{
            val powerButton = findViewById<ImageButton>(R.id.flashlightButton)
            camera = when{
                camera == true -> {
                    if(hasFlashlight){
                        powerButton.setImageResource(R.drawable.ic_power_off)
                        cameraManager.setTorchMode("0", false)
                        enabled = true
                    }
                    whichLayout = "Selfie_Camera"
                    super.setContentView(R.layout.selfie_light)
                    false
                }
                else -> {
                    whichLayout = "Front_Camera"
                    super.setContentView(R.layout.activity_main)
                    true
                }
            }
            resizeLayout()
        }
    }

    private fun toggleEditText() {
        val userInput = findViewById<EditText>(R.id.userInput)
        userInput.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_computer, 0, 0, 0)
        userInput.setEnabled(false)
        userInput.setText(morseCode)
    }
}