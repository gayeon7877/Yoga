package com.sharkaboi.yogapartner.modules.main

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.google.mlkit.vision.pose.Pose
import com.sharkaboi.yogapartner.R
import com.sharkaboi.yogapartner.common.extensions.showToast
import com.sharkaboi.yogapartner.databinding.ActivityMainBinding
import com.sharkaboi.yogapartner.ml.ConvertedModel83ValAcc
import com.sharkaboi.yogapartner.ml.classification.ClassificationResult
import com.sharkaboi.yogapartner.ml.classification.TFLiteAsanaClassifier
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var tftest:TFLiteAsanaClassifier
    private lateinit var pose: Pose
    private lateinit var result: ClassificationResult

    override fun onCreate(savedInstanceState: Bundle?) {
        // Handle the splash screen transition.
        installSplashScreen()

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initNav()



    }

    private fun initNav() {
        navController = findNavController(R.id.fragmentContainer)
    }


}