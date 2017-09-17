package com.olegsagenadatrytwo.partyapp.view.registeractivity;

import android.content.Context;

import com.google.firebase.auth.FirebaseAuth;
import com.olegsagenadatrytwo.partyapp.BasePresenter;
import com.olegsagenadatrytwo.partyapp.BaseView;
import com.olegsagenadatrytwo.partyapp.view.loginactivity.LoginActivityContract;

/**
 * Created by Admin on 9/14/2017.
 */

public interface RegisterActivityContract {
    interface View extends BaseView {
        void signInResult(boolean response);
    }
    interface Presenter extends BasePresenter<LoginActivityContract.View> {
        void signIn(String email, String password);
        void removeAuthStateListener();
        void init(Context context, FirebaseAuth mAuth, FirebaseAuth.AuthStateListener mAuthListener);


    }
}
