package com.example.driverchecker;

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
        binding.txtResult.text = "DRIVER"

        binding.txtDriver.text = String.format("%s:%s", model.driverInfo.value?.first, model.driverInfo.value?.second)
        binding.txtPassenger.text = String.format("%s:%s", model.passengerInfo.value?.first, model.passengerInfo.value?.second)

        binding.recyclerView.layoutManager = LinearLayoutManager(view.context, RecyclerView.HORIZONTAL, false)
        binding.recyclerView.itemAnimator = null
        binding.recyclerView.adapter = PartialsAdapter(model.simpleListClassesPredictions)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}