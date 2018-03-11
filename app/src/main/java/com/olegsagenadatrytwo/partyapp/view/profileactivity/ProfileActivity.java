package com.olegsagenadatrytwo.partyapp.view.profileactivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.olegsagenadatrytwo.partyapp.R;
import com.olegsagenadatrytwo.partyapp.customviews.AutoResizeTextView;
import com.olegsagenadatrytwo.partyapp.data.remote.FirebaseHelper;
import com.olegsagenadatrytwo.partyapp.eventbus.Caller;
import com.olegsagenadatrytwo.partyapp.eventbus.User;
import com.olegsagenadatrytwo.partyapp.view.addpartyactivity.AddPartyActivity;
import com.olegsagenadatrytwo.partyapp.view.friends_activity.FriendsActivity;
import com.olegsagenadatrytwo.partyapp.view.homeactivity.HomeActivity;
import com.olegsagenadatrytwo.partyapp.view.loginactivity.LoginActivity;
import com.olegsagenadatrytwo.partyapp.view.profileactivity.tabs.FirstFragment;
import com.olegsagenadatrytwo.partyapp.view.profileactivity.tabs.MyPartiesFragment;
import com.olegsagenadatrytwo.partyapp.view.profileactivity.tabs.SecondFragment;
import com.olegsagenadatrytwo.partyapp.view.settingsactivity.SettingsActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    private static final int RESULT_LOAD_IMAGE = 1;
    private static final String TAG = "Profile";
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
    @BindView(R.id.action_profileSettings)
    ImageButton actionProfileSettings;
    @BindView(R.id.action_location)
    TextView actionLocation;
    private PagerAdapter myAdapter;
    private String userName;
    private boolean flag;
    private Uri selectedImage;
    private Bitmap bm;
    private FirebaseStorage storage;
    private StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_activity);
        ButterKnife.bind(this);
        actionLocation.setText(R.string.Profile);
        actionLocation.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        myAdapter = new PagerAdapter(getSupportFragmentManager());
        flag = false;
        pager.beginFakeDrag();
        setupAdapter(pager, myAdapter);
        tabs.setupWithViewPager(pager);

        FirebaseHelper helper = new FirebaseHelper();
        helper.getMyUsername();

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReferenceFromUrl("gs://partyapp-fc6fb.appspot.com/");

        //download image
        storageRef.child("images/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                //there is a user image
                RequestOptions requestOptions = new RequestOptions();
                requestOptions.centerCrop();
                Glide.with(getApplicationContext())
                        .load(uri)
                        .apply(requestOptions)
                        .into(civProfilePicture);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                //there is no user image
            }
        });
    }

    /**
     * Register event bus
     **/
    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);

    }

    /**
     * UnRegister event bus
     **/
    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    /**
     * When username of the current user is retrieved this method will update the textView
     **/
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(User user) {
        userName = user.getUsername();
        if (userName != null) {
            if (!userName.equals(""))
                displayName.setText(userName);
            else
                displayName.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        } else
            displayName.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
    }

    /**
     * setUp Adapters and viewPager
     **/
    public void setupAdapter(ViewPager viewPager, PagerAdapter adapter) {
        adapter.addFragment(new MyPartiesFragment(), "Hosting");
        adapter.addFragment(new FirstFragment(), "Likes");
        adapter.addFragment(new SecondFragment(), "Invitations");

        viewPager.setAdapter(adapter);

    }

    /**
     * onClick for each button
     **/
    @OnClick({R.id.action_backbutton, R.id.action_add_party, R.id.action_profileSettings, R.id.ibEdit, R.id.ibSave, R.id.civProfilePicture})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.action_backbutton:
                onBackPressed();
                break;
            case R.id.action_add_party:
                addParty();
                break;
            case R.id.action_profileSettings:
                PopupMenu popupMenu = new PopupMenu(ProfileActivity.this, actionProfileSettings);
                popupMenu.setOnMenuItemClickListener(this);
                popupMenu.getMenuInflater().inflate(R.menu.menu_actions, popupMenu.getMenu());
                popupMenu.show();

                break;
            case R.id.ibEdit:
                if (flag) {
                    ibEdit.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_mode_edit_black_48dp));
                    ibSave.setVisibility(View.GONE);
                    etName.setVisibility(View.GONE);
                    displayName.setVisibility(View.VISIBLE);
                    flag = false;
                } else {
                    flag = true;
                    ibSave.setVisibility(View.VISIBLE);
                    etName.setVisibility(View.VISIBLE);
                    displayName.setVisibility(View.GONE);
                    ibEdit.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_cancel_black_48dp));
                    if (!displayName.getText().toString().equals(""))
                        etName.setText(displayName.getText().toString());
                }
                break;
            case R.id.ibSave:
                ibSave.setVisibility(View.GONE);
                etName.setVisibility(View.GONE);
                displayName.setVisibility(View.VISIBLE);

                if(!userName.equals(etName.getText().toString().trim())){
                    FirebaseHelper firebaseHelper = new FirebaseHelper();
                    firebaseHelper.updateUsername(etName.getText().toString(), userName);
                }
                ibEdit.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_mode_edit_black_48dp));
                break;
            case R.id.civProfilePicture:
                Intent i = new Intent(
                        Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);
        }
    }

    /**
     * This method is to display a toast with passed in message
     **/
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(Caller caller) {
        String message = caller.getMessage();
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            selectedImage = data.getData();
            onSelectFromGalleryResult(data);
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setPhotoUri(selectedImage)
                    .build();
            FirebaseAuth.getInstance().getCurrentUser().updateProfile(profileUpdates);
            //get reference to storage
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReferenceFromUrl("gs://partyapp-fc6fb.appspot.com/");
            StorageReference mountainImagesRef = storageRef.child("images/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + ".jpg");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] datas = baos.toByteArray();
            UploadTask uploadTask = mountainImagesRef.putBytes(datas);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                    Toast.makeText(ProfileActivity.this, "Image not saved", Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    if (downloadUrl != null) {
                        Toast.makeText(ProfileActivity.this, "Image saved", Toast.LENGTH_SHORT).show();
                        FirebaseHelper firebaseHelper = new FirebaseHelper();
                        firebaseHelper.updateImageURLForUser(downloadUrl.toString());
                    }
                }
            });
        }
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {
        bm = null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        civProfilePicture.setImageBitmap(bm);
    }

    private void addParty() {
        //if there is no current user send the user to log in
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            Intent addPartyIntent = new Intent(this, AddPartyActivity.class);
            startActivity(addPartyIntent);
        } else {
            Intent logInIntent = new Intent(this, LoginActivity.class);
            startActivity(logInIntent);
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_party:
                addParty();
                return true;
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            case R.id.action_logout:
                // Sign out
                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    FirebaseAuth.getInstance().signOut();
                    Toast.makeText(this, "Logged out!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, HomeActivity.class));
                }
                return true;
            case R.id.action_people:
                startActivity(new Intent(this, FriendsActivity.class));
                return true;
            default:
                return false;
        }
    }
}
