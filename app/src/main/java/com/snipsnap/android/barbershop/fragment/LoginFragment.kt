package com.snipsnap.android.barbershop.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavDirections
import androidx.navigation.Navigation
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.snipsnap.android.barbershop.LoginBarberMutation
import com.snipsnap.android.barbershop.databinding.FragmentLoginBinding
import com.snipsnap.android.barbershop.helpers.ApolloClientNetwork
import com.snipsnap.android.barbershop.helpers.BarberViewModel
import com.snipsnap.android.barbershop.type.UserLogin

class LoginFragment : Fragment() {
    private val TAG = "barbershop: login"
    private var tmpLoginBinding: FragmentLoginBinding? = null
    private val lBinding get() = tmpLoginBinding!!

    // Has to be activityViewModels because the VM is being shared
    //  among all of my fragments!
    private val barberViewModel: BarberViewModel by activityViewModels()


    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        tmpLoginBinding = FragmentLoginBinding.inflate(inflater,
            container, false)
        return lBinding.root
    }

    override fun onResume() {
        super.onResume()
        lBinding.BTNLogin.setOnClickListener {
            if (isInputNull) {
                val toast = Toast.makeText(context,
                    "Requires Username and Password", Toast.LENGTH_SHORT)
                toast.show()
                return@setOnClickListener
            }
            val username = "${lBinding.ETXTUsername.text}"
            val password = "${lBinding.ETXTPassword.text}"

            val loginMutation = LoginBarberMutation(
                UserLogin(username, password)
            )
            val callback =
                object : ApolloCall.Callback<LoginBarberMutation.Data>() {
                    override fun onFailure(e: ApolloException) {
                        Log.d(TAG, e.message, e)
                    }

                    override fun onResponse(response: Response<LoginBarberMutation.Data>) {
                        // Replace this auth logic in view model!
                        val errStr = "Authentication error."
                        val login = response.data?.login
                        if (login == null) {
                            Log.d(TAG, "login is null")
                            return
                        }
                        if (login.error.contains(errStr)) {
                            requireActivity().runOnUiThread {
                                val text = "Incorrect Username or Password"
                                val dur = Toast.LENGTH_SHORT
                                Toast.makeText(requireContext(), text, dur)
                                    .show()
                            }
                            return
                        }
                        val token: String = login.response
                        Log.d(TAG, "token: $token")
                        barberViewModel.setBarberUsername(username)
                        navigateToCalendar(token)
                    }
                }
            ApolloClientNetwork.getClient()
                .mutate(loginMutation)
                .enqueue(callback)
        }

        lBinding.TXTVSignup.setOnClickListener { navigateToSignup() }
    }

    override fun onDestroyView() {
        tmpLoginBinding = null
        super.onDestroyView()
    }

    private fun navigateToSignup() {
        val toSignup: NavDirections = LoginFragmentDirections
            .actionLoginFragmentToSignupFragment()
        Navigation.findNavController(lBinding.root)
            .navigate(toSignup)
    }

    private fun navigateToCalendar(tkn: String) {
        val toCalendar: NavDirections = LoginFragmentDirections
            .actionLoginFragmentToCalendarFragment(tkn)
        Navigation.findNavController(lBinding.root)
            .navigate(toCalendar)
    }

    private val isInputNull: Boolean
        get() = lBinding.ETXTUsername.text.toString().isEmpty() ||
            lBinding.ETXTPassword.text.toString().isEmpty()
}

