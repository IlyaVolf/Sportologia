package com.thesis.sportologia

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.thesis.sportologia.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val binding = ActivityMainBinding.inflate(layoutInflater).also { setContentView(it.root) }


    }
}