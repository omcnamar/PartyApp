package com.olegsagenadatrytwo.partyapp.view.loginactivity;

import android.content.Context;

import com.facebook.CallbackManager;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.olegsagenadatrytwo.partyapp.BasePresenter;
import com.olegsagenadatrytwo.partyapp.BaseView;

/**
 * Created by omcna on 9/5/2017.
 */

public interface LoginActivityContract {

    interface View extends BaseView {
        void signInResult(boolean response);
        void googleApiClientReady(GoogleApiClient googleApiClient);
    }


    interface Presenter extends BasePresenter<View> {
        void signIn(String email, String password);
        void removeAuthStateListener();
        void init(Context context, FirebaseAuth mAuth, FirebaseAuth.AuthStateListener mAuthListener);
        void logInWithFacebookSetUp(LoginButton loginButton, CallbackManager callbackManager, final LoginActivity loginActivity);
        void loginWithGoogleSetUp(LoginActivity loginActivity, FirebaseAuth firebaseAuth);
        void firebaseAuthWithGoogle(GoogleSignInAccount acct, LoginActivity loginActivity);

    }

}
