
package com.olegsagenadatrytwo.partyapp.model.eventbrite;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Pagination {

    @SerializedName("page_number")
    @Expose
    private Integer pageNumber;
    @SerializedName("page_size")
    @Expose
    private Integer pageSize;
    @SerializedName("continuation")
    @Expose
    private String continuation;
    @SerializedName("has_more_items")
    @Expose
    private Boolean hasMoreItems;

    public Integer getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public String getContinuation() {
        return continuation;
    }

    public void setContinuation(String continuation) {
        this.continuation = continuation;
    }

    public Boolean getHasMoreItems() {
        return hasMoreItems;
    }

    public void setHasMoreItems(Boolean hasMoreItems) {
        this.hasMoreItems = hasMoreItems;
    }

}
