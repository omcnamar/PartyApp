package com.olegsagenadatrytwo.partyapp.view.loginactivity;

import com.olegsagenadatrytwo.partyapp.BasePresenter;
import com.olegsagenadatrytwo.partyapp.BaseView;

/**
 * Created by omcna on 9/5/2017.
 */

public interface LoginActivityContract {

    interface View extends BaseView {
        void signInResult(boolean response);
    }


    interface Presenter extends BasePresenter<View> {
        void signIn(String email, String password);
    }

}
