package com.example.driverchecker;

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.driverchecker.databinding.FragmentResultBinding
import com.example.driverchecker.machinelearning.data.MLResult
import com.example.driverchecker.machinelearning.general.local.LiveEvaluationState

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
        /* OBSERVE CHANGES ON THE RESULT*/
        val txt = binding.txtResult
        val resultObserver = Observer<String> { result ->
            txt.text = result
        }
        model.result.observe(viewLifecycleOwner, resultObserver)

        /* OBSERVE CHANGES ON THE URI*/
        val img = binding.imgView
        val imageObserver = Observer<Uri?> { uri ->
            img.setImageURI(uri)
        }
        model.imageUri.observe(viewLifecycleOwner, imageObserver)

        var i = 0
        lifecycleScope.launchWhenCreated {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                model.analysisState?.collect { state ->
                    when (state) {
                        is LiveEvaluationState.Ready -> {
                            // view gone
                            Toast.makeText(context, "The Repo is ${if (!state.isReady) "not" else ""} ready!", Toast.LENGTH_SHORT)
                                .show()
                        }
                        is LiveEvaluationState.Start -> {
                            // show ui
                            Toast.makeText(context, "Start of flow", Toast.LENGTH_SHORT)
                                .show()
                        }
                        is LiveEvaluationState.Loading<MLResult<Float>> -> {
                            // show ui
                            i++
                            Toast.makeText(context, "Loading: ${state.partialResult?.result} for the $i time", Toast.LENGTH_SHORT)
                                .show()
                        }
                        is LiveEvaluationState.End<MLResult<Float>> -> {
                            // show error message
                            Toast.makeText(context, "End: error=${state.exception} & result=${state.result}", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
            }
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}