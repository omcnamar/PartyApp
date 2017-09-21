package com.olegsagenadatrytwo.partyapp.inject.view.myparties_activity;

import com.olegsagenadatrytwo.partyapp.view.mypartiesactivity.MyPartiesActivity;

import dagger.Component;

/**
 * Created by Admin on 9/13/2017.
 */
@Component(modules = MyPartiesActivityModule.class)
public interface MyPartiesActivityComponent {

    void inject(MyPartiesActivity myPartiesActivity);

}
