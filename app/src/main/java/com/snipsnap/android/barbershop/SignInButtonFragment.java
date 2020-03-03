package com.snipsnap.android.barbershop;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class SignInButtonFragment extends Fragment {
    private FragmentUtil fragUtil = new FragmentUtil();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_in_button,
                container, false);

        Fragment frag_signInPage = new SignInPageFragment();
        Button btn_submit = view.findViewById(R.id.button_submit);
        FragmentManager fm = getFragmentManager();
//        FragmentManager fm = getChildFragmentManager();
        btn_submit.setOnClickListener(s -> {
            fragUtil.addFragment(frag_signInPage,fm, R.id.main);
        });
        return view;
    }
}
