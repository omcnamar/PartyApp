package com.olegsagenadatrytwo.partyapp.view.friends_activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.olegsagenadatrytwo.partyapp.R;
import com.olegsagenadatrytwo.partyapp.eventbus.AllUsers;
import com.olegsagenadatrytwo.partyapp.inject.view.friends_activity.DaggerFriendsActivityComponent;
import com.olegsagenadatrytwo.partyapp.view.friends_activity.adapters.AdapterCategoty;
import com.olegsagenadatrytwo.partyapp.view.friends_activity.adapters.AdapterPeopleInfo;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FriendsActivity extends AppCompatActivity implements FriendsActivityContract.view {

    @Inject
    FriendsActivityPresenter presenter;
    @BindView(R.id.action_backbutton)
    ImageButton mActionBackbutton;
    @BindView(R.id.action_search)
    SearchView mActionSearch;
    @BindView(R.id.action_profileSettings)
    ImageButton mActionProfileSettings;
    @BindView(R.id.toolbar2)
    LinearLayout mToolbar2;
    @BindView(R.id.recycler_view_category)
    RecyclerView mRecyclerViewCategory;
    @BindView(R.id.recycler_view_people)
    RecyclerView mRecyclerViewPeople;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        ButterKnife.bind(this);
        DaggerFriendsActivityComponent.create().inject(this);
        presenter.attachView(this);
        presenter.getAllUsers();
        setUpCategoryRecycler();
        setUpPeopleRecycler();

    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);

    }

    private void setUpCategoryRecycler() {
        List<String> categoryList = new ArrayList<>();
        categoryList.add("My Friends");
        categoryList.add("all");
        categoryList.add("Recent");
        AdapterCategoty adapter = new AdapterCategoty(categoryList, this);
        mRecyclerViewCategory.setAdapter(adapter);
        mRecyclerViewCategory.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mRecyclerViewCategory.setItemAnimator( new DefaultItemAnimator());
        adapter.notifyDataSetChanged();
    }

    private void setUpPeopleRecycler() {
        mRecyclerViewPeople.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerViewPeople.setItemAnimator( new DefaultItemAnimator());
    }

    @Override
    public void caregoryChanged(String category) {
        Toast.makeText(this, category, Toast.LENGTH_SHORT).show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(AllUsers allUsers) {
        List<String> userIDs = allUsers.getUserIds();
        AdapterPeopleInfo adapter = new AdapterPeopleInfo(userIDs, this, this);
        mRecyclerViewPeople.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
}
