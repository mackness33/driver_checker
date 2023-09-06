package com.example.driverchecker.ui.fragments;

import android.Manifest
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.driverchecker.R
import com.example.driverchecker.data.CameraViewModel
import com.example.driverchecker.data.Page
import com.example.driverchecker.databinding.FragmentResultBinding
import com.example.driverchecker.ui.adapters.MetricsTableAdapter
import com.example.driverchecker.ui.adapters.PredictionsAdapter
import java.util.*

class ResultFragment : Fragment() {
    private lateinit var layout: View
    private var _binding: FragmentResultBinding? = null
    private val binding get() = _binding!!
    private val model: CameraViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = FragmentResultBinding.inflate(inflater, container, false)
        layout = binding.root
        return layout
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        model.output.observe(viewLifecycleOwner) { output ->
            binding.txtResults.text = String.format("%s",
                output?.supergroup?.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
            )
            binding.txtConfidence.text = String.format("%s", output?.confidence?.times(100))
        }

        binding.finalResultsView.layoutManager = LinearLayoutManager(view.context, RecyclerView.VERTICAL, false)
        binding.finalResultsView.itemAnimator = null
        binding.finalResultsView.adapter = PredictionsAdapter(model.lastItemsList, model.classificationGroups.lastValue)

        binding.groupTableBody.layoutManager = LinearLayoutManager(view.context, RecyclerView.VERTICAL, false)
        binding.groupTableBody.itemAnimator = null
        binding.groupTableBody.adapter = MetricsTableAdapter(model.metricsPerGroup)

        model.setActualPage (Page.Result)
        model.resetShown()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showDialog(title: String) {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
//        dialog.setContentView(R.layout.custom_layout)

//        val body = dialog.findViewById(R.id.body) as TextView
//        body.text = title
//
//        val yesBtn = dialog.findViewById(R.id.yesBtn) as Button
//        yesBtn.setOnClickListener {
//            dialog.dismiss()
//        }
//
//        val noBtn = dialog.findViewById(R.id.noBtn) as Button
//        noBtn.setOnClickListener {
//            dialog.dismiss()
//        }

        dialog.show()
    }

//    private val requestMultiplePermissionsLauncher =
//        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissionsGranted ->
//            for (grant in permissionsGranted) {
//                if (grant.value) {
//                    // Permission is granted. Continue the action or workflow in your
//                    // app.
//                    Log.i("Permission ${grant.key}: ", "Granted")
//                    if (grant.key == Manifest.permission.CAMERA) runCamera()
//                } else {
//                    // Explain to the user that the feature is unavailable because the
//                    // feature requires a permission that the user has denied. At the
//                    // same time, respect the user's decision. Don't link to system
//                    // settings in an effort to convince the user to change their
//                    // decision.
//                    Log.i("Permission: ${grant.key}", "Denied")
//                }
//            }
//        }

//    private val requestToSaveResult = registerOnBack
}