package com.snipsnap.android.barbershop.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.Navigation
import com.snipsnap.android.barbershop.databinding.FragmentSignupPage1Binding

class SignupFragment : Fragment() {
    private val TAG = "barbershop: signup"
    private var tmpSignupBinding: FragmentSignupPage1Binding? = null
    private val sBinding get() = tmpSignupBinding!!

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        tmpSignupBinding = FragmentSignupPage1Binding.inflate(inflater,
            container, false)
        return sBinding.root
    }

    override fun onResume() {
        super.onResume()
        sBinding.BTNContinue.setOnClickListener {
            if (inputIsNull) {
                val toast = Toast.makeText(context,
                    "Requires Username and Password", Toast.LENGTH_SHORT)
                toast.show()
                return@setOnClickListener
            }
            // Check whether username is already taken
            navigateToPage2()
        }
    }

    override fun onDestroyView() {
        tmpSignupBinding = null
        super.onDestroyView()
    }

    private fun navigateToPage2() {
        val toSignup2 : NavDirections = SignupFragmentDirections
            .actionSignupFragmentToSignup2Fragment()
        Navigation.findNavController(sBinding.root)
            .navigate(toSignup2)
    }

    private val inputIsNull : Boolean
    get() = sBinding.ETXTUsername.text.toString().isEmpty() ||
        sBinding.ETXTPassword.text.toString().isEmpty()
}