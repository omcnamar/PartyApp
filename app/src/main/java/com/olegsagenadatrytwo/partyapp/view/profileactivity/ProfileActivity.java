package com.olegsagenadatrytwo.partyapp.view.profileactivity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.olegsagenadatrytwo.partyapp.R;
import com.olegsagenadatrytwo.partyapp.view.profileactivity.tabs.FirstFragment;
import com.olegsagenadatrytwo.partyapp.view.profileactivity.tabs.MyPartiesFragment;
import com.olegsagenadatrytwo.partyapp.view.profileactivity.tabs.SecondFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProfileActivity extends AppCompatActivity {

    @BindView(R.id.tabs)
    TabLayout tabs;
    @BindView(R.id.pager)
    ViewPager pager;
    private PagerAdapter myAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_activity);
        ButterKnife.bind(this);
        myAdapter = new PagerAdapter(getSupportFragmentManager());

        setupAdapter(pager, myAdapter);
        tabs.setupWithViewPager(pager);


    }

    public void setupAdapter(ViewPager viewPager, PagerAdapter adapter) {

        adapter.addFragment(new FirstFragment(), "Saved");
        adapter.addFragment(new SecondFragment(), "Invites");
        adapter.addFragment(new MyPartiesFragment(), "Parties");
        viewPager.setAdapter(adapter);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @OnClick({R.id.action_backbutton})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.action_backbutton:
                onBackPressed();
                break;

        }
    }
}
