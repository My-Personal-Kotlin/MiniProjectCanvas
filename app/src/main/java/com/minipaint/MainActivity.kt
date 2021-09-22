package com.minipaint

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.minipaint.customcanvas.MyCanvasView
import com.minipaint.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var myCanvasView: MyCanvasView

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        myCanvasView = binding.myCanvasView

        setContentView(binding.root)

    }

    fun clear(view: View){
        myCanvasView.clear()
    }
}