package com.olegsagenadatrytwo.partyapp.view.homeactivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.olegsagenadatrytwo.partyapp.R;
import com.olegsagenadatrytwo.partyapp.inject.home_activity.DaggerHomeActivityComponent;
import com.olegsagenadatrytwo.partyapp.model.custompojos.Party;
import com.olegsagenadatrytwo.partyapp.view.addpartyactivity.AddPartyActivity;
import com.olegsagenadatrytwo.partyapp.view.loginactivity.LoginActivity;

import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity implements HomeActivityContract.view {

    private static final String TAG = "HomeActivity";
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.party_view_pager)
    ViewPager viewPager;

    @Inject HomeActivityPresenter presenter;

    private static final String PARTY_ID = "party_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        DaggerHomeActivityComponent.create().inject(this);
        presenter.attachView(this);
        presenter.setContext(this);
        presenter.fetchEventbriteEvents();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_actions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_profile:
                Intent loginIntent = new Intent(this, LoginActivity.class);
                startActivity(loginIntent);
                return true;
            case R.id.action_add_party:
                //if there is no current user send the user to log in
                if(FirebaseAuth.getInstance().getCurrentUser() != null) {
                    Intent addPartyIntent = new Intent(this, AddPartyActivity.class);
                    startActivity(addPartyIntent);
                }else{
                    Intent logInIntent = new Intent(this, LoginActivity.class);
                    startActivity(logInIntent);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {
        presenter.removeView();
        super.onDestroy();
    }

    @Override
    public void eventsLoadedUpdateUI(final List<Party> parties) {

        final FragmentStatePagerAdapter adapter = new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            //each view of the view pager is an Instance of the PartyFragment
            @Override
            public Fragment getItem(int position) {
                //get the Event from the list of the events
                Party party = parties.get(position);
                //return an instance of the PartyFragment which is initialized with with the id of the event
                return PartyFragment.newInstance(party.getId());
            }

            @Override
            public int getCount() {
                return parties.size();
            }
        };
        //set Adapter for ViewPager
        viewPager.setAdapter(adapter);

        //add the listener to change the list when its changed
        DatabaseReference partiesReference = FirebaseDatabase.getInstance().getReference("parties");
        partiesReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                List<Party> existingParties = PartyLabSingleTon.getInstance(getApplication()).getEvents();

                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    Party party =  snapshot.getValue(Party.class);
                    party.setId(UUID.fromString(snapshot.getKey()));
                    boolean changed = false;
                    //determine if the party is in the current list
                    for (int i = 0; i < existingParties.size(); i++) {
                        if(party.getId().toString().equals(existingParties.get(i).getId().toString())){
                            //update party
                            existingParties.set(i, party);
                            changed = true;
                        }
                    }
                    //if party was not in the current list add it to it
                    if(!changed){
                        Log.d(TAG, "onDataChange: " + "adding " + party.getId());
                        existingParties.add(party);
                    }

                }
                PartyLabSingleTon.getInstance(getApplication()).setEvents(existingParties);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
