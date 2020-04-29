package com.snipsnap.android.barbershop.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.snipsnap.android.barbershop.R;
import com.snipsnap.android.barbershop.databinding.FragmentLoginBinding;
import com.snipsnap.android.barbershop.databinding.FragmentMainBinding;

import org.jetbrains.annotations.NotNull;

public class LoginFragment extends Fragment {
    private FragmentLoginBinding loginBinding;
    private TextView txtv_signup;

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        loginBinding = FragmentLoginBinding.inflate(inflater,
                container,
                false);
        return loginBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        txtv_signup = loginBinding.TXTVSignup;
    }

    @Override
    public void onResume() {
        super.onResume();
        txtv_signup.setOnClickListener( s -> {
            NavDirections toSignup = LoginFragmentDirections
                    .actionLoginFragmentToSignupFragment();
            Navigation.findNavController(loginBinding.getRoot())
                    .navigate(toSignup);
        });
    }
}
