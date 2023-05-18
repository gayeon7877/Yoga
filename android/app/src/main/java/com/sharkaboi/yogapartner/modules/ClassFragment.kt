package com.sharkaboi.yogapartner.modules

import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.common.io.Files.append
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.sharkaboi.yogapartner.R
import com.sharkaboi.yogapartner.common.extensions.observe
import com.sharkaboi.yogapartner.common.extensions.showToast
import com.sharkaboi.yogapartner.databinding.FragmentClassBinding
import com.sharkaboi.yogapartner.ml.classification.AsanaClass
import com.sharkaboi.yogapartner.ml.classification.TFLiteAsanaClassifier
import com.sharkaboi.yogapartner.ml.config.DetectorOptions
import com.sharkaboi.yogapartner.ml.models.Classification
import com.sharkaboi.yogapartner.ml.models.PoseWithAsanaClassification
import com.sharkaboi.yogapartner.ml.processor.AsanaProcessor
import com.sharkaboi.yogapartner.modules.asana_pose.ui.custom.LandMarksOverlay
import com.sharkaboi.yogapartner.modules.asana_pose.util.ResultSmoother
import com.sharkaboi.yogapartner.modules.asana_pose.util.TTSSpeechManager
import com.sharkaboi.yogapartner.modules.asana_pose.vm.AsanaPoseViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.checkerframework.checker.units.qual.A
import org.checkerframework.checker.units.qual.min
import timber.log.Timber
import java.util.*
import javax.inject.Inject

@ExperimentalGetImage
@AndroidEntryPoint
class ClassFragment : Fragment() {

    @Inject
    lateinit var detectorOptions: DetectorOptions

    private var _binding: FragmentClassBinding? = null
    private val binding get() = _binding!!
    private val asanaPoseViewModel by viewModels<AsanaPoseViewModel>()
    private val navController get() = findNavController()
    private val mainExecutor get() = ContextCompat.getMainExecutor(requireContext())
    private val resultSmoother = ResultSmoother()
    private lateinit var ttsSpeechManager: TTSSpeechManager
    private lateinit var yogaClass: YogaClass

    private val previewView: PreviewView get() = binding.previewView
    private val landMarksOverlay: LandMarksOverlay get() = binding.landmarksOverlay
    private var cameraProvider: ProcessCameraProvider? = null
    private var previewUseCase: Preview? = null
    private var analysisUseCase: ImageAnalysis? = null
    private var asanaProcessor: AsanaProcessor? = null
    private var needUpdateGraphicOverlayImageSourceInfo = false
    private var lensFacing = CameraSelector.LENS_FACING_FRONT
    private var cameraSelector: CameraSelector? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentClassBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        bindAllCameraUseCases()
    }

    override fun onPause() {
        ttsSpeechManager.stop()
        yogaClass.stop()
        super.onPause()
        asanaProcessor?.run { this.stop() }

    }

    override fun onDestroyView() {
        asanaProcessor?.run { this.stop() }
        ttsSpeechManager.stop()
        ttsSpeechManager.shutdown()
        yogaClass.stop()
        yogaClass.shutdown()
        _binding = null
        super.onDestroyView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initTTS()
        initCamera()
        initListeners()
        initObservers()
        //initYogaClass()
        test()
    }


    private fun initTTS() {
        ttsSpeechManager = TTSSpeechManager(requireContext())
    }

    private fun initYogaClass() {
        yogaClass = YogaClass(requireContext())

    }

    private fun initCamera() {
        cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()
    }

    private fun initListeners() {
        binding.btnSwitchCamera.setOnClickListener { switchCamera() }
        binding.toolbar.setNavigationOnClickListener { navController.navigateUp() }
    }

    private fun initObservers() {
        observe(asanaPoseViewModel.processCameraProvider) { provider: ProcessCameraProvider? ->
            cameraProvider = provider
            bindAllCameraUseCases()
        }
    }

    private fun switchCamera() {
        if (cameraProvider == null) {
            return
        }

        binding.progress.isVisible = true
        val newLensFacing = if (lensFacing == CameraSelector.LENS_FACING_FRONT) {
            CameraSelector.LENS_FACING_BACK
        } else {
            CameraSelector.LENS_FACING_FRONT
        }
        val newCameraSelector = CameraSelector.Builder().requireLensFacing(newLensFacing).build()
        try {
            if (cameraProvider!!.hasCamera(newCameraSelector)) {
                Timber.d("Set facing to $newLensFacing")
                lensFacing = newLensFacing
                cameraSelector = newCameraSelector
                bindAllCameraUseCases()
                binding.progress.isVisible = false
                return
            }
        } catch (e: Exception) {
            Log.d("sdf", "sdfsd")
        }
        showToast(
            "This device does not have lens with facing: $newLensFacing"
        )
        binding.progress.isVisible = false
    }

    private fun setCameraPreviewToSurfaceView() {
        if (cameraProvider == null) {
            return
        }

        if (previewUseCase != null) {
            cameraProvider!!.unbind(previewUseCase)
        }

        val builder = Preview.Builder()
        previewUseCase = builder.build()
        previewUseCase!!.setSurfaceProvider(previewView.surfaceProvider)
        cameraProvider!!.bindToLifecycle(viewLifecycleOwner, cameraSelector!!, previewUseCase)

    }

    private fun bindAllCameraUseCases() {
        if (cameraProvider != null) {
            cameraProvider!!.unbindAll()
            setCameraPreviewToSurfaceView()
            bindAnalysisUseCase()
        }
    }

    private fun bindAnalysisUseCase() {
        Log.d("여긴?", "응!!!")
        if (cameraProvider == null) {
            return
        }

        if (analysisUseCase != null) {
            cameraProvider!!.unbind(analysisUseCase)
        }

        if (asanaProcessor != null) {
            asanaProcessor!!.stop()
        }

        resultSmoother.clearCache()

        try {

            asanaProcessor = AsanaProcessor(detectorOptions)
        } catch (e: Exception) {
            Timber.d("Can not create image processor", e)
            showToast("Can not create image processor: " + e.localizedMessage)
            FirebaseCrashlytics.getInstance().recordException(e)
            return
        }

        val builder = ImageAnalysis.Builder()
        analysisUseCase = builder.build()

        needUpdateGraphicOverlayImageSourceInfo = true

        analysisUseCase?.setAnalyzer(mainExecutor) { imageProxy: ImageProxy ->
            handleImageProxy(imageProxy)
        }
        binding.landmarksOverlay.clear()
        cameraProvider!!.bindToLifecycle(viewLifecycleOwner, cameraSelector!!, analysisUseCase)
    }

    private fun handleImageProxy(imageProxy: ImageProxy) {
        if (needUpdateGraphicOverlayImageSourceInfo) {
            val isImageFlipped = lensFacing == CameraSelector.LENS_FACING_FRONT
            val rotationDegrees = imageProxy.imageInfo.rotationDegrees
            if (rotationDegrees == 0 || rotationDegrees == 180) {
                landMarksOverlay.setImageSourceInfo(
                    imageProxy.width,
                    imageProxy.height,
                    isImageFlipped
                )
            } else {
                landMarksOverlay.setImageSourceInfo(
                    imageProxy.height,
                    imageProxy.width,
                    isImageFlipped

                )


            }
            needUpdateGraphicOverlayImageSourceInfo = false
        }
        try {

            asanaProcessor!!.processImageProxy(
                imageProxy,
                landMarksOverlay,
                onInference = ::onInference,
                isLoading = ::isLoadingCallback
            )

        } catch (e: Exception) {
            Timber.d("Failed to process image. Error: " + e.localizedMessage)
            showToast(e.localizedMessage)
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    private fun isLoadingCallback(isLoading: Boolean) {
        lifecycleScope.launch(Dispatchers.Main) {
            binding.progress.isVisible = isLoading
        }
    }

    private fun onInference(poseWithAsanaClassification: PoseWithAsanaClassification) {
        resultSmoother.setInferredPose(poseWithAsanaClassification)
        setInferenceUi(poseWithAsanaClassification)

    }

    fun test() {
        Log.d("왔니", "응!!")
        analysisUseCase?.setAnalyzer(mainExecutor) { imageProxy: ImageProxy ->
            handleImageProxy(imageProxy)
            asanaProcessor!!.processImageProxy(
                imageProxy,
                landMarksOverlay,
                onInference = ::onInference,
                isLoading = ::isLoadingCallback
            )
        }

    }


    fun setInferenceUi(poseWithAsanaClassification: PoseWithAsanaClassification) {
        val typeToConfidences = poseWithAsanaClassification.pose.allPoseLandmarks.map {
            Pair(it.landmarkType, it.inFrameLikelihood)
        }

        val isNotConfident = typeToConfidences.isEmpty() || typeToConfidences.any {
            detectorOptions.isImportantLandMark(it.first)
                    && it.second < DetectorOptions.LANDMARK_CONF_THRESHOLD
        }

        //Log.d("여기니",isNotConfident.toString())->true

        if (isNotConfident) {
            binding.tvInference.text = getString(R.string.unknown)
            binding.tvNotConfidentMessage.alpha = 1f
            return
        }


        binding.tvNotConfidentMessage.alpha = 0f

        val result = resultSmoother.getMajorityPose()

        val string = buildString {
            if (detectorOptions.shouldShowConfidence()) {
                append("${result.confidence.toInt()}% - ")
            }
            append(result.asanaClass.getFormattedString())
        }

        //Log.d("result",result.toString())classification(asanaClass= AsanaClass.adho_mukha_svanasana, confidence=99.99905)
        //자세 결과를 텍스트로 변환해서 붙여준다

        binding.tvInference.text = string

        //자세 이름 얘기해줘
        ttsSpeechManager.speakAsana(result.asanaClass)

//         test(result.asanaClass)

        if (result.asanaClass == AsanaClass.adho_mukha_svanasana) {
            asanaProcessor?.stop()
            countDown("0000100",AsanaClass.bidalasana)
            Log.d("work","work1")

        }
        if (result.asanaClass==AsanaClass.bidalasana){
            asanaProcessor?.stop()
            countDown("0000100",AsanaClass.bhujangasana)
            Log.d("work","여긴 왜 안와?")
        }
        if (result.asanaClass==AsanaClass.bhujangasana){
            asanaProcessor?.stop()
            countDown("0000100",AsanaClass.ustrasana)
            Log.d("work","여긴 왜 안와?")
        }

//        analysisUseCase?.setAnalyzer(mainExecutor) { imageProxy: ImageProxy ->
//            handleImageProxy(imageProxy)
//            asanaProcessor!!.processImageProxy(
//                imageProxy,
//                landMarksOverlay,
//                onInference = ::onInference,
//                isLoading = ::isLoadingCallback
//            )
//            Log.d("제발","제발")
//        }


    }

//         if(result.asanaClass==AsanaClass.ustrasana){
//
//         }




//    fun test(asanaClass:AsanaClass ){
//        if(asanaClass== AsanaClass.adho_mukha_svanasana) {
//            dowork()
//            ttsSpeechManager.speakNextAsana(AsanaClass.bidalasana)
//        }
//        if(asanaClass==AsanaClass.bidalasana){
//            dowork2()
//            ttsSpeechManager.speakNextAsana(AsanaClass.ustrasana)
//        }
//    }

 fun dowork(){


    }

 suspend fun dowork2(){
        delay(6000)
      //  countDown("0000050")
        Log.d("work","work2")

    }

    private fun works(){
        GlobalScope.launch {
            val one=dowork()
            val two=dowork2()
        }
    }




    fun countDown(time: String,asanaClass: AsanaClass) {
        var conversionTime: Long = 0


        // 1000 단위가 1초
        // 60000 단위가 1분
        // 60000 * 3600 = 1시간
        var getHour = time.substring(0, 2)
        var getMin = time.substring(2, 4)
        var getSecond = time.substring(4, 6)

        // "00"이 아니고, 첫번째 자리가 0 이면 제거
        if (getHour.substring(0, 1) === "0") {
            getHour = getHour.substring(1, 2)
        }
        if (getMin.substring(0, 1) === "0") {
            getMin = getMin.substring(1, 2)
        }
        if (getSecond.substring(0, 1) === "0") {
            getSecond = getSecond.substring(1, 2)
        }

        // 변환시간
        conversionTime =
            java.lang.Long.valueOf(getHour) * 1000 * 3600 + java.lang.Long.valueOf(getMin) * 60 * 1000 + java.lang.Long.valueOf(
                getSecond
            ) * 1000

        // 첫번쨰 인자 : 원하는 시간 (예를들어 30초면 30 x 1000(주기))
        // 두번쨰 인자 : 주기( 1000 = 1초)
        object : CountDownTimer(conversionTime, 1000) {
            // 특정 시간마다 뷰 변경
            override fun onTick(millisUntilFinished: Long) {

                // 시간단위
                var hour = (millisUntilFinished / (60 * 60 * 1000)).toString()

                // 분단위
                val getMin = millisUntilFinished - millisUntilFinished / (60 * 60 * 1000)
                var min = (getMin / (60 * 1000)).toString() // 몫

                // 초단위
                var second = (getMin % (60 * 1000) / 1000).toString() // 나머지

                // 밀리세컨드 단위
                val millis = (getMin % (60 * 1000) % 1000).toString() // 몫

                // 시간이 한자리면 0을 붙인다
                if (hour.length == 1) {
                    hour = "0$hour"
                }

                // 분이 한자리면 0을 붙인다
                if (min.length == 1) {
                    min = "0$min"
                }

                // 초가 한자리면 0을 붙인다
                if (second.length == 1) {
                    second = "0$second"
                }
                binding.textView.setText("$hour:$min:$second")

            }


            // 제한시간 종료시
            override fun onFinish() {


                // 변경 후
                binding.textView.setText("Done")

                // TODO : 타이머가 모두 종료될때 어떤 이벤트를 진행할지
                ttsSpeechManager.speakNextAsana(asanaClass)
                onResume()





            }

        }.start()
    }

}