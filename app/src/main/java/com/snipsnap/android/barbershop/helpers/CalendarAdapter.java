package com.snipsnap.android.barbershop.helpers;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.snipsnap.android.barbershop.R;
import com.snipsnap.android.barbershop.databinding.CardAppointmentBinding;

import java.util.List;

// CalendarAdapter.MyViewHolder necessitates nested class of MyViewHolder
// and onCreateViewHolder.
public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.MyViewHolder> {
    private List<AppointmentModel> mAppointmentList;
    private AppointmentModel appointmentModel;

    public CalendarAdapter(List<AppointmentModel> apptList) {
        mAppointmentList = apptList;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        AppointmentModel apptModel = mAppointmentList.get(position);
        holder.textView.setText(apptModel.cFirstName);
        // Create holder.setData?
    }

    @Override
    public int getItemCount() {
        return mAppointmentList.size();
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_appointment,parent, false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView textView;
        private CardAppointmentBinding cardBinding;
        // If explicitly set, change View to CardView.
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.TXTV_client_name);
            // Figure out how to make ViewBinding work in HERE!
//            textView = cardBinding.TXTVClientName;
        }
    }
}
