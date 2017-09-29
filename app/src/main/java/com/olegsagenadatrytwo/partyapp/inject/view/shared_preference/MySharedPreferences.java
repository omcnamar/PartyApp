package com.olegsagenadatrytwo.partyapp.inject.view.shared_preference;

import android.content.SharedPreferences;

import javax.inject.Inject;

/**
 * Created by Admin on 9/27/2017.
 */

public class MySharedPreferences {

    private SharedPreferences mSharedPreferences;

    @Inject
    public MySharedPreferences(SharedPreferences mSharedPreferences) {
        this.mSharedPreferences = mSharedPreferences;
    }

    public void putStringData(String key, String data) {
        mSharedPreferences.edit().putString(key,data).apply();
    }

    public String getStringData(String key) {
        return mSharedPreferences.getString(key, null);
    }

    public void putIntData(String key, int data) {
        mSharedPreferences.edit().putInt(key,data).apply();
    }

    public int getIntData(String key) {
        return mSharedPreferences.getInt(key, -0);
    }
}
