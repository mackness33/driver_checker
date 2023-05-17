package com.example.driverchecker;

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.example.driverchecker.databinding.FragmentResultBinding

class ResultFragment : Fragment() {
    private lateinit var layout: View
    private var _binding: FragmentResultBinding? = null
    private val binding get() = _binding!!
    private val model: CameraViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentResultBinding.inflate(inflater, container, false)
        layout = binding.root

        /* OBSERVE CHANGES ON THE RESULT*/
        val resultObserver = Observer<String> { result ->
            binding.txtResult.text = result
        }
        model.path.observe(this.requireActivity(), resultObserver)

        /* OBSERVE CHANGES ON THE URI*/
        val imageObserver = Observer<Uri?> { uri ->
            binding.imgView.setImageURI(uri)
        }
        model.imageUri.observe(this.requireActivity(), imageObserver)

        return layout
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}