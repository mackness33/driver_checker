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
import com.example.driverchecker.ui.adapters.MetricsTableAdapter
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
            binding.txtResults.text = String.format("%s with %s", output?.supergroup, output?.confidence?.times(100))
        }



//        model.showResults.observe(viewLifecycleOwner) { show ->
//            Log.i("LiveData - showResults", "Evaluation ${if (show == true) "correctly ended" else "failed to end"}")
//        }

        binding.recyclerView.layoutManager = LinearLayoutManager(view.context, RecyclerView.VERTICAL, false)
        binding.recyclerView.itemAnimator = null
        binding.recyclerView.adapter = PredictionsAdapter(model.evaluatedItemsList)

        binding.recyclerView.layoutManager = LinearLayoutManager(view.context, RecyclerView.VERTICAL, false)
        binding.recyclerView.itemAnimator = null
        binding.recyclerView.adapter = MetricsTableAdapter(model.metricsPerGroup)

        model.setActualPage (Page.Result)
        model.resetShown()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}