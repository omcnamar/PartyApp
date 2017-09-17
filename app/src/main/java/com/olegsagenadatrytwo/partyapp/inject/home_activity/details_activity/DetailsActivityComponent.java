package com.olegsagenadatrytwo.partyapp.inject.home_activity.details_activity;

import dagger.Component;

/**
 * Created by Admin on 9/13/2017.
 */
@Component(modules = DetailsActivityModule.class)
public interface DetailsActivityComponent {

    void inject(HomeActivity homeActivity);

}
