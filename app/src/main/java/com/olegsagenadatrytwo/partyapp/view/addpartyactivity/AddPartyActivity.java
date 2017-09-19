package com.olegsagenadatrytwo.partyapp.view.addpartyactivity;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.olegsagenadatrytwo.partyapp.R;
import com.olegsagenadatrytwo.partyapp.model.custompojos.Party;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddPartyActivity extends AppCompatActivity implements AddPartyActivityContract.view {

    @BindView(R.id.ivPartyLogo)
    ImageView mIvPartyLogo;
    @BindView(R.id.ivStartGallery)
    ImageView mIvStartGallery;
    @BindView(R.id.etPartyName)
    EditText mEtPartyName;
    @BindView(R.id.etPartyAddress)
    EditText mEtPartyAddress;
    @BindView(R.id.etPartyDescription)
    EditText mEtPartyDescription;
    @BindView(R.id.etDate)
    EditText mEtDate;
    @BindView(R.id.etStartTime)
    EditText mEtStartTime;
    @BindView(R.id.etEndTime)
    EditText mEtEndTime;
    @BindView(R.id.etMinAge)
    EditText mEtMinAge;
    @BindView(R.id.etCapacity)
    EditText mEtCapacity;
    @BindView(R.id.btnSubmitParty)
    Button mBtnSubmitParty;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private AddPartyActivityPresenter presenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_party);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        //set up presenter
        presenter = new AddPartyActivityPresenter();
        presenter.attachView(this);
        presenter.setContext(this);
        presenter.init();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_actions, menu);
        return true;
    }

    //method to add a party to the dataBase
    public void addParty(View view) {

        Party party = new Party();
        party.setPartyName(mEtPartyName.getText().toString());
        party.setAddress(mEtPartyAddress.getText().toString());
        party.setDescription(mEtPartyDescription.getText().toString());
        party.setDate(mEtDate.getText().toString());
        party.setStartTime(mEtStartTime.getText().toString());
        party.setEndTime(mEtEndTime.getText().toString());
        party.setAgeRequired(mEtMinAge.getText().toString());
        party.setCapacity(Integer.parseInt(mEtCapacity.getText().toString()));

        presenter.addNewParty(party);
    }

    @Override
    public void partySaved(Boolean saved) {
        if(saved){
            Toast.makeText(this, "Saved Successfull", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
        }
    }
}
