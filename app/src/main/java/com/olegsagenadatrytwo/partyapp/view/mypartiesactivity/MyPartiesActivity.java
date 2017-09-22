package com.olegsagenadatrytwo.partyapp.view.mypartiesactivity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.olegsagenadatrytwo.partyapp.R;
import com.olegsagenadatrytwo.partyapp.inject.view.myparties_activity.DaggerMyPartiesActivityComponent;
import com.olegsagenadatrytwo.partyapp.model.custompojos.Party;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyPartiesActivity extends AppCompatActivity implements MyPartiesActivityContract.View{

    public static final String TAG = "MyPartiesActivity";

    @Inject
    MyPartiesActivityPresenter presenter;
    @BindView(R.id.pbLoading)
    ProgressBar pbLoading;

    //Recycler view movie stuff
    private RecyclerView rvMyParties;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.ItemAnimator itemAnimator;
    private MyPartiesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_parties);
        ButterKnife.bind(this);
        DaggerMyPartiesActivityComponent.create().inject(this);

        //initialize recycler vies movie stuff
        rvMyParties = findViewById(R.id.rvMyParties);
        layoutManager = new LinearLayoutManager(this);
        itemAnimator = new DefaultItemAnimator();
        rvMyParties.setLayoutManager(layoutManager);
        rvMyParties.setItemAnimator(itemAnimator);

        presenter.attachView(this);
        presenter.setContext(this);
        presenter.getMyParties();

    }

    @Override
    public void myPartiesLoadedUpdateUI(List<Party> parties) {

        adapter = new MyPartiesAdapter(parties, this);
        rvMyParties.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        pbLoading.setVisibility(View.GONE);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (adapter != null) {
            adapter.saveStates(outState);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (adapter != null) {
            adapter.restoreStates(savedInstanceState);
        }
    }
}
