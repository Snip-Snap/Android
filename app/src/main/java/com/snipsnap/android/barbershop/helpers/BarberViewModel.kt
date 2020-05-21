package com.snipsnap.android.barbershop.helpers

import android.icu.text.SimpleDateFormat
import android.net.ParseException
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.snipsnap.android.barbershop.GetApptByUsernameQuery
import java.util.*

object ApolloClientNetwork {
    private const val URL =
        "http://ec2-18-144-86-87.us-west-1.compute.amazonaws.com:69/query"
    private lateinit var apolloClient: ApolloClient

    fun getClient(): ApolloClient {
        apolloClient = ApolloClient.builder().serverUrl(URL).build()
        return apolloClient
    }
}

data class AppointmentModel(
    val bFirstName: String,
    val bLastName: String,
    val shopName: String,
    val shopAddr: String,
    val apptDate: String,
    val startTime: String,
    val endTime: String,
    val paymentType: String,
    val clientCancelled: Boolean,
    val barberCancelled: Boolean,
    val cFirstName: String,
    val cLastName: String,
    val serviceName: String,
    val serviceDesc: String,
    val serviceDuration: Int,
    val price: Double
)

class BarberViewModel : ViewModel() {
    // Look into adding a repository component 'beneath' ViewMode/LiveData.
    // The repository would handle the data fetching, not the VM which does now.
    private val TAG = "barbershop: BVM"
    private val mBarberUsername = MutableLiveData<String>()
    private val mDate = MutableLiveData<String>()
    private val mMasterAppointments = MutableLiveData<List<AppointmentModel>?>()
    private val mDayAppointments = MutableLiveData<List<AppointmentModel>>()
    val barberFullName =
        Transformations.map(mMasterAppointments) {
//            it?.let { // This approach seems unnecessary...a nested lambda...
//                "${it[0].bFirstName} ${it[0].bLastName}"
//            }
            "${it?.get(0)?.bFirstName} ${it?.get(0)?.bLastName}"
        }

    val dayAppointments: LiveData<List<AppointmentModel>>
        get() = mDayAppointments

    fun setCalendarDate(date: String) {
        mDate.value = date
        filterAppointmentsByDate()
    }

    private fun filterAppointmentsByDate() {
        if (appointmentsAreNull()) return
        val tmpList: MutableList<AppointmentModel> = ArrayList()
        for (apptModel in mMasterAppointments.value!!) {
            // Look into parsing dates and times at the API level.
            // Don't parse data at the android level!
            val viewModelDate = parseDate(mDate.value)
            var apptDate = apptModel.apptDate
            apptDate = parseDate(apptDate)
            if (viewModelDate == apptDate) {
                tmpList.add(apptModel)
            }
        }
        mDayAppointments.value = tmpList
    }

    private fun appointmentsAreNull(): Boolean {
        if (mMasterAppointments.value == null) {
            Log.d(TAG, "mMasterAppointments is null")
            return true
        }
        return false
    }

    // REFACTOR
    fun getAppointmentByMonth(fMonth: String,
                              eMonth: String): LiveData<List<AppointmentModel>?> {
        val mTmpAppointments = MutableLiveData<List<AppointmentModel>?>()
        if (mMasterAppointments.value!!.isEmpty()) {
            Log.d(TAG, "mAppointments is empty")
            return mTmpAppointments
        }
        val tmpList: MutableList<AppointmentModel> = ArrayList()
        mTmpAppointments.value = mMasterAppointments.value
        for (apptModel in mTmpAppointments.value!!) {
            val dfMonth = parseMonth(fMonth)
            val deMonth = parseMonth(eMonth)
            val dt = apptModel.apptDate
            val selMonth = parseMonthDate(dt)
            if (selMonth.after(dfMonth) && selMonth.before(deMonth)) {
                Log.d(TAG, "apptByMonthAdded to list!")
                tmpList.add(apptModel)
            }
        }
        mTmpAppointments.value = tmpList
        return mTmpAppointments
    }

    fun setBarberUsername(barberName: String) {
        // postValue because method called within a background thread.
        mBarberUsername.postValue(barberName)
    }

    // REFACTOR parseMonth and parseDate into one function? (str ,typeOfDate)?
    //  Or, just parse at the api level.
    private fun parseMonth(dt: String): Date {
        val fmt = SimpleDateFormat("yyyy-MM-dd")
        return try {
            fmt.parse(dt)
        } catch (pe: ParseException) {
            // SKETCHY!
            Date(1111111)
        } catch (pe: java.text.ParseException) {
            Date(1111111)
        }
    }

    private fun parseDate(dt: String?): String {
        val fmt = SimpleDateFormat("yyyy-MM-dd")
        return try {
            val d = fmt.parse(dt)
            fmt.format(d)
        } catch (pe: ParseException) {
            "Date"
        } catch (pe: java.text.ParseException) {
            "Date"
        }
    }

    private fun parseMonthDate(dt: String?): Date {
        val fmt = SimpleDateFormat("yyyy-MM-dd")
        return try {
            val d = fmt.parse(dt)
            val newDate = fmt.format(d)
            fmt.parse(newDate)
        } catch (pe: ParseException) {
            Date(1111111)
        } catch (pe: java.text.ParseException) {
            Date(1111111)
        }
    }

    fun loadBarberAppointments() {
        fetchAppointments()
    }

    private fun fetchAppointments() {
        val barberUsername: String = mBarberUsername.value ?: return
        val usernameQuery = GetApptByUsernameQuery(barberUsername)
        val callback =
            object : ApolloCall.Callback<GetApptByUsernameQuery.Data>() {
                override fun onFailure(e: ApolloException) {
                    Log.d(TAG, e.message, e)
                }

                override fun onResponse(response: Response<GetApptByUsernameQuery.Data>) {
                    val apptList = mutableListOf<AppointmentModel>()
                    // REVIEW: Is this the best approach to deal with no appts?
                    //  keeping in mind the recyclerview
                    val appts = response.data?.getAppointmentsByUsername
                    if (appts == null) {
                        Log.d(TAG, "Query list is null")
                        return
                    }
                    appts.forEach {
                        val am = AppointmentModel(
                            bFirstName = it?.barber?.firstName ?: "bFName",
                            bLastName = it?.barber?.lastName ?: "bLName",
                            shopName = it?.shop?.shopName ?: "F Shop",
                            shopAddr = it?.shop?.streetAddr ?: "Street",
                            apptDate = it?.appointment?.apptDate ?: "Date",
                            startTime = it?.appointment?.startTime ?: "sTime",
                            endTime = it?.appointment?.endTime ?: "eTime",
                            paymentType = it?.appointment?.paymentType
                                ?: "payType",
                            clientCancelled = it?.appointment?.clientCancelled
                                ?: false,
                            barberCancelled = it?.appointment?.barberCancelled
                                ?: false,
                            cFirstName = it?.client?.firstName ?: "cFName",
                            cLastName = it?.client?.lastName ?: "cLName",
                            serviceName = it?.service?.serviceName ?: "Service",
                            serviceDesc = it?.service?.serviceDescription
                                ?: "Description",
                            serviceDuration = it?.service?.duration ?: 30,
                            price = it?.service?.price ?: 15.00
                        )
                        apptList.add(am)
                        mMasterAppointments.postValue(apptList)
                    }
                }
            }

        ApolloClientNetwork.getClient()
            .query(usernameQuery)
            .enqueue(callback)
    }
}
