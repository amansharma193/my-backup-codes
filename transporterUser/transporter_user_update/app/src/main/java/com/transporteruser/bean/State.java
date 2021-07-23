package com.transporteruser.bean;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class State {

    @SerializedName("stateName")
    @Expose
    private String stateName;
    @SerializedName("stateId")
    @Expose
    private String stateId;

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public String getStateId() {
        return stateId;
    }

    public void setStateId(String stateId) {
        this.stateId = stateId;
    }

    @Override
    public String toString() {
        return stateName;
    }
}
