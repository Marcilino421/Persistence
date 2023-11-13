package edu.fullerton.fz.cs411.colormaker

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.SeekBar
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import edu.fullerton.fz.cs411.colormaker.R
import java.util.concurrent.Flow
import java.util.prefs.Preferences

@Suppress("Since15")
class ColorViewModel(private val colorDataStore: ColorDataStore) : ViewModel() {
    var redIntensity: Float = 1.0f
    var greenIntensity: Float = 1.0f
    var blueIntensity: Float = 1.0f

    fun saveColorIntensities(red: Float, green: Float, blue: Float) {
}
class ColorDataStore(context: Context) {
    suspend fun saveColorIntensities(red: Float, green: Float, blue: Float) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.RED_INTENSITY] = red
            preferences[PreferencesKeys.GREEN_INTENSITY] = green
            preferences[PreferencesKeys.BLUE_INTENSITY] = blue
        }
    }

    val colorIntensities: Flow<Triple<Float, Float, Float>> = dataStore.data
        .map { preferences ->
            Triple(
                preferences[PreferencesKeys.RED_INTENSITY] ?: 1.0f,
                preferences[PreferencesKeys.GREEN_INTENSITY] ?: 1.0f,
                preferences[PreferencesKeys.BLUE_INTENSITY] ?: 1.0f
            )
        }
    private object PreferencesKeys {
        val RED_INTENSITY = floatPreferencesKey("red_intensity")
        val GREEN_INTENSITY = floatPreferencesKey("green_intensity")
        val BLUE_INTENSITY = floatPreferencesKey("blue_intensity")
    }
}
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
    private lateinit var colorViewModel: ColorViewModel
    private lateinit var colorDataStore: ColorDataStore
    private var redIntensity: Float = 1.0f
    private var greenIntensity: Float = 1.0f
    private var blueIntensity: Float = 1.0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        colorDataStore = ColorDataStore(this)
        colorViewModel = ViewModelProvider(this).get(ColorViewModel::class.java)
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
        if (savedInstanceState != null) {
            colorViewModel.redIntensity = savedInstanceState.getFloat("redIntensity", 1.0f)
            colorViewModel.greenIntensity = savedInstanceState.getFloat("greenIntensity", 1.0f)
            colorViewModel.blueIntensity = savedInstanceState.getFloat("blueIntensity", 1.0f)
            updateColorView()
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putFloat("redIntensity", colorViewModel.redIntensity)
        outState.putFloat("greenIntensity", colorViewModel.greenIntensity)
        outState.putFloat("blueIntensity", colorViewModel.blueIntensity)
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