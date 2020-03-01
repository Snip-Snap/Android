package com.snipsnap.android.barbershop;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private Button btn_signIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_signIn = findViewById(R.id.go_to_login_page);
    }

    @Override
    protected void onResume() {
        super.onResume();
        FragmentTransaction frag_transaction = getSupportFragmentManager()
                .beginTransaction();
        final Intent intent_signin = new Intent(this, SignInFragment.class);
        btn_signIn.setOnClickListener(s -> {
            Fragment frag_signin = new SignInFragment();
            frag_transaction.add(R.id.main, frag_signin)
                    .addToBackStack(null)
                    .commit();
        });
    }
}
