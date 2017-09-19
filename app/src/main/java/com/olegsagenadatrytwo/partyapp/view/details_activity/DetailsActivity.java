package com.olegsagenadatrytwo.partyapp.view.details_activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.olegsagenadatrytwo.partyapp.R;
import com.olegsagenadatrytwo.partyapp.inject.view.details_activity.DaggerDetailsActivityComponent;

import javax.inject.Inject;

public class DetailsActivity extends AppCompatActivity implements DetailsActivityContract.view {

    @Inject DetailsActivityPresenter presenter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        DaggerDetailsActivityComponent.create().inject(this);
        presenter.attachView(this);
    }
}
