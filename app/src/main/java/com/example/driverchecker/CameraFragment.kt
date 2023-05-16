package com.example.driverchecker;

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

//1
class CameraFragment : Fragment() {
    //2
    companion object {
        fun newInstance(): CameraFragment {
            return CameraFragment()
        }
    }

    //3
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout., container, false)
    }

}
