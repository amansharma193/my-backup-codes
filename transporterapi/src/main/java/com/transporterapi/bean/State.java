package com.transporterapi.bean;

public class State {
	private String stateName;
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
