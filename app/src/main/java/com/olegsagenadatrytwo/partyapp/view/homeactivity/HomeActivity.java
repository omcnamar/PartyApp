package com.olegsagenadatrytwo.partyapp.view.homeactivity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.olegsagenadatrytwo.partyapp.R;

public class HomeActivity extends AppCompatActivity implements HomeActivityContract.view {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }
}
