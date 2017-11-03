package com.olegsagenadatrytwo.partyapp.view.homeactivity;

import android.content.Context;
import android.support.annotation.NonNull;

import com.olegsagenadatrytwo.partyapp.data.remote.FirebaseHelper;
import com.olegsagenadatrytwo.partyapp.data.remote.RetrofitHelper;
import com.olegsagenadatrytwo.partyapp.eventbus.LocalEvent;
import com.olegsagenadatrytwo.partyapp.inject.view.home_activity.DaggerHomeActivityPresenterComponent;
import com.olegsagenadatrytwo.partyapp.model.geocoding_profile.GeocodingProfile;
import com.olegsagenadatrytwo.partyapp.model.geocoding_profile.Result;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HomeActivityPresenter implements HomeActivityContract.presenter {

    private HomeActivityContract.view view;
    private RetrofitHelper.ApiService apiService;
    private Context context;
    // Collects subscriptions to un subscribe later
    @NonNull
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    @Inject
    FirebaseHelper firebaseHelper;

    public void attachView(HomeActivityContract.view view) {
        this.view = view;
        DaggerHomeActivityPresenterComponent.create().inject(this);
        // TODO: 9/22/2017 get current area
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public void removeView() {
        compositeDisposable.clear();
        this.view = null;
    }

    @Override
    public void getPartiesFromFireBase() {
        firebaseHelper.getAllParties(context);
    }

    @Override
    public void getLocaleRetrofit(String zip) {
        apiService = new RetrofitHelper().getLocaleService();
        retrofit2.Call<GeocodingProfile> getLocale = apiService.queryGetLocale("postal_code:" + zip);
        getLocale.enqueue(new Callback<GeocodingProfile>() {
            @Override
            public void onResponse(Call<GeocodingProfile> call, Response<GeocodingProfile> response) {
                String locale;
                List<Result> results = response.body().getResults();
                if (results.size() > 0) {
                    locale = results.get(0).getAddressComponents().get(1).getShortName();
                } else {
                    locale = null;
                }
                EventBus.getDefault().post(new LocalEvent(locale));
            }

            @Override
            public void onFailure(Call<GeocodingProfile> call, Throwable t) {

            }
        });
    }

    @Override
    public void getCurrentLocale(String latlng) {
        apiService = new RetrofitHelper().getLocaleService();
        retrofit2.Call<GeocodingProfile> getLocale = apiService.queryGetCurrentLocale(latlng);
        getLocale.enqueue(new Callback<GeocodingProfile>() {
            @Override
            public void onResponse(Call<GeocodingProfile> call, Response<GeocodingProfile> response) {
                String locale;
                List<Result> results = response.body().getResults();
                if (results.size() > 0) {
                    locale = results.get(0).getAddressComponents().get(2).getShortName();
                } else {
                    locale = null;
                }
                EventBus.getDefault().post(new LocalEvent(locale));
            }

            @Override
            public void onFailure(Call<GeocodingProfile> call, Throwable t) {

            }
        });
    }
}
