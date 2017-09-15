
package com.olegsagenadatrytwo.partyapp.model.eventbrite;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Name {

    @SerializedName("text")
    @Expose
    private String text;
    @SerializedName("html")
    @Expose
    private String html;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

}