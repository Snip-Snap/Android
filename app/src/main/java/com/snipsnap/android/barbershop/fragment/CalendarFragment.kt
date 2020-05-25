package com.snipsnap.android.barbershop.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.snipsnap.android.barbershop.databinding.FragmentCalendarBinding
import com.snipsnap.android.barbershop.helpers.BarberViewModel
import com.snipsnap.android.barbershop.helpers.CalendarAdapter

class CalendarFragment : Fragment() {
    private val TAG = "barbershop: calendar"
    private var tmpBinding: FragmentCalendarBinding? = null
    private val cBinding get() = tmpBinding!!
    private val barberViewModel: BarberViewModel by activityViewModels()

    private lateinit var adapter: RecyclerView.Adapter<*>
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var lifecycleOwner: LifecycleOwner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        barberViewModel.fetchBarber()
        barberViewModel.fetchAppointments()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        tmpBinding = FragmentCalendarBinding.inflate(inflater,
            container, false)
        lifecycleOwner = viewLifecycleOwner
        return cBinding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cBinding.BTNReport.visibility = View.INVISIBLE
        cBinding.barberRecyclerView.setHasFixedSize(true)
        layoutManager = LinearLayoutManager(requireActivity())
        cBinding.barberRecyclerView.layoutManager = layoutManager
        setCalendarObserver()
        setBarberNameObserver()
    }

    override fun onResume() {
        super.onResume()
        cBinding.calendarView.setOnDateChangeListener { _: CalendarView?, year: Int, month: Int, day: Int ->
            val mon = month + 1
            val date = "$year-$mon-$day"
            barberViewModel.setCalendarDate(date)
        }
        cBinding.BTNReport.setOnClickListener {
            val toast = Toast.makeText(requireContext(),
                "Under Construction!", Toast.LENGTH_SHORT)
            toast.show()
        }
    }

    override fun onDestroyView() {
        tmpBinding = null
        super.onDestroyView()
    }

    private fun setCalendarObserver() {
        barberViewModel.dayAppointments.observe(lifecycleOwner, Observer {
            adapter = CalendarAdapter(it)
            cBinding.barberRecyclerView.adapter = adapter
            if (it.isEmpty()) {
                cBinding.BTNReport.visibility = View.INVISIBLE
            } else {
                cBinding.BTNReport.visibility = View.VISIBLE
            }
        })
    }

    private fun setBarberNameObserver() {
        barberViewModel.currentBarber.observe(lifecycleOwner,
            Observer {
                val fullName = "${it.firstName} ${it.lastName}"
                cBinding.TXTVBarber.text = fullName
            })
    }
}