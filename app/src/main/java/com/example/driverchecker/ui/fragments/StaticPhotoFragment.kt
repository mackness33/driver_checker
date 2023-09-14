package com.example.driverchecker.ui.fragments;

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.driverchecker.DriverChecker
import com.example.driverchecker.viewmodels.CameraViewModel
import com.example.driverchecker.databinding.FragmentResultBinding
import com.example.driverchecker.ui.adapters.MetricsTableAdapter
import com.example.driverchecker.ui.adapters.OutputsAdapter
import com.example.driverchecker.viewmodels.DisplayResultViewModel
import com.example.driverchecker.viewmodels.DisplayResultViewModelFactory
import com.example.driverchecker.viewmodels.LogViewModelFactory
import java.util.*

class StaticPhotoFragment : Fragment() {
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

        binding.finalResultsView.layoutManager = LinearLayoutManager(view.context, RecyclerView.VERTICAL, false)
        binding.finalResultsView.itemAnimator = null
        binding.groupTableBody.layoutManager = LinearLayoutManager(view.context, RecyclerView.VERTICAL, false)
        binding.groupTableBody.itemAnimator = null

        displayResultViewModel.partials.observe(viewLifecycleOwner) {
            if (it != null)
                binding.finalResultsView.adapter = OutputsAdapter(it)
        }

        displayResultViewModel.metricsPerGroup.observe(viewLifecycleOwner) {
            if (it != null)
                binding.groupTableBody.adapter = MetricsTableAdapter(it)
        }

        displayResultViewModel.evaluation.observe(viewLifecycleOwner) { output ->
            if (output != null) {
                binding.textResults.text = String.format("%s",
                    output.group.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
                )
                binding.textConfidence.text = String.format("%.2f", output.confidence.times(100))

                binding.editTitle.setText(output.name)
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