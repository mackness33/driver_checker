package com.example.driverchecker.ui.fragments;

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.driverchecker.R
import com.example.driverchecker.data.CameraViewModel
import com.example.driverchecker.data.Page
import com.example.driverchecker.databinding.FragmentResultBinding
import com.example.driverchecker.ui.adapters.PredictionsAdapter

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

        model.showResults.observe(viewLifecycleOwner) { show ->
            Log.i("LiveData - showResults", "Evaluation ${if (show == true) "correctly ended" else "failed to end"}")
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(view.context, RecyclerView.HORIZONTAL, false)
        binding.recyclerView.itemAnimator = null
        binding.recyclerView.adapter = PredictionsAdapter(model.evaluatedItemsList)

        model.setActualPage (Page.Result)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}