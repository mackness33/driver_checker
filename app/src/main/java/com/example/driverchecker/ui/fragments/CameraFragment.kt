package com.example.driverchecker.ui.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.ImageProxy
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.driverchecker.R
import com.example.driverchecker.data.CameraViewModel
import com.example.driverchecker.databinding.FragmentCameraBinding
import com.example.driverchecker.ui.adapters.PartialsAdapter
import com.example.driverchecker.utils.CameraXHandler
import com.example.driverchecker.utils.showSnackbar
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking


//1
class CameraFragment : Fragment() {
    private lateinit var layout: View
    private var _binding: FragmentCameraBinding? = null
    private val binding get() = _binding!!
    private val model: CameraViewModel by activityViewModels()
    private val cameraXHandler: CameraXHandler = CameraXHandler()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
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

        binding.partialsView.layoutManager = GridLayoutManager(view.context, 5, RecyclerView.VERTICAL, true)
        binding.partialsView.itemAnimator = null
        binding.partialsView.adapter = PartialsAdapter(model.simpleListClassesPredictions)

        model.isEnabled.observe(viewLifecycleOwner) { enableLive ->
            Log.i("LiveData - LiveBtn", "Live Button is ${if (enableLive) "not" else ""} enabled")
            binding.btnLive.isEnabled = enableLive
        }

        model.isEvaluating.observe(viewLifecycleOwner) { isEvaluating ->
            val record = isEvaluating ?: false
            Log.i("LiveData - LiveBtn", "Live Button ${if (record) "start" else "stop"} recording")
            binding.btnLive.text =
                getString(if (record) R.string.stop_live else R.string.start_live)

//            if (record) findNavController().navigate(R.id.action_cameraFragment_to_resultFragment)
        }

        model.lastResult.observe(viewLifecycleOwner) { partial ->
            binding.resultView.setResults(partial)
            binding.resultView.invalidate()
        }

        model.passengerInfo.observe(viewLifecycleOwner) { info ->
            binding.txtPassenger.text = String.format("%s:%s", info.first, info.second)
        }

        model.driverInfo.observe(viewLifecycleOwner) { info ->
            binding.txtDriver.text = String.format("%s:%s", info.first, info.second)
        }

        model.onPartialResultsChanged.observe(viewLifecycleOwner) { size ->
            if (binding.partialsView.adapter is PartialsAdapter) {
                when {
                    size == 0 ->{
                        (binding.partialsView.adapter as PartialsAdapter).notifyDataSetChanged();
                        Log.d("LiveEvaluationState", "CLEAR: $size deleting with array: ${model.evaluatedItemsList.size}")
                    }

                    size > 0 -> {
                        (binding.partialsView.adapter as PartialsAdapter).notifyItemInserted(size-1)
                        Log.d("LiveEvaluationState", "APPEND: $size inserting with array: ${model.evaluatedItemsList.size}")
                    }

                    else -> {}
                }
            }
        }


//        binding.btnTakePhoto.setOnClickListener {
//            if (!hasPermissions(REQUIRED_PERMISSIONS_TAKE_PHOTO))
//                onClickRequestPermissions(it, REQUIRED_PERMISSIONS_TAKE_PHOTO)
//            else {
//                cameraXHandler.takePhoto(this.requireContext(), FILENAME_FORMAT, PICTURE_FILE_NAME, model)
//                cameraXHandler.pauseCamera()
//                this.findNavController().navigate(R.id.action_cameraFragment_to_resultFragment)
//            }
//        }
//
//        binding.btnChoosePhoto.setOnClickListener {
//            if (!hasPermissions(REQUIRED_PERMISSIONS_CHOOSE_PHOTO))
//                onClickRequestPermissions(it, REQUIRED_PERMISSIONS_CHOOSE_PHOTO)
//            else{
//                chooseImageFromGallery()
//            }
//        }
//
//        binding.btnRecordVideo.setOnClickListener {
//            if (!hasPermissions(REQUIRED_PERMISSIONS_RECORD_VIDEO))
//                onClickRequestPermissions(it, REQUIRED_PERMISSIONS_RECORD_VIDEO)
//            else{
//                cameraXHandler.captureVideo(this.requireContext(), FILENAME_FORMAT, model, ::onFinalize)
//            }
//        }

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

//    private val activityResultLauncher =
//        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//            if (result.resultCode == Activity.RESULT_OK) {
//                // There are no request codes
//                val data: Intent? = result.data
//                if (data != null && data.data != null && data.data is Uri) {
//                    val path: String? = FileUtils.getPath(data.data as Uri, this.requireActivity())
//
//                    if (path != null) {
//                        model.updateImageUri(data.data)
//                        model.updatePath(path)
//                        cameraXHandler.pauseCamera()
//                    }
//                }
//            }
//        }

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
            cameraXHandler.startCamera(this.requireContext(), binding.viewFinder.surfaceProvider, this::analyzeImage)
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

//    private fun chooseImageFromGallery() {
//        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//        intent.type = "image/* video/*"
//        activityResultLauncher.launch(intent)
//    }

    private fun analyzeImage (image: ImageProxy) {
        runBlocking(Dispatchers.Default) {
            model.produceImage(image)
        }
    }
}