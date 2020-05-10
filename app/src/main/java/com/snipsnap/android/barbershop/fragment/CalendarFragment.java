package com.snipsnap.android.barbershop.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import com.snipsnap.android.barbershop.databinding.FragmentCalendarBinding;

import org.jetbrains.annotations.NotNull;

public class CalendarFragment extends Fragment {
    private FragmentCalendarBinding calendarBinding;
    private CalendarView calendarView;
    private TextView txtv_barberName;

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        calendarBinding = FragmentCalendarBinding.inflate(inflater,
                container, false);
        return calendarBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        calendarView = calendarBinding.calendarview;
        txtv_barberName = calendarBinding.TXTVBarber;
        // This is how setText will be dynamic for each barber.
        // String array corresponding with barberID
        String bn = CalendarFragmentArgs.fromBundle(getArguments()).getToken();
        txtv_barberName.setText(bn);
    }

    @Override
    public void onResume() {
        super.onResume();
        calendarView.setOnDateChangeListener((cv, year, month, day ) -> {
            Toast toast = Toast.makeText(getContext(),
                    String.valueOf(day),
                    Toast.LENGTH_SHORT);
            toast.show();
        });
    }
}
