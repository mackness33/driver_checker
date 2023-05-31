package com.example.driverchecker

import android.Manifest
import android.app.Activity
import android.content.Context
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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.ImageProxy
import androidx.camera.video.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.driverchecker.databinding.FragmentCameraBinding
import com.google.android.material.snackbar.Snackbar
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*


//1
class CameraFragment : Fragment() {
    private lateinit var layout: View
    private var _binding: FragmentCameraBinding? = null
    private val binding get() = _binding!!
    private val model: CameraViewModel by activityViewModels()

//    private val model: CameraViewModel by viewModels(
//        ownerProducer = { requireParentFragment() }
//    )
    private val cameraXHandler: CameraXHandler = CameraXHandler()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentCameraBinding.inflate(inflater, container, false)
        layout = binding.root
//        model.setImageDetectionRepository(FileUtils.assetFilePath(this.requireContext(), "coco_detection_lite.ptl"), "somePath")
        return layout
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // companion object
    companion object {
        fun newInstance(): CameraFragment {
            return CameraFragment()
        }
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

        val txt = binding.txtResult
        val resultObserver = Observer<String?> { result ->
            Log.i("LiveData", result)
            txt.text = result ?: "is null"
        }
        model.frame.observe(this.requireActivity(), resultObserver)

        val btn = binding.btnRecordVideo
        val isEnabledObserver = Observer<Boolean?> { enable ->
            Log.i("LiveData", "Recoding Button is ${if (enable) "not" else ""} enabled")
            btn.isEnabled = enable
        }
        model.isEnabled.observe(this.requireActivity(), isEnabledObserver)

        val isRecordingObserver = Observer<Boolean?> { isRecording ->
            val record = isRecording ?: false
            Log.i("LiveData", "Recoding Button ${if (record) "start" else "stop"} recording")
            btn.text = getString(if (record) R.string.stop_capture else R.string.start_capture)
        }
        model.isRecording.observe(this.requireActivity(), isRecordingObserver)

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
    
    /**
     * Copies specified asset to the file in /files app directory and returns this file absolute path.
     *
     * @return absolute file path
     */
    @Throws(IOException::class)
    private fun assetFilePath(context: Context, assetName: String?): String {
        if (assetName == null) return ""

        val file = File(context.filesDir, assetName)
        if (file.exists() && file.length() > 0) {
            return file.absolutePath
        }
        context.assets.open(assetName).use { `is` ->
            FileOutputStream(file).use { os ->
                val buffer = ByteArray(4 * 1024)
                var read: Int
                while (`is`.read(buffer).also { read = it } != -1) {
                    os.write(buffer, 0, read)
                }
                os.flush()
            }
            return file.absolutePath
        }
    }

    private fun analyzeImage (image: ImageProxy) {
        val bitmap = toBitmap(image)
//        bitmap.recycle()
        // Do image analysis here if you need bitmap
        model.nextFrame(bitmap)
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
