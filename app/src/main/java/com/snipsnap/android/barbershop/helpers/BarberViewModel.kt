package com.snipsnap.android.barbershop.helpers

import android.icu.text.SimpleDateFormat
import android.net.ParseException
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.snipsnap.android.barbershop.GetApptByUsernameQuery
import com.snipsnap.android.barbershop.GetBarberByUsernameQuery
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

data class BarberModel(
    val barberID: String,
    val shopID: Int,
    val username: String,
    val firstName: String,
    val lastName: String,
    val phoneNumber: String,
    val gender: String?,
    val dob: String,
    val hireDate: String,
    val dismissDate: String?,
    val seatNum: Int
)

// Look into adding a repository component 'beneath' ViewMode/LiveData.
// The repository would handle the data fetching, not the VM which does now.
class BarberViewModel : ViewModel() {
    private val TAG = "barbershop: BVM"
    private val mBarberUsername = MutableLiveData<String>()
    private val mMasterAppointments = MutableLiveData<List<AppointmentModel>>()
    private val mDayAppointments = MutableLiveData<List<AppointmentModel>>()
    private val mBarber = MutableLiveData<BarberModel>()

    fun fetchBarber() {
        val barberUsername: String = mBarberUsername.value ?: return
        val barberQuery = GetBarberByUsernameQuery(barberUsername)
        val callback =
            object : ApolloCall.Callback<GetBarberByUsernameQuery.Data>() {
                override fun onFailure(e: ApolloException) {
                    Log.d(TAG, e.message, e)
                }

                override fun onResponse(response: Response<GetBarberByUsernameQuery.Data>) {
                    val barber = response.data?.getBarberByUsername
                    if (barber == null) {
                        Log.d(TAG, "Barber is null")
                        return
                    }
                    val bm = BarberModel(
                        barberID = barber.barberID,
                        shopID = barber.shopID,
                        username = barber.userName,
                        firstName = barber.firstName,
                        lastName = barber.lastName,
                        phoneNumber = barber.phoneNumber,
                        gender = barber.gender ?: "U",
                        dob = barber.dob,
                        hireDate = barber.hireDate,
                        dismissDate = barber.dismissDate ?: "U",
                        seatNum = barber.seatNum
                    )
                    mBarber.postValue(bm)
                }
            }
        ApolloClientNetwork.getClient()
            .query(barberQuery)
            .enqueue(callback)
    }

    fun fetchAppointments() {
        val barberUsername: String = mBarberUsername.value ?: return
        val apptQuery = GetApptByUsernameQuery(barberUsername)
        val callback =
            object : ApolloCall.Callback<GetApptByUsernameQuery.Data>() {
                override fun onFailure(e: ApolloException) {
                    Log.d(TAG, e.message, e)
                }

                override fun onResponse(response: Response<GetApptByUsernameQuery.Data>) {
                    // REVIEW: Is this the best approach to deal with no appts?
                    //  keeping in mind the recyclerview
                    val appts = response.data?.getAppointmentsByUsername
                    if (appts == null) {
                        Log.d(TAG, "Query list is null")
                        return
                    }
                    val apptList = mutableListOf<AppointmentModel>()
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
                    }
                    mMasterAppointments.postValue(apptList)
                }
            }

        ApolloClientNetwork.getClient()
            .query(apptQuery)
            .enqueue(callback)
    }

    private fun filterAppointmentsByDate(date: String) {
        val list: MutableList<AppointmentModel> = ArrayList()
        // Figure out how to get rid of !!.
        val appts: List<AppointmentModel>? = mMasterAppointments.value
        if (appts == null) {
            Log.d(TAG, "mMasterAppointments.value is null")
            return
        }
        appts.forEach {
            if (date == it.apptDate) list.add(it)
        }
        mDayAppointments.value = list
    }

    fun setCalendarDate(d: String) {
        val date: String = parseDate(d)
        filterAppointmentsByDate(date)
    }

    private fun parseDate(dt: String?): String {
        val fmt = SimpleDateFormat("yyyy-MM-dd")
        return try {
            val d = fmt.parse(dt)
            fmt.format(d)
        } catch (pe: ParseException) {
            "Date"
        }
    }

    fun setBarberUsername(barberName: String) {
        // postValue because method called within a background thread.
        mBarberUsername.postValue(barberName)
    }

    val currentBarber: LiveData<BarberModel>
        get() = mBarber

    val dayAppointments: LiveData<List<AppointmentModel>>
        get() = mDayAppointments

}
