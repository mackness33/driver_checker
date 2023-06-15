package com.example.driverchecker

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.ImageProxy
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.driverchecker.databinding.FragmentCameraBinding
import com.example.driverchecker.machinelearning.data.ImageDetectionBox
import com.example.driverchecker.machinelearning.data.MLResult
import com.example.driverchecker.machinelearning.general.local.LiveEvaluationState
import com.example.driverchecker.machinelearning.imagedetection.ImageDetectionArrayResult
import com.example.driverchecker.media.FileUtils
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.runBlocking
import java.io.ByteArrayOutputStream


//1
class CameraFragment : Fragment() {
    private lateinit var layout: View
    private var _binding: FragmentCameraBinding? = null
    private val binding get() = _binding!!
    private val model: CameraViewModel by activityViewModels()
    private val cameraXHandler: CameraXHandler = CameraXHandler()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentCameraBinding.inflate(inflater, container, false)
        layout = binding.root
        return layout
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // companion object
    companion object {
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private val REQUIRED_PERMISSIONS_CHOOSE_PHOTO =
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.INTERNET)
        private val REQUIRED_PERMISSIONS_TAKE_PHOTO =
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.INTERNET)
        private val REQUIRED_PERMISSIONS_RECORD_VIDEO =
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.INTERNET)
        private const val PICTURE_FILE_NAME: String = "driver_checker"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onClickRequestPermission(view, Manifest.permission.CAMERA)

//        val cube: Array<Int> = arrayOf(Color.GREEN, Color.GREEN, Color.GREEN, Color.GREEN)
        binding.partialsView.layoutManager = GridLayoutManager(view.context, 2, RecyclerView.HORIZONTAL, false)
        binding.partialsView.adapter = PartialsAdapter(model.list)

        val btnVideo = binding.btnRecordVideo
        val isEnabledObserver = Observer<Boolean?> { enable ->
            Log.i("LiveData", "Recoding Button is ${if (enable) "not" else ""} enabled")
            btnVideo.isEnabled = enable
        }
        model.isEnabled.observe(viewLifecycleOwner, isEnabledObserver)

        val isRecordingObserver = Observer<Boolean?> { isRecording ->
            val record = isRecording ?: false
            Log.i("LiveData", "Recoding Button ${if (record) "start" else "stop"} recording")
            btnVideo.text = getString(if (record) R.string.stop_capture else R.string.start_capture)
        }
        model.isRecording.observe(viewLifecycleOwner, isRecordingObserver)

        val btnLive = binding.btnLive
        val liveIsEnabledObserver = Observer<Boolean?> { enableLive ->
            Log.i("LiveData - LiveBtn", "Live Button is ${if (enableLive) "not" else ""} enabled")
            btnLive.isEnabled = enableLive
        }
        model.liveIsEnabled.observe(viewLifecycleOwner, liveIsEnabledObserver)

        val liveIsRecordingObserver = Observer<Boolean?> { isEvaluating ->
            val record = isEvaluating ?: false
            Log.i("LiveData - LiveBtn", "Live Button ${if (record) "start" else "stop"} recording")
            btnLive.text = getString(if (record) R.string.stop_live else R.string.start_live)
        }
        model.isEvaluating.observe(viewLifecycleOwner, liveIsRecordingObserver)

        model.lastResult.observe(viewLifecycleOwner) { partial ->
            binding.resultView.setResults(partial)
            binding.resultView.invalidate()
        }

        model.onPartialResultsChanged.observe(viewLifecycleOwner) { size ->
            if (binding.partialsView.adapter is PartialsAdapter) {
                when {
                    size < 0 ->
                        (binding.partialsView.adapter as PartialsAdapter).notifyDataSetChanged();

                    size > 0 ->
                        (binding.partialsView.adapter as PartialsAdapter).notifyItemInserted(size-1)

                    else -> {}
                }
            }
        }


        binding.btnTakePhoto.setOnClickListener {
            if (!hasPermissions(REQUIRED_PERMISSIONS_TAKE_PHOTO))
                onClickRequestPermissions(it, REQUIRED_PERMISSIONS_TAKE_PHOTO)
            else {
                cameraXHandler.takePhoto(this.requireContext(), FILENAME_FORMAT, PICTURE_FILE_NAME, model)
                cameraXHandler.pauseCamera()
                this.findNavController().navigate(R.id.action_cameraFragment_to_resultFragment)
            }
        }

        binding.btnChoosePhoto.setOnClickListener {
            if (!hasPermissions(REQUIRED_PERMISSIONS_CHOOSE_PHOTO))
                onClickRequestPermissions(it, REQUIRED_PERMISSIONS_CHOOSE_PHOTO)
            else{
                chooseImageFromGallery()
            }
        }

        binding.btnRecordVideo.setOnClickListener {
            if (!hasPermissions(REQUIRED_PERMISSIONS_RECORD_VIDEO))
                onClickRequestPermissions(it, REQUIRED_PERMISSIONS_RECORD_VIDEO)
            else{
                cameraXHandler.captureVideo(this.requireContext(), FILENAME_FORMAT, model, ::onFinalize)
            }
        }

        binding.btnLive.setOnClickListener {
            if (!hasPermissions(REQUIRED_PERMISSIONS_RECORD_VIDEO))
                onClickRequestPermissions(it, REQUIRED_PERMISSIONS_RECORD_VIDEO)
            else{
                model.updateLiveClassification()
            }
        }
    }

    // TODO: Create callback to manage onStart and onFinalize
    private fun onFinalize () : Unit {
        cameraXHandler.pauseCamera()
        this.findNavController().navigate(R.id.action_cameraFragment_to_resultFragment)
    }

    private val activityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                val data: Intent? = result.data
                if (data != null && data.data != null && data.data is Uri) {
                    val path: String? = FileUtils.getPath(data.data as Uri, this.requireActivity())

                    if (path != null) {
                        model.updateImageUri(data.data)
                        model.updatePath(path)
                        cameraXHandler.pauseCamera()
                        findNavController().navigate(R.id.action_cameraFragment_to_resultFragment)
                    }
                }
            }
        }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                // Permission is granted. Continue the action or workflow in your
                // app.
                Log.i("Permission: ", "Granted")
                runCamera()
            } else {
                // Explain to the user that the feature is unavailable because the
                // feature requires a permission that the user has denied. At the
                // same time, respect the user's decision. Don't link to system
                // settings in an effort to convince the user to change their
                // decision.
                Log.i("Permission: ", "Denied")
            }
        }

    private val requestMultiplePermissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissionsGranted ->
            for (grant in permissionsGranted) {
                if (grant.value) {
                    // Permission is granted. ContiimageDetectionService.analyzeData(bitmap, false)nue the action or workflow in your
                    // app.
                    Log.i("Permission ${grant.key}: ", "Granted")
                    if (grant.key == Manifest.permission.CAMERA) runCamera()
                } else {
                    // Explain to the user that the feature is unavailable because the
                    // feature requires a permission that the user has denied. At the
                    // same time, respect the user's decision. Don't link to system
                    // settings in an effort to convince the user to change their
                    // decision.
                    Log.i("Permission: ${grant.key}", "Denied")
                }
            }
        }

    private fun runCamera() {
        if (hasPermissions(arrayOf(Manifest.permission.CAMERA)) && !cameraXHandler.hasCameraStarted)
            cameraXHandler.startCamera(this.requireContext(), binding.viewFinder.surfaceProvider, this::analyzeImage, model)
    }

    private fun onClickRequestPermission(view: View, permission: String) {
        when {
            ContextCompat.checkSelfPermission(
                    this.requireActivity(),
                    permission
                ) == PackageManager.PERMISSION_GRANTED -> {
                runCamera()
                layout.showSnackbar(
                    view,
                    getString(R.string.permission_granted),
                    Snackbar.LENGTH_SHORT,
                    null
                ) {}
            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                this.requireActivity(),
                permission
            ) -> {
                layout.showSnackbar(
                    view,
                    getString(R.string.permission_required),
                    Snackbar.LENGTH_SHORT,
                    getString(R.string.ok)
                ) {
                    requestPermissionLauncher.launch(
                        permission
                    )
                }
            }

            else -> {
                requestPermissionLauncher.launch(
                    permission
                )
            }
        }
    }

    private fun hasPermissions(permissions: Array<String>): Boolean = permissions.all {
        ActivityCompat.checkSelfPermission(this.requireActivity(), it) == PackageManager.PERMISSION_GRANTED
    }

    private fun onClickRequestPermissions(view: View, permissions: Array<String>) {
        val ungrantedPermissions = permissions.filter { ContextCompat.checkSelfPermission(
            this.requireActivity(),
            it
        ) != PackageManager.PERMISSION_GRANTED }

        when {
            ungrantedPermissions.isEmpty() -> {
                layout.showSnackbar(
                    view,
                    getString(R.string.permission_granted),
                    Snackbar.LENGTH_SHORT,
                    null
                ) {}
            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                this.requireActivity(),
                ungrantedPermissions.first()
            ) -> {
                layout.showSnackbar(
                    view,
                    getString(R.string.permission_required),
                    Snackbar.LENGTH_SHORT,
                    getString(R.string.ok)
                ) {
                    requestMultiplePermissionsLauncher.launch(
                        ungrantedPermissions.toTypedArray()
                    )
                }
            }

            else -> {
                requestMultiplePermissionsLauncher.launch(
                    ungrantedPermissions.toTypedArray()
                )
            }
        }
    }

    private fun chooseImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/* video/*"
        activityResultLauncher.launch(intent)
    }

    private fun analyzeImage (image: ImageProxy) {
        runBlocking(Dispatchers.Default) {
            model.produceImage(image)
        }
    }

    private fun toBitmap(image: ImageProxy): Bitmap {
        val yBuffer = image.planes[0].buffer // Y
        val vuBuffer = image.planes[2].buffer // VU

        val ySize = yBuffer.remaining()
        val vuSize = vuBuffer.remaining()

        val nv21 = ByteArray(ySize + vuSize)

        yBuffer.get(nv21, 0, ySize)
        vuBuffer.get(nv21, ySize, vuSize)

        val yuvImage = YuvImage(nv21, ImageFormat.NV21, image.width, image.height, null)
        val out = ByteArrayOutputStream()
        yuvImage.compressToJpeg(Rect(0, 0, yuvImage.width, yuvImage.height), 50, out)
        val imageBytes = out.toByteArray()
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    }

}

fun View.showSnackbar(
    view: View,
    msg: String,
    length: Int,
    actionMessage: CharSequence?,
    action: (View) -> Unit
) {
    val snackbar = Snackbar.make(view, msg, length)
    if (actionMessage != null) {
        snackbar.setAction(actionMessage) {
            action(this)
        }.show()
    } else {
        snackbar.show()
    }
}
