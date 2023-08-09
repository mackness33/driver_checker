package com.example.driverchecker.ui.fragments;

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.driverchecker.data.CameraViewModel
import com.example.driverchecker.ui.adapters.PartialsAdapter
import com.example.driverchecker.databinding.FragmentResultBinding

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
            binding.txtResult.text = String.format("%s with %s", output?.supergroup, output?.confidence?.times(100))
        }

        model.passengerInfo.observe(viewLifecycleOwner) { info ->
            binding.txtPassenger.text = String.format("%s:%s", info.first, info.second)
        }

        model.driverInfo.observe(viewLifecycleOwner) { info ->
            binding.txtDriver.text = String.format("%s:%s", info.first, info.second)
        }

//        binding.recyclerView.layoutManager = LinearLayoutManager(view.context, RecyclerView.HORIZONTAL, false)
//        binding.recyclerView.itemAnimator = null
//        binding.recyclerView.adapter = PartialsAdapter(model.simpleListClassesPredictions)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}