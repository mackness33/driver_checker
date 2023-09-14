package com.example.driverchecker.ui.fragments;

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.driverchecker.DriverChecker
import com.example.driverchecker.databinding.FragmentStaticPhotoBinding
import com.example.driverchecker.viewmodels.*

class StaticPhotoFragment : Fragment() {
    private lateinit var layout: View
    private var _binding: FragmentStaticPhotoBinding? = null
    private val binding get() = _binding!!
    private val activityModel: CameraViewModel by activityViewModels()
    private val staticPhotoViewModel: StaticPhotoViewModel by viewModels {
        StaticPhotoViewModelFactory((requireActivity().application as DriverChecker).evaluationRepository)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = FragmentStaticPhotoBinding.inflate(inflater, container, false)
        layout = binding.root
        return layout
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        staticPhotoViewModel.initEvaluationId(arguments?.getLong("evaluationId"))


        staticPhotoViewModel.items.observe(viewLifecycleOwner) { triple ->
            binding.resultView.setColorSchemes(triple.second)
            binding.resultView.setResults(triple.first, triple.third)
            binding.resultView.invalidate()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}