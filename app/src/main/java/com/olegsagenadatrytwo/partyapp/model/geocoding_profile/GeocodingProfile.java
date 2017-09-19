
package com.olegsagenadatrytwo.partyapp.model.geocoding_profile;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class GeocodingProfile implements Serializable, Parcelable
{

    @SerializedName("results")
    @Expose
    private List<Result> results = null;
    @SerializedName("status")
    @Expose
    private String status;
    public final static Creator<GeocodingProfile> CREATOR = new Creator<GeocodingProfile>() {


        @SuppressWarnings({
            "unchecked"
        })
        public GeocodingProfile createFromParcel(Parcel in) {
            GeocodingProfile instance = new GeocodingProfile();
            in.readList(instance.results, (com.olegsagenadatrytwo.partyapp.model.geocoding_profile.Result.class.getClassLoader()));
            instance.status = ((String) in.readValue((String.class.getClassLoader())));
            return instance;
        }

        public GeocodingProfile[] newArray(int size) {
            return (new GeocodingProfile[size]);
        }

    }
    ;
    private final static long serialVersionUID = 8945047583473808791L;

    public List<Result> getResults() {
        return results;
    }

    public void setResults(List<Result> results) {
        this.results = results;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(results);
        dest.writeValue(status);
    }

    public int describeContents() {
        return  0;
    }

}
