package com.olegsagenadatrytwo.partyapp.view.loginactivity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.olegsagenadatrytwo.partyapp.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity implements LoginActivityContract.View {

    private static final String TAG = "LoginActivity";
    @BindView(R.id.etUserName)
    EditText mEtUserName;
    @BindView(R.id.etPassword)
    EditText mEtPassword;
    @BindView(R.id.fb_login_button)
    LoginButton mFbLoginButton;
    private CallbackManager callbackManager;
//    @BindView(R.id.btnLogIn)
//    Button mBtnLogIn;
//    @BindView(R.id.btnRegister)
//    TextView mBtnRegister;
//    @BindView(R.id.login_with_google)
//    SignInButton mLoginWithGoogle;
//    @BindView(R.id.button_twitter_login)
//    TwitterLoginButton mButtonTwitterLogin;
    private LoginActivityPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        setContentView(R.layout.activity_login);
        Log.d(TAG, "onCreate: ");
        ButterKnife.bind(this);

        //initialize presenter
        mPresenter = new LoginActivityPresenter();
        mPresenter.attachView(this);
        mPresenter.setContext(this);
        mPresenter.initializeFirebase();

        //facebook
        mFbLoginButton.setReadPermissions("email");
        callbackManager = CallbackManager.Factory.create();
        // Callback registration
        mFbLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                Log.d(TAG, "onSuccess: ");
            }

            @Override
            public void onCancel() {
                // App code
                Log.d(TAG, "onCancel: ");
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                Log.d(TAG, "onError: ");
            }
        });

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code
                        Log.d(TAG, "onSuccess: manager");
                        Toast.makeText(LoginActivity.this, "Success manager", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancel() {
                        // App code
                        Log.d(TAG, "onCancel: manager");
                        Toast.makeText(LoginActivity.this, "onCancel manager", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                        Log.d(TAG, "onError: manager");
                        Toast.makeText(LoginActivity.this, "onError manager", Toast.LENGTH_SHORT).show();
                    }
                });


    }

    public void authenticateUser(View view) {
        mPresenter.signIn(mEtUserName.getText().toString(), mEtPassword.getText().toString());
    }

    @Override
    public void signInResult(boolean response) {
        if(response){
            Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mPresenter.removeView();
    }
}
