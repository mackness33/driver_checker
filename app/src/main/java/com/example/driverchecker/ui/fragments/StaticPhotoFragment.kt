package com.example.driverchecker.ui.fragments;

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.driverchecker.DriverChecker
import com.example.driverchecker.databinding.FragmentStaticPhotoBinding
import com.example.driverchecker.machinelearning.data.IImageDetectionItem
import com.example.driverchecker.utils.BitmapUtils
import com.example.driverchecker.viewmodels.*

class StaticPhotoFragment : Fragment() {
    private lateinit var layout: View
    private var _binding: FragmentStaticPhotoBinding? = null
    private val binding get() = _binding!!
    private val activityModel: CameraViewModel by activityViewModels()
    private val staticPhotoViewModel: StaticPhotoViewModel by viewModels {
        StaticPhotoViewModelFactory((requireActivity().application as DriverChecker).evaluationRepository)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = FragmentStaticPhotoBinding.inflate(inflater, container, false)
        layout = binding.root
        return layout
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        staticPhotoViewModel.initPartialId(arguments?.getLong("partialId"))

        staticPhotoViewModel.partial.observe(viewLifecycleOwner) { path ->
            if (path != null) {
                val bitmap: Bitmap? = BitmapUtils.loadImageFromStorage(path)
                if (bitmap != null) {
//                    val resizedBitmap: Bitmap? = Bitmap.createScaledBitmap(bitmap, 640, 64, false)
                    binding.imageView.setImageBitmap(bitmap)
                }
            }
        }

        staticPhotoViewModel.items.observe(viewLifecycleOwner) { triple ->
            if (triple != null) {
//                binding.resultView.setColorSchemes(triple.second)
                binding.resultView.setResults(triple.first, triple.second)
                binding.resultView.invalidate()
            }
        }

        val indexPartial = arguments?.getInt("indexPartial")

        if (indexPartial != null && indexPartial >= 1 && indexPartial <= activityModel.lastItemsList.size){
            val output = activityModel.lastItemsList.elementAt(indexPartial-1)
//            val rotatedBitmap: Bitmap = BitmapUtils.rotateBitmap(output.input.input, -90.0f)
            binding.imageView.setImageBitmap(output.input.input)

            binding.resultView.setColorSchemes(activityModel.classificationGroups.value)
            binding.resultView.setResults(output)
            binding.resultView.invalidate()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}