package com.olegsagenadatrytwo.partyapp.view.homeactivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.olegsagenadatrytwo.partyapp.R;
import com.olegsagenadatrytwo.partyapp.inject.view.home_activity.DaggerHomeActivityComponent;
import com.olegsagenadatrytwo.partyapp.model.eventbrite.Event;
import com.olegsagenadatrytwo.partyapp.model.eventbrite.EventbriteEvents;
import com.olegsagenadatrytwo.partyapp.view.loginactivity.LoginActivity;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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

    @OnClick({R.id.action_map, R.id.action_location, R.id.action_profile})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.action_map:
                // TODO: 9/17/17 implement the Map View
                Toast.makeText(this, "Map", Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_location:
                Toast.makeText(this, "Location", Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_profile:
                Intent loginIntent = new Intent(this, LoginActivity.class);
                startActivity(loginIntent);
                // TODO: 9/17/17 need to implement back button for profile class
                break;

        }
    }

    public void goToLocation(View view) {
    }
}
