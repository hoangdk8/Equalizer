package com.mobiai.app.equalizer

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.mobiai.R
import com.mobiai.databinding.ActivityModeBinding

class ModeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityModeBinding
    private val sharedPreferences by lazy {
        getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityModeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.back.setOnClickListener {
            finish()
        }
        binding.modeNormal.setOnClickListener {
            sendSelectedMode("normal")
        }

        binding.modeClassical.setOnClickListener {
            sendSelectedMode("classical")
        }
        binding.modeDance.setOnClickListener {
            sendSelectedMode("dance")
        }
        binding.modePop.setOnClickListener {
            sendSelectedMode("pop")
        }
        binding.modeFolk.setOnClickListener {
            sendSelectedMode("folk")
        }
        binding.modeHiphop.setOnClickListener {
            sendSelectedMode("hiphop")
        }
        binding.modeJazz.setOnClickListener {
            sendSelectedMode("jazz")
        }
        binding.modeRock.setOnClickListener {
            sendSelectedMode("rock")
        }
        binding.modeNative.setOnClickListener {
            sendSelectedMode("native")
        }
    }

    private fun sendSelectedMode(selectedMode: String) {
        sharedPreferences.edit()
            .putString("SelectedMode", selectedMode)
            .apply()
        finish()
        Log.d("checkccc", "sendSelectedMode:$selectedMode ")
    }
}
