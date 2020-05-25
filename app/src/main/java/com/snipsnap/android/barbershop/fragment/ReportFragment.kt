package com.snipsnap.android.barbershop.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.snipsnap.android.barbershop.R
import com.snipsnap.android.barbershop.databinding.FragmentReportBinding

class ReportFragment : Fragment(), OnItemSelectedListener {
    private val TAG = "barbershop: report"
    private var tmpBinding: FragmentReportBinding? = null
    private val rBinding get() = tmpBinding!!

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        tmpBinding = FragmentReportBinding.inflate(inflater,
            container, false)
        return rBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val startMonthAdapter = getArrayAdapter()
        rBinding.spinnerMonthsStart.adapter = startMonthAdapter
        rBinding.spinnerMonthsStart.onItemSelectedListener = this
        val endMonthAdapter = getArrayAdapter()
        rBinding.spinnerMonthsEnd.adapter = endMonthAdapter
        rBinding.spinnerMonthsEnd.onItemSelectedListener = classMonthEnd()
    }

    override fun onResume() {
        super.onResume()
        rBinding.BTNMonthReport.setOnClickListener {
            val toast = Toast.makeText(requireContext(),
                "Under Construction!", Toast.LENGTH_SHORT)
            toast.show()
        }
    }

    override fun onDestroyView() {
        tmpBinding = null
        super.onDestroyView()
    }

    private fun getArrayAdapter(): ArrayAdapter<CharSequence> {
        val arrayAdapter = ArrayAdapter.createFromResource(requireContext(),
            R.array.months_array,
            android.R.layout.simple_spinner_item)
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        return arrayAdapter
    }

    inner class classMonthEnd : OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>, view: View,
                                    pos: Int, id: Long) {
            var month = parent.getItemAtPosition(pos).toString()
            month = "2020-${parseMonthString(month)}-01"
            Log.d(TAG, month)
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {}
    }

    override fun onItemSelected(parent: AdapterView<*>,
                                view: View,
                                position: Int,
                                id: Long) {
        var month = parent.getItemAtPosition(position).toString()
        month = "2020-${parseMonthString(month)}-01"
        Log.d(TAG, month)
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {}

    private fun parseMonthString(month: String): String {
        when (month) {
            "January" -> return "01"
            "February" -> return "02"
            "March" -> return "03"
            "April" -> return "04"
            "May" -> return "05"
            "June" -> return "06"
            "July" -> return "07"
            "August" -> return "08"
            "September" -> return "09"
            "October" -> return "10"
            "November" -> return "11"
            "December" -> return "12"
        }
        return "month"
    }
}