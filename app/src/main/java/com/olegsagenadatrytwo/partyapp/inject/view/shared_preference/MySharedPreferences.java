package com.olegsagenadatrytwo.partyapp.inject.view.shared_preference;

import android.content.SharedPreferences;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
        mSharedPreferences.edit().putString(key, data).apply();
    }

    public String getStringData(String key) {
        return mSharedPreferences.getString(key, null);
    }

    public void putIntData(String key, int data) {
        mSharedPreferences.edit().putInt(key, data).apply();
    }

    public int getIntData(String key) {
        return mSharedPreferences.getInt(key, -0);
    }

    public List<String> getMyLikes(String key) {
        Set<String> set = mSharedPreferences.getStringSet(key, null);
        List<String> likesList = new ArrayList<>();
        if (set != null) {
            for (String like : set) {
                likesList.add(like);
            }
        }
        return likesList;
    }

    public void addLike(String key, String partyId) {

        //save like to shared preference
        Set<String> set = mSharedPreferences.getStringSet(key, null);
        List<String> likesList = getMyLikes(key);

        if (set != null) {
            set.add(partyId);

        }else{
            set = new HashSet<>();
            set.add(partyId);
        }
        mSharedPreferences.edit().putStringSet(key, set).apply();
        likesList.add(partyId);

        //save like to database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference profileReference = database.getReference("profiles");
        profileReference
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("myLikedParties")
                .setValue(likesList);
    }

}
