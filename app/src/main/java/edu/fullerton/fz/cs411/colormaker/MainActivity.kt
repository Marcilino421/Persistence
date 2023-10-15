package edu.fullerton.fz.cs411.colormaker

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.SeekBar
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener

class MainActivity : AppCompatActivity() {
    private lateinit var redSwitch: Switch
    private lateinit var greenSwitch: Switch
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private lateinit var blueSwitch: Switch
    private lateinit var redSeekBar: SeekBar
    private lateinit var greenSeekBar: SeekBar
    private lateinit var blueSeekBar: SeekBar
    private lateinit var redValue: TextView
    private lateinit var greenValue: TextView
    private lateinit var blueValue: TextView
    private lateinit var colorView: View
    private var redIntensity: Float = 1.0f
    private var greenIntensity: Float = 1.0f
    private var blueIntensity: Float = 1.0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        redSwitch = findViewById(R.id.redSwitch)
        greenSwitch = findViewById(R.id.greenSwitch)
        blueSwitch = findViewById(R.id.blueSwitch)
        redSeekBar = findViewById(R.id.redSeekBar)
        greenSeekBar = findViewById(R.id.greenSeekBar)
        blueSeekBar = findViewById(R.id.blueSeekBar)
        redValue = findViewById(R.id.redValue)
        greenValue = findViewById(R.id.greenValue)
        blueValue = findViewById(R.id.blueValue)
        colorView = findViewById(R.id.colorView)

        setSwitchListener(redSwitch, redSeekBar, redValue)
        setSwitchListener(greenSwitch, greenSeekBar, greenValue)
        setSwitchListener(blueSwitch, blueSeekBar, blueValue)

        setSeekBarListener(redSeekBar, redValue)
        setSeekBarListener(greenSeekBar, greenValue)
        setSeekBarListener(blueSeekBar, blueValue)

        findViewById<Button>(R.id.resetButton).setOnClickListener {
            resetValues()
        }
        updateColorView()
    }

    private fun setSwitchListener(switch: Switch, seekBar: SeekBar, textView: TextView) {
        switch.setOnCheckedChangeListener { _, isChecked ->
            seekBar.isEnabled = isChecked
            textView.isEnabled = isChecked
            if (isChecked) {
                when (seekBar) {
                    redSeekBar -> seekBar.progress = (redIntensity * 255).toInt()
                    greenSeekBar -> seekBar.progress = (greenIntensity * 255).toInt()
                    blueSeekBar -> seekBar.progress = (blueIntensity * 255).toInt()
                }
            } else {
                when (seekBar) {
                    redSeekBar -> redIntensity = 0.0f
                    greenSeekBar -> greenIntensity = 0.0f
                    blueSeekBar -> blueIntensity = 0.0f
                }
            }
            updateColorView()
        }
    }

    private fun setSeekBarListener(seekBar: SeekBar, textView: TextView) {
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val intensity = progress / 255.0f
                when (seekBar) {
                    redSeekBar -> redIntensity = intensity
                    greenSeekBar -> greenIntensity = intensity
                    blueSeekBar -> blueIntensity = intensity
                }
                updateColorView()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

        textView.addTextChangedListener { text ->
            val value = text.toString().toFloatOrNull() ?: 0.0f
            val intensity = (value / 255.0f).coerceIn(0.0f, 1.0f)
            when (textView) {
                redValue -> redIntensity = intensity
                greenValue -> greenIntensity = intensity
                blueValue -> blueIntensity = intensity
            }
            seekBar.progress = (intensity * 255).toInt()
            updateColorView()
        }
    }

    private fun resetValues() {
        redSwitch.isChecked = true
        greenSwitch.isChecked = true
        blueSwitch.isChecked = true
        redIntensity = 1.0f
        greenIntensity = 1.0f
        blueIntensity = 1.0f
        redSeekBar.progress = 255
        greenSeekBar.progress = 255
        blueSeekBar.progress = 255
        redValue.text = "10"
        greenValue.text = "10"
        blueValue.text = "10"
        updateColorView()
    }

    private fun updateColorView() {
        val color = Color.argb(255, (redIntensity * 255).toInt(), (greenIntensity * 255).toInt(), (blueIntensity * 255).toInt())
        colorView.setBackgroundColor(color)
    }
}