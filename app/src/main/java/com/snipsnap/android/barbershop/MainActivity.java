package com.snipsnap.android.barbershop;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    private FragmentUtil fragUtil = new FragmentUtil();
    private FragmentManager fragMan = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Fragment frag_signInButton = new SignInButtonFragment();
        fragUtil.addFragment(frag_signInButton, fragMan, R.id.main);
    }
}
