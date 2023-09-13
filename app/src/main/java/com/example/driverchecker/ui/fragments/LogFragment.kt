package com.example.driverchecker.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.driverchecker.DriverChecker
import com.example.driverchecker.databinding.FragmentLogBinding
import com.example.driverchecker.ui.adapters.EvaluationAdapter
import com.example.driverchecker.viewmodels.LogViewModel
import com.example.driverchecker.viewmodels.LogViewModelFactory

class LogFragment : Fragment() {
    private lateinit var layout: View
    private var _binding: FragmentLogBinding? = null
    private val binding get() = _binding!!
    private val activityViewModel: LogViewModel by activityViewModels()
    private val logViewModel: LogViewModel by viewModels {
        LogViewModelFactory((requireActivity().application as DriverChecker).evaluationRepository)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = FragmentLogBinding.inflate(inflater, container, false)
        layout = binding.root
        return layout
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        val recyclerView = binding.recyclerview
        val adapter = EvaluationAdapter(::itemListener, ::deleteListener)
        binding.recyclerview.adapter = adapter
        binding.recyclerview.layoutManager = LinearLayoutManager(requireContext())

        // Add an observer on the LiveData returned by getAlphabetizedWords.
        // The onChanged() method fires when the observed data changes and the activity is
        // in the foreground.
        logViewModel.allEvaluations.observe(viewLifecycleOwner) { evaluations ->
            // Update the cached copy of the words in the adapter.
            evaluations.let { adapter.submitList(it) }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun deleteListener(evaluationId: Int) {
        logViewModel.delete(evaluationId)
    }

    private fun itemListener(evaluationId: Int) {
//        findNavController().navigate(R.id.action_cameraFragment_to_resultFragment)
        Log.d("LogItemClick", "Item with id: $evaluationId has been pressed")
    }
}