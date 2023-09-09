package com.example.driverchecker.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.driverchecker.databinding.FragmentLogBinding

class AboutFragment : Fragment() {
    private lateinit var layout: View
    private var _binding: FragmentLogBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = FragmentLogBinding.inflate(inflater, container, false)
        layout = binding.root
        return layout
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}