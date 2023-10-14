package com.example.driverchecker.ui.fragments;

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.example.driverchecker.databinding.DialogSettingsBinding
import com.example.driverchecker.machinelearning.data.SettingsException
import com.example.driverchecker.viewmodels.CameraViewModel

class SettingsDialog : DialogFragment() {
    private lateinit var layout: View
    private var _binding: DialogSettingsBinding? = null
    private val binding get() = _binding!!
    private val activityModel: CameraViewModel by activityViewModels()
//    private var radioGroupValue: Map<Int, Int> = emptyMap()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = DialogSettingsBinding.inflate(inflater, container, false)
        layout = binding.root
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

//        binding.editWindowFrame.setText(
//            String.format("%d", activityModel.oldSettings.windowFrames)
//        )
//        binding.editWindowThreshold.setText(
//            String.format("%.2f", activityModel.oldSettings.windowThreshold)
//        )
        binding.editModelThreshold.setText(
            String.format("%.2f", null)
        )

        binding.buttonSave.setOnClickListener {
            try {
//                val possibleWindowFrame = binding.editWindowFrame.text.toString().toIntOrNull()
//                val windowFrame: Int = when {
//                    possibleWindowFrame == null -> throw SettingsException("The number of frames for the window is not valid", null)
//                    possibleWindowFrame > 11 || possibleWindowFrame < 1 -> throw SettingsException("The number of frames must be between then 1 and 11 included", null)
//                    else -> possibleWindowFrame
//                }
//
//                val possibleWindowThreshold = binding.editWindowThreshold.text.toString().toFloatOrNull()
//                val windowThreshold: Float = when {
//                    possibleWindowThreshold == null -> throw SettingsException("The threshold for the window is not valid", null)
//                    possibleWindowThreshold > 1.00f || possibleWindowThreshold < 0.00f -> throw SettingsException("The threshold for the window must be between 0.00 and 1.00", null)
//                    else -> possibleWindowThreshold
//                }

                val possibleModelThreshold = binding.editModelThreshold.text.toString().toFloatOrNull()
                val modelThreshold: Float = when {
                    possibleModelThreshold == null -> throw SettingsException("The threshold for the model is not valid", null)
                    possibleModelThreshold > 1.00f || possibleModelThreshold < 0.00f -> throw SettingsException("The threshold of the model must be between 0.00 and 1.00", null)
                    else -> possibleModelThreshold
                }

//                activityModel.saveSettings(OldSettings(1, 0.10f, modelThreshold))
                activityModel.updateModelThreshold(modelThreshold)
                Toast.makeText(requireContext(), "The new settings have been saved", Toast.LENGTH_LONG).show()
            } catch (se: SettingsException) {
                Toast.makeText(requireContext(), se.message, Toast.LENGTH_LONG).show()
            }

            Log.d(TAG, "Save button has been pressed")
        }

        binding.buttonCancel.setOnClickListener {
            Log.d(TAG, "Cancel button has been pressed")
            dismiss()
        }
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