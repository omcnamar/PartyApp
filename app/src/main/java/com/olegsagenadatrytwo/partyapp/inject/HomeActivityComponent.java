package com.olegsagenadatrytwo.partyapp.inject;

import com.olegsagenadatrytwo.partyapp.view.homeactivity.HomeActivity;

import dagger.Component;

/**
 * Created by Admin on 9/13/2017.
 */
@Component(modules = HomeActivityModule.class)
public interface HomeActivityComponent {

    void inject(HomeActivity homeActivity);
}
