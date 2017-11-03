package com.olegsagenadatrytwo.partyapp.inject.view.details_activity;

import com.olegsagenadatrytwo.partyapp.view.details_activity.DetailsActivity;

import dagger.Component;


@Component(modules = DetailsActivityModule.class)
public interface DetailsActivityComponent {

    void inject(DetailsActivity detailsActivity);

}
