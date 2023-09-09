package com.example.driverchecker.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.driverchecker.DriverChecker
import com.example.driverchecker.R
import com.example.driverchecker.data.TestEntity
import com.example.driverchecker.databinding.FragmentLogBinding
import com.example.driverchecker.ui.adapters.TestAdapter
import com.example.driverchecker.viewmodels.LogViewModel
import com.example.driverchecker.viewmodels.LogViewModelFactory
import com.google.android.material.floatingactionbutton.FloatingActionButton

class LogFragment : Fragment() {
    private lateinit var layout: View
    private var _binding: FragmentLogBinding? = null
    private val binding get() = _binding!!
    private val logViewModel: LogViewModel by viewModels {
        LogViewModelFactory((requireActivity().application as DriverChecker).testRepository)
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
        val adapter = TestAdapter()
        binding.recyclerview.adapter = adapter
        binding.recyclerview.layoutManager = LinearLayoutManager(requireContext())

        // Add an observer on the LiveData returned by getAlphabetizedWords.
        // The onChanged() method fires when the observed data changes and the activity is
        // in the foreground.
        logViewModel.allWords.observe(viewLifecycleOwner) { tests ->
            // Update the cached copy of the words in the adapter.
            tests.let { adapter.submitList(it) }
        }

        val button = binding.buttonSave
        button.setOnClickListener {
            if(!TextUtils.isEmpty(binding.editWord.text)) {
                logViewModel.insert(TestEntity(binding.editWord.text.toString()))
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, intentData: Intent?) {
//        super.onActivityResult(requestCode, resultCode, intentData)
//
//        if (requestCode == newWordActivityRequestCode && resultCode == Activity.RESULT_OK) {
//            intentData?.getStringExtra(NewWordActivity.EXTRA_REPLY)?.let { reply ->
//                val word = Word(reply)
//                wordViewModel.insert(word)
//            }
//        } else {
//            Toast.makeText(
//                applicationContext,
//                R.string.empty_not_saved,
//                Toast.LENGTH_LONG
//            ).show()
//        }
//    }


    companion object {
        const val EXTRA_REPLY = "com.example.driverchecker.REPLY"
    }

}