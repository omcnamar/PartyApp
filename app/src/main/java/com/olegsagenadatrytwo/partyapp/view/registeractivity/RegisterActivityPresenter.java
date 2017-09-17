package com.olegsagenadatrytwo.partyapp.view.registeractivity;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.olegsagenadatrytwo.partyapp.view.homeactivity.HomeActivity;
import com.olegsagenadatrytwo.partyapp.view.loginactivity.LoginActivityContract;


public class RegisterActivityPresenter implements  RegisterActivityContract.Presenter{

    private static final String TAG = "RegisterPresenter";
    private LoginActivityContract.View view;
    private Context context;
    //google Fire base stuff
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    public void attachView(LoginActivityContract.View view) {
        this.view =  view;
    }

    @Override
    public void removeView() {
        this.view = null;
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
    public void initializeFirebase(){
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                } else {
                    // User is signed out
                }
            }
        };
    }
    public void setContext(Context context) {
        this.context = context;
    }
    @Override
    public void signIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener((RegisterActivity)context, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            view.signInResult(true);
                            Intent homeIntent = new Intent(context, HomeActivity.class);
                            context.startActivity(homeIntent);
                        }
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            view.signInResult(false);
                        }

                    }
                });
    }
    @Override
    public void init(Context context, FirebaseAuth mAuth, FirebaseAuth.AuthStateListener mAuthListener) {
        this.mAuth = mAuth;
        this.mAuthListener = mAuthListener;
        this.context = context;
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };
        mAuth.addAuthStateListener(mAuthListener);
    }
    @Override
    public void removeAuthStateListener() {
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
