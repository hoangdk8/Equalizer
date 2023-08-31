package com.mobiai.app.equalizer

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.audiofx.BassBoost
import android.media.audiofx.Equalizer
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.mobiai.R
import com.mobiai.databinding.ActivityEqualizerBinding
import kotlin.math.abs
import kotlin.math.atan2

class EqualizerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEqualizerBinding
    private lateinit var bassKnob: ImageView
    private lateinit var stereoKnob: ImageView
    private lateinit var equalizer: Equalizer
    private var previousBassAngle = 0f
    private var previousStereoAngle = 0f
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var seekBars: List<SeekBar>
    private var currentBass = 10
    private val sharedPreferences by lazy {
        getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEqualizerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bassKnob = findViewById(R.id.bassKnob)
        stereoKnob = findViewById(R.id.stereoKnob)
        binding.menuMode.setOnClickListener {
            startActivity(Intent(this, ModeActivity::class.java))
        }

        bassKnob.setOnTouchListener { _, event ->
            handleKnobTouch(event, bassKnob, ::updateBass)
            true
        }

        stereoKnob.setOnTouchListener { _, event ->
            handleKnobTouch(event, stereoKnob, ::updateStereo)
            true
        }
        mediaPlayer = MediaPlayer.create(this, R.raw.nhac) // Thay đổi thành tên file nhạc của bạn
        equalizer = Equalizer(0, mediaPlayer.audioSessionId)
        equalizer.enabled = true
        binding.buttonPlay.setOnClickListener {
            mediaPlayer.start()
            binding.buttonPlay.visibility = View.GONE
            binding.buttonPause.visibility = View.VISIBLE
        }
        binding.buttonPause.setOnClickListener {
            mediaPlayer.pause()
            binding.buttonPause.visibility = View.GONE
            binding.buttonPlay.visibility = View.VISIBLE
        }

        var isOverlayVisible = false

        fun showOverlay() {
            val overlayView = findViewById<View>(R.id.overlayView)
            overlayView.visibility = if (isOverlayVisible) View.GONE else View.VISIBLE
            isOverlayVisible = !isOverlayVisible
        }

        binding.actionPower.setOnClickListener {
            showOverlay()
        }


        seekBars = listOf(
            binding.seekBar1,
            binding.seekBar2,
            binding.seekBar3,
            binding.seekBar4,
            binding.seekBar5
        )
        val frequencies = listOf(31, 62, 125, 250, 500)
        for (i in seekBars.indices) {
            val seekBar = seekBars[i]
            seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?, progress: Int, fromUser: Boolean
                ) {
                    val frequency = frequencies[i]
                    val minBandLevel = equalizer.bandLevelRange[0]
                    val maxBandLevel = equalizer.bandLevelRange[1]

                    val level = minBandLevel + (maxBandLevel - minBandLevel) * progress / 100
                    equalizer.setBandLevel(i.toShort(), level.toShort())
                    changeFrequency(frequency, level)
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            })
        }
    }

    private fun setProgressSeekBarFromMode() {
        when (sharedPreferences.getString("SelectedMode", null)) {
            "normal" -> {
                binding.seekBar1.progress = 50
                binding.seekBar2.progress = 50
                binding.seekBar3.progress = 50
                binding.seekBar4.progress = 50
                binding.seekBar5.progress = 50
            }

            "classical" -> {
                binding.seekBar1.progress = 70
                binding.seekBar2.progress = 60
                binding.seekBar3.progress = 40
                binding.seekBar4.progress = 60
                binding.seekBar5.progress = 60
            }

            "dance" -> {
                binding.seekBar1.progress = 70
                binding.seekBar2.progress = 50
                binding.seekBar3.progress = 55
                binding.seekBar4.progress = 60
                binding.seekBar5.progress = 50
            }

            "pop" -> {
                binding.seekBar1.progress = 40
                binding.seekBar2.progress = 55
                binding.seekBar3.progress = 80
                binding.seekBar4.progress = 50
                binding.seekBar5.progress = 40
            }

            "folk" -> {
                binding.seekBar1.progress = 60
                binding.seekBar2.progress = 50
                binding.seekBar3.progress = 50
                binding.seekBar4.progress = 60
                binding.seekBar5.progress = 50
            }

            "hiphop" -> {
                binding.seekBar1.progress = 70
                binding.seekBar2.progress = 60
                binding.seekBar3.progress = 50
                binding.seekBar4.progress = 60
                binding.seekBar5.progress = 70
            }

            "jazz" -> {
                binding.seekBar1.progress = 60
                binding.seekBar2.progress = 50
                binding.seekBar3.progress = 40
                binding.seekBar4.progress = 50
                binding.seekBar5.progress = 60
            }

            "rock" -> {
                binding.seekBar1.progress = 60
                binding.seekBar2.progress = 50
                binding.seekBar3.progress = 40
                binding.seekBar4.progress = 50
                binding.seekBar5.progress = 60
            }

            "native" -> {
                binding.seekBar1.progress = 60
                binding.seekBar2.progress = 50
                binding.seekBar3.progress = 80
                binding.seekBar4.progress = 60
                binding.seekBar5.progress = 50
            }
        }
        equalizer = Equalizer(0, mediaPlayer.audioSessionId)
        equalizer.enabled = true
        seekBars = listOf(
            findViewById(R.id.seek_bar_1),
            findViewById(R.id.seek_bar_2),
            findViewById(R.id.seek_bar_3),
            findViewById(R.id.seek_bar_4),
            findViewById(R.id.seek_bar_5)
        )
        val frequencies = listOf(31, 62, 125, 250, 500)
        for (i in seekBars.indices) {
            val seekBar = seekBars[i]
            seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?, progress: Int, fromUser: Boolean
                ) {
                    val frequency = frequencies[i]
                    val minBandLevel = equalizer.bandLevelRange[0]
                    val maxBandLevel = equalizer.bandLevelRange[1]

                    val level = minBandLevel + (maxBandLevel - minBandLevel) * progress / 100
                    equalizer.setBandLevel(i.toShort(), level.toShort())
                    changeFrequency(frequency, level)
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            })
        }
    }


    private fun changeFrequency(frequency: Int, level: Int) {

        Log.d("FrequencyChange", "Frequency: $frequency Hz, Level: $level dB")
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }

    override fun onResume() {
        super.onResume()
        setProgressSeekBarFromMode()
    }


    private fun handleKnobTouch(
        event: MotionEvent, knob: ImageView, updateFunction: (Float) -> Unit
    ) {
        val centerX = knob.width / 2
        val centerY = knob.height / 2
        val x = event.x
        val y = event.y

        val angle = calculateAngle(centerX, centerY, x, y)

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (knob == bassKnob) {
                    previousBassAngle = angle
                } else if (knob == stereoKnob) {
                    previousStereoAngle = angle
                }
            }

            MotionEvent.ACTION_MOVE -> {
                val deltaAngle =
                    angle - (if (knob == bassKnob) previousBassAngle else previousStereoAngle)
                updateFunction(deltaAngle)
                knob.rotation += deltaAngle
                if (knob == bassKnob) {
                    previousBassAngle = angle
                } else if (knob == stereoKnob) {
                    previousStereoAngle = angle
                }
            }
        }
    }

    private fun calculateAngle(centerX: Int, centerY: Int, x: Float, y: Float): Float {
        val deltaX = x - centerX
        val deltaY = y - centerY
        return Math.toDegrees(atan2(deltaY.toDouble(), deltaX.toDouble())).toFloat()
    }

    private fun calculateStrength(deltaAngle: Float, maxStrength: Int): Int {
        val maxAngle = 360f // 360 degrees
        val anglePercent = deltaAngle / maxAngle // Calculate the angle as a percentage
        val strengthChange = (anglePercent * maxStrength).toInt() // Calculate the strength change
        return strengthChange.coerceIn(-maxStrength, maxStrength) // Ensure it's within limits
    }

    private fun updateBass(deltaAngle: Float) {
        // Giới hạn góc xoay trong khoảng 180 độ
        val limitedDeltaAngle = deltaAngle.coerceIn(-90f, 90f)

        // Tính toán giá trị tăng bass dựa trên góc xoay
        val bassIncrement = 1
        val bassChange = (limitedDeltaAngle / 10 * bassIncrement).toInt()

        // Cập nhật giá trị bass
        currentBass += bassChange

        // Giới hạn giá trị bass trong khoảng từ 10 đến 19
        currentBass = currentBass.coerceIn(1, 19)

        // Áp dụng giá trị bass vào xử lý âm thanh của bạn
        val bassBoost = BassBoost(0, mediaPlayer.audioSessionId)
        bassBoost.enabled = true
        val strengthChange =
            calculateStrength(limitedDeltaAngle, 1000) // 1000 is just a placeholder value
        bassBoost.setStrength(strengthChange.toShort())
        Log.d("hoang", "Bass Strength: $currentBass, Rotation: $limitedDeltaAngle degrees")
    }

    private fun updateStereo(deltaAngle: Float) {
        val audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)

        val stepsPerRotation = maxVolume.toFloat() / 360

        val volumeChange = (deltaAngle * stepsPerRotation).toInt()

        val currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)

        val newVolume = (currentVolume + volumeChange).coerceIn(0, maxVolume)

        audioManager.setStreamVolume(
            AudioManager.STREAM_MUSIC, newVolume, AudioManager.FLAG_SHOW_UI
        )

        Log.d("hoang", "Volume Change: $volumeChange steps ($newVolume)")
    }

    companion object {
        const val EXTRA_SELECTED_MODE = "selected_mode"
    }
}
