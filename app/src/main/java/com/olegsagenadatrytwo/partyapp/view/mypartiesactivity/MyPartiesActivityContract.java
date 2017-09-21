package com.olegsagenadatrytwo.partyapp.view.mypartiesactivity;

import com.olegsagenadatrytwo.partyapp.BasePresenter;
import com.olegsagenadatrytwo.partyapp.BaseView;
import com.olegsagenadatrytwo.partyapp.model.custompojos.Party;

import java.util.List;

/**
 * Created by omcna on 9/21/2017.
 */

public interface MyPartiesActivityContract {

    interface View extends BaseView {
        void myPartiesLoadedUpdateUI(List<Party> parties);
    }

    interface Presenter extends BasePresenter<View> {
        void getMyParties();
    }
}
