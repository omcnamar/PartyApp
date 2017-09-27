package com.olegsagenadatrytwo.partyapp.inject.view.shared_preference;

import android.content.Context;
import android.content.SharedPreferences;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Admin on 9/27/2017.
 */
@Module
public class SharedPreferencesModule {

    @Provides
    @Singleton
    @Inject
    SharedPreferences provideSharedPreferences(Context context) {
        return context.getSharedPreferences("PrefName",Context.MODE_PRIVATE);
    }
}
