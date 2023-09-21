package com.example.driverchecker.ui.fragments;

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.driverchecker.DriverChecker
import com.example.driverchecker.R
import com.example.driverchecker.viewmodels.CameraViewModel
import com.example.driverchecker.databinding.FragmentResultBinding
import com.example.driverchecker.ui.adapters.MetricsTableAdapter
import com.example.driverchecker.ui.adapters.OutputsAdapter
import com.example.driverchecker.ui.adapters.WindowsAdapter
import com.example.driverchecker.viewmodels.DisplayResultViewModel
import com.example.driverchecker.viewmodels.DisplayResultViewModelFactory
import com.example.driverchecker.viewmodels.LogViewModelFactory
import java.util.*

class DisplayResultFragment : Fragment() {
    private lateinit var layout: View
    private var _binding: FragmentResultBinding? = null
    private val binding get() = _binding!!
    private val activityModel: CameraViewModel by activityViewModels()
    private val displayResultViewModel: DisplayResultViewModel by viewModels {
        DisplayResultViewModelFactory((requireActivity().application as DriverChecker).evaluationRepository)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = FragmentResultBinding.inflate(inflater, container, false)
        layout = binding.root
        return layout
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        displayResultViewModel.initEvaluationId(arguments?.getLong("evaluationId"))

        binding.finalWindowView.layoutManager = LinearLayoutManager(view.context, RecyclerView.VERTICAL, false)
        binding.finalWindowView.itemAnimator = null
//        binding.groupTableBody.layoutManager = LinearLayoutManager(view.context, RecyclerView.VERTICAL, false)
//        binding.groupTableBody.itemAnimator = null

//        displayResultViewModel.partials.observe(viewLifecycleOwner) { listPartials ->
//            if (listPartials != null)
//                binding.finalWindowView.adapter = OutputsAdapter(listPartials) { partialId ->
//                    val bundle = bundleOf("partialId" to partialId)
//                    findNavController().navigate(R.id.staticPhotoFragment, bundle)
//                    Log.d("DisplayResultItemClick", "Item with id: $partialId has been pressed")
//                }
//        }

//        displayResultViewModel.metricsPerGroup.observe(viewLifecycleOwner) {
//            if (it != null)
//                binding.groupTableBody.adapter = MetricsTableAdapter(it)
//        }

        displayResultViewModel.windowInformation.observe(viewLifecycleOwner) {
            if (it != null)
                binding.finalWindowView.adapter = WindowsAdapter(it) { _ ->
                    val bundle = bundleOf("evaluationId" to (displayResultViewModel.evaluationId ?: -1L))
                    findNavController().navigate(R.id.outputFragment, bundle)
                }
        }

        displayResultViewModel.evaluation.observe(viewLifecycleOwner) { output ->
            if (output != null) {
                binding.textMostGroup.text = String.format("%s",
                    output.supergroup.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
                )
                binding.textAvgConfidence.text = String.format("%.2f%%", output.confidence.times(100))
//                binding.textTime.text = String.format("%.2fs", output.metrics?.totalTime)

                binding.editTitle.setText(output.name)
                binding.textModelThreshold.text = String.format("%.2f%%", output.modelThreshold.times(100))
//                binding.finalWindowView.adapter = WindowsAdapter(output, activityModel.classificationGroups.value) { indexLastImage ->
//                    val bundle = bundleOf("indexLastImage" to indexLastImage)
//                    findNavController().navigate(R.id.outputFragment, bundle)
//                }
//                if (it != null)
//                    binding.finalWindowView.adapter = WindowsAdapter(it) { _ ->
//                        val bundle = bundleOf("evaluationId" to evaluationId)
//                        findNavController().navigate(R.id.outputFragment, bundle)
//                    }
            }
        }

        binding.buttonSave.text = "Update"

        binding.buttonSave.setOnClickListener { _ ->
            displayResultViewModel.update(binding.editTitle.text.toString())
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}