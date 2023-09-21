package com.example.driverchecker.ui.fragments;

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.driverchecker.DriverChecker
import com.example.driverchecker.R
import com.example.driverchecker.databinding.FragmentOutputsBinding
import com.example.driverchecker.viewmodels.CameraViewModel
import com.example.driverchecker.ui.adapters.*
import com.example.driverchecker.utils.BitmapUtils
import com.example.driverchecker.utils.Page
import com.example.driverchecker.viewmodels.DisplayResultViewModel
import com.example.driverchecker.viewmodels.DisplayResultViewModelFactory
import kotlinx.coroutines.runBlocking
import java.util.*

class OutputsFragment : Fragment() {
    private lateinit var layout: View
    private var _binding: FragmentOutputsBinding? = null
    private val binding get() = _binding!!
    private val activityModel: CameraViewModel by activityViewModels()
    private val displayResultViewModel: DisplayResultViewModel by viewModels {
        DisplayResultViewModelFactory((requireActivity().application as DriverChecker).evaluationRepository)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = FragmentOutputsBinding.inflate(inflater, container, false)
        layout = binding.root
        return layout
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val evaluationId: Long? = arguments?.getLong("evaluationId")
        val indexLastPhoto = arguments?.getInt("indexLastPhoto")


        binding.finalWindowView.layoutManager = LinearLayoutManager(view.context, RecyclerView.VERTICAL, false)
        binding.finalWindowView.itemAnimator = null
        when {
            evaluationId == -1L -> {
                binding.finalWindowView.adapter = PredictionsAdapter(
                    activityModel.lastItemsList,
                    activityModel.classificationGroups.value,
                    ::navigateToStaticPhoto
                )
            }
            evaluationId == null -> {}
            evaluationId > 0L -> {
                displayResultViewModel.initPartials(evaluationId)

                displayResultViewModel.partials.observe(viewLifecycleOwner) { listPartials ->
                    if (listPartials != null)
                        binding.finalWindowView.adapter = OutputsAdapter(
                            listPartials,
                            ::navigateToStaticPhoto
                        )
                }
            }
            else -> {}
        }
    }


    private fun navigateToStaticPhoto (partialId: Long?, index: Int?) {
        val bundle = bundleOf("partialId" to (partialId ?: -1L), "indexPartial" to (index ?: -1))
        findNavController().navigate(R.id.staticPhotoFragment, bundle)
        Log.d("DisplayResultItemClick", "Item with id: $partialId has been pressed")
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}