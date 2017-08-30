
package com.olegsagenadatrytwo.partyapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "onCreate: Pinche Segundo" );
        Log.d(TAG, "onCreate: Rodrigo" );
        Log.d(TAG, "onCreate: Test2" );
        //edited from web
    }
}
