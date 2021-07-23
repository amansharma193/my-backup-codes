package com.transporterapi.bean;

public class BidWithLead {
	private Bid bid;
	private Leads leads;
	
	public BidWithLead() {
		super();
	}
	public BidWithLead(Bid bid, Leads leads) {
		super();
		this.bid = bid;
		this.leads = leads;
	}
	public Bid getBid() {
		return bid;
	}
	public void setBid(Bid bid) {
		this.bid = bid;
	}
	public Leads getLeads() {
		return leads;
	}
	public void setLeads(Leads leads) {
		this.leads = leads;
	}
	

}
