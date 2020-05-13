package com.snipsnap.android.barbershop.helpers;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
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
import java.util.concurrent.CompletableFuture;


public class CalendarViewModel extends ViewModel {
    // Look into integrating live data to future version of ViewModel.
    // Also, look into adding a repository component 'beneath' ViewMode/LiveData.
    // The repository would handle the data fetching, not the VM which does now.

    final private String TAG = "barbershop: CVM";
    final private String myUrl = "http://ec2-18-144-86-87.us-west-1.compute.amazonaws.com:69/query";
    final private ApolloClient mApolloClient = ApolloClient.builder().serverUrl(myUrl).build();
    private MutableLiveData<String> mBarberUsername = new MutableLiveData<>();
    private List<AppointmentModel> mApptList;
//    private MutableLiveData<List<AppointmentModel>> amld = new MutableLiveData<>();
//    private CompletableFuture<List<AppointmentModel>> mApptList;

    public List<AppointmentModel> getAppointments() {
        loadBarberAppointments();
//        CompletableFuture.anyOf(mApptList).thenAccept(r -> {
//        })
//        .exceptionally(throwable -> {
//            Log.e(TAG, "Exception loading", throwable);
//            return null;
//        });
        return mApptList;
    }

    public void setBarberUsername(String bn) {
        mBarberUsername.setValue(bn);
    }

    public LiveData<String> getBarberUsername() {
        return mBarberUsername;
    }

    private void loadBarberAppointments() {
//        mApolloClient = ApolloClient.builder().serverUrl(myUrl).build();
        final GetApptByUsernameQuery getappt = GetApptByUsernameQuery.builder()
                .username(mBarberUsername.getValue())
                .build();
        mApolloClient.query(getappt)
                .enqueue(new ApolloCall.Callback<GetApptByUsernameQuery.Data>() {
                    @Override
                    public void onResponse(@NotNull Response<GetApptByUsernameQuery.Data> r) {
                        mApptList = new ArrayList<>();
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
                            mApptList.add(am);
                            Log.i(TAG, mApptList.toString());
                        }
                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {
                        Log.i(TAG, e.toString());
                    }
                });
//        amld.setValue(mApptList);
    }

}
