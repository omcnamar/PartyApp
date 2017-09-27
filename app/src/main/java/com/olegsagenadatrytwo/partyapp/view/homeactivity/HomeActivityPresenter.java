package com.olegsagenadatrytwo.partyapp.view.homeactivity;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.olegsagenadatrytwo.partyapp.eventbus.LocalEvent;
import com.olegsagenadatrytwo.partyapp.model.custompojos.Party;
import com.olegsagenadatrytwo.partyapp.model.eventbrite.Event;
import com.olegsagenadatrytwo.partyapp.model.eventbrite.EventbriteEvents;
import com.olegsagenadatrytwo.partyapp.model.geocoding_profile.GeocodingProfile;
import com.olegsagenadatrytwo.partyapp.data.remote.RetrofitHelper;
import com.olegsagenadatrytwo.partyapp.model.geocoding_profile.Result;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HomeActivityPresenter implements HomeActivityContract.presenter {

    private static final String TAG = "HomeActivityPresenter";
    HomeActivityContract.view view;
    // Query Eventbrite with this
    @NonNull
    RetrofitHelper.ApiService apiService;
    private Context context;
    // Collects subscriptions to un subscribe later
    @NonNull
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    public void attachView(HomeActivityContract.view view) {
        this.view = view;
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

    /**
     * This method will make a call to the Eventbrite Api and retrieve a list of events uses RxJava
     */
    @Override
    public void rxJavaEventbrite() {

        apiService = new RetrofitHelper().getEventBriteService();
        compositeDisposable.add(apiService.queryEventList("")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Function<EventbriteEvents, List<Event>>() {
                    @Override
                    public List<Event> apply(@NonNull EventbriteEvents eventbriteEvents) throws Exception {

                        return eventbriteEvents.getEvents();
                    }
                })
                .subscribe(new Consumer<List<Event>>() {
                    @Override
                    public void accept(final List<Event> events) throws Exception {

                        Log.d(TAG, "accept: ");
                        PartyLabSingleTon partyLabSingleTon = PartyLabSingleTon.getInstance(context);
                        List<Party> parties = convertEventsToParties(events);
                        partyLabSingleTon.setEvents(parties);
                        //add the listener to change the list when its changed
                        DatabaseReference partiesReference = FirebaseDatabase.getInstance().getReference("parties");
//                        partiesReference.addListenerForSingleValueEvent(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(DataSnapshot dataSnapshot) {
//                                //get the singleton class that will hold the event list
//                                PartyLabSingleTon partyLabSingleTon = PartyLabSingleTon.getInstance(context);
//                                List<Party> parties = convertEventsToParties(events);
//                                partyLabSingleTon.setEvents(parties);
//                                Log.d("wwwww", "onDataChange: ");
//                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                                    Party party = snapshot.getValue(Party.class);
//                                    Log.d("wwwww", "onDataChange: " + party.getPartyName());
//                                    party.setId(snapshot.getKey());
//                                    parties.add(party);
//                                }
//                                partyLabSingleTon.setEvents(parties);
//                                view.eventsLoadedUpdateUI(parties);
//                            }
//
//                            @Override
//                            public void onCancelled(DatabaseError databaseError) {
//
//                            }
//                        });
                        partiesReference.addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                final Party party = dataSnapshot.getValue(Party.class);
                                party.setId(dataSnapshot.getKey());

                                //get reference to storage
                                FirebaseStorage storage = FirebaseStorage.getInstance();
                                StorageReference storageRef = storage.getReferenceFromUrl("gs://partyapp-fc6fb.appspot.com/");

                                //download image
                                storageRef.child("images/" + party.getId() + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        party.setImageURL(uri.toString());
                                        PartyLabSingleTon partyLabSingleTon = PartyLabSingleTon.getInstance(context);
                                        List<Party> parties = partyLabSingleTon.getEvents();
                                        parties.add(party);
                                        partyLabSingleTon.setEvents(parties);
                                        view.eventsLoadedUpdateUI(parties);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        // Handle any errors
                                        PartyLabSingleTon partyLabSingleTon = PartyLabSingleTon.getInstance(context);
                                        List<Party> parties = partyLabSingleTon.getEvents();
                                        parties.add(party);
                                        partyLabSingleTon.setEvents(parties);
                                        view.eventsLoadedUpdateUI(parties);
                                    }
                                });
                            }

                            @Override
                            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                                final Party party = dataSnapshot.getValue(Party.class);
                                party.setId(dataSnapshot.getKey());

                                //get reference to storage
                                FirebaseStorage storage = FirebaseStorage.getInstance();
                                StorageReference storageRef = storage.getReferenceFromUrl("gs://partyapp-fc6fb.appspot.com/");

                                //download image
                                storageRef.child("images/" + party.getId() + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        party.setImageURL(uri.toString());
                                        PartyLabSingleTon partyLabSingleTon = PartyLabSingleTon.getInstance(context);
                                        List<Party> parties = partyLabSingleTon.getEvents();
                                        int i = parties.indexOf(party);
                                        parties.set(i, party);
                                        partyLabSingleTon.setEvents(parties);
                                        view.eventsLoadedUpdateUI(parties);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {

                                        // Handle any errors
                                        PartyLabSingleTon partyLabSingleTon = PartyLabSingleTon.getInstance(context);
                                        List<Party> parties = partyLabSingleTon.getEvents();
                                        int i = parties.indexOf(party);
                                        parties.set(i, party);
                                        partyLabSingleTon.setEvents(parties);
                                        view.eventsLoadedUpdateUI(parties);
                                    }
                                });
                            }

                            @Override
                            public void onChildRemoved(DataSnapshot dataSnapshot) {
                                final Party party = dataSnapshot.getValue(Party.class);
                                party.setId(dataSnapshot.getKey());
                                PartyLabSingleTon partyLabSingleTon = PartyLabSingleTon.getInstance(context);
                                List<Party> parties = partyLabSingleTon.getEvents();
                                int i = parties.indexOf(party);
                                parties.remove(i);
                                partyLabSingleTon.setEvents(parties);
                                view.eventsLoadedUpdateUI(parties);
                            }

                            @Override
                            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                                Log.d(TAG, "onChildMoved: ");
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.d(TAG, "onCancelled: ");
                            }
                        });
                    }
                }));
    }

    @Override
    public void getLocaleRetrofit(String zip) {
        apiService = new RetrofitHelper().getLocaleService();
        retrofit2.Call<GeocodingProfile> getLocale = apiService.queryGetLocale("postal_code:"+zip);
        getLocale.enqueue(new Callback<GeocodingProfile>() {
            @Override
            public void onResponse(Call<GeocodingProfile> call, Response<GeocodingProfile> response) {
                String locale;
                List<Result> results = response.body().getResults();
                if(results.size() > 0){
                    locale = results.get(0).getAddressComponents().get(1).getShortName();
                }else {
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
                if(results.size() > 0){
                    locale = results.get(0).getAddressComponents().get(2).getShortName();
                }else {
                    locale = null;
                }
                EventBus.getDefault().post(new LocalEvent(locale));
            }

            @Override
            public void onFailure(Call<GeocodingProfile> call, Throwable t) {

            }
        });
    }


    private List<Party> convertEventsToParties(List<Event> events) {

        //copy events from API into parties
        List<Party> parties = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Party p = new Party();
            p.setId(UUID.randomUUID().toString());
            p.setPartyName(events.get(i).getName().getText());
            p.setDescription(events.get(i).getDescription().getText());
            p.setStartTime(events.get(i).getStart().getUtc());
            p.setEndTime(events.get(i).getEnd().getUtc());
            p.setCapacity(events.get(i).getCapacity());
            p.setImageURL(events.get(i).getLogo().getUrl());
            p.setLiked(false);
            parties.add(p);

        }
        return parties;
    }
}
