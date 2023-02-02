package com.thesis.sportologia

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.thesis.sportologia.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    //        Toast.makeText(context, "$savedActivatedButtonId", Toast.LENGTH_SHORT).show()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val binding = ActivityMainBinding.inflate(layoutInflater).also { setContentView(it.root) }

        val fragment = RegistrationFragment()

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .add(R.id.fragment_container, fragment)
                .commit()
        }

        /*val tf = Typeface.createFromAsset(
            assets,
            "fonts/SourceSansPro/SourceSansPro-Bold.ttf"
        )
        val tv1 = findViewById<View>(R.id.title) as TextView
        val tv2 = findViewById<View>(R.id.account_type) as TextView
        tv1.typeface = tf
        tv2.typeface = tf*/

        // access the spinner
        /*val accountTypes = listOf(getString(R.string.athlete), getString(R.string.organization))
        val spinnerBlockAccountType = findViewById<SpinnerBasicView>(R.id.spinner_account_type)
        spinnerBlockAccountType.initAdapter(accountTypes, getString(R.string.hint_account_type))

        val genders = listOf(getString(R.string.male), getString(R.string.female))
        val spinnerBlockGender = findViewById<SpinnerBasicView>(R.id.spinner_gender)
        spinnerBlockGender.initAdapter(genders, getString(R.string.hint_gender))*/

    }
}