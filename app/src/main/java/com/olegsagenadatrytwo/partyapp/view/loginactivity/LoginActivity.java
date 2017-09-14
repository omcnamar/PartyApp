package com.olegsagenadatrytwo.partyapp.view.loginactivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.TwitterAuthProvider;
import com.olegsagenadatrytwo.partyapp.R;
import com.olegsagenadatrytwo.partyapp.view.homeactivity.HomeActivity;
import com.olegsagenadatrytwo.partyapp.view.registeractivity.RegisterActivity;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity implements LoginActivityContract.View {

    private static final String TAG = "LoginActivity";
    private static final int RC_SIGN_IN = 1;
    @BindView(R.id.etUserName)
    AutoCompleteTextView mEtUserName;
    @BindView(R.id.etPassword)
    EditText mEtPassword;
    @BindView(R.id.fb_login_button)
    LoginButton mFbLoginButton;
    @BindView(R.id.login_with_google)
    SignInButton loginWithGoogle;
    @BindView(R.id.btnTwitter)
    TwitterLoginButton btnTwitter;

    private CallbackManager callbackManager;

    private LoginActivityPresenter mPresenter;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private String name;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        TwitterConfig config = new TwitterConfig.Builder(this)
                .logger(new DefaultLogger(Log.DEBUG))
                .twitterAuthConfig(new TwitterAuthConfig(getString(R.string.twitterCONSUMER_KEY), getString(R.string.twitterCONSUMER_SECRET)))
                .debug(true)
                .build();
        Twitter.initialize(config);
        setContentView(R.layout.activity_login);
        Log.d(TAG, "onCreate: ");
        ButterKnife.bind(this);

        //initialize presenter
        mAuth = FirebaseAuth.getInstance();
        mPresenter = new LoginActivityPresenter();
        mPresenter.attachView(this);
        mPresenter.setContext(this);
        mPresenter.initializeFirebase();
        mPresenter.init(this, mAuth, authStateListener);
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
        loginWithGoogle.setSize(SignInButton.SIZE_STANDARD);
        loginWithGoogle.setColorScheme(SignInButton.COLOR_LIGHT);
        mPresenter.loginWithGoogleSetUp(this, mAuth);
        loginWithGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.login_with_google:
                        signIn();
                        break;
                    // ...
                }
            }
        });

        //facebook
        mFbLoginButton.setReadPermissions("email");
        callbackManager = CallbackManager.Factory.create();
        mPresenter.logInWithFacebookSetUp(mFbLoginButton, callbackManager, this);
        //Twitter
        btnTwitter.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                Log.d(TAG, "twitterLogin:success" + result);
                handleTwitterSession(result.data);
            }

            @Override
            public void failure(TwitterException exception) {
                Log.w(TAG, "twitterLogin:failure", exception);
                //updateUI(null);
            }
        });
    }
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                mPresenter.firebaseAuthWithGoogle(account, this);
                Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Login Unsuccessful", Toast.LENGTH_SHORT).show();
            }
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
            // Pass the activity result to the Twitter login button.
            btnTwitter.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void handleTwitterSession(TwitterSession session) {
        Log.d(TAG, "handleTwitterSession:" + session);
        AuthCredential credential = TwitterAuthProvider.getCredential(
                session.getAuthToken().token,
                session.getAuthToken().secret);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithCredential:success");
                            user = mAuth.getCurrentUser();
                            toToSecondActivity();
                            Toast.makeText(LoginActivity.this, "Authentication successful", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void toToSecondActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

    public void authenticateUser(View view) {
        switch (view.getId()) {
            case R.id.btnLogIn:
                if (mEtUserName.getText().toString().equals("") || mEtPassword.getText().toString().equals(""))
                    Toast.makeText(this, " Email and Password required", Toast.LENGTH_SHORT).show();
                else
                    mPresenter.signIn(mEtUserName.getText().toString(), mEtPassword.getText().toString());
                break;
            case R.id.btnRegister:
                Intent registerIntent = new Intent(this, RegisterActivity.class);
                startActivity(registerIntent);
        }
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
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
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
}
