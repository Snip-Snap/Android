package com.snipsnap.android.barbershop.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import com.snipsnap.android.barbershop.databinding.FragmentCalendarBinding;
import com.snipsnap.android.barbershop.helpers.AppointmentModel;
import com.snipsnap.android.barbershop.helpers.CalendarViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CalendarFragment extends Fragment {
    private FragmentCalendarBinding calendarBinding;
    private CalendarView mCalendar;
    private CalendarViewModel mCalendarViewModel;

    private TextView mTxtv_barberName;
    private String mBarberUsername;
    private List<AppointmentModel> apptModel;
    final private String TAG = "barbershop: calV";


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCalendarViewModel = new ViewModelProvider(requireActivity()).get(CalendarViewModel.class);
    }

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
        mCalendar = calendarBinding.calendarView;
        mTxtv_barberName = calendarBinding.TXTVBarber;
        mBarberUsername = CalendarFragmentArgs.fromBundle(getArguments()).getUsername();
        mTxtv_barberName.setText(mBarberUsername);
        getAppointments();
    }

    @Override
    public void onResume() {
        super.onResume();
        mCalendar.setOnDateChangeListener((cv, year, month, day) -> {
            Toast toast = Toast.makeText(getContext(),
                    year + "-" + month + "-" + day,
                    Toast.LENGTH_SHORT);
            toast.show();
            // Since I have one big call to get all appointments for a barber
            // I should have a control structure to handle the appointment dates.
        });
    }

    @Override
    public void onDestroyView() {
        calendarBinding = null;
        super.onDestroyView();
    }

    private void getAppointments() {
        mCalendarViewModel.setBarberUsername(mBarberUsername);
       apptModel = mCalendarViewModel.getAppointments();
//        mCalendarViewModel.getAppointments().observe(getViewLifecycleOwner(), item -> {
//            Log.i(TAG, item.toString());
//        }   );
       Log.i(TAG,apptModel.toString());
    }
}
