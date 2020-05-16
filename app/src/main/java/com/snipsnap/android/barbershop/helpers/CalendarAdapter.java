package com.snipsnap.android.barbershop.helpers;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
        String fullName = apptModel.cFirstName;
        fullName = fullName.concat(" " + apptModel.cLastName);
        holder.txtv_clientName.setText(fullName);
        holder.txtv_service.setText(apptModel.serviceName);
        holder.txtv_startTime.setText(apptModel.startTime);
        setServiceIcon(holder, apptModel.serviceName);
        // Create holder.setData?
    }

    private void setServiceIcon(MyViewHolder holder, String serviceName) {
        switch (serviceName) {
            case "haircut":
                holder.imgv_serviceIcon.setImageResource(R.drawable.haircut_icon_512px);
                break;
            case "highlight":
                holder.imgv_serviceIcon.setImageResource(R.drawable.highlight_icon_512px);
                break;
            case "shave":
                holder.imgv_serviceIcon.setImageResource(R.drawable.shave_icon_512px);
            default:
                holder.imgv_serviceIcon.setImageResource(R.drawable.icons8_facial_mask_64);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mAppointmentList.size();
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_appointment,parent, false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView txtv_clientName;
        private TextView txtv_service;
        private TextView txtv_startTime;
        private ImageView imgv_serviceIcon;
        private CardAppointmentBinding cardBinding;
        // If explicitly set, change View to CardView.
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtv_clientName = itemView.findViewById(R.id.TXTV_client_name);
            txtv_service = itemView.findViewById(R.id.TXTV_service_done);
            txtv_startTime = itemView.findViewById(R.id.TXTV_appt_start_time);
            imgv_serviceIcon = itemView.findViewById(R.id.IMGV_service_icon);
            // Figure out how to make ViewBinding work in HERE!
//            textView = cardBinding.TXTVClientName;
        }
    }
}
