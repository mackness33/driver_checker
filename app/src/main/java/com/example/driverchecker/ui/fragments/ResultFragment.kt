package com.example.driverchecker.ui.fragments;

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.driverchecker.viewmodels.CameraViewModel
import com.example.driverchecker.databinding.FragmentResultBinding
import com.example.driverchecker.ui.adapters.MetricsTableAdapter
import com.example.driverchecker.ui.adapters.PredictionsAdapter
import com.example.driverchecker.utils.BitmapUtils
import com.example.driverchecker.utils.Page
import kotlinx.coroutines.runBlocking
import java.util.*

class ResultFragment : Fragment() {
    private lateinit var layout: View
    private var _binding: FragmentResultBinding? = null
    private val binding get() = _binding!!
    private val activityModel: CameraViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = FragmentResultBinding.inflate(inflater, container, false)
        layout = binding.root
        return layout
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activityModel.finalResult.observe(viewLifecycleOwner) { output ->
            binding.textResults.text = String.format("%s",
                output?.supergroup?.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
            )
            binding.textConfidence.text = String.format("%.2f", output?.confidence?.times(100))
        }

        activityModel.saveImages.observe(viewLifecycleOwner) { images ->
            if (images != null) {
                runBlocking {
                    val paths = BitmapUtils.saveMultipleBitmapInStorage(
                        images.map { BitmapUtils.rotateBitmap(it, -90.0f) },
                        requireContext()
                    )

                    activityModel.usePaths(paths)
                }
                binding.buttonSave.text = "Update"
            }
        }

        activityModel.awaitEndInsert.observe(viewLifecycleOwner) { evaluationId ->
            if (evaluationId != null && evaluationId > 0)
                Toast.makeText(requireContext(), "The evaluation has been correctly saved!", Toast.LENGTH_SHORT).show()
        }

        binding.finalResultsView.layoutManager = LinearLayoutManager(view.context, RecyclerView.VERTICAL, false)
        binding.finalResultsView.itemAnimator = null
        binding.finalResultsView.adapter = PredictionsAdapter(activityModel.lastItemsList, activityModel.classificationGroups.lastValue)

        binding.groupTableBody.layoutManager = LinearLayoutManager(view.context, RecyclerView.VERTICAL, false)
        binding.groupTableBody.itemAnimator = null
        binding.groupTableBody.adapter = MetricsTableAdapter(activityModel.metricsPerGroup)

        binding.buttonSave.text = "Save"

        binding.buttonSave.setOnClickListener { _ ->
            activityModel.save(binding.editTitle.text.toString())
        }

        activityModel.setActualPage (Page.Result)
        activityModel.resetShown()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}