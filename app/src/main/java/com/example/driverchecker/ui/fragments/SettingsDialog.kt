package com.example.driverchecker.ui.fragments;

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.forEach
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.driverchecker.data.EvaluationEntity
import com.example.driverchecker.databinding.DialogSettingsBinding
import com.example.driverchecker.viewmodels.CameraViewModel
import com.example.driverchecker.viewmodels.Page
import com.example.driverchecker.databinding.FragmentResultBinding
import com.example.driverchecker.ui.adapters.MetricsTableAdapter
import com.example.driverchecker.ui.adapters.PredictionsAdapter
import java.util.*

class SettingsDialog : DialogFragment() {
    private lateinit var layout: View
    private var _binding: DialogSettingsBinding? = null
    private val binding get() = _binding!!
    private val model: CameraViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = DialogSettingsBinding.inflate(inflater, container, false)
        layout = binding.root
        isCancelable = true
        return layout
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var radioButtonValue = 1
        binding.radioWindowFrame.forEach { radio ->
            radio.setOnClickListener {
                Log.d(TAG, "radio n. $radioButtonValue has been pressed")
            }

            radioButtonValue += 2
        }

        binding.buttonSave.setOnClickListener {
            Log.d(TAG, "Save button has been pressed")
        }

        binding.buttonCancel.setOnClickListener {
            Log.d(TAG, "Cancel button has been pressed")
            dismiss()
        }

        model.setActualPage (Page.Result)
        model.resetShown()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        Log.d(TAG, "Is being dismissed")
    }

    companion object {
        const val TAG = "SettingsDialog"
    }
}