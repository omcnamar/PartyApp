package com.olegsagenadatrytwo.partyapp.view.addpartyactivity;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.olegsagenadatrytwo.partyapp.R;
import com.olegsagenadatrytwo.partyapp.model.custompojos.Party;

import java.io.IOException;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.olegsagenadatrytwo.partyapp.utilities.location.LocationUtilities.getDistanceFromDeviceLocation;

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
    @BindView(R.id.etEndDate)
    EditText mEtEndDate;
    @BindView(R.id.etEndTime)
    EditText mEtEndTime;
    @BindView(R.id.etMinAge)
    EditText mEtMinAge;
    @BindView(R.id.etCapacity)
    EditText mEtCapacity;
    @BindView(R.id.btnSubmitParty)
    Button mBtnSubmitParty;
    @BindView(R.id.tvStartTime)
    TextInputLayout tvStartTime;
    @BindView(R.id.tvEndTime)
    TextInputLayout tvEndTime;
    @BindView(R.id.tvMinAge)
    TextInputLayout tvMinAge;
    @BindView(R.id.action_backbutton)
    ImageButton actionBackbutton;
    @BindView(R.id.action_add_party)
    ImageButton actionAddParty;
    @BindView(R.id.action_profileSettings)
    ImageButton actionProfileSettings;
    @BindView(R.id.tvAddress)
    TextInputLayout tvAddress;
    @BindView(R.id.tvDescription)
    TextInputLayout tvDescription;
    @BindView(R.id.tvDate)
    TextInputLayout tvDate;
    @BindView(R.id.tvEndDate)
    TextInputLayout tvEndDate;
    @BindView(R.id.tvCapacity)
    TextInputLayout tvCapacity;

    private AddPartyActivityPresenter presenter;
    private Party party;
    private Bitmap bitmap;

    //DatePicker and TimePicker implementation
    private static final int DIALOG_CAL = 0;
    private static final int DIALOG_TIME = 1;
    private Calendar calendar;
    private int year, month, day;
    private String hour, min, value;
    private String am_pm = "";
    private StringBuilder minutes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_party);
        ButterKnife.bind(this);
        //hiding party and settings buttons from toolbar
        actionAddParty.setVisibility(View.INVISIBLE);
        actionProfileSettings.setVisibility(View.INVISIBLE);
        //set up presenter
        presenter = new AddPartyActivityPresenter();
        presenter.attachView(this);
        presenter.setContext(this);
        presenter.init();
        //DatePicker and TimerPicker implementation
        mEtDate.setInputType(InputType.TYPE_NULL);
        mEtEndDate.setInputType(InputType.TYPE_NULL);
        mEtStartTime.setInputType(InputType.TYPE_NULL);
        mEtEndTime.setInputType(InputType.TYPE_NULL);
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);

        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        hour = String.valueOf(calendar.get(Calendar.HOUR_OF_DAY));
        min = String.valueOf(calendar.get(Calendar.MINUTE));
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        showDate(year, month + 1, day, "start");
        showStartTime(hour, min, "start");

        party = new Party();

        //onclick for the image icon
        mIvStartGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (ActivityCompat.checkSelfPermission(AddPartyActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(AddPartyActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PICK_FROM_GALLERY);
                    } else {
                        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(galleryIntent, PICK_FROM_GALLERY);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PICK_FROM_GALLERY:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
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
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            if (cursor != null) {
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

        boolean valid = true;
        if (mEtPartyName.getText().toString().equals("")) {
            mEtPartyName.setError("Please name your party!");
            valid = false;
        }
        if (mEtPartyAddress.getText().toString().equals("")) {
            mEtPartyAddress.setError("Address is required!");
            valid = false;
        }
        if (mEtPartyDescription.getText().toString().equals("")) {
            mEtPartyDescription.setError("Description is required!");
            valid = false;
        }
        if (mEtDate.getText().toString().equals("")) {
            mEtDate.setError("Date is required!");
            valid = false;
        }
        if (mEtStartTime.getText().toString().equals("")) {
            mEtStartTime.setError("Start time is required!");
            valid = false;
        }
        if (mEtEndDate.getText().toString().equals("")) {
            mEtEndDate.setError("End date is required!");
            valid = false;
        }
        if (mEtEndTime.getText().toString().equals("")) {
            mEtEndTime.setError("End time is required!");
            valid = false;
        }
        if (mEtMinAge.getText().toString().equals("")) {
            mEtMinAge.setError("Age required!");
            valid = false;
        } else if (Integer.parseInt(mEtMinAge.getText().toString()) > 100) {
            mEtMinAge.setError("Check age, must be less than 100");
            valid = false;
        }
        if ((mEtCapacity.getText().toString().equals(""))) {
            mEtCapacity.setError("Capacity is required!");
            valid = false;
        } else if (Integer.parseInt(mEtCapacity.getText().toString()) >= 2147483647) {
            mEtCapacity.setError("Capacity is to big!");
            valid = false;
        }
        if (valid) {
            Party party = new Party();
            party.setOwnerId(FirebaseAuth.getInstance().getCurrentUser().getUid());

            party.setPartyName(mEtPartyName.getText().toString());
            party.setAddress(mEtPartyAddress.getText().toString());
            party.setDescription(mEtPartyDescription.getText().toString());
            party.setDate(mEtDate.getText().toString());
            party.setStartTime(mEtStartTime.getText().toString());
            party.setEndDate(mEtEndDate.getText().toString());
            party.setEndTime(mEtEndTime.getText().toString());
            party.setAgeRequired(mEtMinAge.getText().toString());
            party.setCapacity(Integer.parseInt(mEtCapacity.getText().toString()));
            try {
                party.setDistance(getDistanceFromDeviceLocation(party, this));
            } catch (IOException e) {
                e.printStackTrace();
            }
            presenter.addNewParty(party, bitmap);

        }
    }

    @Override
    public void partySaved(Boolean saved) {
        if (saved) {
            Toast.makeText(this, "Saved Successfull", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Implementing DatePicker and TimerPicker
     */

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 0) {
            return new DatePickerDialog(this,
                    myDateListener, year, month, day);
        }
        if (id == 1) {
            return new TimePickerDialog(this, myTimeListener, Integer.parseInt(hour), Integer.parseInt(min),
                    false);
        }

        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int arg1, int arg2, int arg3) {
                    showDate(arg1, arg2 + 1, arg3, value);
                }
            };

    private TimePickerDialog.OnTimeSetListener myTimeListener = new
            TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay,
                                      int minute) {
                    showStartTime(String.valueOf(hourOfDay), String.valueOf(minute), value);
                }
            };

    private void showDate(int year, int month, int day, String val) {
        if (val == "start")
            mEtDate.setText(new StringBuilder().append(month).append("/")
                    .append(day).append("/").append(year));
        else
            mEtEndDate.setText(new StringBuilder().append(month).append("/")
                    .append(day).append("/").append(year));

    }

    private void showStartTime(String hour, String minute, String val) {
        //checking for time
        if (Integer.parseInt(hour) < 12) {
            am_pm = "AM";

        } else {
            am_pm = "PM";
            hour = String.valueOf(Integer.parseInt(hour) - 12);
        }
        if (minute.length() == 1) {
            minutes = new StringBuilder().append("0").append(minute);
            minute = String.valueOf(minutes);
        } else {
            minutes = new StringBuilder().append(minute);
            minute = String.valueOf(minutes);
        }
        if (val == "start")
            mEtStartTime.setText(new StringBuilder().append(hour).append(":").append(minute).append(" ").append(am_pm));
        else
            mEtEndTime.setText(new StringBuilder().append(hour).append(":").append(minute).append(" ").append(am_pm));

    }

    @OnClick({R.id.etDate, R.id.etStartTime, R.id.etEndDate, R.id.etEndTime})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.etDate:
                showDialog(DIALOG_CAL);
                value = "start";
                break;
            case R.id.etEndDate:
                showDialog(DIALOG_CAL);
                value = "end";
                break;
            case R.id.etStartTime:
                showDialog(DIALOG_TIME);
                value = "start";
                break;
            case R.id.etEndTime:
                showDialog(DIALOG_TIME);
                value = "end";
                break;
        }
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
