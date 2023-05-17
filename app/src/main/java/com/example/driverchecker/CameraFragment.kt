package com.example.driverchecker;

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.driverchecker.databinding.FragmentCameraBinding
import com.google.android.material.snackbar.Snackbar

//1
class CameraFragment : Fragment() {
    private lateinit var layout: View
    private var _binding: FragmentCameraBinding? = null
    private val binding get() = _binding!!
    private val model: CameraViewModel by activityViewModels()

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


    private val imageRecognitionService: ImageRecognitionService = ImageRecognitionService()
    private val cameraXHandler: CameraXHandler = CameraXHandler()
    private var videoCapture: VideoCapture<Recorder>? = null
    private var recording: Recording? = null

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
        private const val PICTURE_FILE_NAME: String = "driver_checker"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onClickRequestPermission(view, Manifest.permission.CAMERA)

        binding.btnTake.setOnClickListener {
            if (!hasPermissions(REQUIRED_PERMISSIONS_TAKE_PHOTO))
                onClickRequestPermissions(it, REQUIRED_PERMISSIONS_TAKE_PHOTO)
            else {
                cameraXHandler.takePhoto(this.requireContext(), FILENAME_FORMAT, PICTURE_FILE_NAME, model)
                this.findNavController().navigate(R.id.action_cameraFragment_to_resultFragment)
            }
        }

        binding.btnChoose.setOnClickListener {
            if (!hasPermissions(REQUIRED_PERMISSIONS_CHOOSE_PHOTO))
                onClickRequestPermissions(it, REQUIRED_PERMISSIONS_CHOOSE_PHOTO)
            else{
                chooseImageGallery()
            }
        }
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
//                        model.updateResult(imageRecognitionService.awaitPrediction(path, true))
                        this.findNavController().navigate(R.id.action_cameraFragment_to_resultFragment)
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
                    // Permission is granted. Continue the action or workflow in your
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
            cameraXHandler.startCamera(this.requireContext(), binding.viewFinder.surfaceProvider)
    }

    private fun onClickRequestPermission(view: View, permission: String) {
        when {
            ContextCompat.checkSelfPermission(
                    this.requireActivity(),
                    permission
                ) == PackageManager.PERMISSION_GRANTED -> {
//                runCamera()
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

    private fun chooseImageGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        activityResultLauncher.launch(intent)
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
