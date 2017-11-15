package com.olegsagenadatrytwo.partyapp.view.registeractivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.olegsagenadatrytwo.partyapp.R;
import com.olegsagenadatrytwo.partyapp.model.custompojos.Profile;
import com.olegsagenadatrytwo.partyapp.view.homeactivity.HomeActivity;
import com.olegsagenadatrytwo.partyapp.view.loginactivity.LoginActivityContract;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterActivity extends AppCompatActivity implements LoginActivityContract.View {

    private static final String TAG = "RegisterActivity";

    @BindView(R.id.signup_input_email)
    EditText signupInputEmail;
    @BindView(R.id.signup_input_password)
    EditText signupInputPassword;
    @BindView(R.id.btn_signup)
    Button btnSignup;
    @BindView(R.id.signup_input_password_confirm)
    EditText signupInputPasswordConfirm;
    @BindView(R.id.signup_input_username)
    EditText signupInputUsername;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser user;
    private RegisterActivityPresenter mPresenter;
    private GoogleApiClient mGoogleApiClient;
    private boolean userCreated = false;
    private boolean registrationComplete = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        //TODO: working
        mAuth = FirebaseAuth.getInstance();
        mPresenter = new RegisterActivityPresenter();
        mPresenter.attachView(this);
        mPresenter.setContext(this);
        mPresenter.init(this, mAuth, mAuthListener);
        mPresenter.initializeFirebase();
        //mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null) {

                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged: " + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @OnClick({R.id.btn_signup})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_signup:

                final String email = signupInputEmail.getText().toString();
                final String password = signupInputPassword.getText().toString();
                final String username = signupInputUsername.getText().toString();

                if (!signupInputPasswordConfirm.getText().toString().equals(signupInputPassword.getText().toString())) {
                    signupInputPasswordConfirm.setError("Passwords do not match!");
                    Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                } else if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    signupInputEmail.setError("Please enter a valid email address");
                } else {

                    if(!userCreated) {
                        mAuth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {

                                        if (task.isSuccessful()) {
                                            userCreated = true;
                                            createUserProfileDatabase(username, email);
                                        }else{
                                            Toast.makeText(RegisterActivity.this, "Email exists", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }else{
                        createUserProfileDatabase(username, email);
                    }
                    break;
                }
        }
    }

    private void createUserProfileDatabase(final String username, final String email) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference firebaseRef = database.getReference();
        firebaseRef.child("usernames").child(username).runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                if (mutableData.getValue() == null) {
                    mutableData.setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    return Transaction.success(mutableData);
                }

                return Transaction.abort();
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean commited, DataSnapshot dataSnapshot) {
                if (commited) {
                    // username saved
                    //add user to fire base database with no parties
                    DatabaseReference profileReference = database.getReference("profiles");

                    Profile profile = new Profile();
                    profile.setEmail(email);
                    profile.setUsername(username);
                    profileReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(profile);
                    Toast.makeText(RegisterActivity.this, "Your account was created successfully!", Toast.LENGTH_LONG).show();
                    registrationComplete = true;
                    Intent homeIntent = new Intent(getApplicationContext(), HomeActivity.class);
                    getApplicationContext().startActivity(homeIntent);
                } else {
                    // username exists
                    Toast.makeText(RegisterActivity.this, "Username already exists", Toast.LENGTH_LONG).show();
                }
            }


        });
    }

    @Override
    public void signInResult(boolean response) {
        if (response) {
            Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null)
            mAuth.removeAuthStateListener(mAuthListener);
        mPresenter.removeView();
        mPresenter.removeAuthStateListener();
    }

    @Override
    public void googleApiClientReady(GoogleApiClient googleApiClient) {
        this.mGoogleApiClient = googleApiClient;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(!registrationComplete){
            if(FirebaseAuth.getInstance().getCurrentUser() != null) {
                FirebaseAuth.getInstance().getCurrentUser().delete();
            }
        }
    }
}
