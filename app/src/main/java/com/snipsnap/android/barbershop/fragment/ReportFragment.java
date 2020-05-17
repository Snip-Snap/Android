package com.snipsnap.android.barbershop.fragment;

import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.android.material.textview.MaterialTextView;
import com.snipsnap.android.barbershop.R;
import com.snipsnap.android.barbershop.databinding.FragmentReportBinding;
import com.snipsnap.android.barbershop.helpers.BarberViewModel;

import org.jetbrains.annotations.NotNull;

public class ReportFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    private FragmentReportBinding mReportBinding;
    private BarberViewModel mBarberViewModel;
    private MaterialTextView mTxtv_greeting;
    private Spinner mSpinner;
    final private String TAG = "barber: RF";

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
        mReportBinding = FragmentReportBinding.inflate(inflater,
                container, false);
        return mReportBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mTxtv_greeting = mReportBinding.TXTVGreeting;
        mSpinner = mReportBinding.spinnerMonths;
        ArrayAdapter<CharSequence> adapter = ArrayAdapter
                .createFromResource(requireContext(),
                        R.array.months_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(adapter);
        mSpinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onDestroyView() {
        mReportBinding = null;
        super.onDestroyView();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String sel_month = parent.getItemAtPosition(position).toString();
        Log.d(TAG, sel_month);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
