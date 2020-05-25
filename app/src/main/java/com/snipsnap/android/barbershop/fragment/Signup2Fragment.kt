package com.snipsnap.android.barbershop.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.snipsnap.android.barbershop.databinding.FragmentSignupPage2Binding

class Signup2Fragment : Fragment() {
    private val TAG = "barbershop: signup2"
    private var tmpSignup2Binding: FragmentSignupPage2Binding? = null
    private val s2Binding get() = tmpSignup2Binding!!

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        tmpSignup2Binding = FragmentSignupPage2Binding.inflate(inflater,
        container, false)
        return s2Binding.root
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onDestroyView() {
        tmpSignup2Binding = null
        super.onDestroyView()
    }
}