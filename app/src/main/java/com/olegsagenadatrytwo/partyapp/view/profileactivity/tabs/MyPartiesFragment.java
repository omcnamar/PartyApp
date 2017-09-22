package com.olegsagenadatrytwo.partyapp.view.profileactivity.tabs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.olegsagenadatrytwo.partyapp.R;
import com.olegsagenadatrytwo.partyapp.model.custompojos.Party;
import com.olegsagenadatrytwo.partyapp.view.profileactivity.MyPartiesAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MyPartiesFragment extends Fragment {

    @BindView(R.id.pbLoading)
    ProgressBar pbLoading;
    Unbinder unbinder;
    @BindView(R.id.rvMyParties)
    RecyclerView rvMyParties;
    //Recycler view movie stuff
    private MyPartiesAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_third, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        pbLoading.setVisibility(View.VISIBLE);
        rvMyParties.setLayoutManager(new LinearLayoutManager(view.getContext()));
        rvMyParties.setItemAnimator( new DefaultItemAnimator());
        getMyParties();
    }


    public void getMyParties() {
        //database.getReference().child("Student1").child("Grades");
        DatabaseReference partiesReference = FirebaseDatabase.getInstance().getReference("profiles").
                child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("parties");
        partiesReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Party> parties = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Party party = snapshot.getValue(Party.class);
                    if (party != null) {
                        party.setId(snapshot.getKey());
                        parties.add(party);
                    }
                }
                myPartiesLoadedUpdateUI(parties);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void myPartiesLoadedUpdateUI(List<Party> parties) {

        adapter = new MyPartiesAdapter(parties, getContext());
        if(rvMyParties != null) {
            rvMyParties.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            pbLoading.setVisibility(View.GONE);

        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
