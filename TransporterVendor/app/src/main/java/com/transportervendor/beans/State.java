package com.transportervendor.beans;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class State {
    @SerializedName("stateName")
    @Expose
    private String stateName;

    @SerializedName("stateId")
    @Expose
    private String stateId;
    public State(String stateName, String stateId) {
        super();
        this.stateName = stateName;
        this.stateId = stateId;
    }
    public State() {
        super();
    }
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
}