package com.snipsnap.android.barbershop.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.snipsnap.android.barbershop.LoginBarberMutation;
import com.snipsnap.android.barbershop.databinding.FragmentLoginBinding;
import com.snipsnap.android.barbershop.helpers.BarberViewModel;
import com.snipsnap.android.barbershop.type.UserLogin;

import org.jetbrains.annotations.NotNull;


public class LoginFragment extends Fragment {
    private FragmentLoginBinding loginBinding;
    private ApolloClient apolloClient;
    private BarberViewModel mBarberViewModel;

    private TextView txtv_signup;
    private EditText etxt_username;
    private EditText etxt_password;
    private Button btn_submit;
    final private String TAG = "barbershop: LoginF";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBarberViewModel = new ViewModelProvider(requireActivity())
                .get(BarberViewModel.class);
    }

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
        btn_submit = loginBinding.BTNLogin;
        etxt_username = loginBinding.ETXTUsername;
        etxt_password = loginBinding.ETXTPassword;

        String myUrl = "http://ec2-18-144-86-87.us-west-1.compute.amazonaws.com:69/query";
        apolloClient = ApolloClient.builder().serverUrl(myUrl).build();
    }

    @Override
    public void onResume() {
        super.onResume();

        btn_submit.setOnClickListener(l -> {
            if (!isInputNull()) {
                String username = etxt_username.getText().toString();
                String password = etxt_password.getText().toString();
                // Building input for Login mutation.
                UserLogin userLogin = UserLogin.builder()
                        .username(username)
                        .password(password)
                        .build();
                // Building Login mutation.
                final LoginBarberMutation login = LoginBarberMutation.builder()
                        .input(userLogin)
                        .build();
                apolloClient.mutate(login)
                        .enqueue(new ApolloCall.Callback<LoginBarberMutation.Data>() {
                            @Override
                            public void onResponse(@NotNull Response<LoginBarberMutation.Data> r) {
                                // r.getData().login() is the field of
                                // the LoginBarber mutation.
                                if (r.getData()
                                        .login()
                                        .error()
                                        .contains("Authentication error.")) {
                                    requireActivity().runOnUiThread(() -> {
                                        Toast toast = Toast.makeText(getContext(),
                                                "Incorrect username & pw",
                                                Toast.LENGTH_SHORT);
                                        toast.show();
                                    });
                                } else {
                                    Log.i(TAG,
                                            "User found!:" + r.getData()
                                                    .login()
                                                    .response());
                                    String token = r.getData()
                                            .login()
                                            .response();
                                    mBarberViewModel.setBarberUsername(username);
                                    // Pass data to the next fragment screen!
                                    navigateToCalendar(token);
                                }
                            }

                            @Override
                            public void onFailure(@NotNull ApolloException e) {
                                Log.e(TAG, e.getMessage(), e);
                            }
                        });

            } else {
                Toast toast = Toast.makeText(getContext(),
                        "Needs username & password", Toast.LENGTH_SHORT);
                toast.show();
            }
        });

        txtv_signup.setOnClickListener(s -> {
            navigateToSignup();
        });
    }

    @Override
    public void onDestroyView() {
        loginBinding = null;
        super.onDestroyView();
    }

    private void navigateToSignup() {
        NavDirections toSignup = LoginFragmentDirections
                .actionLoginFragmentToSignupFragment();
        Navigation.findNavController(loginBinding.getRoot())
                .navigate(toSignup);
    }

    private void navigateToCalendar(String tkn) {
        NavDirections toCalendar = LoginFragmentDirections
                .actionLoginFragmentToCalendarFragment(tkn);
        Navigation.findNavController(loginBinding.getRoot())
                .navigate(toCalendar);
    }

    private boolean isInputNull() {
        return (etxt_username.getText().toString().isEmpty() ||
                etxt_password.getText().toString().isEmpty());
    }
}
