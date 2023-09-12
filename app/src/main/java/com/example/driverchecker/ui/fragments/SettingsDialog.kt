package com.example.driverchecker.ui.fragments;

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
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
//    private var radioGroupValue: Map<Int, Int> = emptyMap()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = DialogSettingsBinding.inflate(inflater, container, false)
        layout = binding.root
        isCancelable = true
        return layout
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /*
        var radioValue = -1
        val radioGroupValueTop = binding.radioWindowFrameTop.children.map { it.id }.associateWith {
            radioValue += 2
            radioValue
        }
        val radioGroupValueBottom = binding.radioWindowFrameBottom.children.map { it.id }.associateWith {
            radioValue += 2
            radioValue
        }
        radioGroupValue = radioGroupValueTop.plus(radioGroupValueBottom)

        binding.radioWindowFrame.setOnCheckedChangeListener { group, radio ->
            group.clearCheck()
            group.check(radio)
            Log.d(TAG, "radio n. ${radioGroupValue[group.id]} has been pressed")
        }

        binding.radioWindowFrameTop.setOnCheckedChangeListener { group, _ ->
            if (binding.radioWindowFrameBottom.checkedRadioButtonId != -1)
                binding.radioWindowFrameBottom.clearCheck()
            Log.d(TAG, "radio n. ${radioGroupValue[group.id]} has been pressed")
        }

        binding.radioWindowFrameBottom.setOnCheckedChangeListener { group, _ ->
            if (binding.radioWindowFrameTop.checkedRadioButtonId != -1)
                binding.radioWindowFrameTop.clearCheck()
            Log.d(TAG, "radio n. ${radioGroupValue[group.id]} has been pressed")
        }

        binding.layoutWindowFrame.setOnCheckedChangeListener{ group, id ->
            Log.d(TAG, "radio n. ${radioGroupValue[group.id]} has been pressed")
        }

         */

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