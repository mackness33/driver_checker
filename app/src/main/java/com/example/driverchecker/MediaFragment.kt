package com.example.driverchecker;

import android.net.Uri
import android.os.Bundle
import android.util.Log
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
import com.example.driverchecker.databinding.FragmentMediaBinding
import com.example.driverchecker.machinelearning.data.ImageDetectionArrayListOutput
import com.example.driverchecker.machinelearning.general.local.LiveEvaluationState

class MediaFragment : Fragment() {
    private lateinit var layout: View
    private var _binding: FragmentMediaBinding? = null
    private val binding get() = _binding!!
    private val model: CameraViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = FragmentMediaBinding.inflate(inflater, container, false)
        layout = binding.root
        return layout
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        /* OBSERVE CHANGES ON THE RESULT*/
        val txt = binding.txtResult
        val resultObserver = Observer<ImageDetectionArrayListOutput?> { result ->
            txt.text = if (result == null) result.toString() else "null"
            binding.resultView.setResults(result)
            binding.resultView.invalidate()
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
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                model.analysisState?.collect { state ->
                    when (state) {
                        is LiveEvaluationState.Ready -> {
                            // view gone
                            Toast.makeText(
                                context,
                                "The Repo is ${if (!state.isReady) "not" else ""} ready!",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                        is LiveEvaluationState.Start -> {
                            // show ui
                            Toast.makeText(context, "Start of flow", Toast.LENGTH_SHORT)
                                .show()
                        }
                        is LiveEvaluationState.Loading<ImageDetectionArrayListOutput> -> {
                            // show ui
//                            Toast.makeText(context, "Loading: ${state.partialResult?.result} for the ${state.index} time", Toast.LENGTH_SHORT)
//                                .show()
                            Log.d(
                                "LiveEvaluationState",
                                "Loading: ${state.partialResult} for the ${state.index} time"
                            )

                            binding.txtResult.text = state.partialResult?.toString()

                            binding.resultView.setResults(state.partialResult)
                            binding.resultView.invalidate()
                        }
                        is LiveEvaluationState.End<ImageDetectionArrayListOutput> -> {
                            // show error message
                            Toast.makeText(
                                context,
                                "End: error=${state.exception} & result=${state.result}",
                                Toast.LENGTH_SHORT
                            )
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