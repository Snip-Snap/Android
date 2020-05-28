package com.snipsnap.android.barbershop.helpers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.snipsnap.android.barbershop.R
import com.snipsnap.android.barbershop.helpers.CalendarAdapter.MyViewHolder

// CalendarAdapter.MyViewHolder necessitates nested class of MyViewHolder
// and onCreateViewHolder.
class CalendarAdapter(private val mAppointmentList: List<AppointmentModel>) : RecyclerView.Adapter<MyViewHolder>() {
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val apptModel = mAppointmentList[position]
        val fullName = "${apptModel.cFirstName} ${apptModel.cLastName}"
        holder.txtvClientname.text = fullName
        holder.txtvStartTime.text = apptModel.startTime
        setServiceIcon(holder, apptModel.serviceName)
    }

    override fun getItemCount(): Int {
        return mAppointmentList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_appointment, parent, false)
        return MyViewHolder(view)
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtvClientname: TextView =
            itemView.findViewById(R.id.TXTV_client_name)
        val txtvStartTime: TextView =
            itemView.findViewById(R.id.TXTV_appt_start_time)
        val imgvServiceIcon: ImageView =
            itemView.findViewById(R.id.IMGV_service_icon)
    }

    private fun setServiceIcon(holder: MyViewHolder, serviceName: String?) {
        when (serviceName) {
            "haircut" -> holder.imgvServiceIcon.setImageResource(R.drawable.haircut_icon_512px)
            "highlight" -> holder.imgvServiceIcon.setImageResource(R.drawable.highlight_icon_512px)
            "shave" -> holder.imgvServiceIcon.setImageResource(R.drawable.shave_icon_512px)
            "facial" -> holder.imgvServiceIcon.setImageResource(R.drawable.ic_facial_mask_512px)
            "perm" -> holder.imgvServiceIcon.setImageResource(R.drawable.ic_wavy_hair_512px)
            "eyebrow threading" -> holder.imgvServiceIcon.setImageResource(R.drawable.ic_eyebrow_512px)
            "hairdye" -> holder.imgvServiceIcon.setImageResource(R.drawable.ic_haird_dye_512px)
        }
    }
}