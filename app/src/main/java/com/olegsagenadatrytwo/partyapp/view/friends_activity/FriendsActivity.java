package com.olegsagenadatrytwo.partyapp.view.friends_activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.olegsagenadatrytwo.partyapp.R;
import com.olegsagenadatrytwo.partyapp.eventbus.AllUsers;
import com.olegsagenadatrytwo.partyapp.eventbus.Friends;
import com.olegsagenadatrytwo.partyapp.eventbus.UsersWhoRequested;
import com.olegsagenadatrytwo.partyapp.inject.view.friends_activity.DaggerFriendsActivityComponent;
import com.olegsagenadatrytwo.partyapp.view.friends_activity.adapters.AdapterCategoty;
import com.olegsagenadatrytwo.partyapp.view.friends_activity.adapters.AdapterFriends;
import com.olegsagenadatrytwo.partyapp.view.friends_activity.adapters.AdapterPeopleInfo;
import com.olegsagenadatrytwo.partyapp.view.friends_activity.adapters.AdapterRequests;

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

    private String currentPage;

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

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    private void setUpCategoryRecycler() {
        List<String> categoryList = new ArrayList<>();
        categoryList.add("My Friends");
        categoryList.add("all");
        currentPage = "all";
        categoryList.add("Requests");
        AdapterCategoty adapter = new AdapterCategoty(categoryList, this);
        mRecyclerViewCategory.setAdapter(adapter);
        mRecyclerViewCategory.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mRecyclerViewCategory.setItemAnimator( new DefaultItemAnimator());
        adapter.notifyDataSetChanged();
    }

    private void setUpPeopleRecycler() {

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerViewPeople.getContext(), layoutManager.getOrientation());
        mRecyclerViewPeople.setLayoutManager(layoutManager);
        mRecyclerViewPeople.addItemDecoration(dividerItemDecoration);
        mRecyclerViewPeople.setItemAnimator(itemAnimator);
    }

    @Override
    public void caregoryChanged(String category) {
        Toast.makeText(this, category, Toast.LENGTH_SHORT).show();
        switch (category){
            case "Requests":
                currentPage = "Requests";
                presenter.getPendingFriendRequests();
                break;
            case "all":
                currentPage = "all";
                presenter.getAllUsers();
                break;
            case "My Friends":
                currentPage = "My Friends";
                presenter.getMyFriends();
                break;

        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(AllUsers allUsers) {
        if(currentPage.equals("all")) {
            List<String> userIDs = allUsers.getUserIds();
            List<String> userNames = allUsers.getUserNames();
            List<String> alreadyFriendsList = allUsers.getMyFriendsList();
            List<String> alreadyRequested = allUsers.getListOfAlreadyRequested();
            AdapterPeopleInfo adapter = new AdapterPeopleInfo(userIDs, userNames, alreadyFriendsList, alreadyRequested, this, this);
            mRecyclerViewPeople.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(UsersWhoRequested usersWhoRequested) {

        if(currentPage.equals("Requests")) {
            List<String> userIDs = usersWhoRequested.getUserIDsWhoRequested();
            AdapterRequests adapter = new AdapterRequests(userIDs, this, this);
            mRecyclerViewPeople.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(Friends friends) {

        if(currentPage.equals("My Friends")) {
            List<String> friendsList = friends.getFriendsList();
            AdapterFriends adapter = new AdapterFriends(friendsList, this, this);
            mRecyclerViewPeople.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }

    }

}
