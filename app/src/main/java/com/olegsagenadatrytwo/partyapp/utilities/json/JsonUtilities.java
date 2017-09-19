package com.olegsagenadatrytwo.partyapp.utilities.json;

import java.io.IOException;
import java.net.URL;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by aaron on 9/17/17.
 */

public class JsonUtilities {
      //                      //
     //  Get Json Response   //
    //**********************//
    public static String getJsonResponse(URL passedURL) throws IOException {
        String responseReturn;
        final OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(passedURL)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            responseReturn = response.body().string();
        }

        return responseReturn;
    }
}
