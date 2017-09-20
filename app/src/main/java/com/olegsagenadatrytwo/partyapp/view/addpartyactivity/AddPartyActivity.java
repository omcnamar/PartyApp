package com.olegsagenadatrytwo.partyapp.view.addpartyactivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
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

    private static final int RESULT_LOAD_IMAGE = 1;
    private static final int PICK_FROM_GALLERY = 2;
    private static final String TAG = "AddPartyActivity";
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

    private AddPartyActivityPresenter presenter;
    private Party party;
    private Bitmap bitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_party);
        ButterKnife.bind(this);
        //set up presenter
        presenter = new AddPartyActivityPresenter();
        presenter.attachView(this);
        presenter.setContext(this);
        presenter.init();

        party = new Party();

        //onclick for the image icon
        mIvStartGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (ActivityCompat.checkSelfPermission(AddPartyActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(AddPartyActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PICK_FROM_GALLERY);
                    } else {
                        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(galleryIntent, PICK_FROM_GALLERY);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults)
    {
        switch (requestCode) {
            case PICK_FROM_GALLERY:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(galleryIntent, PICK_FROM_GALLERY);
                } else {
                    //do something like displaying a message that he didn`t allow the app to access gallery and you wont be able to let him select from gallery
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_FROM_GALLERY && resultCode == RESULT_OK) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(selectedImage,filePathColumn, null, null, null);
            if(cursor != null) {
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();
                bitmap = BitmapFactory.decodeFile(picturePath);
                mIvPartyLogo.setImageBitmap(bitmap);
            }

        }
    }

    //method to add a party to the dataBase
    public void addParty(View view) {

        party.setPartyName(mEtPartyName.getText().toString());
        party.setAddress(mEtPartyAddress.getText().toString());
        party.setDescription(mEtPartyDescription.getText().toString());
        party.setDate(mEtDate.getText().toString());
        party.setStartTime(mEtStartTime.getText().toString());
        party.setEndTime(mEtEndTime.getText().toString());
        party.setAgeRequired(mEtMinAge.getText().toString());
        party.setCapacity(Integer.parseInt(mEtCapacity.getText().toString()));

        presenter.addNewParty(party, bitmap);
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
