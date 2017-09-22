package com.olegsagenadatrytwo.partyapp.view.profileactivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.olegsagenadatrytwo.partyapp.view.addpartyactivity.AddPartyActivity;
import com.olegsagenadatrytwo.partyapp.view.homeactivity.HomeActivity;
import com.olegsagenadatrytwo.partyapp.view.loginactivity.LoginActivity;
import com.olegsagenadatrytwo.partyapp.view.profileactivity.tabs.FirstFragment;
import com.olegsagenadatrytwo.partyapp.view.profileactivity.tabs.MyPartiesFragment;
import com.olegsagenadatrytwo.partyapp.view.profileactivity.tabs.SecondFragment;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

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
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        myAdapter = new PagerAdapter(getSupportFragmentManager());
        flag = false;
        pager.beginFakeDrag();
        setupAdapter(pager, myAdapter);
        tabs.setupWithViewPager(pager);
        userName = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        if(userName != null) {
            if (!userName.equals(""))
                displayName.setText(userName);
            else
                displayName.setText("New user");
        }

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReferenceFromUrl("gs://partyapp-fc6fb.appspot.com/");
        //download image

        storageRef.child("images/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Toast.makeText(ProfileActivity.this, "Image Saved", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(ProfileActivity.this, "Image Not Saved", Toast.LENGTH_SHORT).show();
            }
        });
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

    @OnClick({R.id.action_backbutton, R.id.action_add_party, R.id.action_profileSettings, R.id.ibEdit, R.id.ibSave, R.id.civProfilePicture})
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
                    if (!displayName.getText().toString().equals(""))
                        etName.setText(displayName.getText().toString());
                }
                break;
            case R.id.ibSave:
                ibSave.setVisibility(View.GONE);
                etName.setVisibility(View.GONE);
                displayName.setVisibility(View.VISIBLE);
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(etName.getText().toString())
                        .setPhotoUri(selectedImage)
                        .build();
                FirebaseAuth.getInstance().getCurrentUser().updateProfile(profileUpdates);
                ibEdit.setImageDrawable(getResources().getDrawable(R.drawable.ic_mode_edit_black_48dp));
                displayName.setText(etName.getText().toString());
                break;
            case R.id.civProfilePicture:
                Intent i = new Intent(
                        Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);
        }
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
                    Toast.makeText(ProfileActivity.this, "Image saved", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onSuccess: " + downloadUrl);
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

}
