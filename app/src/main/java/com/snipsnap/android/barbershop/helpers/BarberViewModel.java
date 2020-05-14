package com.snipsnap.android.barbershop.helpers;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.snipsnap.android.barbershop.GetApptByUsernameQuery;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public class BarberViewModel extends ViewModel {
    // Look into adding a repository component 'beneath' ViewMode/LiveData.
    // The repository would handle the data fetching, not the VM which does now.

    final private String TAG = "barbershop: CVM";
    final private String myUrl = "http://ec2-18-144-86-87.us-west-1.compute.amazonaws.com:69/query";
    final private ApolloClient mApolloClient = ApolloClient.builder().serverUrl(myUrl).build();

    private MutableLiveData<String> mBarberUsername = new MutableLiveData<>();
    private MutableLiveData<List<AppointmentModel>> mAppointments = new MutableLiveData<>();

    public LiveData<List<AppointmentModel>> getAppointments() {
        return mAppointments;
    }

    public void setBarberUsername(String barberName) {
        // postValue because method called within a background thread.
        mBarberUsername.postValue(barberName);
    }

    public void loadBarberAppointments() {
        fetchAppointments();
    }

    private void fetchAppointments() {
        if (mBarberUsername.getValue().isEmpty()) {
            Log.d(TAG, "mBarberName is empty.");
            return;
        }
        final GetApptByUsernameQuery getappt = GetApptByUsernameQuery.builder()
                .username(mBarberUsername.getValue())
                .build();
        mApolloClient.query(getappt)
                .enqueue(new ApolloCall.Callback<GetApptByUsernameQuery.Data>() {
                    @Override
                    public void onResponse(@NotNull Response<GetApptByUsernameQuery.Data> r) {
                        if (r.getData().getAppointmentsByUsername().isEmpty()) {
                            Log.d(TAG, "Query list is empty.");
                            return;
                        }
                        List<AppointmentModel> apptList = new ArrayList<>();
                        for (GetApptByUsernameQuery.GetAppointmentsByUsername data : r.getData().getAppointmentsByUsername()) {
                            AppointmentModel am = new AppointmentModel();
                            am.bFirstName = data.barber().firstName();
                            am.bLastName = data.barber().lastName();
                            am.shopName = data.shop().shopName();
                            am.shopAddr = data.shop().streetAddr();
                            am.apptDate = data.appointment().apptDate();
                            am.startTime = data.appointment().startTime();
                            am.endTime = data.appointment().endTime();
                            am.paymentType = data.appointment().paymentType();
                            am.clientCancelled = data.appointment().clientCancelled();
                            am.barberCancelled = data.appointment().barberCancelled();
                            am.cFirstName = data.client().firstName();
                            am.cLastName = data.client().lastName();
                            am.serviceName = data.service().serviceName();
                            am.serviceDesc = data.service().serviceDescription();
                            am.price = data.service().price();
                            am.serviceDuration = data.service().duration();
                            apptList.add(am);
                        }
                        // This HAS to be like this.
                        mAppointments.postValue(apptList);
                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {
                        Log.i(TAG, e.toString());
                    }
                });
    }
}
