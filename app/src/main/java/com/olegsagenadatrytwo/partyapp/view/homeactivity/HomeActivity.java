package com.olegsagenadatrytwo.partyapp.view.homeactivity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.olegsagenadatrytwo.partyapp.R;
import com.olegsagenadatrytwo.partyapp.inject.home_activity.DaggerHomeActivityComponent;
import com.olegsagenadatrytwo.partyapp.model.eventbrite.Event;
import com.olegsagenadatrytwo.partyapp.model.eventbrite.EventbriteEvents;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity implements HomeActivityContract.view {

    private static final String TAG = "HomeActivity";

    @BindView(R.id.party_view_pager)
    ViewPager viewPager;

    @Inject HomeActivityPresenter presenter;

    private static final String PARTY_ID = "party_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        DaggerHomeActivityComponent.create().inject(this);
        presenter.attachView(this);
        presenter.setContext(this);
        presenter.fetchEventbriteEvents();

    }

    @Override
    protected void onDestroy() {
        presenter.removeView();
        super.onDestroy();
    }

    @Override
    public void eventsLoadedUpdateUI(final EventbriteEvents events) {
        //get the id of the 0th event so that will be the first item on the screen
        String id = events.getEvents().get(0).getId();

        //set Adapter for ViewPager
        viewPager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            //each view of the view pager is an Instance of the PartyFragment
            @Override
            public Fragment getItem(int position) {
                //get the Event from the list of the events
                Event event = events.getEvents().get(position);
                //return an instance of the PartyFragment which is initialized with with the id of the event
                return PartyFragment.newInstance(event.getId());
            }

            @Override
            public int getCount() {
                return events.getEvents().size();
            }
        });

    }
}
