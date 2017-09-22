package com.olegsagenadatrytwo.partyapp.view.profileactivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.olegsagenadatrytwo.partyapp.R;
import com.olegsagenadatrytwo.partyapp.customviews.AutoResizeTextView;
import com.olegsagenadatrytwo.partyapp.view.addpartyactivity.AddPartyActivity;
import com.olegsagenadatrytwo.partyapp.view.homeactivity.HomeActivity;
import com.olegsagenadatrytwo.partyapp.view.loginactivity.LoginActivity;
import com.olegsagenadatrytwo.partyapp.view.profileactivity.tabs.FirstFragment;
import com.olegsagenadatrytwo.partyapp.view.profileactivity.tabs.MyPartiesFragment;
import com.olegsagenadatrytwo.partyapp.view.profileactivity.tabs.SecondFragment;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    @BindView(R.id.tabs)
    TabLayout tabs;
    @BindView(R.id.pager)
    ViewPager pager;
    @BindView(R.id.displayName)
    AutoResizeTextView displayName;
    @BindView(R.id.civProfilePicture)
    CircleImageView civProfilePicture;
    @BindView(R.id.ibSave)
    ImageButton ibSave;
    @BindView(R.id.ibEdit)
    ImageButton ibEdit;
    @BindView(R.id.etName)
    EditText etName;
    private PagerAdapter myAdapter;
    private String userName;
    private boolean flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_activity);
        ButterKnife.bind(this);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        myAdapter = new PagerAdapter(getSupportFragmentManager());
        flag = false;
        setupAdapter(pager, myAdapter);
        tabs.setupWithViewPager(pager);
        userName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        displayName.setText(userName);
        Picasso.with(this).load(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl()).into(civProfilePicture);

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

    @OnClick({R.id.action_backbutton, R.id.action_add_party, R.id.action_profileSettings, R.id.ibEdit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.action_backbutton:
                onBackPressed();
                break;
            case R.id.action_add_party:
                //if there is no current user send the user to log in
                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    Intent addPartyIntent = new Intent(this, AddPartyActivity.class);
                    startActivity(addPartyIntent);
                } else {
                    Intent logInIntent = new Intent(this, LoginActivity.class);
                    startActivity(logInIntent);
                }
                break;
            case R.id.action_profileSettings:
                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    FirebaseAuth.getInstance().signOut();
                    Toast.makeText(this, "Logged out!", Toast.LENGTH_SHORT).show();
                    Intent homeIntent = new Intent(this, HomeActivity.class);
                    startActivity(homeIntent);
                }
                break;
            case R.id.ibEdit:
                if (flag) {
                    ibEdit.setImageDrawable(getResources().getDrawable(R.drawable.ic_mode_edit_black_48dp));
                    ibSave.setVisibility(View.GONE);
                    etName.setVisibility(View.GONE);
                    displayName.setVisibility(View.VISIBLE);
                    flag = false;
                } else {
                    flag = true;
                    ibSave.setVisibility(View.VISIBLE);
                    etName.setVisibility(View.VISIBLE);
                    displayName.setVisibility(View.GONE);
                    ibEdit.setImageDrawable(getResources().getDrawable(R.drawable.ic_cancel_black_48dp));
                }
                break;
        }
    }

}
