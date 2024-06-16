package com.example.myapplication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityWeatherBinding


class WeatherActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWeatherBinding
    private var temp = "11"

    @Synchronized override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWeatherBinding.inflate(layoutInflater)
        val lat = intent.getDoubleExtra("lat", 1.0)
        val long = intent.getDoubleExtra("long", 1.0)
        setContentView(binding.root)
        val uni= '\u00B0'
        val temp = intent.getStringExtra("temp")
        val city = intent.getStringExtra("cityName")
        binding.temp.text = "$temp$uni C"
        binding.citytext.text = city



        println(temp)
    }


    fun initData(temp : String) {
        binding.temp.text = temp
    }

}
