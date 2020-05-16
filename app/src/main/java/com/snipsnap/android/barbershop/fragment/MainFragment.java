package com.snipsnap.android.barbershop.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.apollographql.apollo.ApolloClient;
import com.snipsnap.android.barbershop.databinding.FragmentMainBinding;

import org.jetbrains.annotations.NotNull;

public class MainFragment extends Fragment {
    private Button btn_login;
    private ApolloClient apolloClient;
    private FragmentMainBinding mainBinding;

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        mainBinding = FragmentMainBinding.inflate(inflater,
                container,
                false);
        // getRoot() returns parent view.
        return mainBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btn_login = mainBinding.BTNLogin;

        String myUrl ="http://ec2-18-144-86-87.us-west-1.compute.amazonaws.com:69/query";
        apolloClient = ApolloClient.builder().serverUrl(myUrl).build();
    }

    @Override
    public void onResume() {
        super.onResume();

        btn_login.setOnClickListener(l -> {
            NavDirections toLogin = MainFragmentDirections
                    .actionMainFragmentToLoginFragment();
            Navigation.findNavController(mainBinding
                    .getRoot()).navigate(toLogin);
        });
    }

    @Override
    public void onDestroyView() {
        mainBinding = null;
        super.onDestroyView();
    }
}
