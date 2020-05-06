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

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.snipsnap.android.barbershop.GetAllBarbersQuery;
import com.snipsnap.android.barbershop.databinding.FragmentMainBinding;

import org.jetbrains.annotations.NotNull;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class MainFragment extends Fragment {
    private Button btn_login;
    private Button btn_query;
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
        btn_query = mainBinding.btnQueryBarbers;

        String myUrl ="http://ec2-18-144-86-87.us-west-1.compute.amazonaws.com:69/query";
        apolloClient = ApolloClient.builder().serverUrl(myUrl).build();
    }

    @Override
    public void onResume() {
        super.onResume();
        final GetAllBarbersQuery gbq = GetAllBarbersQuery.builder().build();

        btn_query.setOnClickListener(r -> apolloClient.query(gbq).
                enqueue(new ApolloCall.Callback<GetAllBarbersQuery.Data>() {
            @Override
            public void onResponse(@NotNull Response<GetAllBarbersQuery.Data> response) {
                Log.i(TAG, response.getData().toString());
            }

            @Override
            public void onFailure(@NotNull ApolloException e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }));

        btn_login.setOnClickListener(l -> {
            NavDirections toLogin = MainFragmentDirections
                    .actionMainFragmentToLoginFragment();
            Navigation.findNavController(mainBinding
                    .getRoot()).navigate(toLogin);
        });
    }
}
