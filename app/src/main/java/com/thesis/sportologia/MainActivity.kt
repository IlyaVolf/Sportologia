package com.thesis.sportologia

import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.thesis.sportologia.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val binding = ActivityMainBinding.inflate(layoutInflater).also { setContentView(it.root) }

        /*val tf = Typeface.createFromAsset(
            assets,
            "fonts/SourceSansPro/SourceSansPro-Bold.ttf"
        )
        val tv1 = findViewById<View>(R.id.title) as TextView
        val tv2 = findViewById<View>(R.id.account_type) as TextView
        tv1.typeface = tf
        tv2.typeface = tf*/

    }
}