package com.snipsnap.android.barbershop.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.snipsnap.android.barbershop.databinding.FragmentMainBinding

class MainFragment : Fragment() {
    private var tmpBinding: FragmentMainBinding? = null
    private val mBinding get() = tmpBinding!!

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        tmpBinding = FragmentMainBinding.inflate(inflater,
            container,
            false)
        return mBinding.root
    }

    override fun onResume() {
        super.onResume()
        mBinding.BTNLogin.setOnClickListener {
            navigateToLogin()
        }
    }

    override fun onDestroyView() {
        tmpBinding = null
        super.onDestroyView()
    }

    private fun navigateToLogin() {
        val toLogin = MainFragmentDirections
            .actionMainFragmentToLoginFragment()
        Navigation.findNavController(mBinding
            .root).navigate(toLogin)
    }
}